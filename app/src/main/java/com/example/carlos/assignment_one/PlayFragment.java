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
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;

public class PlayFragment extends Fragment {

    private static final int REQUEST_MAP = 0;

    private static final String resetUrl = "http://cs65.cs.dartmouth.edu/resetlist.pl";


    private View view;
    private TextView welcomeName;
    private TextView welcomeCat;
    private Button goPlayButton;
    private Button resetButton;

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
        resetButton = view.findViewById(R.id.ResetButton);

        SharedPreferences sp = getActivity().getSharedPreferences(GlobalValue.SHARED_PREF, 0);
        welcomeName.setText("Hi, "+sp.getString("fName","You")+"!");
        welcomeCat.setText("Now you have "+sp.getInt("score",0)+" cats");

        goPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playButtonClick();
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                resetButtonClick();
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


    @Override
    public void onStart(){
        super.onStart();
    }

    //get called when click the play button
    private void playButtonClick(){
        // Start the Map activity
        Intent intent = new Intent(getActivity(),MapActivity.class);
        startActivityForResult(intent, REQUEST_MAP);
    }

    //get called when click the reset cat list button
    private void resetButtonClick(){
        SharedPreferences sp = getActivity().getSharedPreferences(GlobalValue.SHARED_PREF, 0);
        String username = sp.getString("cName", "");
        String password = sp.getString("pW", "");
        //reset the cat
        String url=resetUrl+"?name="+ URLEncoder.encode(username)+"&password="+URLEncoder.encode(password);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        Log.d("Reset the cat list", response.toString());

                        if (!response.has("error")) {

                        } else {
                            Toast.makeText(getActivity().getApplicationContext(), "Reset cat list succeed", Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        Log.d("Reset the cat list","Error");
                    }
                });

        MyVolleySingleton.getInstance(getActivity()).addToRequestQueue(jsObjRequest);
    }






}
