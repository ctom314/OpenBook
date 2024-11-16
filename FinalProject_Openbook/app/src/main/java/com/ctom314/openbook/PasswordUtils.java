package com.ctom314.openbook;

import android.util.Log;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class PasswordUtils
{
    /**
     * Hashes a password with a salt using SHA-256
     * @param password The password to hash
     * @param salt The salt to add to the password
     * @return The hashed password
     */
    public static byte[] hashPassword(String password, byte[] salt)
    {
        byte[] hash = null;

        try
        {
            // Get instance of SHA-256 to hash password with
            MessageDigest md = MessageDigest.getInstance("SHA-256");

            // Add salt to password
            md.update(salt);

            // Hash password
            hash = md.digest(password.getBytes(StandardCharsets.UTF_8));
        }
        catch (NoSuchAlgorithmException e)
        {
            Log.e("DBUtils", "NoSuchAlgorithmException: " + e.getMessage());
        }

        return hash;
    }

    /**
     * Generates a random salt
     * @return The generated salt
     */
    public static byte[] generateSalt()
    {
        // Use SecureRandom instead of regular Random for better security
        SecureRandom r = new SecureRandom();
        byte[] salt = new byte[16];

        // Generate random salt
        r.nextBytes(salt);

        return salt;
    }

    /**
     * Compares a password with a saved hash and salt
     * @param enteredPassword The password to compare
     * @param savedSalt The salt used to hash the saved password
     * @param savedHash The saved password hash
     * @return True if the password matches the saved hash, false otherwise
     */
    public static boolean passwordHashesMatch(String enteredPassword, byte[] savedSalt, byte[] savedHash)
    {
        // Hash the password with the salt
        byte[] enteredHash = hashPassword(enteredPassword, savedSalt);

        // Compare the two hashes
        return MessageDigest.isEqual(savedHash, enteredHash);
    }

    // Compare entered password with password confirm
    /**
     * Compares two passwords to see if they match
     * @param password The first password
     * @param passwordConfirm The second password
     * @return True if the passwords match, false otherwise
     */
    public static boolean passwordsMatch(String password, String passwordConfirm)
    {
        return password.equals(passwordConfirm);
    }
}
