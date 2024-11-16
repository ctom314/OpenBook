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
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.ctom314.openbook.Account;
import com.ctom314.openbook.DBUtils;
import com.ctom314.openbook.PasswordUtils;
import com.ctom314.openbook.R;
import com.ctom314.openbook.Result;
import com.ctom314.openbook.Utilities;
import com.google.android.material.navigation.NavigationView;

import java.util.Arrays;

public class ChangePassPage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
{
    // Vars
    EditText et_j_cpp_oldPass;
    EditText et_j_cpp_newPass;
    EditText et_j_cpp_confirm;

    Button btn_j_cpp_change;
    Button btn_j_cpp_cancel;

    TextView tv_j_cpp_error;

    // Toolbar vars
    Toolbar tb_j_cpp_toolbar;
    DrawerLayout dl_j_cpp_drawer;
    ActionBarDrawerToggle cpp_drawerToggle;
    NavigationView nv_j_cpp_navMenu;
    TextView tv_v_cpp_curLoggedIn;

    Intent intent_j_settings;

    DBUtils dbUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pass_page);

        dbUtils = new DBUtils(this);

        // Connect vars
        et_j_cpp_oldPass = findViewById(R.id.et_v_cpp_oldPassword);
        et_j_cpp_newPass = findViewById(R.id.et_v_cpp_newPassword);
        et_j_cpp_confirm = findViewById(R.id.et_v_cpp_confirm);
        btn_j_cpp_change = findViewById(R.id.btn_v_cpp_change);
        btn_j_cpp_cancel = findViewById(R.id.btn_v_cpp_cancel);
        tv_j_cpp_error = findViewById(R.id.tv_v_cpp_error);

        // Setup toolbar
        tb_j_cpp_toolbar = findViewById(R.id.tb_v_cpp_toolbar);
        setSupportActionBar(tb_j_cpp_toolbar);

        // Setup Drawer
        dl_j_cpp_drawer = findViewById(R.id.dl_v_cpp_main);
        cpp_drawerToggle = new ActionBarDrawerToggle(this, dl_j_cpp_drawer, tb_j_cpp_toolbar, R.string.nav_open, R.string.nav_close);
        dl_j_cpp_drawer.addDrawerListener(cpp_drawerToggle);
        cpp_drawerToggle.syncState();

        // Setup Navigation View
        nv_j_cpp_navMenu = findViewById(R.id.nv_v_cpp_nav);
        nv_j_cpp_navMenu.setItemIconTintList(null);
        nv_j_cpp_navMenu.setNavigationItemSelectedListener(this);

        // Set logged in text
        tv_v_cpp_curLoggedIn = findViewById(R.id.tv_v_cpp_curLoggedIn);
        tv_v_cpp_curLoggedIn.setText("Logged in as: " + Utilities.getLoggedInUser(this));

        // Setup intents
        intent_j_settings = new Intent(ChangePassPage.this, SettingsPage.class);

        // Button handlers
        changeButtonHandler();
        cancelButtonHandler();

    }

    // Change password
    private Result changePass(String password, Account a)
    {
        // Check if old and new password are the same
        if (PasswordUtils.passwordHashesMatch(password, a.getPasswordSalt(), a.getPasswordHash()))
        {
            Utilities.showError(tv_j_cpp_error, "New and Old passwords cannot be the same.");
            return Result.failure("New and Old passwords cannot be the same.");
        }

        // Hash new password
        byte[] salt = PasswordUtils.generateSalt();
        byte[] hash = PasswordUtils.hashPassword(password, salt);

        // Update account
        a = new Account(a.getName(), a.getEmail(), a.getUsername(), hash, salt);
        dbUtils.updateAccount(a);

        Toast.makeText(this, "Password Changed Successfully", Toast.LENGTH_SHORT).show();

        return Result.success();
    }

    // BTN: Change Password
    private void changeButtonHandler()
    {
        btn_j_cpp_change.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                // Get passwords
                String oldPass = et_j_cpp_oldPass.getText().toString();
                String newPass = et_j_cpp_newPass.getText().toString();
                String confirm = et_j_cpp_confirm.getText().toString();

                // Get account
                String username = Utilities.getLoggedInUser(ChangePassPage.this);
                Account a = dbUtils.getAccount(username);

                // Check if any field is empty
                if (oldPass.isEmpty() || newPass.isEmpty() || confirm.isEmpty())
                {
                    Utilities.showError(tv_j_cpp_error, "All fields must be filled.");
                    return;
                }

                // Check if old password is correct
                if (!PasswordUtils.passwordHashesMatch(oldPass, a.getPasswordSalt(), a.getPasswordHash()))
                {
                    Utilities.showError(tv_j_cpp_error, "Old password is incorrect.");
                    return;
                }

                // Check if new password and confirm match
                if (!PasswordUtils.passwordsMatch(newPass, confirm))
                {
                    Utilities.showError(tv_j_cpp_error, "New password and confirm do not match.");
                    return;
                }

                Utilities.hideError(tv_j_cpp_error);

                // Prompt user to change password
                new AlertDialog.Builder(ChangePassPage.this)
                        .setTitle("Change Password")
                        .setMessage("Are you sure you want to change your password?")
                        .setPositiveButton("Yes", (dialog, which) -> {

                            if (changePass(newPass, a).isSuccess())
                            {
                                // Go back to settings
                                startActivity(intent_j_settings);
                                finish();
                            }

                        })
                        .setNegativeButton("No", null)
                        .show();

            }
        });
    }

    // BTN: Cancel
    private void cancelButtonHandler()
    {
        btn_j_cpp_cancel.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                // Go back to settings
                startActivity(intent_j_settings);
                finish();
            }
        });
    }


    // Needed for navigation view to work properly
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item)
    {
        dl_j_cpp_drawer.closeDrawer(GravityCompat.START);
        return Utilities.onNavigationItemSelected(item, this, dl_j_cpp_drawer);
    }

}