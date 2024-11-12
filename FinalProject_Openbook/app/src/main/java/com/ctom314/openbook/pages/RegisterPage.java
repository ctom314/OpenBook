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

import com.ctom314.openbook.Account;
import com.ctom314.openbook.DBUtils;
import com.ctom314.openbook.PasswordUtils;
import com.ctom314.openbook.R;
import com.ctom314.openbook.Utilities;

// =================================================================
//                           Register Page
// =================================================================

public class RegisterPage extends AppCompatActivity
{
    // Vars
    TextView tv_j_rp_error;

    EditText et_j_rp_name;
    EditText et_j_rp_username;
    EditText et_j_rp_email;
    EditText et_j_rp_password;
    EditText et_j_rp_passwordConfirm;

    Button btn_j_rp_register;
    Button btn_j_rp_back;

    Intent intent_j_loginPage;
    Intent intent_j_homepage;

    DBUtils dbUtils;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_page);

        // Connect vars
        tv_j_rp_error = findViewById(R.id.tv_v_rp_error);
        et_j_rp_name = findViewById(R.id.et_v_rp_name);
        et_j_rp_username = findViewById(R.id.et_v_rp_username);
        et_j_rp_email = findViewById(R.id.et_v_rp_email);
        et_j_rp_password = findViewById(R.id.et_v_rp_password);
        et_j_rp_passwordConfirm = findViewById(R.id.et_v_rp_confirm);
        btn_j_rp_register = findViewById(R.id.btn_v_rp_register);
        btn_j_rp_back = findViewById(R.id.btn_v_rp_back);

        // Setup intents
        intent_j_loginPage = new Intent(RegisterPage.this, LoginPage.class);
        intent_j_homepage = new Intent(RegisterPage.this, Homepage.class);

        dbUtils = new DBUtils(this);

        // Button handlers
        registerButtonHandler();
        backButtonHandler();
    }

    // Button: Register
    private void registerButtonHandler()
    {
        btn_j_rp_register.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                // Get user input
                String name = et_j_rp_name.getText().toString();
                String username = et_j_rp_username.getText().toString();
                String email = et_j_rp_email.getText().toString();
                String password = et_j_rp_password.getText().toString();
                String passwordConfirm = et_j_rp_passwordConfirm.getText().toString();

                // Trim whitespace
                name = Utilities.trimWhitespace(name);
                username = Utilities.trimWhitespace(username);
                email = Utilities.trimWhitespace(email);
                password = Utilities.trimWhitespace(password);
                passwordConfirm = Utilities.trimWhitespace(passwordConfirm);
                
                // Validate user input
                if (validateRegister(name, username, email, password, passwordConfirm))
                {
                    // Hash password and generate salt
                    byte[] salt = PasswordUtils.generateSalt();
                    byte[] hash = PasswordUtils.hashPsasword(password, salt);

                    // Add user to DB
                    Account account = new Account(name, email, username, hash, salt);
                    dbUtils.addAccount(account);
                    
                    // Set logged in user
                    Utilities.setLoggedInUser(RegisterPage.this, username);

                    Toast.makeText(RegisterPage.this, "Account registered successfully", Toast.LENGTH_SHORT).show();
                    
                    // Redirect to homepage
                    startActivity(intent_j_homepage);
                    finish();
                }
            }
        });
    }

    // Button: Back
    private void backButtonHandler()
    {
        btn_j_rp_back.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                startActivity(intent_j_loginPage);
                finish();
            }
        });
    }
    
    // Validate Register
    private boolean validateRegister(String name, String username, String email, String password, String pConfirm)
    {
        // Check if any fields are empty
        if (name.isEmpty() || username.isEmpty() || email.isEmpty() || password.isEmpty() || pConfirm.isEmpty())
        {
            Utilities.showError(tv_j_rp_error, "All fields must be filled in.");
            return false;
        }
        
        // Check if username is valid
        if (!Utilities.validateUsername(username))
        {
            Utilities.showError(tv_j_rp_error, "Invalid username.");
            return false;
        }
        
        // Check if email is valid
        if (!Utilities.validateEmail(email))
        {
            Utilities.showError(tv_j_rp_error, "Invalid email.");
            return false;
        }
        
        // Check if password is valid
        if (!Utilities.validatePassword(password))
        {
            Utilities.showError(tv_j_rp_error, "Invalid password.");
            return false;
        }
        
        // Check if passwords match
        if (!PasswordUtils.passwordsMatch(password, pConfirm))
        {
            Utilities.showError(tv_j_rp_error, "Passwords do not match.");
            return false;
        }
        
        // Check if username and email already exists in DB
        if (dbUtils.accountExists(username))
        {
            Utilities.showError(tv_j_rp_error, "Username is already taken.");
            return false;
        }
        
        // Clear any error messages
        Utilities.hideError(tv_j_rp_error);
        return true;
    }
}