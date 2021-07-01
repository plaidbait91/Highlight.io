package com.example.myproject;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;

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
    Spinner spinner;
    EditText team;
    EditText tourn;
    Button search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.list);
        matches = new ArrayList<Match>();
        progress = (ProgressBar) findViewById(R.id.progress);
        spinner = (Spinner) findViewById(R.id.query);
        team = (EditText) findViewById(R.id.team);
        tourn = (EditText) findViewById(R.id.tour);
        search = (Button) findViewById(R.id.search);

        ArrayAdapter<CharSequence> spinAdapter = ArrayAdapter.createFromResource(this, R.array.quantity, android.R.layout.simple_spinner_item);
        spinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinAdapter);

        adapter = new MatchAdapter(getBaseContext(), R.layout.list_item, matches);
        listView.setAdapter(adapter);
        progress.setVisibility(View.GONE);

        search.setOnClickListener(v -> {
            String selected = spinner.getSelectedItem().toString();
            String arg1 = team.getText().toString();
            String arg2 = tourn.getText().toString();
            Start start = new Start();

            if(!selected.equals("Select")) {

                if (!selected.equals("All")) {
                    start.execute(selected, arg1, arg2);
                } else start.execute("1000", arg1, arg2);
            }
        });

        listView.setOnItemClickListener((parent, view, position, id) -> {
            Match clicked = matches.get(position);
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(clicked.getURL())));
        });


    }

    private class Start extends AsyncTask<String, Void, ArrayList<Match>> {

        @Override
        protected void onPreExecute() {
            adapter.clear();
            progress.setVisibility(View.VISIBLE);
        }

        @Override
        protected ArrayList<Match> doInBackground(String... strings) {
            int number = Integer.parseInt(strings[0]);
            return Query.getMatches(number, strings[1], strings[2]);
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