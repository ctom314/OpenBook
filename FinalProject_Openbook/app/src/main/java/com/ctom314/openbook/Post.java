package com.ctom314.openbook;

public class Post
{
    // Vars
    private int bookId;
    private String username;
    private String timestamp;
    private String content;

    // Constructor
    public Post(int b, String u, String t, String c)
    {
        bookId = b;
        username = u;
        timestamp = t;
        content = c;
    }

    // Getters
    public int getBookId()
    {
        return bookId;
    }

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
    public void setBookId(int b)
    {
        bookId = b;
    }

    public void setUsername(String s)
    {
        username = s;
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
