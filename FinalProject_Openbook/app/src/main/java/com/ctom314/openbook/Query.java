package com.ctom314.openbook;

public class Query
{
    // Vars
    private String title;
    private String author;

    /**
     * Constructor
     * @param t The title of the book
     * @param a The author of the book
     */
    public Query(String t, String a)
    {
        title = t;
        author = a;
    }

    // Getters
    public String getTitle()
    {
        return title;
    }

    public String getAuthor()
    {
        return author;
    }

    // Setters
    public void setTitle(String t)
    {
        title = t;
    }

    public void setAuthor(String a)
    {
        author = a;
    }
}
