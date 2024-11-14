package com.ctom314.openbook.pages;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.MotionEvent;
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
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.ctom314.openbook.Comment;
import com.ctom314.openbook.DBUtils;
import com.ctom314.openbook.Post;
import com.ctom314.openbook.R;
import com.ctom314.openbook.RepliesListAdapter;
import com.ctom314.openbook.Utilities;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class EditPostPage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{
    // Vars
    Button btn_j_ep_update;
    Button btn_j_ep_delete;
    Button btn_j_ep_cancel;

    TextView tv_j_ep_error;

    EditText et_j_ep_post;
    
    // Toolbar vars
    Toolbar tb_j_ep_toolbar;
    DrawerLayout dl_j_ep_drawer;
    ActionBarDrawerToggle ep_drawerToggle;
    NavigationView nv_j_ep_navMenu;
    TextView tv_v_ep_curLoggedIn;

    Intent intent_j_viewPost;
    Intent intent_j_homepage;

    DBUtils dbUtils;

    // Default post to null
    private Post post = null;

    boolean postChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post_page);

        dbUtils = new DBUtils(this);

        // Retrieve post
        Intent cameFrom = getIntent();
        if (cameFrom.getIntExtra("postId", -1) != -1)
        {
            int postId = cameFrom.getIntExtra("postId", -1);
            post = dbUtils.getPost(postId);
        }

        // Connect vars
        btn_j_ep_update = findViewById(R.id.btn_v_ep_update);
        btn_j_ep_delete = findViewById(R.id.btn_v_ep_delete);
        btn_j_ep_cancel = findViewById(R.id.btn_v_ep_cancel);
        tv_j_ep_error = findViewById(R.id.tv_v_ep_error);
        et_j_ep_post = findViewById(R.id.et_v_ep_post);

        // Setup toolbar
        tb_j_ep_toolbar = findViewById(R.id.tb_v_ep_toolbar);
        setSupportActionBar(tb_j_ep_toolbar);

        // Setup Drawer
        dl_j_ep_drawer = findViewById(R.id.dl_v_ep_main);
        ep_drawerToggle = new ActionBarDrawerToggle(this, dl_j_ep_drawer, tb_j_ep_toolbar, R.string.nav_open, R.string.nav_close);
        dl_j_ep_drawer.addDrawerListener(ep_drawerToggle);
        ep_drawerToggle.syncState();

        // Setup Navigation View
        nv_j_ep_navMenu = findViewById(R.id.nv_v_ep_nav);
        nv_j_ep_navMenu.setItemIconTintList(null);
        nv_j_ep_navMenu.setNavigationItemSelectedListener(this);

        // Set logged in text
        tv_v_ep_curLoggedIn = findViewById(R.id.tv_v_ep_curLoggedIn);
        tv_v_ep_curLoggedIn.setText("Logged in as: " + Utilities.getLoggedInUser(this));

        // Setup intents
        intent_j_viewPost = new Intent(EditPostPage.this, ViewPostPage.class);
        intent_j_homepage = new Intent(EditPostPage.this, Homepage.class);

        // Set ET text to post contents
        et_j_ep_post.setText(post.getContent());

        // Setup update button
        updateButtonState();
        postTextChangeListener();
        updateButtonHandler();
        deleteButtonHandler();
        cancelButtonHandler();

    }

    // Set update button state based on if post was changed
    private void updateButtonState()
    {
        if (!postChanged)
        {
            // Post not changed
            btn_j_ep_update.setEnabled(false);
            btn_j_ep_update.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.btnDisabled));
        }
        else
        {
            // Post changed
            btn_j_ep_update.setEnabled(true);
            btn_j_ep_update.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.secondary));
        }
    }

    // ET: detect changes to post and update button state
    private void postTextChangeListener()
    {
        et_j_ep_post.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2)
            {
                // If the content in the ET is different from the post content,
                // change update button state.
                if (!et_j_ep_post.getText().toString().equals(post.getContent()))
                {
                    postChanged = true;
                }
                else
                {
                    postChanged = false;
                }

                updateButtonState();
            }

            @Override
            public void afterTextChanged(Editable editable)
            {

            }
        });
    }

    // BTN: Update
    private void updateButtonHandler()
    {
        btn_j_ep_update.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (et_j_ep_post.getText().toString().isEmpty())
                {
                    // Show error
                    Utilities.showError(tv_j_ep_error, "Post must not be empty");
                }
                else
                {
                    Utilities.hideError(tv_j_ep_error);

                    // Update post
                    post.setContent(et_j_ep_post.getText().toString());
                    dbUtils.updatePost(post);

                    Toast.makeText(EditPostPage.this, "Post updated successfully", Toast.LENGTH_SHORT).show();

                    // Send post to view post page
                    intent_j_viewPost.putExtra("postId", dbUtils.getPostId(post.getUsername(), post.getTimestamp()));

                    // Go to view post page
                    startActivity(intent_j_viewPost);
                    finish();
                }
            }
        });
    }

    // BTN: Delete
    private void deleteButtonHandler()
    {
        btn_j_ep_delete.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                // Prompt user before deletion
                new AlertDialog.Builder(EditPostPage.this)
                        .setTitle("Delete Post")
                        .setMessage("Are you sure you want to delete this post?")
                        .setPositiveButton("Yes", (dialog, which) -> {

                            // Delete post
                            dbUtils.deletePost(dbUtils.getPostId(post.getUsername(), post.getTimestamp()));

                            Toast.makeText(EditPostPage.this, "Post deleted successfully", Toast.LENGTH_SHORT).show();

                            // Go back to homepage
                            startActivity(intent_j_homepage);
                            finish();
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });
    }

    // BTN: Cancel
    private void cancelButtonHandler()
    {
        btn_j_ep_cancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                // Send post to view post page
                intent_j_viewPost.putExtra("postId", dbUtils.getPostId(post.getUsername(), post.getTimestamp()));

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
        dl_j_ep_drawer.closeDrawer(GravityCompat.START);
        return Utilities.onNavigationItemSelected(item, this, dl_j_ep_drawer);
    }
    
}