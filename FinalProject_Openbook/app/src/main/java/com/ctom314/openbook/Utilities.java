package com.ctom314.openbook;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.ctom314.openbook.pages.AddBookPage;
import com.ctom314.openbook.pages.Homepage;
import com.ctom314.openbook.pages.LoginPage;
import com.ctom314.openbook.pages.MakePostPage;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

public class Utilities
{
    // Getting/Setting the logged in user
    public static String getLoggedInUser(Context context)
    {
        // Get the logged in user
        SharedPreferences prefs = context.getSharedPreferences("User", MODE_PRIVATE);
        return prefs.getString("username", null);
    }

    public static void setLoggedInUser(Context context, String username)
    {
        // Set the logged in user
        SharedPreferences prefs = context.getSharedPreferences("User", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("username", username);
        editor.apply();
    }

    // Debug: Clear the logged in user
    public static void clearLoggedInUser(Context context)
    {
        // Clear the logged in user
        SharedPreferences prefs = context.getSharedPreferences("User", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();
    }

    // Error messages
    public static void showError(TextView errTxt, String msg)
    {
        errTxt.setVisibility(TextView.VISIBLE);
        errTxt.setText(msg);
    }

    public static void hideError(TextView errTxt)
    {
        errTxt.setVisibility(TextView.INVISIBLE);
    }

    // Login/Register: Valid ET checks
    public  static boolean validateUsername(String username)
    {
        // No spaces, at least 4 characters, no special characters
        return username.matches("^[a-zA-Z0-9]{4,}$");
    }

    public static boolean validateEmail(String email)
    {
        // No spaces, includes @, includes . after @
        return email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");
    }

    public static boolean validatePassword(String password)
    {
        // No spaces
        return password.matches("^\\S+$");
    }

    // Navigation View setup
    public static boolean onNavigationItemSelected(@NonNull MenuItem item, Context context, DrawerLayout drawerLayout)
    {
        // Handle navigation view item clicks
        int id = item.getItemId();
        Intent intent = null;

        if (id == R.id.nav_home)
        {
            // Go to homepage
            intent = new Intent(context, Homepage.class);
        }

        else if (id == R.id.nav_make_post)
        {
            // Go to make post page
            intent = new Intent(context, MakePostPage.class);
        }

        else if (id == R.id.nav_add_book)
        {
            // Go to add book page
            intent = new Intent(context, AddBookPage.class);
        }

        // TODO: Make other pages and add them here

        else if (id == R.id.nav_logout)
        {
            // Prompt user to logout
            new AlertDialog.Builder(context)
                    .setTitle("Logout")
                    .setMessage("Are you sure you want to logout?")
                    .setPositiveButton("Yes", (dialog, which) -> {

                        // Log out the user and go back to login page
                        clearLoggedInUser(context);

                        context.startActivity(new Intent(context, LoginPage.class));
                        ((Activity) context).finish();
                    })
                    .setNegativeButton("No", null)
                    .show();

        }

        // Start intent if not null
        if (intent != null)
        {
            Activity curActivity = (Activity) context;
            ComponentName compName = curActivity.getComponentName();

            // If the intent that started the activity is the same as new intent,
            // Don't start the new intent, but close the drawer.
            if (compName != null && !compName.equals(intent.getComponent()))
            {
                context.startActivity(intent);
            }
            else
            {
                // Close the drawer
                return true;
            }
        }

        // Close the drawer
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    // Change Navigation Menu Item based on if its the current page
    public static void updateNavMenu(MenuItem item, Context context)
    {
        // Set item as checked
        item.setChecked(true);

        // TODO: Change background color of item
    }

    // Get current date and time and store in timestamp format
    public static String getCurrentTime()
    {
        Calendar cal = Calendar.getInstance();

        // Get current date and time
        int year = cal.get(Calendar.YEAR);

        // Months are 0 based, so add 1
        int month = cal.get(Calendar.MONTH) + 1;

        int day = cal.get(Calendar.DAY_OF_MONTH);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        int second = cal.get(Calendar.SECOND);

        return generateTimestamp(year, month, day, hour, minute, second);
    }

    // =============================================================================================
    //                              TIME / TIMESTAMP FUNCTIONS
    // =============================================================================================

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // Generate timestamp
    public static String generateTimestamp(int year, int month, int day)
    {
        return String.format("%04d-%02d-%02d", year, month, day);
    }

    public static String generateTimestamp(int year, int month, int day, int hour, int minute, int second)
    {
        return String.format("%04d-%02d-%02d %02d:%02d:%02d", year, month, day, hour, minute, second);
    }

    // Get time since post was made. Return as n minutes, n hours, n days, etc.
    @SuppressLint("DefaultLocale")
    public static String getTimeSincePost(String timestamp)
    {
        // Format timestamp
        LocalDateTime postTime = LocalDateTime.parse(timestamp, DATE_FORMAT);

        // Get current time
        LocalDateTime currentTime = LocalDateTime.now();

        // Get time since post
        Duration duration = Duration.between(postTime, currentTime);

        // Convert to mins, hours, days
        long mins = duration.toMinutes();
        long hours = duration.toHours();
        long days = duration.toDays();

        // Get more time formats
        long weeks = days / 7;
        long months = days / 30;
        double years = days / 365.0;

        // return based on time
        if (mins < 60)
        {
            String ago = mins == 1 ? " minute ago" : " minutes ago";
            return mins + ago;
        }
        else if (hours < 24)
        {
            String ago = hours == 1 ? " hour ago" : " hours ago";
            return hours + ago;
        }
        else if (days < 7)
        {
            String ago = days == 1 ? " day ago" : " days ago";
            return days + ago;
        }
        else if (weeks < 4)
        {
            String ago = weeks == 1 ? " week ago" : " weeks ago";
            return weeks + ago;
        }
        else if (months < 12)
        {
            String ago = months == 1 ? " month ago" : " months ago";
            return months + ago;
        }
        else
        {
            String ago = years == 1 ? " year ago" : " years ago";
            return String.format("%.1f", years) + ago;
        }
    }

}
