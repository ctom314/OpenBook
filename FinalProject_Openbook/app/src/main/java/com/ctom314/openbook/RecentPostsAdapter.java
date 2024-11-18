package com.ctom314.openbook;

import android.content.Context;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class RecentPostsAdapter extends BaseAdapter
{
    Context context;
    ArrayList<Post> posts;
    DBUtils dbUtils;

    /**
     * Constructor
     * @param c Context of the activity
     * @param p ArrayList of Post objects
     * @param db DBUtils object
     */
    public RecentPostsAdapter(Context c, ArrayList<Post> p, DBUtils db)
    {
        context = c;
        posts = p;
        dbUtils = db;
    }

    @Override
    public int getCount()
    {
        return posts.size();
    }

    @Override
    public Object getItem(int i)
    {
        return posts.get(i);
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
            view = mInflater.inflate(R.layout.recent_post_cell, null);
        }

        // Find GUI elements
        TextView username = view.findViewById(R.id.tv_v_rpc_username);
        TextView timestamp = view.findViewById(R.id.tv_v_rpc_timestamp);
        TextView bookTitle = view.findViewById(R.id.tv_v_rpc_bookTitle);
        TextView content = view.findViewById(R.id.tv_v_rpc_content);

        // Get post object
        Post post = posts.get(i);

        // Shorten title if necessary
        String title = dbUtils.getBook(post.getBookId()).getTitle();
        String titleShort = Utilities.shortenString(bookTitle, title, 600);

        // Shorten content preview
        String contentPrev = Utilities.shortenString(content, post.getContent());

        // Get time since post using timestamp
        String timeSince = Utilities.getTimeSincePost(post.getTimestamp());

        // Set GUI elements
        username.setText(post.getUsername());
        timestamp.setText(timeSince);
        content.setText(contentPrev);

        // Setup book title
        // Make title underline using SpannableString
        SpannableString underlineTitle = new SpannableString(titleShort);
        underlineTitle.setSpan(new UnderlineSpan(), 0, underlineTitle.length(), 0);
        bookTitle.setText(underlineTitle);

        return view;
    }
}
