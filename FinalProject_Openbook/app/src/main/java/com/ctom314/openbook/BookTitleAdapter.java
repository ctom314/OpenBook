package com.ctom314.openbook;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;

import java.util.List;

// =================================================================================================
// Custom adapter class for the book title spinner.
// Changes the font and shortens the text if it's too long.
// =================================================================================================

public class BookTitleAdapter extends ArrayAdapter<String>
{
    private final int maxWidth;

    /**
     * Constructor
     * @param context The context
     * @param resource The resource
     * @param objects The list of objects
     * @param maxWidth The maximum width of the text
     */
    public BookTitleAdapter(Context context, int resource, List<String> objects, int maxWidth)
    {
        super(context, resource, objects);
        this.maxWidth = maxWidth;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent)
    {
        View view = super.getView(position, convertView, parent);
        TextView tv = view.findViewById(R.id.tv_spinnerItem);

        // Set TV font
        tv.setTypeface(ResourcesCompat.getFont(getContext(), R.font.archivo));

        // Shorten the text if it's too long
        shortenText(tv);

        return view;
    }

    @Override
    public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent)
    {
        View view = super.getDropDownView(position, convertView, parent);
        TextView tv = view.findViewById(R.id.tv_spinnerItem);

        // Set TV font
        tv.setTypeface(ResourcesCompat.getFont(getContext(), R.font.archivo));

        return view;
    }

    /**
     * Shortens the text if it's too long
     * @param tv The text view
     */
    private void shortenText(TextView tv)
    {
        if (tv != null)
        {
            CharSequence text = tv.getText();
            CharSequence shortTxt = TextUtils.ellipsize(text, tv.getPaint(),
                    maxWidth, TextUtils.TruncateAt.END);
            tv.setText(shortTxt);
        }
    }
}
