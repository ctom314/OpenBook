package com.ctom314.openbook.pages;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.ctom314.openbook.Book;
import com.ctom314.openbook.BookListAdapter;
import com.ctom314.openbook.DBUtils;
import com.ctom314.openbook.R;
import com.ctom314.openbook.Utilities;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class CatalogPage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{
    // Vars
    Button btn_j_bc_home;

    ListView lv_j_bc_bookList;

    // Toolbar vars
    Toolbar tb_j_bc_toolbar;
    DrawerLayout dl_j_bc_drawer;
    ActionBarDrawerToggle bc_drawerToggle;
    NavigationView nv_j_bc_navMenu;
    TextView tv_v_bc_curLoggedIn;

    Intent intent_j_homepage;
    Intent intent_j_bookThread;

    BookListAdapter adapter;
    ArrayList<Book> books;

    DBUtils dbUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog_page);
        
        dbUtils = new DBUtils(this);

        // Get books from database
        books = dbUtils.getAllBooks();

        // Connect vars
        btn_j_bc_home = findViewById(R.id.btn_v_bc_home);
        lv_j_bc_bookList = findViewById(R.id.lv_v_bc_bookList);

        // Setup toolbar
        tb_j_bc_toolbar = findViewById(R.id.tb_v_bc_toolbar);
        setSupportActionBar(tb_j_bc_toolbar);

        // Setup Drawer
        dl_j_bc_drawer = findViewById(R.id.dl_v_bc_main);
        bc_drawerToggle = new ActionBarDrawerToggle(this, dl_j_bc_drawer, tb_j_bc_toolbar, R.string.nav_open, R.string.nav_close);
        dl_j_bc_drawer.addDrawerListener(bc_drawerToggle);
        bc_drawerToggle.syncState();

        // Setup Navigation View
        nv_j_bc_navMenu = findViewById(R.id.nv_v_bc_nav);
        nv_j_bc_navMenu.setItemIconTintList(null);
        nv_j_bc_navMenu.setNavigationItemSelectedListener(this);

        // Setup Nav View Selected Item (Changes item color for current page)
        Utilities.updateNavMenu(nv_j_bc_navMenu.getMenu().findItem(R.id.nav_catalog), this);
        
        // Set logged in text
        tv_v_bc_curLoggedIn = findViewById(R.id.tv_v_bc_curLoggedIn);
        tv_v_bc_curLoggedIn.setText("Logged in as: " + Utilities.getLoggedInUser(this));

        // Setup intents
        intent_j_homepage = new Intent(CatalogPage.this, Homepage.class);
        intent_j_bookThread = new Intent(CatalogPage.this, BookThreadPage.class);

        // Button handlers
        homeButtonHandler();
        bookListHandler();

        fillListView();
    }

    // BTN: Home
    private void homeButtonHandler()
    {
        btn_j_bc_home.setOnClickListener(new View.OnClickListener()
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

    // LV: Tap on book to view more details about
    private void bookListHandler()
    {
        lv_j_bc_bookList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                // Get book from list
                Book book = books.get(i);

                // Get book id and pass to book thread.
                int bookId = dbUtils.getBookId(book.getTitle());
                intent_j_bookThread.putExtra("bookId", bookId);

                // Go to the book's thread.
                startActivity(intent_j_bookThread);
                finish();
            }
        });
    }

    // Needed for navigation view to work properly
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item)
    {
        dl_j_bc_drawer.closeDrawer(GravityCompat.START);
        return Utilities.onNavigationItemSelected(item, this, dl_j_bc_drawer);
    }

    private void fillListView()
    {
        // Create new adapter
        adapter = new BookListAdapter(this, books, dbUtils);

        // Set adapter
        lv_j_bc_bookList.setAdapter(adapter);
    }
    
}