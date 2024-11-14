package com.ctom314.openbook.pages;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.ctom314.openbook.Book;
import com.ctom314.openbook.DBUtils;
import com.ctom314.openbook.R;
import com.ctom314.openbook.Utilities;
import com.google.android.material.navigation.NavigationView;

public class AddBookPage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{
    // Vars
    EditText et_j_ab_title;
    EditText et_j_ab_author;
    EditText et_j_ab_year;

    Button btn_j_ab_add;
    Button btn_j_ab_back;

    TextView tv_j_ab_error;

    // Toolbar vars
    Toolbar tb_j_ab_toolbar;
    DrawerLayout dl_j_ab_drawer;
    ActionBarDrawerToggle ab_drawerToggle;
    NavigationView nv_j_ab_navMenu;
    TextView tv_v_ab_curLoggedIn;

    Intent intent_j_makePost;

    DBUtils dbUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book_page);

        dbUtils = new DBUtils(this);

        // Connect vars
        et_j_ab_title = findViewById(R.id.et_v_ab_title);
        et_j_ab_author = findViewById(R.id.et_v_ab_author);
        et_j_ab_year = findViewById(R.id.et_v_ab_year);
        btn_j_ab_add = findViewById(R.id.btn_v_ab_add);
        btn_j_ab_back = findViewById(R.id.btn_v_ab_back);
        tv_j_ab_error = findViewById(R.id.tv_v_ab_error);

        // Setup toolbar
        tb_j_ab_toolbar = findViewById(R.id.tb_v_ab_toolbar);
        setSupportActionBar(tb_j_ab_toolbar);

        // Setup Drawer
        dl_j_ab_drawer = findViewById(R.id.dl_v_ab_main);
        ab_drawerToggle = new ActionBarDrawerToggle(this, dl_j_ab_drawer, tb_j_ab_toolbar, R.string.nav_open, R.string.nav_close);
        dl_j_ab_drawer.addDrawerListener(ab_drawerToggle);
        ab_drawerToggle.syncState();

        // Setup Navigation View
        nv_j_ab_navMenu = findViewById(R.id.nv_v_ab_nav);
        nv_j_ab_navMenu.setItemIconTintList(null);
        nv_j_ab_navMenu.setNavigationItemSelectedListener(this);

        // Set logged in text
        tv_v_ab_curLoggedIn = findViewById(R.id.tv_v_ab_curLoggedIn);
        tv_v_ab_curLoggedIn.setText("Logged in as: " + Utilities.getLoggedInUser(this));

        // Setup Nav View Selected Item (Changes item color for current page)
        Utilities.updateNavMenu(nv_j_ab_navMenu.getMenu().findItem(R.id.nav_add_book), this);

        // Setup intents
        intent_j_makePost = new Intent(AddBookPage.this, MakePostPage.class);


        // Button Handlers
        addBookButtonHandler();
        backButtonHandler();

    }

    private void addBook()
    {
        String title = et_j_ab_title.getText().toString();
        String author = et_j_ab_author.getText().toString();
        String yearStr = et_j_ab_year.getText().toString();

        // Trim whitespace
        title = Utilities.trimWhitespace(title);
        author = Utilities.trimWhitespace(author);
        yearStr = Utilities.trimWhitespace(yearStr);

        // Checks
        if (title.isEmpty() || author.isEmpty() || yearStr.isEmpty())
        {
            // One or more fields are empty
            Utilities.showError(tv_j_ab_error, "Please fill out all fields.");
        }
        else if (!Utilities.isValidYear(yearStr))
        {
            // Year is invalid
            Utilities.showError(tv_j_ab_error, "Please enter a valid year.");
        }
        else if (dbUtils.bookAlreadyAdded(title, author))
        {
            // Book already exists
            Utilities.showError(tv_j_ab_error, "Book has already been added.");
        }
        else
        {
            // No errors
            Utilities.hideError(tv_j_ab_error);
            int year = Integer.parseInt(yearStr);

            // Make book
            Book b = new Book(title, author, year);

            // Add book to database
            dbUtils.addBook(b);

            Toast.makeText(this, "Book added successfully", Toast.LENGTH_SHORT).show();

            // Go back to make post page
            startActivity(intent_j_makePost);
            finish();
        }
    }

    // Button: Back
    private void backButtonHandler()
    {
        btn_j_ab_back.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                // Go back to make post page
                startActivity(intent_j_makePost);
                finish();
            }
        });
    }

    // Button: Add
    private void addBookButtonHandler()
    {
        btn_j_ab_add.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                addBook();
            }
        });
    }


    // Needed for navigation view to work properly
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item)
    {
        dl_j_ab_drawer.closeDrawer(GravityCompat.START);
        return Utilities.onNavigationItemSelected(item, this, dl_j_ab_drawer);
    }
}