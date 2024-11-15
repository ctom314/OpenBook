package com.ctom314.openbook.pages;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.ctom314.openbook.BookTitleAdapter;
import com.ctom314.openbook.DBUtils;
import com.ctom314.openbook.Post;
import com.ctom314.openbook.R;
import com.ctom314.openbook.Utilities;
import com.google.android.material.navigation.NavigationView;

public class MakePostPage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{
    // Vars
    Spinner sp_j_mp_bookList;

    TextView tv_j_mp_addBookMsg;
    TextView tv_j_mp_error;

    EditText et_j_mp_post;

    Button btn_j_mp_post;
    Button btn_j_mp_home;

    // Toolbar vars
    Toolbar tb_j_mp_toolbar;
    DrawerLayout dl_j_mp_drawer;
    ActionBarDrawerToggle mp_drawerToggle;
    NavigationView nv_j_mp_navMenu;
    TextView tv_v_mp_curLoggedIn;

    Intent intent_j_addBook;
    Intent intent_j_homepage;
    Intent intent_j_viewPost;

    BookTitleAdapter adapter;

    DBUtils dbUtils;

    // Default book id to -1
    int bookId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_post_page);

        dbUtils = new DBUtils(this);

        // Retrieve bookID if one was passed
        Intent cameFrom = getIntent();
        if (cameFrom.getIntExtra("bookId", -1) != -1)
        {
            bookId = cameFrom.getIntExtra("bookId", -1);
        }

        // Connect vars
        sp_j_mp_bookList = findViewById(R.id.sp_v_mp_bookList);
        tv_j_mp_addBookMsg = findViewById(R.id.tv_v_mp_addBookMsg);
        tv_j_mp_error = findViewById(R.id.tv_v_mp_error);
        et_j_mp_post = findViewById(R.id.et_v_mp_post);
        btn_j_mp_post = findViewById(R.id.btn_v_mp_post);
        btn_j_mp_home = findViewById(R.id.btn_v_mp_home);

        // Setup toolbar
        tb_j_mp_toolbar = findViewById(R.id.tb_v_mp_toolbar);
        setSupportActionBar(tb_j_mp_toolbar);

        // Setup Drawer
        dl_j_mp_drawer = findViewById(R.id.dl_v_mp_main);
        mp_drawerToggle = new ActionBarDrawerToggle(this, dl_j_mp_drawer, tb_j_mp_toolbar, R.string.nav_open, R.string.nav_close);
        dl_j_mp_drawer.addDrawerListener(mp_drawerToggle);
        mp_drawerToggle.syncState();

        // Setup Navigation View
        nv_j_mp_navMenu = findViewById(R.id.nv_v_mp_nav);
        nv_j_mp_navMenu.setItemIconTintList(null);
        nv_j_mp_navMenu.setNavigationItemSelectedListener(this);

        // Set logged in text
        tv_v_mp_curLoggedIn = findViewById(R.id.tv_v_mp_curLoggedIn);
        tv_v_mp_curLoggedIn.setText("Logged in as: " + Utilities.getLoggedInUser(this));

        // Setup Nav View Selected Item (Changes item color for current page)
        Utilities.updateNavMenu(nv_j_mp_navMenu.getMenu().findItem(R.id.nav_make_post), this);

        // Setup intents
        intent_j_addBook = new Intent(this, AddBookPage.class);
        intent_j_homepage = new Intent(MakePostPage.this, Homepage.class);
        intent_j_viewPost = new Intent(MakePostPage.this, ViewPostPage.class);

        // Setup spinner
        setupBookDisplay();

        // Button Listeners
        postButtonHandler();
        addBookButtonHandler();
        homeButtonHandler();

    }

    private void makePost()
    {
        String post = et_j_mp_post.getText().toString();
        String book = sp_j_mp_bookList.getSelectedItem().toString();

        // Trim whitespace
        post = Utilities.trimWhitespace(post);

        // Checks
        if (post.isEmpty())
        {
            // Post is empty
            Utilities.showError(tv_j_mp_error, "Post cannot be empty.");
        }
        else
        {
            Utilities.hideError(tv_j_mp_error);

            // Get book id
            int bookId = dbUtils.getBookId(book);

            // Make post
            Post p = new Post(bookId, Utilities.getLoggedInUser(this), Utilities.generateTimestamp(), post);

            // Add post to database
            dbUtils.addPost(p);

            Toast.makeText(this, "Created post successfully", Toast.LENGTH_SHORT).show();

            // Pass post id to view post page
            intent_j_viewPost.putExtra("postId", dbUtils.getPostId(p.getUsername(), p.getTimestamp()));

            // Go back the post's page
            startActivity(intent_j_viewPost);
            finish();
        }
    }

    // Button: Post
    private void postButtonHandler()
    {
        btn_j_mp_post.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                makePost();
            }
        });
    }

    // Button: Home
    private void homeButtonHandler()
    {
        btn_j_mp_home.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                // Go back to homepage
                startActivity(intent_j_homepage);
                finish();
            }
        });
    }

    // Button: Add Book
    private void addBookButtonHandler()
    {
        tv_j_mp_addBookMsg.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                // Go to add book page
                startActivity(intent_j_addBook);
                finish();
            }
        });
    }

    // Book display setup based on if book id was passed
    private void setupBookDisplay()
    {
        adapter = new BookTitleAdapter
                (this,
                        R.layout.spinner_layout, dbUtils.getBookList(), 700);
        sp_j_mp_bookList.setAdapter(adapter);

        // If book id was passed, set spinner to that book and lock it
        if (bookId != -1)
        {
            sp_j_mp_bookList.setSelection(adapter.getPosition(dbUtils.getBook(bookId).getTitle()));
            sp_j_mp_bookList.setEnabled(false);

            // Hide add book message
            tv_j_mp_addBookMsg.setVisibility(View.INVISIBLE);
        }
    }

    // Needed for navigation view to work properly
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item)
    {
        dl_j_mp_drawer.closeDrawer(GravityCompat.START);
        return Utilities.onNavigationItemSelected(item, this, dl_j_mp_drawer);
    }
}