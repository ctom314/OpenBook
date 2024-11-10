package com.ctom314.openbook;

public class Comment
{
    // Vars
    private String username;
    private String timestamp;
    private String content;

    // Constructor
    public Comment(String u, String t, String c)
    {
        username = u;
        timestamp = t;
        content = c;
    }

    // Getters
    public String getUsername()
    {
        return username;
    }

    public String getTimestamp()
    {
        return timestamp;
    }

    public String getContent()
    {
        return content;
    }

    // Setters
    public void setUsername(String u)
    {
        username = u;
    }

    public void setTimestamp(String t)
    {
        timestamp = t;
    }

    public void setContent(String c)
    {
        content = c;
    }
}
