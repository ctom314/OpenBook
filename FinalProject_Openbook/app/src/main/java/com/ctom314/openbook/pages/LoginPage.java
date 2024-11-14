package com.ctom314.openbook.pages;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.ctom314.openbook.DBUtils;
import com.ctom314.openbook.R;
import com.ctom314.openbook.Utilities;

// =================================================================
//                           Login Page
// =================================================================

public class LoginPage extends AppCompatActivity
{
    // Vars
    TextView tv_j_lp_registerMsg;
    TextView tv_j_lp_error;

    EditText et_j_lp_username;
    EditText et_j_lp_password;

    Button btn_j_lp_login;

    DBUtils dbUtils;

    Intent intent_j_registerPage;
    Intent intent_j_homepage;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        // Link vars
        tv_j_lp_registerMsg = findViewById(R.id.tv_v_lp_registerMsg);
        tv_j_lp_error = findViewById(R.id.tv_v_lp_error);
        et_j_lp_username = findViewById(R.id.et_v_lp_username);
        et_j_lp_password = findViewById(R.id.et_v_lp_password);
        btn_j_lp_login = findViewById(R.id.btn_v_lp_login);

        // Setup intents
        intent_j_registerPage = new Intent(LoginPage.this, RegisterPage.class);
        intent_j_homepage = new Intent(LoginPage.this, Homepage.class);

        dbUtils = new DBUtils(this);


        // Button handlers
        loginButtonHandler();
        registerButtonHandler();
    }

    // Button: Login
    private void loginButtonHandler()
    {
        btn_j_lp_login.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String username = et_j_lp_username.getText().toString();
                String password = et_j_lp_password.getText().toString();

                if (validateLogin(username, password))
                {
                    // Set logged in user
                    Utilities.setLoggedInUser(LoginPage.this, username);

                    Toast.makeText(LoginPage.this, "Logged in as " + username, Toast.LENGTH_SHORT).show();
                    
                    // Redirect to homepage
                    startActivity(intent_j_homepage);
                    finish();
                }
            }
        });
    }

    // Button: Register
    private void registerButtonHandler()
    {
        tv_j_lp_registerMsg.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                startActivity(intent_j_registerPage);
                finish();
            }
        });
    }

    // Validate login
    private boolean validateLogin(String username, String password)
    {
        // Validate ETs
        if (!username.isEmpty() && !password.isEmpty())
        {
            if (!Utilities.validateUsername(username) && Utilities.validatePassword(password))
            {
                Utilities.showError(tv_j_lp_error, "Invalid username.");
                return false;
            }

            else if (!Utilities.validatePassword(password) && Utilities.validateUsername(username))
            {
                Utilities.showError(tv_j_lp_error, "Invalid password.");
                return false;
            }

            else if (!Utilities.validateUsername(username) && !Utilities.validatePassword(password))
            {
                Utilities.showError(tv_j_lp_error, "Invalid username and password.");
                return false;
            }

            // Verify account login
            if (!dbUtils.verifyAccount(username, password).isSuccess())
            {
                Utilities.showError(tv_j_lp_error, "Username or password is incorrect.");
                return false;
            }
        }
        else
        {
            Utilities.showError(tv_j_lp_error, "All fields must be filled in.");
            return false;
        }

        return true;
    }
}