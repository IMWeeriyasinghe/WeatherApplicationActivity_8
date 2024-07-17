package com.example.myapplicationactivity_8;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomListAdapter extends ArrayAdapter<String> {
    private final Activity context;
    private final String[] item_date;
    private final String[] item_temp;
    private final Integer[] icons;

    public CustomListAdapter(Activity context, String[] item_date, String[] item_temp, Integer[] icons) {
        super(context, R.layout.my_list, item_date);
        this.context = context;
        this.item_date = item_date;
        this.item_temp = item_temp;
        this.icons = icons;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.my_list, null, true);
        TextView txtDate = rowView.findViewById(R.id.txt_date);
        TextView txtTemp = rowView.findViewById(R.id.txt_temp);
        ImageView imgIcon = rowView.findViewById(R.id.icon);

        txtDate.setText(item_date[position]);
        txtTemp.setText(item_temp[position] + " ℃");
        imgIcon.setImageResource(icons[position]);

        return rowView;
    }
}
