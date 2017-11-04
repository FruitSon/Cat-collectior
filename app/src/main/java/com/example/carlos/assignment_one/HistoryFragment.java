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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;

public class HistoryFragment extends Fragment {

    private static final int REQUEST_MAP = 0;

    private View view;
    private List<CatInfo> catList;
    private static final String getCatUrl = "http://cs65.cs.dartmouth.edu/catlist.pl";
    ListView listView;
    HistoryAdapter adapter;
    List<HistoryInfo> listInfo;

    public HistoryFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listInfo = new LinkedList<>();
        catList = new LinkedList<>();
        updateCatList();
        //name, double lng, double lat, int imageId, boolean pette

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_history, container, false);
        listView = view.findViewById(R.id.history_list);
        HistoryAdapter adapter = new HistoryAdapter
                (getActivity(), R.layout.fragment_history_list,listInfo );
        listView.setAdapter(adapter);
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

    //method used post to upload profile
    public void updateCatList(){
        SharedPreferences sp = getActivity().getSharedPreferences(GlobalValue.SHARED_PREF, 0);
        String username = sp.getString("cName", "Null");
        String password = sp.getString("pW","Null");
        String url=getCatUrl+"?name="+ username+"&password="+password+"&mode=easy";

        Log.d("URL is",url);

        StringRequest strRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response){
                        Gson gson = new Gson();
                        catList = gson.fromJson(response, new TypeToken<List<CatInfo>>(){}.getType());
                        for(CatInfo cat:catList){
                            listInfo.add(new HistoryInfo(cat.name,cat.lng,cat.lng,cat.picUrl,cat.petted));
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error){
                Log.d("Get all cats Info",error.toString());
            }
        }
        );
        MyVolleySingleton.getInstance(getActivity()).addToRequestQueue(strRequest);
    }





}
