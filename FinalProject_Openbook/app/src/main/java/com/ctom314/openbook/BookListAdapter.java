package com.ctom314.openbook;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class BookListAdapter extends BaseAdapter
{
    Context context;
    ArrayList<Book> books;
    DBUtils dbUtils;

    // Constructor
    public BookListAdapter(Context c, ArrayList<Book> b, DBUtils db)
    {
        context = c;
        books = b;
        dbUtils = db;
    }

    @Override
    public int getCount()
    {
        return books.size();
    }

    @Override
    public Object getItem(int i)
    {
        return books.get(i);
    }

    @Override
    public long getItemId(int i)
    {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup)
    {
        if (view == null)
        {
            // Get the LayoutInflater service from the context
            LayoutInflater mInflater = (LayoutInflater)
                    context.getSystemService(MainActivity.LAYOUT_INFLATER_SERVICE);

            // Inflate new view
            view = mInflater.inflate(R.layout.book_cell, null);
        }

        // Find GUI elements
        TextView title = view.findViewById(R.id.tv_v_bcc_title);
        TextView author = view.findViewById(R.id.tv_v_bcc_author);
        TextView year = view.findViewById(R.id.tv_v_bcc_year);

        // Get book object
        Book book = books.get(i);

        // Set GUI elements
        title.setText(book.getTitle());
        author.setText(book.getAuthor());
        year.setText(String.valueOf(book.getYear()));

        return view;
    }
}
