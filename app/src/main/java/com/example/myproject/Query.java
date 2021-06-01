package com.example.myproject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class Query {

    private Query() {

    }

    private static final String queryURL = "https://www.scorebat.com/video-api/v1/";

    public static ArrayList<Match> getMatches() {
        URL url = null;

        try {
            url = new URL(queryURL);
        }

        catch(MalformedURLException e) {
            Log.e("Query", "Something has gone seriously wrong!", e);
        }

        return extract(request(url));
    }

    private static String request(URL url) {

        HttpURLConnection connection = null;
        InputStream stream = null;
        String response = null;

        if(url == null) return null;

        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setReadTimeout(2000);
            connection.setConnectTimeout(5000);
            connection.connect();

            if(connection.getResponseCode() == 200) {
                Log.v("OOOOOOOOOOOOOOOOOO", "got data");
                stream = connection.getInputStream();
                response = read(stream);
                Log.v("OOOOOOOOOOOOOOOOOO", "got response");
            }

            else {
                Log.e("Query", "Error in connection. Response code = " + connection.getResponseCode());
            }
        }

        catch(IOException e) {
            e.printStackTrace();
        }

        finally {
            try {
                if (connection != null) connection.disconnect();
                if (stream != null) stream.close();
            }
            catch(IOException x) {
                x.printStackTrace();
            }
        }

        return response;
    }

    private static ArrayList<Match> extract(String s) {
        ArrayList<Match> list = new ArrayList<Match>();

        if(s == null) return list;

        try {
            JSONArray root = new JSONArray(s);

            for(int i = 0; i < 10; i++) {
                JSONObject match = root.getJSONObject(i);

                String team1 = match.getJSONObject("side1").getString("name");
                String team2 = match.getJSONObject("side2").getString("name");
                String title = team1 + " V/S " + team2;
                String tourn = match.getJSONObject("competition").getString("name");
                String url = match.getString("url");
                String time = match.getString("date");
                String img_src = match.getString("thumbnail");

                list.add(new Match(title, tourn, url, null, time, img_src));
            }
        } catch (JSONException e) {
            Log.v("OOOOOOOOOOOOOOOOOO", "ERROR");
        }
        Log.v("OOOOOOOOOOOOOOOOOO", "got list");
        return list;
    }

    private static String read(InputStream stream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        StringBuilder output = new StringBuilder();

        String line = reader.readLine();

        while(line != null) {
            output.append(line);
            line = reader.readLine();
        }
        return output.toString();
    }


}