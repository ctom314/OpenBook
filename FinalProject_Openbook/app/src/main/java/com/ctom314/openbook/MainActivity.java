// =================================================================
// Author: Cody Thompson
// Date: 11/08/2024
// Desc: Book Club CRUD app for Android
// =================================================================

package com.ctom314.openbook;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.ctom314.openbook.pages.Homepage;
import com.ctom314.openbook.pages.LoginPage;

// =================================================================
//                    Launcher page for the app
// =================================================================

public class MainActivity extends AppCompatActivity
{
    Intent intent_j_loginPage;
    Intent intent_j_homepage;

    DBUtils dbUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Setup intents
        intent_j_loginPage = new Intent(MainActivity.this, LoginPage.class);
        intent_j_homepage = new Intent(MainActivity.this, Homepage.class);

        // Get logged in user
        String user = Utilities.getLoggedInUser(this);

        // Setup Database
        dbUtils = new DBUtils(this);
        dbUtils.initAllTables();

        // Redirect to login page if no user is logged in
        if (user == null)
        {
            startActivity(intent_j_loginPage);
        }
        else
        {
            startActivity(intent_j_homepage);
        }

        // Close the current activity
        finish();
    }
}