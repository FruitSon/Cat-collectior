package com.example.carlos.assignment_one;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.app.Fragment;
import android.widget.Button;
import android.widget.TextView;

public class PlayFragment extends Fragment {

    private static final int REQUEST_MAP = 0;

    private View view;
    private TextView welcomeName;
    private TextView welcomeCat;
    private Button goPlayButton;

    public PlayFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_play, container, false);
        welcomeName = view.findViewById(R.id.WelcomeTextName);
        welcomeCat = view.findViewById(R.id.WelcomTextCat);
        goPlayButton = view.findViewById(R.id.GoPlayButton);

        SharedPreferences sp = getActivity().getSharedPreferences(GlobalValue.SHARED_PREF, 0);
        welcomeName.setText("Hi, "+sp.getString("fName","You")+"!");
        welcomeCat.setText("Now you have "+sp.getInt("score",0)+" cats");

        goPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playButtonClick();
            }
        });

        return view;
    }

    @Override
    public void onAttach(Context context) {

        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    //get called when click the play button
    private void playButtonClick(){
        // Start the Map activity
        Intent intent = new Intent(getActivity(),MapActivity.class);
        startActivityForResult(intent, REQUEST_MAP);
    }

    @Override
    public void onStart(){
        super.onStart();
    }






}
