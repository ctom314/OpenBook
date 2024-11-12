package com.ctom314.openbook;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toolbar;

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

    // Valid year check
    public static boolean isValidYear(String yearTxt)
    {
        // Check if year is valid
        try
        {
            int year = Integer.parseInt(yearTxt);

            // Ensure year is after 0 and not in the future
            return year >= 0 && year <= Calendar.getInstance().get(Calendar.YEAR);
        }
        catch (NumberFormatException e)
        {
            return false;
        }
    }

    // Remove leading and trailing whitespace
    public static String trimWhitespace(String str)
    {
        return str.trim();
    }

    // Add ellipsis to string if it is too long
    // One for no view, just string and max length
    public static String shortenString(String str, int maxLength)
    {
        // If txt is too long, cut it off with ellipsis
        maxLength = maxLength == 0 ? 1000 : maxLength;

        CharSequence shortChar = TextUtils.ellipsize(str, new TextPaint(),
                maxLength, TextUtils.TruncateAt.END);

        // Cut off any stray letters or spaces
        String shortTxt = shortChar.toString();

        if (shortTxt.endsWith("…"))
        {
            if (shortTxt.contains(" "))
            {
                shortTxt = shortTxt.substring(0,
                        shortTxt.lastIndexOf(" ")) + "...";
            }
        }

        return shortTxt;
    }

    // One specifically for TV
    public static String shortenString(TextView tv, String str, int maxLength)
    {
        // If txt is too long, cut it off with ellipsis
        maxLength = maxLength == 0 ? 1000 : maxLength;

        CharSequence shortChar = TextUtils.ellipsize(str, tv.getPaint(),
                maxLength, TextUtils.TruncateAt.END);

        // Cut off any stray letters or spaces
        String shortTxt = shortChar.toString();

        if (shortTxt.endsWith("…"))
        {
            if (shortTxt.contains(" "))
            {
                shortTxt = shortTxt.substring(0,
                        shortTxt.lastIndexOf(" ")) + "...";
            }
        }

        return shortTxt;
    }

    public static String shortenString(View view, String str)
    {
        // Determine what obj to use
        if (view instanceof TextView)
        {
            // TextView obj
            TextView tv = (TextView) view;

            // If txt is too long, cut it off with ellipsis
            int maxLength = tv.getWidth();
            maxLength = maxLength == 0 ? 1000 : maxLength;

            CharSequence shortChar = TextUtils.ellipsize(str, tv.getPaint(),
                    maxLength, TextUtils.TruncateAt.END);

            // Cut off any stray letters or spaces
            String shortTxt = shortChar.toString();

            if (shortTxt.endsWith("…"))
            {
                if (shortTxt.contains(" "))
                {
                    shortTxt = shortTxt.substring(0,
                            shortTxt.lastIndexOf(" ")) + "...";
                }
            }

            return shortTxt;
        }
        else if (view instanceof Toolbar)
        {
            // Toolbar obj
            Toolbar tb = (Toolbar) view;

            // If txt is too long, cut it off with ellipsis
            int maxLength = tb.getWidth();
            maxLength = maxLength == 0 ? 1000 : maxLength;

            // Get the toolbar's title tv
            TextView titleTv = null;

            for (int i = 0; i < tb.getChildCount(); i++)
            {
                View c = tb.getChildAt(i);
                if (c instanceof  TextView)
                {
                    titleTv = (TextView) c;
                    break;
                }
            }

            if (titleTv != null)
            {
                CharSequence shortChar = TextUtils.ellipsize(str, titleTv.getPaint(),
                        maxLength, TextUtils.TruncateAt.END);

                // Cut off any stray letters or spaces
                String shortTxt = shortChar.toString();

                if (shortTxt.endsWith("…"))
                {
                    if (shortTxt.contains(" "))
                    {
                        shortTxt = shortTxt.substring(0,
                                shortTxt.lastIndexOf(" ")) + "...";
                    }
                }

                return shortTxt;
            }
            else
            {
                // No TV found. Return original string
                Log.e("Utilities", "ShortenString: No TextView found in Toolbar. Cannot shorten string.");
                return str;
            }

        }
        else
        {
            // Not TV or TB. Return original string
            Log.e("Utilities", "ShortenString: View is not a TextView or Toolbar. Cannot shorten string.");
            return str;
        }


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

    // =============================================================================================
    //                              TIME / TIMESTAMP FUNCTIONS
    // =============================================================================================

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter DATE_FORMAT_NEAT = DateTimeFormatter.ofPattern("d LLLL yyyy, h:mm a");

    // Generate timestamp
    public static String generateTimestamp()
    {
        // Get current date and time
        LocalDateTime now = LocalDateTime.now();

        // Format timestamp
        return now.format(DATE_FORMAT);
    }

    public static String makeTimestamp(int year, int month, int day, int hour, int minute, int second)
    {
        return String.format("%04d-%02d-%02d %02d:%02d:%02d", year, month, day, hour, minute, second);
    }

    // Return full date from timestamp in readable format
    public static String getFullDate(String timestamp)
    {
        // Format timestamp
        LocalDateTime postTime = LocalDateTime.parse(timestamp, DATE_FORMAT);

        // Format date
        return postTime.format(DATE_FORMAT_NEAT);
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
