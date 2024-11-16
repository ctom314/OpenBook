package com.ctom314.openbook.pages;

import android.app.AlertDialog;
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
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.ctom314.openbook.Account;
import com.ctom314.openbook.DBUtils;
import com.ctom314.openbook.R;
import com.ctom314.openbook.Utilities;
import com.google.android.material.navigation.NavigationView;

public class SettingsPage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{
    // Vars
    EditText et_j_stp_name;
    EditText et_j_stp_username;
    EditText et_j_stp_email;

    Button btn_j_stp_changePassword;
    Button btn_j_stp_deleteAccount;
    Button btn_j_stp_edit;
    Button btn_j_stp_save;
    Button btn_j_stp_cancel;
    Button btn_j_stp_home;

    TextView tv_j_stp_error;

    // Toolbar vars
    Toolbar tb_j_stp_toolbar;
    DrawerLayout dl_j_stp_drawer;
    ActionBarDrawerToggle stp_drawerToggle;
    NavigationView nv_j_stp_navMenu;
    TextView tv_v_stp_curLoggedIn;

    Intent intent_j_homepage;
    Intent intent_j_loginPage;
    Intent intent_j_changePassPage;

    DBUtils dbUtils;

    boolean editMode = false;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_page);
        
        dbUtils = new DBUtils(this);
        
        // Connect vars
        et_j_stp_name = findViewById(R.id.et_v_stp_name);
        et_j_stp_username = findViewById(R.id.et_v_stp_username);
        et_j_stp_email = findViewById(R.id.et_v_stp_email);
        btn_j_stp_changePassword = findViewById(R.id.btn_v_stp_changePassword);
        btn_j_stp_deleteAccount = findViewById(R.id.btn_v_stp_deleteAccount);
        btn_j_stp_edit = findViewById(R.id.btn_v_stp_edit);
        btn_j_stp_save = findViewById(R.id.btn_v_stp_save);
        btn_j_stp_cancel = findViewById(R.id.btn_v_stp_cancel);
        btn_j_stp_home = findViewById(R.id.btn_v_stp_home);
        tv_j_stp_error = findViewById(R.id.tv_v_stp_error);

        // Setup toolbar
        tb_j_stp_toolbar = findViewById(R.id.tb_v_stp_toolbar);
        setSupportActionBar(tb_j_stp_toolbar);

        // Setup Drawer
        dl_j_stp_drawer = findViewById(R.id.dl_v_stp_main);
        stp_drawerToggle = new ActionBarDrawerToggle(this, dl_j_stp_drawer, tb_j_stp_toolbar, R.string.nav_open, R.string.nav_close);
        dl_j_stp_drawer.addDrawerListener(stp_drawerToggle);
        stp_drawerToggle.syncState();

        // Setup Navigation View
        nv_j_stp_navMenu = findViewById(R.id.nv_v_stp_nav);
        nv_j_stp_navMenu.setItemIconTintList(null);
        nv_j_stp_navMenu.setNavigationItemSelectedListener(this);

        // Setup Nav View Selected Item (Changes item color for current page)
        Utilities.updateNavMenu(nv_j_stp_navMenu.getMenu().findItem(R.id.nav_settings), this);

        // Set logged in text
        tv_v_stp_curLoggedIn = findViewById(R.id.tv_v_stp_curLoggedIn);
        tv_v_stp_curLoggedIn.setText("Logged in as: " + Utilities.getLoggedInUser(this));

        // Setup intents
        intent_j_homepage = new Intent(SettingsPage.this, Homepage.class);
        intent_j_loginPage = new Intent(SettingsPage.this, LoginPage.class);
        intent_j_changePassPage = new Intent(SettingsPage.this, ChangePassPage.class);

        // Setup ETs
        setupETs();

        // Button handlers
        changePassButtonHandler();
        deleteAccountButtonHandler();
        editButtonHandler();
        saveButtonHandler();
        cancelButtonHandler();
        homeButtonHandler();


    }

    // Set edit mode state
    private void editModeEnabled(boolean state)
    {
        if (state)
        {
            // Edit mode enabled
            tb_j_stp_toolbar.setTitle("Settings [Edit Mode]");

            btn_j_stp_changePassword.setEnabled(false);
            btn_j_stp_deleteAccount.setEnabled(false);

            btn_j_stp_changePassword.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.btnDisabled));
            btn_j_stp_deleteAccount.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.btnDisabled));

            btn_j_stp_home.setVisibility(Button.INVISIBLE);
            btn_j_stp_cancel.setVisibility(Button.VISIBLE);

            btn_j_stp_edit.setVisibility(Button.INVISIBLE);
            btn_j_stp_save.setVisibility(Button.VISIBLE);

            // Enable ETs
            setETsState(true);

        }
        else
        {
            // Edit mode disabled
            tb_j_stp_toolbar.setTitle("Settings");

            btn_j_stp_changePassword.setEnabled(true);
            btn_j_stp_deleteAccount.setEnabled(true);

            btn_j_stp_changePassword.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.secondary));
            btn_j_stp_deleteAccount.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.error));

            btn_j_stp_home.setVisibility(Button.VISIBLE);
            btn_j_stp_cancel.setVisibility(Button.INVISIBLE);

            btn_j_stp_edit.setVisibility(Button.VISIBLE);
            btn_j_stp_save.setVisibility(Button.INVISIBLE);

            // Disable ETs
            setETsState(false);
        }
    }

    private void setupETs()
    {
        // Get user account
        Account account = dbUtils.getAccount(Utilities.getLoggedInUser(this));

        // Set ETs to account info
        et_j_stp_name.setText(account.getName());
        et_j_stp_username.setText(account.getUsername());
        et_j_stp_email.setText(account.getEmail());

        setETsState(false);
    }

    // Set the state of the ETs
    private void setETsState(boolean state)
    {
        et_j_stp_name.setEnabled(state);
        et_j_stp_username.setEnabled(state);
        et_j_stp_email.setEnabled(state);
    }

    // =============================================================================================
    //                                      BUTTONS
    // =============================================================================================

    // BTN: Change Password
    private void changePassButtonHandler()
    {
        btn_j_stp_changePassword.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                // Go to change password page
                startActivity(intent_j_changePassPage);
            }
        });
    }

    // BTN: Delete Account
    private void deleteAccountButtonHandler()
    {
        btn_j_stp_deleteAccount.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                // Prompt user to delete account
                new AlertDialog.Builder(SettingsPage.this)
                        .setTitle("Delete Account")
                        .setMessage("Are you sure you want to delete your account? THIS CANNOT BE UNDONE!")
                        .setPositiveButton("Yes", (dialog, which) ->
                        {
                            // Delete account
                            int userId = dbUtils.getUserId(Utilities.getLoggedInUser(SettingsPage.this));
                            dbUtils.deleteAccount(userId);

                            Toast.makeText(SettingsPage.this, "Account deleted successfully", Toast.LENGTH_SHORT).show();

                            // Go back to login page
                            startActivity(intent_j_loginPage);
                            finish();
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });
    }

    // BTN: Edit
    private void editButtonHandler()
    {
        btn_j_stp_edit.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                // Enable edit mode
                editMode = true;
                editModeEnabled(editMode);
            }
        });
    }

    // BTN: Save
    private void saveButtonHandler()
    {
        btn_j_stp_save.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                // Get new account info
                String name = et_j_stp_name.getText().toString();
                String username = et_j_stp_username.getText().toString();
                String email = et_j_stp_email.getText().toString();

                // Check if any fields are empty
                if (name.isEmpty() || username.isEmpty() || email.isEmpty())
                {
                    Utilities.showError(tv_j_stp_error, "All fields must be filled in.");
                    return;
                }

                Utilities.hideError(tv_j_stp_error);

                // Get user account
                Account a = dbUtils.getAccount(Utilities.getLoggedInUser(SettingsPage.this));

                // Check if no changes were made
                if (a.getName().equals(name) && a.getUsername().equals(username) && a.getEmail().equals(email))
                {
                    Toast.makeText(SettingsPage.this, "No changes were made. Canceling...", Toast.LENGTH_SHORT).show();

                    // Disable edit mode
                    editMode = false;
                    editModeEnabled(editMode);

                    return;
                }

                // Check if username was changed
                if (!a.getUsername().equals(username))
                {
                    // Check if username is already taken
                    if (dbUtils.accountExists(username))
                    {
                        Utilities.showError(tv_j_stp_error, "Username is already taken.");
                        return;
                    }
                }

                // TODO: Fix bug where changing username reverts back to old username

                // Update account
                a = new Account(name, email, username, a.getPasswordHash(), a.getPasswordSalt());
                dbUtils.updateAccount(a);

                Toast.makeText(SettingsPage.this, "Account updated successfully", Toast.LENGTH_SHORT).show();

                // Disable edit mode
                editMode = false;
                editModeEnabled(editMode);
            }
        });
    }

    // BTN: Cancel
    private void cancelButtonHandler()
    {
        btn_j_stp_cancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                // Disable edit mode
                editMode = false;
                editModeEnabled(editMode);

                // Reset ETs
                setupETs();
            }
        });
    }

    // BTN: Home
    private void homeButtonHandler()
    {
        btn_j_stp_home.setOnClickListener(new View.OnClickListener()
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

    // Needed for navigation view to work properly
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item)
    {
        dl_j_stp_drawer.closeDrawer(GravityCompat.START);
        return Utilities.onNavigationItemSelected(item, this, dl_j_stp_drawer);
    }
    
}