package com.ctom314.openbook;

import android.util.Log;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class PasswordUtils
{
    // Hash password
    public static byte[] hashPsasword(String password, byte[] salt)
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

    // Generate Salt
    public static byte[] generateSalt()
    {
        // Use SecureRandom instead of regular Random for better security
        SecureRandom r = new SecureRandom();
        byte[] salt = new byte[16];

        // Generate random salt
        r.nextBytes(salt);

        return salt;
    }

    // Compare password with hash
    public static boolean passwordHashesMatch(String enteredPassword, byte[] savedSalt, byte[] savedHash)
    {
        // Hash the password with the salt
        byte[] enteredHash = hashPsasword(enteredPassword, savedSalt);

        // Compare the two hashes
        return MessageDigest.isEqual(savedHash, enteredHash);
    }

    // Compare entered password with password confirm
    public static boolean passwordsMatch(String password, String passwordConfirm)
    {
        return password.equals(passwordConfirm);
    }
}
