package com.example.highlightio;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

public class DetailsFragment extends Fragment {

    ImageView img;
    TextView title;
    TextView tourn;
    TextView URL;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_details, container, false);

        img = view.findViewById(R.id.image);
        title = view.findViewById(R.id.title);
        tourn = view.findViewById(R.id.tourn);
        URL = view.findViewById(R.id.url);

        // Inflate the layout for this fragment
        assert getArguments() != null;
        Match match = DetailsFragmentArgs.fromBundle(getArguments()).getMatch();

        title.setText(match.getTitle());
        tourn.setText(match.getTourney());
        URL.setText(match.getURL());
        img.setImageBitmap(match.getThumb());

        URL.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(match.getURL()))));

        return view;
    }
}