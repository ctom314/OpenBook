package com.ctom314.openbook.pages;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.ctom314.openbook.Book;
import com.ctom314.openbook.BookListAdapter;
import com.ctom314.openbook.DBUtils;
import com.ctom314.openbook.Post;
import com.ctom314.openbook.PostListAdapter;
import com.ctom314.openbook.R;
import com.ctom314.openbook.Utilities;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class BookThreadPage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{
    // Vars
    Button btn_j_btp_post;
    Button btn_j_btp_back;

    ListView lv_j_btp_postList;

    // No Posts display
    ImageView iv_j_btp_noPosts;
    TextView tv_j_btp_noPosts;

    // Toolbar vars
    Toolbar tb_j_btp_toolbar;
    DrawerLayout dl_j_btp_drawer;
    ActionBarDrawerToggle btp_drawerToggle;
    NavigationView nv_j_btp_navMenu;
    TextView tv_v_btp_curLoggedIn;

    Intent intent_j_catalog;
    Intent intent_j_viewPost;
    Intent intent_j_makePost;

    PostListAdapter adapter;
    ArrayList<Post> posts;

    DBUtils dbUtils;

    // Default book id to -1
    int bookId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_thread_page);
        
        dbUtils = new DBUtils(this);

        // Retrieve book id
        Intent cameFrom = getIntent();
        if (cameFrom.getIntExtra("bookId", -1) != -1)
        {
            bookId = cameFrom.getIntExtra("bookId", -1);
        }

        // Get all posts for book
        posts = dbUtils.getBookPosts(bookId);
        
        // Connect vars
        btn_j_btp_post = findViewById(R.id.btn_v_btp_makePost);
        btn_j_btp_back = findViewById(R.id.btn_v_btp_back);
        lv_j_btp_postList = findViewById(R.id.lv_v_btp_postList);
        iv_j_btp_noPosts = findViewById(R.id.iv_v_btp_noPosts);
        tv_j_btp_noPosts = findViewById(R.id.tv_v_btp_noPosts);

        // Setup toolbar
        tb_j_btp_toolbar = findViewById(R.id.tb_v_btp_toolbar);
        setSupportActionBar(tb_j_btp_toolbar);

        // Set toolbar title to book title. Shorten if needed
        String bookTitle = Utilities.shortenString(tb_j_btp_toolbar, dbUtils.getBook(bookId).getTitle());
        tb_j_btp_toolbar.setTitle(bookTitle);
        
        // Setup Drawer
        dl_j_btp_drawer = findViewById(R.id.dl_v_btp_main);
        btp_drawerToggle = new ActionBarDrawerToggle(this, dl_j_btp_drawer, tb_j_btp_toolbar, R.string.nav_open, R.string.nav_close);
        dl_j_btp_drawer.addDrawerListener(btp_drawerToggle);
        btp_drawerToggle.syncState();

        // Setup Navigation View
        nv_j_btp_navMenu = findViewById(R.id.nv_v_btp_nav);
        nv_j_btp_navMenu.setItemIconTintList(null);
        nv_j_btp_navMenu.setNavigationItemSelectedListener(this);

        // Set logged in text
        tv_v_btp_curLoggedIn = findViewById(R.id.tv_v_btp_curLoggedIn);
        tv_v_btp_curLoggedIn.setText("Logged in as: " + Utilities.getLoggedInUser(this));

        // Setup intents
        intent_j_catalog = new Intent(BookThreadPage.this, CatalogPage.class);
        intent_j_viewPost = new Intent(BookThreadPage.this, ViewPostPage.class);
        intent_j_makePost = new Intent(BookThreadPage.this, MakePostPage.class);

        // Setup no posts display
        noPostsDisplay();

        // Button handlers
        postButtonHandler();
        backButtonHandler();
        postListHandler();
        toolbarEventListener();

        fillListView();
    }

    // Show No Posts Display when there are no posts
    private void noPostsDisplay()
    {
        if (posts.isEmpty())
        {
            // No posts
            lv_j_btp_postList.setVisibility(View.INVISIBLE);
            iv_j_btp_noPosts.setVisibility(View.VISIBLE);
            tv_j_btp_noPosts.setVisibility(View.VISIBLE);
        }
        else
        {
            // Posts
            lv_j_btp_postList.setVisibility(View.VISIBLE);
            iv_j_btp_noPosts.setVisibility(View.INVISIBLE);
            tv_j_btp_noPosts.setVisibility(View.INVISIBLE);
        }
    }

    // BTN: Home
    private void backButtonHandler()
    {
        btn_j_btp_back.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                // Go back to book catalog
                startActivity(intent_j_catalog);
                finish();
            }
        });
    }

    // BTN: Post
    private void postButtonHandler()
    {
        btn_j_btp_post.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                // Pass book id to make post page
                intent_j_makePost.putExtra("bookId", bookId);

                // Go to make post page
                startActivity(intent_j_makePost);
                finish();
            }
        });
    }

    // LV: Tap on post to view it
    private void postListHandler()
    {
        lv_j_btp_postList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                // Get post from list
                Post post = posts.get(i);

                // Get post id and pass it to view post page
                int postId = dbUtils.getPostId(post.getUsername(), post.getTimestamp());
                intent_j_viewPost.putExtra("postId", postId);

                // Go to the book's thread.
                startActivity(intent_j_viewPost);
                finish();
            }
        });
    }

    // TB: Press to see full book title
    private void toolbarEventListener()
    {
        tb_j_btp_toolbar.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                // Get full title
                String title = dbUtils.getBook(bookId).getTitle();

                Toast.makeText(BookThreadPage.this, title, Toast.LENGTH_LONG).show();
            }
        });
    }

    // Needed for navigation view to work properly
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item)
    {
        dl_j_btp_drawer.closeDrawer(GravityCompat.START);
        return Utilities.onNavigationItemSelected(item, this, dl_j_btp_drawer);
    }

    private void fillListView()
    {
        // Create new adapter
        adapter = new PostListAdapter(this, posts, dbUtils);

        // Set adapter
        lv_j_btp_postList.setAdapter(adapter);
    }
    
}