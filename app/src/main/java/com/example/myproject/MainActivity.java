package com.example.myproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
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
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    ArrayList<Match> matches;
    MatchAdapter adapter;
    ProgressBar progress;
    Spinner spinner;
    EditText team;
    EditText tourn;
    Button search;
    TextView emptyView;
    SharedPreferences preferences;
    SharedPreferences.Editor pref_edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.list);
        matches = new ArrayList<Match>();
        progress = findViewById(R.id.progress);
        spinner = findViewById(R.id.query);
        team = findViewById(R.id.team);
        tourn = findViewById(R.id.tour);
        search = findViewById(R.id.search);
        emptyView = findViewById(R.id.empty_view);
        preferences = this.getPreferences(Context.MODE_PRIVATE);

        ArrayAdapter<CharSequence> spinAdapter = ArrayAdapter.createFromResource(this, R.array.quantity, android.R.layout.simple_spinner_item);
        spinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinAdapter);

        ArrayList<String> quantity = new ArrayList(Arrays.asList(getResources().getStringArray(R.array.quantity)));

        if(preferences.contains("Results")) {
            spinner.setSelection(quantity.indexOf(preferences.getString("Results", "Select")));
            team.setText(preferences.getString("Team", ""));
            tourn.setText(preferences.getString("Tourn", ""));
        }

        adapter = new MatchAdapter(getBaseContext(), R.layout.list_item, matches);
        listView.setAdapter(adapter);

        listView.setEmptyView(emptyView);

        progress.setVisibility(View.GONE);
        emptyView.setVisibility(View.GONE);


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

            pref_edit = preferences.edit();

            pref_edit.putString("Results", selected);
            pref_edit.putString("Team", arg1);
            pref_edit.putString("Tourn", arg2);

            pref_edit.apply();

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
            emptyView.setVisibility(View.GONE);

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

            if(!isNetworkConnected()) {
                emptyView.setText(R.string.no_internet);
            }
            else {
                emptyView.setText(R.string.empty_list);
            }

            ImageLoad image = new ImageLoad();

            image.execute();

        }
    }

    private class ImageLoad extends AsyncTask<Void, Void, Void> {
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

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }


}