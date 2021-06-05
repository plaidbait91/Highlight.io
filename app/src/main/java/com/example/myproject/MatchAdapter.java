package com.example.myproject;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MatchAdapter extends ArrayAdapter<Match> {
    public MatchAdapter(@NonNull Context context, int resource, @NonNull List<Match> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View list_view = convertView;

        if(list_view == null) {
            list_view = LayoutInflater.from(getContext()).
                    inflate(R.layout.list_item, parent, false);
        }

        Match item = getItem(position);
        ImageView img = (ImageView) list_view.findViewById(R.id.thumb);
        TextView title = (TextView) list_view.findViewById(R.id.title);
        TextView tournament = (TextView) list_view.findViewById(R.id.tourn);
        TextView date = (TextView) list_view.findViewById(R.id.date);
        TextView time = (TextView) list_view.findViewById(R.id.time);

        if(item.getThumb() != null) img.setImageBitmap(item.getThumb());
        else img.setImageResource(R.drawable.def);

        title.setText(item.getTitle());
        tournament.setText(item.getTourney());

        String datetime = item.getTime();
        Date d = null;

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ssZ", Locale.getDefault());
        try {
            d = format.parse(datetime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if(d != null) {
            SimpleDateFormat day = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
            SimpleDateFormat clock = new SimpleDateFormat("h:mm a", Locale.getDefault());

            date.setText(day.format(d));
            time.setText(clock.format(d));
        }

        else {
            date.setText("N/A");
            time.setText("N/A");
        }

        return list_view;
    }
}
