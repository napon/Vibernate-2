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
public class Tutorial2 extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.tutorial_layout,container,false);
        ((TextView) v.findViewById(R.id.tutorialTextView)).setText(getResources().getString(R.string.tutorial_instruction_2));
        ((ImageView) v.findViewById(R.id.tutorialImageView)).setImageDrawable(getResources().getDrawable(R.drawable.create_timer_view));
        return v;
    }
}