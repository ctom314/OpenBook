package com.ctom314.openbook.pages;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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
    Button btn_j_mp_back;

    // Toolbar vars
    Toolbar tb_j_mp_toolbar;
    DrawerLayout dl_j_mp_drawer;
    ActionBarDrawerToggle hp_drawerToggle;
    NavigationView nv_j_mp_navMenu;
    TextView tv_v_mp_curLoggedIn;

    Intent intent_j_addBook;
    Intent intent_j_homepage;

    ArrayAdapter<String> adapter;

    DBUtils dbUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_post_page);

        dbUtils = new DBUtils(this);

        // Connect vars
        sp_j_mp_bookList = findViewById(R.id.sp_v_mp_bookList);
        tv_j_mp_addBookMsg = findViewById(R.id.tv_v_mp_addBookMsg);
        tv_j_mp_error = findViewById(R.id.tv_v_mp_error);
        et_j_mp_post = findViewById(R.id.et_v_mp_post);
        btn_j_mp_post = findViewById(R.id.btn_v_mp_post);
        btn_j_mp_back = findViewById(R.id.btn_v_mp_back);

        // Setup toolbar
        tb_j_mp_toolbar = findViewById(R.id.tb_v_mp_toolbar);
        setSupportActionBar(tb_j_mp_toolbar);

        // Setup Drawer
        dl_j_mp_drawer = findViewById(R.id.dl_v_mp_main);
        hp_drawerToggle = new ActionBarDrawerToggle(this, dl_j_mp_drawer, tb_j_mp_toolbar, R.string.nav_open, R.string.nav_close);
        dl_j_mp_drawer.addDrawerListener(hp_drawerToggle);
        hp_drawerToggle.syncState();

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

        // Setup spinner
        adapter = new ArrayAdapter<>
                (this,
                        R.layout.spinner_layout, dbUtils.getBookList());
        sp_j_mp_bookList.setAdapter(adapter);

        // Button Listeners
        //postButtonHandler();
        addBookButtonHandler();
        backButtonHandler();

    }

    // Button: Post
    private void postButtonHandler()
    {
        btn_j_mp_post.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {

            }
        });
    }

    // Button: Back
    private void backButtonHandler()
    {
        btn_j_mp_back.setOnClickListener(new View.OnClickListener()
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
            }
        });
    }
    
    // Needed for navigation view to work properly
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item)
    {
        dl_j_mp_drawer.closeDrawer(GravityCompat.START);
        return Utilities.onNavigationItemSelected(item, this, dl_j_mp_drawer);
    }
}