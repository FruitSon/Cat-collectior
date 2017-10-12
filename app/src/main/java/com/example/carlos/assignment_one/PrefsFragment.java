package com.example.carlos.assignment_one;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;


/**
     * Created by RZ on 17/10/10.
 */

public class PrefsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    public  String SETTINGS_NICKNAME_KEY = "reNickname";
    public  String SETTINGS_HOBBY_KEY = "reHobby";
    public  String SETTINGS_PRIVACY_KEY = "settings_privacy";
    public  String SETTINGS_ALERT_KEY = "settings_alert";
    public  String SETTINGS_ALERT_RING_KEY = "settings_alert_v";
    public  String SETTINGS_ALERT_VIBERATE_KEY = "settings_alert_r";
    public  String SETTINGS_ABOUT_KEY = "settings_link";

    //view of the screen
    TextView mName, mNickname;
    ImageView mPortrait;
    Button mLogout;
    Preference pNickname,pAbout,pPassword;
    SwitchPreference pAlert;
    CheckBoxPreference pPrivacy,pAlert_vibrate,pAlert_ring;
    SharedPreferences sharedPreferences;
    SharedPreferences sp;

    Boolean privacy = false, alert = false;
    Boolean alert_vibrate = false, alert_ring = false;
    AudioManager audioManager;
    String charName,link;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        initPreferences();
        sharedPreferences = getPreferenceScreen().getSharedPreferences();
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        audioManager = (AudioManager) getActivity().getSystemService(getActivity().AUDIO_SERVICE);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.settings_userinfo, container,
                false);
    }

    //initialize the view
    @Override
    public void onStart() {
        super.onStart();

        //set profile by the data received
        mName = getActivity().findViewById(R.id.profile_name);
        mNickname = getActivity().findViewById(R.id.profile_nickname);
        mPortrait = getActivity().findViewById(R.id.profile_portrait);
        sp = getActivity().getSharedPreferences(GlobalValue.SHARED_PREF, 0);
        mName.setText(sp.getString("fName","Haven't set"));
        pAbout.setSummary(sp.getString("link", "Haven't set link yet."));
        mNickname.setText(sp.getString("cName", "Null"));
        charName = sp.getString("cName", "");

        if(!sp.getString("productImg","").equals("")) {
            byte[] imagByte = Base64.decode(sp.getString("productImg",""), Base64.DEFAULT);
            ByteArrayInputStream bais2 = new ByteArrayInputStream(imagByte);
            mPortrait.setImageDrawable(Drawable.createFromStream(bais2,  "imagByte"));
        }else{
            mPortrait.setImageResource(R.mipmap.ic_launcher);
        }

        //set call back for profile log out and modify
        mLogout = getActivity().findViewById(R.id.settings_logout);
        mLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //clear data saved locally
                getActivity().getSharedPreferences(GlobalValue.SHARED_PREF, 0).edit().clear().apply();
                sp.edit().clear().apply();

                //log out and jump to the login page
                Intent toLogin = new Intent(getActivity(), LoginActivity.class);
                startActivity(toLogin);
            }
        });
    }

    private void initPreferences() {
        pNickname = findPreference(SETTINGS_NICKNAME_KEY);
        pPassword = findPreference(SETTINGS_HOBBY_KEY);
        pPrivacy = (CheckBoxPreference) findPreference(SETTINGS_PRIVACY_KEY);
        pAlert = (SwitchPreference) findPreference(SETTINGS_ALERT_KEY);
        pAlert_vibrate = (CheckBoxPreference) findPreference(SETTINGS_ALERT_VIBERATE_KEY);
        pAlert_ring = (CheckBoxPreference) findPreference(SETTINGS_ALERT_RING_KEY);
        pAbout = findPreference(SETTINGS_ABOUT_KEY);
    }

    @Override
    //set onSharedPreferenceChanged listener according to the changed preference by key
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {

        if(s.equals(SETTINGS_NICKNAME_KEY)){
            Log.d("cName",sp.getString("cName","")+"sa");
            Log.d("pW",sp.getString("pW","")+"sa");

            String newCharName = sharedPreferences.getString(s,"");
            if(newCharName.trim().length()!=0 && !newCharName.equals(charName)){
                charName = newCharName;
                mName.setText(newCharName);
                sp.edit().putString("fName",charName).apply();
                updateProfile();
            }else{
                mName.setText(charName);
                sharedPreferences.edit().putString(s,charName).apply();
            }

        }
        else if(s.equals(SETTINGS_HOBBY_KEY)){
            Toast.makeText(getActivity(),"You have updated your profile.",
                    Toast.LENGTH_SHORT).show();
        }
        else if(s.equals(SETTINGS_PRIVACY_KEY)){
            privacy = sharedPreferences.getBoolean(s,false);
            Toast.makeText(getActivity(),"The privacy has been set to: "+privacy.toString(),
                    Toast.LENGTH_SHORT).show();
        }
        else if(s.equals(SETTINGS_ALERT_KEY)) {
            alert = sharedPreferences.getBoolean(s,false);
            pAlert_ring.setChecked(alert);
            pAlert_vibrate.setChecked(alert);
            alert_vibrate = alert;
            alert_ring = alert;
            if(alert) audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
            else audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);

            if(alert) Toast.makeText(getActivity(),"You have turn on your alert.",
                    Toast.LENGTH_SHORT).show();
            else Toast.makeText(getActivity(),"You have turn off your alert.",
                    Toast.LENGTH_SHORT).show();
        }
        else if(s.equals(SETTINGS_ALERT_VIBERATE_KEY)){
            alert_vibrate = sharedPreferences.getBoolean(s,false);
            if(alert_vibrate) {
                audioManager.setVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER,
                        AudioManager.VIBRATE_SETTING_ON);
                audioManager.setVibrateSetting(AudioManager.VIBRATE_TYPE_NOTIFICATION,
                        AudioManager.VIBRATE_SETTING_ON);
               Toast.makeText(getActivity(),"You have turn on your vibration alert.",
                       Toast.LENGTH_SHORT).show();
            }else{
                audioManager.setVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER,
                        AudioManager.VIBRATE_SETTING_OFF);
                audioManager.setVibrateSetting(AudioManager.VIBRATE_TYPE_NOTIFICATION,
                        AudioManager.VIBRATE_SETTING_OFF);
                Toast.makeText(getActivity(),"You have turn off your vibration alert.",
                        Toast.LENGTH_SHORT).show();

            }
//            if(alert) updateAlertSettings(alert_ring,alert_vibrate);
        }
        else if(s.equals(SETTINGS_ALERT_RING_KEY)){
            alert_ring = sharedPreferences.getBoolean(s,false);
            if(alert_ring) {
                audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                audioManager.setVibrateSetting(AudioManager.VIBRATE_TYPE_RINGER,
                        AudioManager.VIBRATE_SETTING_OFF);
                audioManager.setVibrateSetting(AudioManager.VIBRATE_TYPE_NOTIFICATION,
                        AudioManager.VIBRATE_SETTING_OFF);
                Toast.makeText(getActivity(),"You have turn on your ring alert.",
                        Toast.LENGTH_SHORT).show();

            }else{
                if(alert_vibrate) audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                else audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                Toast.makeText(getActivity(),"You have turn off your ring alert.",
                        Toast.LENGTH_SHORT).show();
            }
        }
        else if(s.equals(SETTINGS_ABOUT_KEY)){
            pAbout.setSummary(sharedPreferences.getString(SETTINGS_ABOUT_KEY, "Haven't set linklink yet."));
            Toast.makeText(getActivity(),"You have update your profile.",
                    Toast.LENGTH_SHORT).show();
            updateProfile();
        }

    }


    //method used post to upload profile
    public void updateProfile() {
        String url = "http://cs65.cs.dartmouth.edu/profile.pl";
        JSONObject newProfile=new JSONObject();

        try {
            newProfile.put("name",sp.getString("cName",""));
            newProfile.put("password",sp.getString("pW",""));
            newProfile.put("link",sharedPreferences.getString(SETTINGS_ABOUT_KEY,""));
            newProfile.put("fName",sharedPreferences.getString(SETTINGS_NICKNAME_KEY,""));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, url, newProfile, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("response",response.toString());
                        try {
                            if(response.get("status").equals("OK")){
                                Toast.makeText(getActivity(),"Your profile has been updated",
                                        Toast.LENGTH_SHORT).show();
                            }else{
                                Log.d("update","failed");
                                Toast.makeText(getActivity(),"Your profile isn't updated",
                                        Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("error",error.getMessage());
                    }
                });

        MyVolleySingleton.getInstance(getActivity()).addToRequestQueue(jsObjRequest);
    }

    public void updateAlertSettings(boolean alert_ring,boolean alert_vibrate){
        if(!alert_ring && alert_vibrate) audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
        else if(alert_ring && alert_vibrate) audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        else audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        //can't solve the case when only ring is enabled. implemented by another way.
    }
}
