package com.ctom314.openbook.pages;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.ctom314.openbook.DBUtils;
import com.ctom314.openbook.Post;
import com.ctom314.openbook.Comment;
import com.ctom314.openbook.R;
import com.ctom314.openbook.RepliesListAdapter;
import com.ctom314.openbook.Utilities;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class ViewPostPage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{
    // Vars
    TextView tv_j_vp_username;
    TextView tv_j_vp_timestamp;
    TextView tv_j_vp_post;

    // Buttons Users other than OP will see
    Button btn_j_vp_replyNonOP;

    // Buttons OP will see
    Button btn_j_vp_replyOP;
    Button btn_j_vp_editPost;

    ListView lv_j_vp_replies;

    // Toolbar vars
    Toolbar tb_j_vp_toolbar;
    DrawerLayout dl_j_vp_drawer;
    ActionBarDrawerToggle vp_drawerToggle;
    NavigationView nv_j_vp_navMenu;
    TextView tv_v_vp_curLoggedIn;

    Intent intent_j_editPost;
    Intent intent_j_reply;
    Intent intent_j_homepage;

    RepliesListAdapter adapter;
    ArrayList<Comment> comments;

    DBUtils dbUtils;

    // Default post to null
    private Post post = null;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_post_page);

        dbUtils = new DBUtils(this);

        // Retrieve post
        Intent cameFrom = getIntent();
        if (cameFrom.getIntExtra("postId", -1) != -1)
        {
            int postId = cameFrom.getIntExtra("postId", -1);
            post = dbUtils.getPost(postId);
        }

        // Connect vars
        tv_j_vp_username = findViewById(R.id.tv_v_vp_username);
        tv_j_vp_timestamp = findViewById(R.id.tv_v_vp_timestamp);
        tv_j_vp_post = findViewById(R.id.tv_v_vp_content);
        btn_j_vp_replyNonOP = findViewById(R.id.btn_v_vp_replyNonOP);
        btn_j_vp_replyOP = findViewById(R.id.btn_v_vp_replyOP);
        btn_j_vp_editPost = findViewById(R.id.btn_v_vp_editPost);
        lv_j_vp_replies = findViewById(R.id.lv_v_vp_replies);

        // Setup toolbar
        tb_j_vp_toolbar = findViewById(R.id.tb_v_vp_toolbar);
        setSupportActionBar(tb_j_vp_toolbar);

        // Setup Drawer
        dl_j_vp_drawer = findViewById(R.id.dl_v_vp_main);
        vp_drawerToggle = new ActionBarDrawerToggle(this, dl_j_vp_drawer, tb_j_vp_toolbar, R.string.nav_open, R.string.nav_close);
        dl_j_vp_drawer.addDrawerListener(vp_drawerToggle);
        vp_drawerToggle.syncState();

        // Setup Navigation View
        nv_j_vp_navMenu = findViewById(R.id.nv_v_vp_nav);
        nv_j_vp_navMenu.setItemIconTintList(null);
        nv_j_vp_navMenu.setNavigationItemSelectedListener(this);

        // Set logged in text
        tv_v_vp_curLoggedIn = findViewById(R.id.tv_v_vp_curLoggedIn);
        tv_v_vp_curLoggedIn.setText("Logged in as: " + Utilities.getLoggedInUser(this));

        // Setup intents
        intent_j_editPost = new Intent(ViewPostPage.this, EditPostPage.class);
        intent_j_reply = new Intent(ViewPostPage.this, ReplyPage.class);
        intent_j_homepage = new Intent(ViewPostPage.this, Homepage.class);

        // Setup post display
        displayPost();

        // Setup buttons
        setupButtons();

        // Setup edit button
        editButtonState();

        // Button Listeners
        toolbarEventListener();
        timestampEventListener();
        editButtonHandler();
        replyButtonHandler();

        // Setup replies list
        comments = dbUtils.getPostReplies(dbUtils.getPostId(post.getUsername(), post.getTimestamp()));

        Log.d("ViewPostPage", "# Comments: " + comments.size());
        fillListView();
    }

    // Post display
    private void displayPost()
    {
        if (post != null)
        {
            // Get relative timestamp
            String timestamp = Utilities.getTimeSincePost(post.getTimestamp());

            tv_j_vp_username.setText(post.getUsername());
            tv_j_vp_timestamp.setText(timestamp);
            tv_j_vp_post.setText(post.getContent());

            // Set toolbar title to book title. Shorten if needed
            String bookTitle = Utilities.shortenString(tb_j_vp_toolbar, dbUtils.getBook(post.getBookId()).getTitle());
            tb_j_vp_toolbar.setTitle(bookTitle);
        }
    }

    // Set edit button state based on whose is logged in and what post is being viewed
    private void editButtonState()
    {
        String loggedInUser = Utilities.getLoggedInUser(this);
        String postUser = post.getUsername();

        if (loggedInUser.equals(postUser))
        {
            // User's own post. allow editing.
            btn_j_vp_editPost.setVisibility(Button.VISIBLE);
        }
        else
        {
            // Not user's post. don't allow editing.
            btn_j_vp_editPost.setVisibility(Button.GONE);
        }
    }

    // Set which buttons are visible based on if the logged in user is OP (Original Poster)
    private void setupButtons()
    {
        String loggedInUser = Utilities.getLoggedInUser(this);
        String postUser = post.getUsername();

        if (loggedInUser.equals(postUser))
        {
            // User's own post. Show both buttons
            btn_j_vp_replyNonOP.setVisibility(Button.INVISIBLE);
            btn_j_vp_replyOP.setVisibility(Button.VISIBLE);
            btn_j_vp_editPost.setVisibility(Button.VISIBLE);
        }
        else
        {
            // Not user's post. Show only reply button
            btn_j_vp_replyNonOP.setVisibility(Button.VISIBLE);
            btn_j_vp_replyOP.setVisibility(Button.INVISIBLE);
            btn_j_vp_editPost.setVisibility(Button.INVISIBLE);
        }
    }

    // Tap on timestamp to get full date
    private void timestampEventListener()
    {
        tv_j_vp_timestamp.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                // Get full date
                String date = Utilities.getFullDate(post.getTimestamp());

                Toast.makeText(ViewPostPage.this, date, Toast.LENGTH_LONG).show();
            }
        });
    }

    // Press on toolbar to see full book title
    private void toolbarEventListener()
    {
        tb_j_vp_toolbar.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                // Get full title
                String title = dbUtils.getBook(post.getBookId()).getTitle();

                Toast.makeText(ViewPostPage.this, title, Toast.LENGTH_LONG).show();
            }
        });
    }

    // Button: Edit
    private void editButtonHandler()
    {
        btn_j_vp_editPost.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                // Pass post id to edit post page
                intent_j_editPost.putExtra("postId", dbUtils.getPostId(post.getUsername(), post.getTimestamp()));

                // Go to edit post page
                startActivity(intent_j_editPost);
                finish();
            }
        });
    }

    // Button: Reply (Bot OP and non-OP version)
    public void replyButtonHandler()
    {
        btn_j_vp_replyOP.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                // Pass post id to reply page
                intent_j_reply.putExtra("postId", dbUtils.getPostId(post.getUsername(), post.getTimestamp()));

                // Go to reply page
                startActivity(intent_j_reply);
                finish();
            }
        });

        btn_j_vp_replyNonOP.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                // Pass post id to reply page
                intent_j_reply.putExtra("postId", dbUtils.getPostId(post.getUsername(), post.getTimestamp()));

                // Go to reply page
                startActivity(intent_j_reply);
                finish();
            }
        });
    }

    // Needed for navigation view to work properly
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item)
    {
        dl_j_vp_drawer.closeDrawer(GravityCompat.START);
        return Utilities.onNavigationItemSelected(item, this, dl_j_vp_drawer);
    }

    private void fillListView()
    {
        // Create new adapter
        adapter = new RepliesListAdapter(this, comments, dbUtils);

        // Set adapter
        lv_j_vp_replies.setAdapter(adapter);
    }
}