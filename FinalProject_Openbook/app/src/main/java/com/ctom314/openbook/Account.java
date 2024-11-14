package com.ctom314.openbook;

public class Account
{
    private String name;
    private String email;
    private String username;
    private byte[] password_hash;
    private byte[] password_salt;

    /**
     * Constructor
     * @param n Name
     * @param e Email
     * @param u Username
     * @param ph Password hash
     * @param ps Password salt
     */
    public Account(String n, String e, String u, byte[] ph, byte[] ps)
    {
        name = n;
        email = e;
        username = u;
        password_hash = ph;
        password_salt = ps;
    }

    // Getters
    public String getName()
    {
        return name;
    }

    public String getEmail()
    {
        return email;
    }

    public String getUsername()
    {
        return username;
    }

    public byte[] getPasswordHash()
    {
        return password_hash;
    }

    public byte[] getPasswordSalt()
    {
        return password_salt;
    }

    // Setters
    public void setName(String n)
    {
        name = n;
    }

    public void setEmail(String e)
    {
        email = e;
    }

    public void setUsername(String u)
    {
        username = u;
    }

    public void setPasswordHash(byte[] ph)
    {
        password_hash = ph;
    }

    public void setPasswordSalt(byte[] ps)
    {
        password_salt = ps;
    }
}
