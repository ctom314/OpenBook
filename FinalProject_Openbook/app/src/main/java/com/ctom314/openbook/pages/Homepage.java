package com.ctom314.openbook.pages;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.ctom314.openbook.DBUtils;
import com.ctom314.openbook.Post;
import com.ctom314.openbook.R;
import com.ctom314.openbook.RecentPostsAdapter;
import com.ctom314.openbook.Utilities;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class Homepage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{
    // Vars
    Button btn_j_hp_search;
    Button btn_j_hp_makePost;

    ListView lv_j_hp_recentPosts;

    // Toolbar vars
    Toolbar tb_j_hp_toolbar;
    DrawerLayout dl_j_hp_drawer;
    ActionBarDrawerToggle hp_drawerToggle;
    NavigationView nv_j_hp_navMenu;
    TextView tv_v_hp_curLoggedIn;

    ArrayList<Post> recentPosts;
    RecentPostsAdapter adapter;

    Intent intent_j_makePost;
    Intent intent_j_search;
    Intent intent_j_viewPost;

    DBUtils dbUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        dbUtils = new DBUtils(this);

        // Connect vars
        btn_j_hp_search = findViewById(R.id.btn_v_hp_search);
        btn_j_hp_makePost = findViewById(R.id.btn_v_hp_makePost);
        lv_j_hp_recentPosts = findViewById(R.id.lv_v_hp_recentPosts);

        // Setup toolbar
        tb_j_hp_toolbar = findViewById(R.id.tb_v_hp_toolbar);
        setSupportActionBar(tb_j_hp_toolbar);

        // Set title to First part of name
        String name = dbUtils.getAccount(Utilities.getLoggedInUser(this)).getName();
        String fName = name.split(" ")[0];
        tb_j_hp_toolbar.setTitle("Welcome, " + fName);

        // Setup Drawer
        dl_j_hp_drawer = findViewById(R.id.dl_v_hp_main);
        hp_drawerToggle = new ActionBarDrawerToggle(this, dl_j_hp_drawer, tb_j_hp_toolbar, R.string.nav_open, R.string.nav_close);
        dl_j_hp_drawer.addDrawerListener(hp_drawerToggle);
        hp_drawerToggle.syncState();

        // Setup Navigation View
        nv_j_hp_navMenu = findViewById(R.id.nv_v_hp_nav);
        nv_j_hp_navMenu.setItemIconTintList(null);
        nv_j_hp_navMenu.setNavigationItemSelectedListener(this);

        // Set logged in text
        tv_v_hp_curLoggedIn = findViewById(R.id.tv_v_hp_curLoggedIn);
        tv_v_hp_curLoggedIn.setText("Logged in as: " + Utilities.getLoggedInUser(this));

        // Setup Nav View Selected Item (Changes item color for current page)
        Utilities.updateNavMenu(nv_j_hp_navMenu.getMenu().findItem(R.id.nav_home), this);

        // Setup intents
        intent_j_search = new Intent(Homepage.this, SearchPage.class);
        intent_j_makePost = new Intent(Homepage.this, MakePostPage.class);
        intent_j_viewPost = new Intent(Homepage.this, ViewPostPage.class);

        // Button handlers
        makePostButton();
        searchButton();
        recentPostClickEvent();

        // Setup recent posts list
        recentPosts = dbUtils.getRecentPosts(3);
        fillListView();
    }

    // Button: Make Post
    private void makePostButton()
    {
        btn_j_hp_makePost.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                // Go to make post page
                startActivity(intent_j_makePost);
                finish();
            }
        });
    }

    // BTN: Search
    private void searchButton()
    {
        btn_j_hp_search.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                // Go to search page
                startActivity(intent_j_search);
                finish();
            }
        });
    }

    // Click event for recent posts
    private void recentPostClickEvent()
    {
        lv_j_hp_recentPosts.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                // Get post id and pass it to view post page
                Post p = recentPosts.get(i);
                intent_j_viewPost.putExtra("postId", dbUtils.getPostId(p.getUsername(), p.getTimestamp()));

                // Go to view post page
                startActivity(intent_j_viewPost);
                finish();
            }
        });
    }


    // Needed for navigation view to work properly
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item)
    {
        dl_j_hp_drawer.closeDrawer(GravityCompat.START);
        return Utilities.onNavigationItemSelected(item, this, dl_j_hp_drawer);
    }

    private void fillListView()
    {
        // Create new adapter
        adapter = new RecentPostsAdapter(this, recentPosts, dbUtils);

        // Set adapter
        lv_j_hp_recentPosts.setAdapter(adapter);
    }
}