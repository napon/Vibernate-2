package com.napontaratan.vibernate.tutorials;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.napontaratan.vibernate.R;

/**
 * Created by napontaratan on 15-05-18.
 */
public class Tutorial5 extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.tutorial_layout,container,false);
        ((TextView) v.findViewById(R.id.tutorialTextView)).setText(getResources().getString(R.string.tutorial_instruction_5));
        ((ImageView) v.findViewById(R.id.tutorialImageView)).setImageDrawable(getResources().getDrawable(R.drawable.week_view_tab_dimmed));
        return v;
    }
}
