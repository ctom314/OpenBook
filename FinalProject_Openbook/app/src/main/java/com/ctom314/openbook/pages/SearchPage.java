package com.ctom314.openbook.pages;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
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
import com.ctom314.openbook.Query;
import com.ctom314.openbook.R;
import com.ctom314.openbook.Utilities;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class SearchPage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{
    // Vars
    EditText et_j_sp_title;
    EditText et_j_sp_author;

    Button btn_j_sp_search;
    Button btn_j_sp_home;

    TextView tv_j_sp_msg;

    ListView lv_j_sp_results;

    // Toolbar vars
    Toolbar tb_j_sp_toolbar;
    DrawerLayout dl_j_sp_drawer;
    ActionBarDrawerToggle sp_drawerToggle;
    NavigationView nv_j_sp_navMenu;
    TextView tv_v_sp_curLoggedIn;

    Intent intent_j_homepage;
    Intent intent_j_bookThread;

    BookListAdapter adapter;
    ArrayList<Book> bookResults;

    DBUtils dbUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_page);
        
        dbUtils = new DBUtils(this);
        
        // Connect vars
        et_j_sp_title = findViewById(R.id.et_v_sp_title);
        et_j_sp_author = findViewById(R.id.et_v_sp_author);
        btn_j_sp_search = findViewById(R.id.btn_v_sp_search);
        btn_j_sp_home = findViewById(R.id.btn_v_sp_home);
        tv_j_sp_msg = findViewById(R.id.tv_v_sp_msg);
        lv_j_sp_results = findViewById(R.id.lv_v_sp_results);

        // Setup toolbar
        tb_j_sp_toolbar = findViewById(R.id.tb_v_sp_toolbar);
        setSupportActionBar(tb_j_sp_toolbar);

        // Setup Drawer
        dl_j_sp_drawer = findViewById(R.id.dl_v_sp_main);
        sp_drawerToggle = new ActionBarDrawerToggle(this, dl_j_sp_drawer, tb_j_sp_toolbar, R.string.nav_open, R.string.nav_close);
        dl_j_sp_drawer.addDrawerListener(sp_drawerToggle);
        sp_drawerToggle.syncState();

        // Setup Navigation View
        nv_j_sp_navMenu = findViewById(R.id.nv_v_sp_nav);
        nv_j_sp_navMenu.setItemIconTintList(null);
        nv_j_sp_navMenu.setNavigationItemSelectedListener(this);

        // Setup Nav View Selected Item (Changes item color for current page)
        Utilities.updateNavMenu(nv_j_sp_navMenu.getMenu().findItem(R.id.nav_search), this);

        // Set logged in text
        tv_v_sp_curLoggedIn = findViewById(R.id.tv_v_sp_curLoggedIn);
        tv_v_sp_curLoggedIn.setText("Logged in as: " + Utilities.getLoggedInUser(this));

        // Setup intents
        intent_j_homepage = new Intent(SearchPage.this, Homepage.class);
        intent_j_bookThread = new Intent(SearchPage.this, BookThreadPage.class);

        // Button handlers
        searchButtonHandler();
        homeButtonHandler();
        resultClickListener();
    }

    private void search()
    {
        String title = et_j_sp_title.getText().toString();
        String author = et_j_sp_author.getText().toString();

        // Check if fields are filled in
        if (title.isEmpty() && author.isEmpty())
        {
            tv_j_sp_msg.setTextAppearance(R.style.searchError);
            Utilities.showError(tv_j_sp_msg, "One or more fields must be filled in.");
        }
        else
        {
            // Make search query
            Query query = new Query(title, author);

            // Get books from database
            bookResults = dbUtils.searchBooks(query);

            // Check if any books were found
            if (bookResults.isEmpty())
            {
                tv_j_sp_msg.setTextAppearance(R.style.searchError);
                Utilities.showError(tv_j_sp_msg, "0 Results found.");
            }
            else
            {
                tv_j_sp_msg.setTextAppearance(R.style.searchSuccess);
                Utilities.showError(tv_j_sp_msg, bookResults.size() + " Results found.");

                fillListView();
            }
        }
    }
    
    
    // BTN: Home
    private void homeButtonHandler()
    {
        btn_j_sp_home.setOnClickListener(new View.OnClickListener()
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

    // BTN: Search
    private void searchButtonHandler()
    {
        btn_j_sp_search.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                search();
            }
        });
    }

    // LV: Tap on result to go to book thread
    private void resultClickListener()
    {
        lv_j_sp_results.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
            {
                // Get book
                Book book = bookResults.get(i);

                // Pass book id to Book thread
                intent_j_bookThread.putExtra("bookId", dbUtils.getBookId(book.getTitle()));

                // Go to book thread
                startActivity(intent_j_bookThread);
                finish();
            }
        });
    }

    // Needed for navigation view to work properly
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item)
    {
        dl_j_sp_drawer.closeDrawer(GravityCompat.START);
        return Utilities.onNavigationItemSelected(item, this, dl_j_sp_drawer);
    }

    private void fillListView()
    {
        // Create new adapter
        adapter = new BookListAdapter(this, bookResults, dbUtils);

        // Set adapter
        lv_j_sp_results.setAdapter(adapter);
    }
    
}