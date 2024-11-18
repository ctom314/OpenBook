package com.ctom314.openbook;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class PostListAdapter extends BaseAdapter
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
    public PostListAdapter(Context c, ArrayList<Post> p, DBUtils db)
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
            view = mInflater.inflate(R.layout.post_cell, null);
        }

        // Find GUI elements
        TextView username = view.findViewById(R.id.tv_v_pc_username);
        TextView timestamp = view.findViewById(R.id.tv_v_pc_timestamp);
        TextView content = view.findViewById(R.id.tv_v_pc_content);

        // Get post object
        Post post = posts.get(i);

        // Shorten content preview
        String contentPrev = Utilities.shortenString(content, post.getContent());

        // Get time since post using timestamp
        String timeSince = Utilities.getTimeSincePost(post.getTimestamp());

        // Set GUI elements
        username.setText(post.getUsername());
        timestamp.setText(timeSince);
        content.setText(contentPrev);

        return view;
    }
}
