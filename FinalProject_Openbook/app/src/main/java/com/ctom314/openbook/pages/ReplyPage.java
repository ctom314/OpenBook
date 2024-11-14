package com.ctom314.openbook.pages;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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

import com.ctom314.openbook.Comment;
import com.ctom314.openbook.DBUtils;
import com.ctom314.openbook.R;
import com.ctom314.openbook.Utilities;
import com.google.android.material.navigation.NavigationView;

public class ReplyPage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{
    // Vars
    Button btn_j_cp_reply;
    Button btn_j_cp_cancel;

    TextView tv_j_cp_error;

    EditText et_j_cp_reply;

    // Toolbar vars
    Toolbar tb_j_cp_toolbar;
    DrawerLayout dl_j_cp_drawer;
    ActionBarDrawerToggle cp_drawerToggle;
    NavigationView nv_j_cp_navMenu;
    TextView tv_v_cp_curLoggedIn;

    Intent intent_j_viewPost;

    DBUtils dbUtils;

    // Default post id to -1
    private int postId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply_page);

        dbUtils = new DBUtils(this);

        // Retrieve post id
        Intent cameFrom = getIntent();
        if (cameFrom.getIntExtra("postId", -1) != -1)
        {
            postId = cameFrom.getIntExtra("postId", -1);
        }

        // Connect vars
        btn_j_cp_reply = findViewById(R.id.btn_v_cp_reply);
        btn_j_cp_cancel = findViewById(R.id.btn_v_cp_cancel);
        tv_j_cp_error = findViewById(R.id.tv_v_cp_error);
        et_j_cp_reply = findViewById(R.id.et_v_cp_reply);

        // Setup toolbar
        tb_j_cp_toolbar = findViewById(R.id.tb_v_cp_toolbar);
        setSupportActionBar(tb_j_cp_toolbar);

        // Setup Drawer
        dl_j_cp_drawer = findViewById(R.id.dl_v_cp_main);
        cp_drawerToggle = new ActionBarDrawerToggle(this, dl_j_cp_drawer, tb_j_cp_toolbar, R.string.nav_open, R.string.nav_close);
        dl_j_cp_drawer.addDrawerListener(cp_drawerToggle);
        cp_drawerToggle.syncState();

        // Setup Navigation View
        nv_j_cp_navMenu = findViewById(R.id.nv_v_cp_nav);
        nv_j_cp_navMenu.setItemIconTintList(null);
        nv_j_cp_navMenu.setNavigationItemSelectedListener(this);

        // Set logged in text
        tv_v_cp_curLoggedIn = findViewById(R.id.tv_v_cp_curLoggedIn);
        tv_v_cp_curLoggedIn.setText("Logged in as: " + Utilities.getLoggedInUser(this));

        // Setup intents
        intent_j_viewPost = new Intent(ReplyPage.this, ViewPostPage.class);

        // Setup button listeners
        replyButtonHandler();
        cancelButtonHandler();
    }

    // BTN: Reply
    private void replyButtonHandler()
    {
        btn_j_cp_reply.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String reply = et_j_cp_reply.getText().toString();

                if (reply.isEmpty())
                {
                    // Show error
                    Utilities.showError(tv_j_cp_error, "Reply must not be empty");
                }
                else
                {
                    Utilities.hideError(tv_j_cp_error);

                    // Get vars
                    String username = Utilities.getLoggedInUser(ReplyPage.this);
                    String timestamp = Utilities.generateTimestamp();

                    // Make reply
                    Comment c = new Comment(username, timestamp, reply, postId);

                    // Add reply to db
                    dbUtils.addComment(c);

                    Toast.makeText(ReplyPage.this, "Reply created successfully", Toast.LENGTH_SHORT).show();

                    // Send post id to view post page
                    intent_j_viewPost.putExtra("postId", postId);

                    // Go back to view post page
                    startActivity(intent_j_viewPost);
                    finish();
                }
            }
        });
    }

    // BTN: Cancel
    private void cancelButtonHandler()
    {
        btn_j_cp_cancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                // Send post id to view post page
                intent_j_viewPost.putExtra("postId", postId);

                // Go back to view post page
                startActivity(intent_j_viewPost);
                finish();
            }
        });
    }


    // Needed for navigation view to work properly
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item)
    {
        dl_j_cp_drawer.closeDrawer(GravityCompat.START);
        return Utilities.onNavigationItemSelected(item, this, dl_j_cp_drawer);
    }
    
}