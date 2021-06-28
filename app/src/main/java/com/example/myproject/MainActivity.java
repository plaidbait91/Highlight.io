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
import android.widget.Button;
import android.widget.EditText;
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
    EditText field;
    Button load;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.list);
        matches = new ArrayList<Match>();
        progress = (ProgressBar) findViewById(R.id.progress);
        field = (EditText) findViewById(R.id.num);
        load = (Button) findViewById(R.id.start);

        adapter = new MatchAdapter(getBaseContext(), R.layout.list_item, matches);
        listView.setAdapter(adapter);
        progress.setVisibility(View.GONE);
        
        load.setOnClickListener(v -> {
            int query = 10;

            if(field.getText().toString().length() != 0)
                query = Integer.parseInt(field.getText().toString());

            Start start = new Start();
            start.execute(query);
        });

        listView.setOnItemClickListener((parent, view, position, id) -> {
            Match clicked = matches.get(position);
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(clicked.getURL())));
        });


    }

    private class Start extends AsyncTask<Integer, Void, ArrayList<Match>> {

        @Override
        protected void onPreExecute() {
            adapter.clear();
            progress.setVisibility(View.VISIBLE);
        }

        @Override
        protected ArrayList<Match> doInBackground(Integer... integers) {
            return Query.getMatches(integers[0]);
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