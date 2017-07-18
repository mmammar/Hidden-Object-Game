package com.codepodium.mythings.Adapters;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.codepodium.mythings.R;
import com.codepodium.mythings.model.Scores;

import java.util.ArrayList;

public class MyListViewHighScore extends ArrayAdapter<String> {

    Context context;
    int resource;
    ArrayList<Scores> scores;

    public MyListViewHighScore(Context context, int resource, ArrayList<Scores> scores) {
        super(context, resource);
        this.context = context;
        this.resource = resource;
        this.scores = scores;
    }

    @Override
    public int getCount() {
        if(scores != null)
            return scores.size();
        return 0;
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {

        if(v == null)
            v = View.inflate(context, resource, null);

        TextView tvSerial = (TextView) v.findViewById(R.id.tvHSserialNumber);
        TextView tvPlayer = (TextView) v.findViewById(R.id.tvHSplayerName);
        TextView tvScore = (TextView) v.findViewById(R.id.tvHSscore);

        Scores s = scores.get(position);

        int serial = position + 1;

        tvSerial.setText(serial + "");
        tvPlayer.setText(s.getPlayerName());
        tvScore.setText(s.getScores() + "");

        return v;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
