package com.example.carlos.assignment_one;


import android.app.DialogFragment;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.util.Log;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import android.graphics.Bitmap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import static android.content.Context.INPUT_METHOD_SERVICE;


public class Fragment_Settings extends Fragment  {

    public static final int REQUEST_CODE_TAKE_FROM_CAMERA = 0;

    private static final String IMAGE_UNSPECIFIED = "image/*";
    private static final String URI_INSTANCE_STATE_KEY = "saved_uri";

    //private OnFragmentInteractionListener mListener;
    View view;
    ImageView btn;
    Button longButton;
    Button saveButton;
    EditText etCName;
    EditText etFName;
    EditText etPW;
    InputMethodManager mImm;
    boolean dialog_open;
    boolean pwd_checked;
    String pwd_first_time = " ";
    DialogFragment dialog;
    private ConfirmDialog mDialog;
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
            Toast toast_savedata =  Toast.makeText(getContext(),"Your profile is saved", Toast.LENGTH_LONG);
            toast_savedata.show();
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
        editor.apply();


    }
    private void loadData(){
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
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        //---Inflate the layout for this fragment---
        Log.d("Fragment Settings", "onCreateView");
        view= inflater.inflate(R.layout.fragment_fragment_settings, container, false);
        btn=view.findViewById(R.id.imageButton);
        mImm = (InputMethodManager)getActivity().getSystemService(INPUT_METHOD_SERVICE);

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

        //set pwd confirmation
        TextView.OnEditorActionListener mEditorActionListener = new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if(view!=null)
                        view.clearFocus();
                        hideKeyboard(view);
                    return true;
                }
                return false;
            }
        };

        etCName.setOnEditorActionListener(mEditorActionListener);
        etFName.setOnEditorActionListener(mEditorActionListener);

        //trigger dialog for confirmation when the "done" button in keyboard is clicked
        etPW.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                int input_length = etPW.getText().toString().trim().length();
                if(actionId == EditorInfo.IME_ACTION_DONE && input_length!=0){
                    saveButton.setClickable(false);
                    etPW.clearFocus();
                    return true;
                }else if(actionId == EditorInfo.IME_ACTION_DONE && input_length==0){
                    pwd_first_time = etPW.getText().toString();
                    saveButton.setClickable(false);
                    saveButton.setBackgroundColor(getResources().getColor(R.color.btn_disable));
                    hideKeyboard(view);
                    etPW.clearFocus();
                }
                return false;
            }
        });

        //trigger dialog for confirmation when edit field lost focus
        etPW.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!b && !etPW.getText().toString().equals(pwd_first_time) && etPW.getText().toString().length()!=0){
                    hideKeyboard(view);
                    mDialog = new ConfirmDialog();
                    mDialog.show(getActivity().getFragmentManager(), "ConfirmDialog");
                }
            }
        });

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

    private void hideKeyboard(View view) {
        if (mImm != null) {

            Log.d("close", "keyboard");
            mImm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(savedInstanceState==null){

        }
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


}
