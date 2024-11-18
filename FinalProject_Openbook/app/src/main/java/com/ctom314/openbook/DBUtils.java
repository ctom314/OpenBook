package com.ctom314.openbook;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DBUtils extends SQLiteOpenHelper
{
    private static final String DATABASE_NAME = "OpenBook";
    private static final int DATABASE_VERSION = 1;

    // Table names
    private static final String USERS_TABLE = "Users";
    private static final String BOOKS_TABLE = "Books";
    private static final String POSTS_TABLE = "Posts";
    private static final String COMMENTS_TABLE = "Comments";

    public DBUtils(Context c)
    {
        super(c, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        // Create Users table
        db.execSQL("CREATE TABLE " + USERS_TABLE + " ("
                + "userId INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                + "name TEXT, "
                + "email TEXT, "
                + "username TEXT, "
                + "password_hash BLOB, "
                + "password_salt BLOB "
                + ");");

        // Create Books table
        db.execSQL("CREATE TABLE " + BOOKS_TABLE + " ("
                + "bookId INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                + "title TEXT, "
                + "author TEXT, "
                + "year INTEGER "
                + ");");

        // Create Posts table
        db.execSQL("CREATE TABLE " + POSTS_TABLE + " ("
                + "postId INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                + "userId INTEGER, "
                + "bookId INTEGER, "
                + "timestamp TEXT, "
                + "content TEXT, "
                + "FOREIGN KEY (userId) REFERENCES " + USERS_TABLE + "(userId), "
                + "FOREIGN KEY (bookId) REFERENCES " + BOOKS_TABLE + "(bookId) "
                + ");");

        // Create Comments table
        db.execSQL("CREATE TABLE " + COMMENTS_TABLE + " ("
                + "commentId INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                + "postId INTEGER, "
                + "userId INTEGER, "
                + "timestamp TEXT, "
                + "content TEXT, "
                + "FOREIGN KEY (postId) REFERENCES " + POSTS_TABLE + "(postId), "
                + "FOREIGN KEY (userId) REFERENCES " + USERS_TABLE + "(userId) "
                + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV)
    {
        // Drop tables, then create
        db.execSQL("DROP TABLE IF EXISTS " + USERS_TABLE + ";");
        db.execSQL("DROP TABLE IF EXISTS " + BOOKS_TABLE + ";");
        db.execSQL("DROP TABLE IF EXISTS " + POSTS_TABLE + ";");
        db.execSQL("DROP TABLE IF EXISTS " + COMMENTS_TABLE + ";");

        onCreate(db);
    }

    // =============================================================================================
    //                                          ACCOUNTS
    // =============================================================================================

    /**
     * Add a new account to the database.
     * @param a Account object to add to the database.
     */
    public void addAccount(Account a)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        // In order to store bytes in the Database, ContentValues must be used to add directly
        // to the database, instead of concatenating the values into the query string.
        ContentValues values = new ContentValues();
        values.put("name", a.getName());
        values.put("email", a.getEmail());
        values.put("username", a.getUsername());
        values.put("password_hash", a.getPasswordHash());
        values.put("password_salt", a.getPasswordSalt());

        // Insert the new account into the database
        db.insert(USERS_TABLE, null, values);
        db.close();
    }

    /**
     * Verify the account login credentials.
     * @param username Username of the account.
     * @param password Password of the account.
     * @return Result object with success or failure message.
     */
    public Result verifyAccount(String username, String password)
    {
        Result result;
        SQLiteDatabase db = this.getReadableDatabase();

        // Get account using username
        String query = "SELECT * FROM " + USERS_TABLE + " WHERE username = ?;";
        Cursor cursor = db.rawQuery(query, new String[] {username});

        if (cursor.moveToFirst())
        {
            // Get password hash and salt
            byte[] passwordHash = cursor.getBlob(4);
            byte[] passwordSalt = cursor.getBlob(5);

            // Check if password matches
            if (PasswordUtils.passwordHashesMatch(password, passwordSalt, passwordHash))
            {
                // Password matches
                result = Result.success();
            }
            else
            {
                // Password does not match
                // Show vague error message to prevent brute force attacks
                result = Result.failure("Username or password is incorrect.");
            }

        }
        else
        {
            // Account not found
            result = Result.failure("Username or password is incorrect.");
        }

        cursor.close();
        db.close();
        return result;
    }

    /**
     * Check if an account already exists in the database.
     * @param username Username to check.
     * @return True if the account exists, false otherwise.
     */
    public boolean accountExists(String username)
    {
        SQLiteDatabase db = this.getReadableDatabase();

        // Get account using username
        String query = "SELECT * FROM " + USERS_TABLE + " WHERE username = ?;";
        Cursor cursor = db.rawQuery(query, new String[] {username});

        boolean exists = cursor.moveToFirst();
        cursor.close();
        db.close();

        return exists;
    }

    /**
     * Get an account using the username.
     * @param username Username of the account.
     * @return Account object if found, null otherwise.
     */
    public Account getAccount(String username)
    {
        Account a = null;
        SQLiteDatabase db = this.getReadableDatabase();

        // Get account using username
        String query = "SELECT * FROM " + USERS_TABLE + " WHERE username = ?;";
        Cursor cursor = db.rawQuery(query, new String[] {username});

        if (cursor.moveToFirst())
        {
            // Get account details
            String name = cursor.getString(1);
            String email = cursor.getString(2);
            byte[] passwordHash = cursor.getBlob(4);
            byte[] passwordSalt = cursor.getBlob(5);

            a = new Account(name, email, username, passwordHash, passwordSalt);
        }

        cursor.close();
        db.close();

        return a;
    }

    /**
     * Get the user ID using the username.
     * @param username Username of the account.
     * @return User ID if found, -1 otherwise.
     */
    public int getUserId(String username)
    {
        int userId = -1;
        SQLiteDatabase db = this.getReadableDatabase();

        // Get account using username
        String query = "SELECT userId FROM " + USERS_TABLE + " WHERE username = ?;";
        Cursor cursor = db.rawQuery(query, new String[] {username});

        if (cursor.moveToFirst())
        {
            userId = cursor.getInt(0);
        }

        cursor.close();
        db.close();

        return userId;
    }

    /**
     * Get the username using the user ID.
     * @param userId User ID of the account.
     * @return Username if found, null otherwise.
     */
    public String getUsername(int userId)
    {
        String username = null;
        SQLiteDatabase db = this.getReadableDatabase();

        // Get account using userId
        String query = "SELECT username FROM " + USERS_TABLE + " WHERE userId = ?;";
        Cursor cursor = db.rawQuery(query, new String[] {String.valueOf(userId)});

        if (cursor.moveToFirst())
        {
            username = cursor.getString(0);
        }

        cursor.close();
        db.close();

        return username;
    }

    /**
     * Delete an account from the database.
     * @param userId User ID of the account to delete.
     */
    public void deleteAccount(int userId)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        // Delete all psots from user
        String query = "DELETE FROM " + POSTS_TABLE + " WHERE userId = ?;";
        db.execSQL(query, new Object[] {userId});

        // Delete all comments from user
        query = "DELETE FROM " + COMMENTS_TABLE + " WHERE userId = ?;";
        db.execSQL(query, new Object[] {userId});

        // Delete account
        query = "DELETE FROM " + USERS_TABLE + " WHERE userId = ?;";
        db.execSQL(query, new Object[] {userId});

        db.close();
    }

    /**
     * Update an account in the database, excluding the username.
     * @param account Account object to update in the database.
     */
    public void updateAccount(Account account)
    {
        int userId = getUserId(account.getUsername());
        SQLiteDatabase db = this.getWritableDatabase();

        // Update account
        String query = "UPDATE " + USERS_TABLE + " SET name = ?, email = ?, password_hash = ?, " +
                "password_salt = ? WHERE userId = ?;";
        db.execSQL(query, new Object[] {account.getName(), account.getEmail(),
                        account.getPasswordHash(), account.getPasswordSalt(), userId});

        db.close();
    }

    /**
     * Update an account in the database, including the username.
     * @param account Account object to update in the database.
     * @param newUsername New username for the account.
     */
    public void updateAccount(Account account, String newUsername)
    {
        int userId = getUserId(account.getUsername());
        SQLiteDatabase db = this.getWritableDatabase();

         // Update account
        String query = "UPDATE " + USERS_TABLE + " SET name = ?, email = ?, username = ?, " +
                "password_hash = ?, password_salt = ? WHERE userId = ?;";
        db.execSQL(query, new Object[] {account.getName(), account.getEmail(), newUsername,
                account.getPasswordHash(), account.getPasswordSalt(), userId});

        db.close();
    }

    // =============================================================================================
    //                                          BOOKS
    // =============================================================================================

    /**
     * Add a new book to the database.
     * @param b Book object to add to the database.
     */
    public void addBook(Book b)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        // Run query, blocking SQL injection and allowing special characters
        String query = "INSERT INTO " + BOOKS_TABLE + " (title, author, year) VALUES (?, ?, ?);";
        db.execSQL(query, new Object[] {b.getTitle(), b.getAuthor(), b.getYear()});

        db.close();
    }

    /**
     * Get a book using the book ID.
     * @param bookId Book ID of the book.
     * @return Book object if found, null otherwise.
     */
    public Book getBook(int bookId)
    {
        Book b = null;
        SQLiteDatabase db = this.getReadableDatabase();

        // Get book using title
        String query = "SELECT * FROM " + BOOKS_TABLE + " WHERE bookId = ?;";
        Cursor cursor = db.rawQuery(query, new String[] {String.valueOf(bookId)});

        if (cursor.moveToFirst())
        {
            // Get book details
            String title = cursor.getString(1);
            String author = cursor.getString(2);
            int year = cursor.getInt(3);

            b = new Book(title, author, year);
        }

        cursor.close();
        db.close();

        return b;
    }

    /**
     * Get the book ID using the book title.
     * @param title Title of the book.
     * @return Book ID if found, -1 otherwise.
     */
    public int getBookId(String title)
    {
        int bookId = -1;
        SQLiteDatabase db = this.getReadableDatabase();

        // Get book using title
        String query = "SELECT bookId FROM " + BOOKS_TABLE + " WHERE title = ?;";
        Cursor cursor = db.rawQuery(query, new String[] {title});

        if (cursor.moveToFirst())
        {
            bookId = cursor.getInt(0);
        }

        cursor.close();
        db.close();

        return bookId;
    }

    /**
     * Get all book titles from the database.
     * @return ArrayList of book titles.
     */
    public ArrayList<String> getBookList()
    {
        ArrayList<String> books = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Get all book titles
        String query = "SELECT title FROM " + BOOKS_TABLE + ";";
        Cursor cursor = db.rawQuery(query, null);

        while (cursor.moveToNext())
        {
            books.add(cursor.getString(0));
        }

        cursor.close();
        db.close();

        return books;
    }

    /**
     * Get all books from the database.
     * @return ArrayList of Book objects.
     */
    public ArrayList<Book> getAllBooks()
    {
        ArrayList<Book> books = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Get all books. Sort by title
        String query = "SELECT * FROM " + BOOKS_TABLE + " ORDER BY title;";
        Cursor cursor = db.rawQuery(query, null);

        while (cursor.moveToNext())
        {
            // Get book details
            String title = cursor.getString(1);
            String author = cursor.getString(2);
            int year = cursor.getInt(3);

            // Create book object
            Book b = new Book(title, author, year);
            books.add(b);
        }

        cursor.close();
        db.close();

        return books;
    }

    /**
     * Check if a book with the title and author already exists in the database.
     * @param title Title of the book.
     * @param author Author of the book.
     * @return True if the book exists, false otherwise.
     */
    public boolean bookAlreadyAdded(String title, String author)
    {
        SQLiteDatabase db = this.getReadableDatabase();

        // Get book using title
        String query = "SELECT * FROM " + BOOKS_TABLE + " WHERE title = ? AND author = ?;";
        Cursor cursor = db.rawQuery(query, new String[] {title, author});

        boolean exists = cursor.moveToFirst();
        cursor.close();
        db.close();

        return exists;
    }

    /**
     * Search for books in the database.
     * @param q Query object with data to search for.
     * @return ArrayList of Book objects.
     */
    public ArrayList<Book> searchBooks(Query q)
    {
        ArrayList<Book> books = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Track conditions
        boolean hasCondition = false;

        // =========================================================================================
        //                                  Searching Logic
        // =========================================================================================

        String query = "SELECT * FROM " + BOOKS_TABLE + " WHERE ";

        // Arguments for query
        List<String> args = new ArrayList<>();

        // Search by title
        if (!q.getTitle().isEmpty())
        {
            query += "title COLLATE NOCASE LIKE ? ";
            args.add("%" + q.getTitle() + "%");
            hasCondition = true;
        }

        // Search by author
        if (!q.getAuthor().isEmpty())
        {
            if (hasCondition)
            {
                query += "AND ";
            }

            query += "author COLLATE NOCASE LIKE ?";
            args.add("%" + q.getAuthor() + "%");
        }

        // Finish query
        query += "ORDER BY title;";

        // =========================================================================================

        // Get books using query
        Cursor cursor = db.rawQuery(query, args.toArray(new String[0]));

        while (cursor.moveToNext())
        {
            // Get book details
            String title = cursor.getString(1);
            String author = cursor.getString(2);
            int year = cursor.getInt(3);

            // Create book object
            Book b = new Book(title, author, year);
            books.add(b);
        }

        cursor.close();
        db.close();

        return books;
    }

    // =============================================================================================
    //                                          POSTS
    // =============================================================================================

    /**
     * Add a new post to the database.
     * @param p Post object to add to the database.
     */
    public void addPost(Post p)
    {
        // Get userId
        int userId = getUserId(p.getUsername());

        SQLiteDatabase db = this.getWritableDatabase();

        // Run query, blocking SQL injection and allowing special characters
        String query = "INSERT INTO " + POSTS_TABLE + " (userId, bookId, timestamp, content) VALUES (?, ?, ?, ?);";
        db.execSQL(query, new Object[] {userId, p.getBookId(), p.getTimestamp(), p.getContent()});

        db.close();
    }

    /**
     * Get a post using the post ID.
     * @param postId Post ID of the post.
     * @return Post object if found, null otherwise.
     */
    public Post getPost(int postId)
    {
        Post p = null;
        SQLiteDatabase db = this.getReadableDatabase();

        // Get post using postId
        String query = "SELECT * FROM " + POSTS_TABLE + " WHERE postId = ?;";
        Cursor cursor = db.rawQuery(query, new String[] {String.valueOf(postId)});

        if (cursor.moveToFirst())
        {
            // Get post details
            int bookId = cursor.getInt(2);
            String username = getUsername(cursor.getInt(1));
            String timestamp = cursor.getString(3);
            String content = cursor.getString(4);

            // Create post object
            p = new Post(bookId, username, timestamp, content);
        }

        cursor.close();
        db.close();

        return p;
    }

    /**
     * Get the post ID using the username and timestamp.
     * @param username Username of the account.
     * @param timestamp Timestamp of the post.
     * @return Post ID if found, -1 otherwise.
     */
    public int getPostId(String username, String timestamp)
    {
        int postId = -1;

        // Get userId
        int userId = getUserId(username);

        SQLiteDatabase db = this.getReadableDatabase();

        // Get post using userId and timestamp
        String query = "SELECT postId FROM " + POSTS_TABLE + " WHERE userId = ? AND timestamp = ?;";
        Cursor cursor = db.rawQuery(query, new String[] {String.valueOf(userId), timestamp});

        if (cursor.moveToFirst())
        {
            postId = cursor.getInt(0);
        }

        cursor.close();
        db.close();

        return postId;
    }

    /**
     * Get the n most recent posts from the database.
     * @param n Number of posts to retrieve.
     * @return ArrayList of Post objects.
     */
    public ArrayList<Post> getRecentPosts(int n)
    {
        ArrayList<Post> posts = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Get n recent posts
        String query = "SELECT * FROM " + POSTS_TABLE + " ORDER BY timestamp DESC LIMIT " + n + ";";
        Cursor cursor = db.rawQuery(query, null);

        while (cursor.moveToNext())
        {
            // Get post details
            int bookId = cursor.getInt(2);
            String username = getUsername(cursor.getInt(1));
            String timestamp = cursor.getString(3);
            String content = cursor.getString(4);

            // Create post object
            Post p = new Post(bookId, username, timestamp, content);
            posts.add(p);
        }

        cursor.close();
        db.close();

        return posts;
    }

    /**
     * Update a post in the database.
     * @param p Post object to update in the database.
     */
    public void updatePost(Post p)
    {
        int userId = getUserId(p.getUsername());
        SQLiteDatabase db = this.getWritableDatabase();

        // Update post
        String query = "UPDATE " + POSTS_TABLE + " SET content = ? WHERE userId = ? AND timestamp = ?;";
        db.execSQL(query, new Object[] {p.getContent(), userId, p.getTimestamp()});

        db.close();
    }

    /**
     * Delete a post from the database.
     * @param postId Post ID of the post to delete.
     */
    public void deletePost(int postId)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        // Delete post
        String query = "DELETE FROM " + POSTS_TABLE + " WHERE postId = ?;";
        db.execSQL(query, new Object[] {postId});

        db.close();
    }

    /**
     * Get all posts from a specific book.
     * @param bookId Book ID of the book to get posts from.
     * @return ArrayList of Post objects.
     */
    public ArrayList<Post> getBookPosts(int bookId)
    {
        ArrayList<Post> posts = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Get all posts from book. Order by most recent.
        String query = "SELECT * FROM " + POSTS_TABLE + " WHERE bookId = ? ORDER BY timestamp DESC;";
        Cursor cursor = db.rawQuery(query, new String[] {String.valueOf(bookId)});

        while (cursor.moveToNext())
        {
            // Get post details
            String username = getUsername(cursor.getInt(1));
            String timestamp = cursor.getString(3);
            String content = cursor.getString(4);

            // Create post object
            Post p = new Post(bookId, username, timestamp, content);
            posts.add(p);
        }

        cursor.close();
        db.close();

        return posts;
    }

    // =============================================================================================
    //                                          COMMENTS
    // =============================================================================================

    /**
     * Add a new comment to the database.
     * @param c Comment object to add to the database.
     */
    public void addComment(Comment c)
    {
        // Get IDs
        int userId = getUserId(c.getUsername());

        SQLiteDatabase db = this.getWritableDatabase();

        // Run query
        String query = "INSERT INTO " + COMMENTS_TABLE + " (postId, userId, timestamp, content) VALUES (?, ?, ?, ?);";
        db.execSQL(query, new Object[] {c.getPostId(), userId, c.getTimestamp(), c.getContent()});

        db.close();
    }

    /**
     * Get the comment Id using the username and timestamp.
     * @param username Username of the account.
     * @param timestamp Timestamp of the comment.
     * @return Comment ID if found, -1 otherwise.
     */
    public int getCommentId(String username, String timestamp)
    {
        int commentId = -1;

        // Get userId
        int userId = getUserId(username);

        SQLiteDatabase db = this.getReadableDatabase();

        // Get comment using userId and timestamp
        String query = "SELECT commentId FROM " + COMMENTS_TABLE + " WHERE userId = ? AND timestamp = ?;";
        Cursor cursor = db.rawQuery(query, new String[] {String.valueOf(userId), timestamp});

        if (cursor.moveToFirst())
        {
            commentId = cursor.getInt(0);
        }

        cursor.close();
        db.close();

        return commentId;
    }

    /**
     * Get all replies from a post.
     * @param postId Post ID of the post to get replies from.
     * @return ArrayList of Comment objects.
     */
    public ArrayList<Comment> getPostReplies(int postId)
    {
        ArrayList<Comment> comments = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // Get all replies from post. Order by most recent.
        String query = "SELECT * FROM " + COMMENTS_TABLE + " WHERE postId = ? ORDER BY timestamp DESC;";
        Cursor cursor = db.rawQuery(query, new String[] {String.valueOf(postId)});

        while (cursor.moveToNext())
        {
            // Get comment details
            String username = getUsername(cursor.getInt(2));
            String timestamp = cursor.getString(3);
            String content = cursor.getString(4);

            // Create comment object
            Comment c = new Comment(username, timestamp, content, postId);
            comments.add(c);
        }

        cursor.close();
        db.close();

        return comments;
    }

    /**
     * Delete a comment from the database.
     * @param commentId Comment ID of the comment to delete.
     */
    public void deleteComment(int commentId)
    {
        SQLiteDatabase db = this.getWritableDatabase();

        // Delete comment
        String query = "DELETE FROM " + COMMENTS_TABLE + " WHERE commentId = ?;";
        db.execSQL(query, new Object[] {commentId});

        db.close();
    }

    // =============================================================================================
    //                          INITIALIZE DATABASES WITH DUMMY DATA
    // =============================================================================================

    public void initAllTables()
    {
        initUsers();
        initBooks();
        initPosts();
        initComments();
    }

    // Users
    private void initUsers()
    {
        if (countRows(USERS_TABLE) == 0)
        {
            // Hash and salt for all dummy accounts
            byte[] salt = PasswordUtils.generateSalt();
            byte[] hash = PasswordUtils.hashPassword("password", salt);

            // Accounts
            Account test = new Account("John Smith", "jsmith@hotmail.com", "jsmith", PasswordUtils.hashPassword("1234", salt), salt);

            Account u1 = new Account("Tom Jenkins", "tjenkins@email.com", "tjenkins", hash, salt);
            Account u2 = new Account("John Doe", "jdoe@hotmail.com", "jdoe22", hash, salt);
            Account u3 = new Account("Bob Willy", "bwilly34@gmail.com", "bwilly579", hash, salt);
            Account u4 = new Account("Charlie Frank", "cfrank99@omail.co.ca", "cfrank", hash, salt);
            Account u5 = new Account("Benjamin Jonas II", "benjones2@euromail.org", "benjones2", hash, salt);

            // No post account. Just replies
            Account u6 = new Account("Tim Trout", "ttrout@outlook.com", "ttrout", hash, salt);

            Account u7 = new Account("Alice Morgan", "amorgan22@yahoo.com", "alice.morgan", hash, salt);
            Account u8 = new Account("Steve Harper", "sharp12@outlook.com", "sharp12", hash, salt);
            Account u9 = new Account("Rachel Green", "rgreen89@gmail.com", "rachel.green", hash, salt);
            Account u10 = new Account("Mike Ross", "mike.ross@protonmail.com", "rosslawyer", hash, salt);
            Account u11 = new Account("Sara Evans", "sevans1234@gmail.com", "sara.evans", hash, salt);
            Account u12 = new Account("David Johnson", "djohnson45@icloud.com", "davey45", hash, salt);
            Account u13 = new Account("Linda Carter", "lcarter77@hotmail.com", "lcarter77", hash, salt);
            Account u14 = new Account("Kevin Brown", "kevin.brown@mail.com", "kevinb", hash, salt);
            Account u15 = new Account("Emily Stone", "estone90@gmail.com", "emstone90", hash, salt);

            // Add accounts to database
            addAccount(test);

            addAccount(u1);
            addAccount(u2);
            addAccount(u3);
            addAccount(u4);
            addAccount(u5);
            addAccount(u6);
            addAccount(u7);
            addAccount(u8);
            addAccount(u9);
            addAccount(u10);
            addAccount(u11);
            addAccount(u12);
            addAccount(u13);
            addAccount(u14);
            addAccount(u15);
        }
    }

    // Books
    private void initBooks()
    {
        if (countRows(BOOKS_TABLE) == 0)
        {
            // Books
            Book b1 = new Book("The Great Gatsby", "F. Scott Fitzgerald", 1925);
            Book b2 = new Book("To Kill a Mockingbird", "Harper Lee", 1960);
            Book b3 = new Book("1984", "George Orwell", 1949);
            Book b4 = new Book("The Catcher in the Rye", "J.D. Salinger", 1951);
            Book b5 = new Book("Of Mice and Men", "John Steinbeck", 1937);

            // Long title
            Book b6 = new Book("My Grandmother Asked Me to Tell You She's Sorry", "Fredrik Backman", 2013);

            Book b7 = new Book("Pride and Prejudice", "Jane Austen", 1813);
            Book b8 = new Book("Moby-Dick", "Herman Melville", 1851);
            Book b9 = new Book("War and Peace", "Leo Tolstoy", 1869);
            Book b10 = new Book("The Hobbit", "J.R.R. Tolkien", 1937);
            Book b11 = new Book("Crime and Punishment", "Fyodor Dostoevsky", 1866);
            Book b12 = new Book("The Adventures of Huckleberry Finn", "Mark Twain", 1884);
            Book b13 = new Book("Brave New World", "Aldous Huxley", 1932);
            Book b14 = new Book("The Lord of the Rings", "J.R.R. Tolkien", 1954);


            // Add books to database
            addBook(b1);
            addBook(b2);
            addBook(b3);
            addBook(b4);
            addBook(b5);
            addBook(b6);
            addBook(b7);
            addBook(b8);
            addBook(b9);
            addBook(b10);
            addBook(b11);
            addBook(b12);
            addBook(b13);
            addBook(b14);
        }
    }

    // Posts
    private void initPosts()
    {
        if (countRows(POSTS_TABLE) == 0)
        {
            // Timestamps
            String t1 = Utilities.makeTimestamp(2024, 10, 23, 12, 45, 1);
            String t2 = Utilities.makeTimestamp(2024, 11, 9, 13, 1, 21);
            String t3 = Utilities.makeTimestamp(2024, 11, 8, 9, 30, 45);
            String t4 = Utilities.makeTimestamp(2024, 11, 8, 9, 34, 12);
            String t5 = Utilities.makeTimestamp(2024, 11, 1, 17, 59, 53);
            String t6 = Utilities.makeTimestamp(2024, 11, 5, 8, 12, 34);
            String t7 = Utilities.makeTimestamp(2024, 11, 10, 10, 45, 15);
            String t8 = Utilities.makeTimestamp(2024, 11, 11, 14, 30, 10);
            String t9 = Utilities.makeTimestamp(2024, 11, 12, 8, 15, 45);
            String t10 = Utilities.makeTimestamp(2024, 11, 13, 16, 20, 5);
            String t11 = Utilities.makeTimestamp(2024, 11, 14, 19, 10, 30);
            String t12 = Utilities.makeTimestamp(2024, 11, 15, 11, 5, 50);
            String t13 = Utilities.makeTimestamp(2024, 11, 16, 7, 25, 10);
            String t14 = Utilities.makeTimestamp(2024, 11, 16, 20, 45, 15);
            String t15 = Utilities.makeTimestamp(2024, 11, 17, 15, 10, 40);
            String t16 = Utilities.makeTimestamp(2024, 11, 18, 9, 55, 25);
            String t17 = Utilities.makeTimestamp(2024, 10, 19, 12, 30, 35);
            String t18 = Utilities.makeTimestamp(2024, 10, 20, 10, 15, 10);
            String t19 = Utilities.makeTimestamp(2024, 9, 21, 14, 50, 5);
            String t20 = Utilities.makeTimestamp(2023, 10, 22, 17, 20, 55);

            // Posts
            Post p1 = new Post(1, "tjenkins", t1, "This book is amazing!");
            Post p2 = new Post(2, "jdoe22", t2, "I love this book!");
            Post p3 = new Post(3, "bwilly579", t3, "I can't put this book down!");
            Post p4 = new Post(4, "bwilly579", t4, "I'm not a fan of this book.");
            Post p5 = new Post(5, "cfrank", t5, "This book is a classic!");
            Post p6 = new Post(3, "tjenkins", t6, "I'm not sure how I feel about this book.");
            Post p7 = new Post(6, "alice.morgan", t7, "This book made me think deeply about life.");
            Post p8 = new Post(7, "sharp12", t8, "I couldn't finish it, but the writing was excellent.");
            Post p9 = new Post(8, "rachel.green", t9, "The character development was fantastic!");
            Post p10 = new Post(9, "rosslawyer", t10, "A must-read for anyone who loves history.");
            Post p11 = new Post(10, "sara.evans", t11, "I learned so much from this book!");
            Post p12 = new Post(11, "davey45", t12, "The plot twists were mind-blowing.");
            Post p13 = new Post(12, "lcarter77", t13, "A little slow at first, but worth it in the end.");
            Post p14 = new Post(13, "kevinb", t14, "This book changed my perspective on many things.");
            Post p15 = new Post(14, "emstone90", t15, "The imagery was so vivid, it felt like I was there.");
            Post p16 = new Post(1, "alice.morgan", t16, "One of my all-time favorites!");
            Post p17 = new Post(2, "sharp12", t17, "I found this book surprisingly relatable.");
            Post p18 = new Post(3, "rachel.green", t18, "A gripping story from start to finish.");
            Post p19 = new Post(4, "rosslawyer", t19, "I didn't enjoy the ending, but overall a great read.");
            Post p20 = new Post(5, "sara.evans", t20, "I couldn't put it down—read it in one sitting!");

            // Add posts to database
            addPost(p1);
            addPost(p2);
            addPost(p3);
            addPost(p4);
            addPost(p5);
            addPost(p6);
            addPost(p7);
            addPost(p8);
            addPost(p9);
            addPost(p10);
            addPost(p11);
            addPost(p12);
            addPost(p13);
            addPost(p14);
            addPost(p15);
            addPost(p16);
            addPost(p17);
            addPost(p18);
            addPost(p19);
            addPost(p20);
        }
    }

    // Comments
    private void initComments()
    {
        if (countRows(COMMENTS_TABLE) == 0)
        {
            // Timestamps
            String t1_1 = Utilities.makeTimestamp(2024, 10, 23, 13, 5, 32);
            String t1_2 = Utilities.makeTimestamp(2024, 10, 23, 15, 42, 10);
            String t1_3 = Utilities.makeTimestamp(2024, 10, 24, 10, 18, 47);

            String t2_1 = Utilities.makeTimestamp(2024, 11, 9, 13, 45, 14);
            String t2_2 = Utilities.makeTimestamp(2024, 11, 9, 17, 20, 51);
            String t2_3 = Utilities.makeTimestamp(2024, 11, 10, 12, 33, 9);
            String t2_4 = Utilities.makeTimestamp(2024, 11, 10, 14, 5, 22);

            String t3_1 = Utilities.makeTimestamp(2024, 11, 8, 9, 45, 12);
            String t3_2 = Utilities.makeTimestamp(2024, 11, 8, 12, 10, 34);
            String t3_3 = Utilities.makeTimestamp(2024, 11, 9, 14, 55, 20);

            String t4_1 = Utilities.makeTimestamp(2024, 11, 8, 10, 5, 40);
            String t4_2 = Utilities.makeTimestamp(2024, 11, 8, 13, 25, 15);
            String t4_3 = Utilities.makeTimestamp(2024, 11, 9, 11, 5, 55);
            String t4_4 = Utilities.makeTimestamp(2024, 11, 9, 15, 30, 10);

            String t5_1 = Utilities.makeTimestamp(2024, 11, 1, 18, 12, 5);
            String t5_2 = Utilities.makeTimestamp(2024, 11, 1, 21, 45, 37);
            String t5_3 = Utilities.makeTimestamp(2024, 11, 2, 10, 0, 15);

            String t6_1 = Utilities.makeTimestamp(2024, 11, 5, 8, 30, 45);
            String t6_2 = Utilities.makeTimestamp(2024, 11, 5, 14, 15, 0);
            String t6_3 = Utilities.makeTimestamp(2024, 11, 6, 9, 48, 22);

            String t7_1 = Utilities.makeTimestamp(2024, 11, 10, 11, 0, 45);
            String t7_2 = Utilities.makeTimestamp(2024, 11, 10, 12, 15, 30);

            String t8_1 = Utilities.makeTimestamp(2024, 11, 11, 15, 0, 10);
            String t8_2 = Utilities.makeTimestamp(2024, 11, 11, 16, 45, 5);

            String t9_1 = Utilities.makeTimestamp(2024, 11, 12, 9, 30, 40);

            String t10_1 = Utilities.makeTimestamp(2024, 11, 13, 17, 5, 25);
            String t10_2 = Utilities.makeTimestamp(2024, 11, 13, 18, 20, 50);

            String t11_1 = Utilities.makeTimestamp(2024, 11, 14, 20, 15, 35);
            String t11_2 = Utilities.makeTimestamp(2024, 11, 14, 21, 5, 10);

            String t12_1 = Utilities.makeTimestamp(2024, 11, 15, 12, 15, 45);

            String t13_1 = Utilities.makeTimestamp(2024, 11, 16, 8, 30, 20);
            String t13_2 = Utilities.makeTimestamp(2024, 11, 16, 21, 10, 5);

            String t14_1 = Utilities.makeTimestamp(2024, 11, 17, 16, 0, 40);

            String t15_1 = Utilities.makeTimestamp(2024, 11, 18, 10, 20, 15);
            String t15_2 = Utilities.makeTimestamp(2024, 11, 18, 11, 45, 5);

            String t16_1 = Utilities.makeTimestamp(2024, 11, 18, 13, 0, 25);
            String t16_2 = Utilities.makeTimestamp(2024, 11, 18, 14, 5, 50);

            String t17_1 = Utilities.makeTimestamp(2024, 10, 20, 10, 25, 15);

            String t18_1 = Utilities.makeTimestamp(2024, 10, 21, 15, 30, 5);

            String t19_1 = Utilities.makeTimestamp(2024, 9, 22, 18, 10, 40);

            String t20_1 = Utilities.makeTimestamp(2023, 11, 23, 12, 25, 55);

            // Comments
            Comment c1_1 = new Comment("jdoe22", t1_1, "I agree!", 1);
            Comment c1_2 = new Comment("bwilly579", t1_2, "I disagree.", 1);
            Comment c1_3 = new Comment("cfrank", t1_3, "I'm not sure how I feel about this book.", 1);

            Comment c2_1 = new Comment("tjenkins", t2_1, "I love this book!", 2);
            Comment c2_2 = new Comment("bwilly579", t2_2, "I can't put this book down!", 2);
            Comment c2_3 = new Comment("cfrank", t2_3, "This book is a classic!", 2);
            Comment c2_4 = new Comment("ttrout", t2_4, "I've read this book at least 5 times now. It is amazing!", 2);

            Comment c3_1 = new Comment("tjenkins", t3_1, "This book is okay.", 3);
            Comment c3_2 = new Comment("jdoe22", t3_2, "I'm not a fan of this book.", 3);
            Comment c3_3 = new Comment("cfrank", t3_3, "I love this book!", 3);

            Comment c4_1 = new Comment("tjenkins", t4_1, "This book is nice, but not my favorite.", 4);
            Comment c4_2 = new Comment("jdoe22", t4_2, "Why do you not like this book?", 4);
            Comment c4_3 = new Comment("cfrank", t4_4, "I agree with you.", 4);
            Comment c4_4 = new Comment("bwilly579", t4_3, "I don't like how Holden talks to his friends and family in the book.", 4);

            Comment c5_1 = new Comment("tjenkins", t5_1, "This book is not my cup of tea.", 5);
            Comment c5_2 = new Comment("jdoe22", t5_2, "John Steinbeck is a great author.", 5);
            Comment c5_3 = new Comment("cfrank", t5_3, "I don't feel the same way.", 5);

            Comment c6_1 = new Comment("tjenkins", t6_1, "I'm not sure how I feel about this book.", 6);
            Comment c6_2 = new Comment("jdoe22", t6_2, "This book is a cult classic!", 6);
            Comment c6_3 = new Comment("cfrank", t6_3, "George Orwell was ahead of his time.", 6);

            Comment c7_1 = new Comment("sharp12", t7_1, "That's a great perspective!", 7);
            Comment c7_2 = new Comment("rachel.green", t7_2, "I felt the same way.", 7);

            Comment c8_1 = new Comment("rosslawyer", t8_1, "I couldn't agree more.", 8);
            Comment c8_2 = new Comment("alice.morgan", t8_2, "The writing was beautiful.", 8);

            Comment c9_1 = new Comment("davey45", t9_1, "Absolutely loved the characters!", 9);

            Comment c10_1 = new Comment("sara.evans", t10_1, "This book was so informative.", 10);
            Comment c10_2 = new Comment("kevinb", t10_2, "History buffs will adore this one.", 10);

            Comment c11_1 = new Comment("emstone90", t11_1, "Such a great learning experience!", 11);
            Comment c11_2 = new Comment("lcarter77", t11_2, "I recommend this to everyone.", 11);

            Comment c12_1 = new Comment("alice.morgan", t12_1, "Those twists were shocking!", 12);

            Comment c13_1 = new Comment("sharp12", t13_1, "A slow start, but a strong finish.", 13);
            Comment c13_2 = new Comment("kevinb", t13_2, "I agree, it was worth it.", 13);

            Comment c14_1 = new Comment("rosslawyer", t14_1, "This book was truly inspiring.", 14);

            Comment c15_1 = new Comment("sara.evans", t15_1, "The descriptions were so vivid!", 15);
            Comment c15_2 = new Comment("rachel.green", t15_2, "It felt like a journey.", 15);

            Comment c16_1 = new Comment("emstone90", t16_1, "I re-read this every year.", 16);
            Comment c16_2 = new Comment("alice.morgan", t16_2, "It's one of my all-time favorites.", 16);

            Comment c17_1 = new Comment("sharp12", t17_1, "Very relatable content.", 17);

            Comment c18_1 = new Comment("kevinb", t18_1, "A page-turner, for sure.", 18);

            Comment c19_1 = new Comment("sara.evans", t19_1, "The ending left me wanting more.", 19);

            Comment c20_1 = new Comment("rosslawyer", t20_1, "One of the best books I've read recently.", 20);

            // Add comments to database
            addComment(c1_1);
            addComment(c1_2);
            addComment(c1_3);

            addComment(c2_1);
            addComment(c2_2);
            addComment(c2_3);
            addComment(c2_4);

            addComment(c3_1);
            addComment(c3_2);
            addComment(c3_3);

            addComment(c4_1);
            addComment(c4_2);
            addComment(c4_3);
            addComment(c4_4);

            addComment(c5_1);
            addComment(c5_2);
            addComment(c5_3);

            addComment(c6_1);
            addComment(c6_2);
            addComment(c6_3);

            addComment(c7_1);
            addComment(c7_2);

            addComment(c8_1);
            addComment(c8_2);

            addComment(c9_1);

            addComment(c10_1);
            addComment(c10_2);

            addComment(c11_1);
            addComment(c11_2);

            addComment(c12_1);

            addComment(c13_1);
            addComment(c13_2);

            addComment(c14_1);

            addComment(c15_1);
            addComment(c15_2);

            addComment(c16_1);
            addComment(c16_2);

            addComment(c17_1);

            addComment(c18_1);

            addComment(c19_1);

            addComment(c20_1);
        }
    }

    /**
     * Count the number of rows in a table.
     * @param tableName Name of the table to count rows from.
     * @return Number of rows in the table.
     */
    public int countRows(String tableName)
    {
        // Get count
        String query = "SELECT COUNT(*) FROM " + tableName + ";";

        // Get database
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        if (cursor != null)
        {
            cursor.moveToFirst();

            // Cannot just return cursor.getInt(0) because the cursor cannot be closed that way.
            // Must store the count in a var so the cursor can be closed properly.
            int count = cursor.getInt(0);
            cursor.close();

            return count;
        }

        // Close database
        db.close();

        // Return -1 if error
        return -1;
    }

    // =============================================================================================

}
