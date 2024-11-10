package com.ctom314.openbook;

public class Book
{
    // Vars
    private String title;
    private String author;
    private int year;

    // Constructor
    public Book(String t, String a, int y)
    {
        title = t;
        author = a;
        year = y;
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

    public int getYear()
    {
        return year;
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

    public void setYear(int y)
    {
        year = y;
    }
}
