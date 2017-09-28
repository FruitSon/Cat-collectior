package com.example.carlos.assignment_one;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;


import android.graphics.Bitmap;
import android.hardware.Camera;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.Toast;

import com.soundcloud.android.crop.Crop;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.app.Activity.RESULT_OK;


public class Fragment_Settings extends android.support.v4.app.Fragment {

    public static final int REQUEST_CODE_TAKE_FROM_CAMERA = 0;
    //public static final int REQUEST_CODE_CROP_PHOTO = 2;

    private static final String IMAGE_UNSPECIFIED = "image/*";
    private static final String URI_INSTANCE_STATE_KEY = "saved_uri";

    //private OnFragmentInteractionListener mListener;
    Bitmap bitmap;
    View view;
    ImageView btn;
    Button longButton;
    Button saveButton;
    EditText etCName;
    EditText etFName;
    EditText etPW;


    public void onClickImageButton(){
        ((MainActivity)getActivity()).onClickImageButtonSetting();
    }

    public void onClickLongButton(){
        if(longButton.getText()=="Clear"){
            etCName.setText("");
            etFName.setText("");
            etPW.setText("");
        }
    }
    public void onClickSaveButton(){
        if(etCName.getText().length()>0&&etFName.getText().length()>0&&etPW.getText().length()>0){
            //save
            //the judgement above is not definite
            saveData();
        }else{
            String info="";
            if(etCName.getText().length()<1){
                info+="The character Name should has at least 1 character.";
            }
            if(etFName.getText().length()<1){
                info+="The full Name should has at least 1 character.";
            }
            if(etPW.getText().length()<1){
                info+="The password should has at least 1 character.";
            }
            Toast.makeText(getActivity(), info, Toast.LENGTH_LONG).show();
        }
    }
    public static String SHARED_PREF = "my_sharedpref";
    public static String INTERNAL_FILE = "internal-file";
    private void saveData(){
        SharedPreferences sp = getActivity().getSharedPreferences(SHARED_PREF, 0);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("cName", etCName.getText().toString());
        editor.putString("fName", etFName.getText().toString());
        editor.putString("pW", etPW.getText().toString());

        btn.buildDrawingCache(true);
        btn.buildDrawingCache();
        if(btn.getDrawingCache()!=null) {
            ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
            btn.getDrawingCache().compress(Bitmap.CompressFormat.JPEG, 100, baos2);
            String imageBase64 = Base64.encodeToString(baos2.toByteArray(), Base64.DEFAULT);
            editor.putString("productImg", imageBase64);
            Log.d("saveData","ImageDataSaved!!!!!");
        }

        //editor.commit();
        editor.apply();
    }
    private  void loadData(){
        SharedPreferences sp = getActivity().getSharedPreferences(SHARED_PREF, 0);
        etCName.setText(sp.getString("cName",""));
        etFName.setText(sp.getString("fName",""));
        etPW.setText(sp.getString("pW",""));
        if(sp.getString("productImg","")!=null) {
            Log.d("saveData","ImageDataLoaded!!!!!");
            byte[] imagByte = Base64.decode(sp.getString("productImg",""), Base64.DEFAULT);
            ByteArrayInputStream bais2 = new ByteArrayInputStream(imagByte);
            btn.setImageDrawable(Drawable.createFromStream(bais2,  "imagByte"));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }


    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        //---Inflate the layout for this fragment---
        Log.d("Fragment Settings", "onCreateView");
        view= inflater.inflate(R.layout.fragment_fragment__settings, container, false);
        btn=view.findViewById(R.id.imageButton);
        btn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                onClickImageButton();
            }
        });

        longButton = view.findViewById(R.id.buttonTop);
        longButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                onClickLongButton();
            }
        });


        TextWatcher mTextW = new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(etCName.getText().length()<1&&etFName.getText().length()<1&&etPW.getText().length()<1){
                    if(longButton.getText()!="I already have an account")
                        longButton.setText("I already have an account");
                }else{
                    if(longButton.getText()!="Clear")
                        longButton.setText("Clear");
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };
        etCName = view.findViewById(R.id.editTextCName);
        etCName.addTextChangedListener(mTextW);
        etFName = view.findViewById(R.id.editTextFName);
        etFName.addTextChangedListener(mTextW);
        etPW = view.findViewById(R.id.editTextPW);
        etPW.addTextChangedListener(mTextW);

        saveButton=view.findViewById(R.id.buttonSave);
        saveButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                onClickSaveButton();
            }
        });

        loadData();
        return view;
    }

    public Fragment_Settings() {
        super();
        Log.d("Frag", "Fragment Settings constructor");
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.d("Fragment Settings", "onAttach");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("Fragment Settings", "onCreate");
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d("Fragment Settings", "onActivityCreated");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("Fragment Settings", "onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("Fragment Settings", "onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("Fragment Settings", "onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("Fragment Settings", "onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d("Fragment Settings", "onDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("Fragment Settings", "onDestroy");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d("Fragment Settings", "onDetach");
    }





    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    /*public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
    */
}
