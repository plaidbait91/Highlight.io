package com.example.myproject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    ArrayList<Match> matches;
    MatchAdapter adapter;
    ProgressBar progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.list);
        matches = new ArrayList<Match>();
        progress = (ProgressBar) findViewById(R.id.progress);
        adapter = new MatchAdapter(getBaseContext(), R.layout.list_item, matches);
        listView.setAdapter(adapter);


        Start start = new Start();
        start.execute();

        listView.setOnItemClickListener((parent, view, position, id) -> {
            Match clicked = matches.get(position);
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(clicked.getURL())));
        });


    }

    private class Start extends AsyncTask<Void, Void, ArrayList<Match>> {

        @Override
        protected ArrayList<Match> doInBackground(Void... voids) {
            return Query.getMatches();
        }

        @Override
        protected void onPostExecute(ArrayList<Match> matches) {
            adapter.clear();

            if(matches != null && matches.size() != 0) adapter.addAll(matches);

            progress.setVisibility(View.GONE);

            Image image = new Image();

            image.execute();

        }
    }

    private class Image extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            HttpURLConnection urlConnection = null;

            for(int i = 0; i < matches.size(); i++) {
                try {
                    URL url = new URL(matches.get(i).getThumbURL());

                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.setConnectTimeout(2000);
                    urlConnection.setReadTimeout(1000);
                    urlConnection.connect();

                    if (urlConnection.getResponseCode() == 200) {
                        matches.get(i).setThumb(BitmapFactory.decodeStream(urlConnection.getInputStream()));
                    } else {
                        matches.get(i).setThumb(null);
                    }
                } catch (IOException e) {
                    matches.get(i).setThumb(null);
                }

                finally {
                    if(urlConnection != null) urlConnection.disconnect();
                }

            }

            return null;

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            adapter.notifyDataSetChanged();
        }
    }


}