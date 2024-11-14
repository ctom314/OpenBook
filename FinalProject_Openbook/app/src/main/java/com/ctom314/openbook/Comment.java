package com.ctom314.openbook;

public class Comment
{
    // Vars
    private String username;
    private String timestamp;
    private String content;
    private int postId;

    /**
     * Constructor
     * @param u Username
     * @param t Timestamp
     * @param c Content
     * @param p Post ID
     */
    public Comment(String u, String t, String c, int p)
    {
        username = u;
        timestamp = t;
        content = c;
        postId = p;
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

    public int getPostId()
    {
        return postId;
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

    public void setPostId(int p)
    {
        postId = p;
    }
}
