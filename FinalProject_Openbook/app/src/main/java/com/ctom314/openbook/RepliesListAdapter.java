package com.ctom314.openbook;

import android.content.Context;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class RepliesListAdapter extends BaseAdapter
{
    Context context;
    ArrayList<Comment> comments;
    DBUtils dbUtils;

    // Constructor
    public RepliesListAdapter(Context c, ArrayList<Comment> r, DBUtils db)
    {
        context = c;
        comments = r;
        dbUtils = db;
    }

    @Override
    public int getCount()
    {
        return comments.size();
    }

    @Override
    public Object getItem(int i)
    {
        return comments.get(i);
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
            view = mInflater.inflate(R.layout.reply_cell, null);
        }

        // Find GUI elements
        TextView username = view.findViewById(R.id.tv_v_rc_username);
        TextView timestamp = view.findViewById(R.id.tv_v_rc_timestamp);
        TextView content = view.findViewById(R.id.tv_v_rc_content);
        ImageView opCrown = view.findViewById(R.id.iv_v_rc_opCrown);

        // Get comment object
        Comment comment = comments.get(i);

        // Get time since reply using timestamp
        String timeSince = Utilities.getTimeSincePost(comment.getTimestamp());

        // Set GUI elements
        username.setText(comment.getUsername());
        timestamp.setText(timeSince);
        content.setText(comment.getContent());

        // Show OP crown if user is OP (Original Poster)
        String postUsername = dbUtils.getPost(comment.getPostId()).getUsername();
        if (username.getText().toString().equals(postUsername))
        {
            opCrown.setVisibility(View.VISIBLE);
        }
        else
        {
            opCrown.setVisibility(View.INVISIBLE);
        }

        return view;
    }
}
