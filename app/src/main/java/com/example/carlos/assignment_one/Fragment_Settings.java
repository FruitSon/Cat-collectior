package com.example.carlos.assignment_one;


import android.app.DialogFragment;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.michael.easydialog.EasyDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URLEncoder;
import java.util.HashMap;

import static android.content.Context.INPUT_METHOD_SERVICE;


public class Fragment_Settings extends Fragment {

    public static final int REQUEST_CODE_TAKE_FROM_CAMERA = 0;

    private static final String checkNameUrl = "http://cs65.cs.dartmouth.edu/nametest.pl";
    private static final String saveProfileUrl = "http://cs65.cs.dartmouth.edu/profile.pl";
    private static final String IMAGE_UNSPECIFIED = "image/*";
    private static final String URI_INSTANCE_STATE_KEY = "saved_uri";

    //private OnFragmentInteractionListener mListener;
    //views on the screen
    View view;
    ImageView btn;
    Button longButton;
    Button saveButton;
    EditText etCName;
    EditText etFName;
    EditText etPW;
    CheckedTextView nameAvailable;

    InputMethodManager mImm;
    String pwd_first_time = " ";
    DialogFragment dialog;
    private ConfirmDialog mDialog;
    Button popConfirm ;
    EditText popText ;
    TextView popMatch;
    EasyDialog ed;

    //class memebers to follow if name is available and password confirmed
    private boolean isNameValid=false;
    private boolean isPasswordValid=false;
    //local storage path
    public static String SHARED_PREF = "my_sharedpref";
    public static String INTERNAL_FILE = "internal-file";

    //take the photo
    public void onClickImageButton(){
        ((SignupActivity)getActivity()).onClickImageButtonSetting();
    }
    //after clicking the 'clear' or 'I already have an account' button, calling this function
    public void onClickLongButton(){
        if(longButton.getText()=="Clear"){
            etCName.setText("");
            etFName.setText("");
            etPW.setText("");
            btn.setImageResource(0);
            etCName.setError(null);
            etFName.setError(null);
            etPW.setError(null);
            nameAvailable.setText("Available?");
            nameAvailable.setTextColor(Color.parseColor("#000000"));
            saveButton.setClickable(false);
        }
    }
    //after clicking the save button, check if input is valid and do the save job
    public void onClickSaveButton(){
        if(etCName.getText().length()>0&&isNameValid&&isPasswordValid&&etFName.getText().length()>0&&etPW.getText().length()>0){
            //save
            //the judgement above is not definite
            saveData();
            Toast toast_savedata =  Toast.makeText(getContext(),"Your profile is saved", Toast.LENGTH_LONG);
            toast_savedata.show();

            ((SignupActivity)getActivity()).signupSuccess();
        }else{
            if(etCName.getText().length()<1){
                etCName.setError("The character Name should has at least 1 character.");
            }else if(!isNameValid){
                etCName.setError("The character Name is not available");
            }
            if(etFName.getText().length()<1){
                etFName.setError("The full Name should has at least 1 character.");
            }
            if(etPW.getText().length()<1){
                etPW.setError("The password should has at least 1 character.");
            }else if(!isPasswordValid){
                etPW.setError("The password is not confirmed yet.");
            }
        }
    }
    //save the data on local storage and on the server
    private void saveData(){
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("name", etCName.getText().toString());
        params.put("password", etPW.getText().toString());
        Log.d("pwd into sq, sign up",URLEncoder.encode(etPW.getText().toString()));
        params.put("fName",etFName.getText().toString());
        JsonObjectRequest req = new JsonObjectRequest(saveProfileUrl, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            VolleyLog.v("Response:%n %s", response.toString(4));
                            Log.d("post profile",response.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.e("Error: ", error.getMessage());
                    }
                }
        );
        MyVolleySingleton.getInstance(getActivity()).addToRequestQueue(req);


        SharedPreferences sp = getActivity().getSharedPreferences(SHARED_PREF, 0);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("cName", etCName.getText().toString());
        editor.putString("pW", URLEncoder.encode(etPW.getText().toString()));
        editor.putString("fName", etFName.getText().toString());

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

    //loading the data from local storage
    private void loadData(){
        SharedPreferences sp = getActivity().getSharedPreferences(SHARED_PREF, 0);
        etCName.setText(sp.getString("cName",""));
        etFName.setText(sp.getString("fName",""));
        etPW.setText(sp.getString("pW",""));
        if(!sp.getString("productImg","").equals("")) {
            Log.d("saveData","ImageDataLoaded!!!!!");
            byte[] imagByte = Base64.decode(sp.getString("productImg",""), Base64.DEFAULT);
            ByteArrayInputStream bais2 = new ByteArrayInputStream(imagByte);
            btn.setImageDrawable(Drawable.createFromStream(bais2,  "imagByte"));
        }


        if (sp.getString("productImg","").equals("")&&etCName.getText().length() < 1 && etFName.getText().length() < 1 && etPW.getText().length() < 1) {
            if (longButton.getText() != "I already have an account")
                longButton.setText("I already have an account");
        } else {
            if (longButton.getText() != "Clear")
                longButton.setText("Clear");
        }
    }
    //when the fragment's view is created, do the binding job and set the listener
    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        //---Inflate the layout for this fragment---
        Log.d("Fragment Settings", "onCreateView");
        //get the fragment view
        view = inflater.inflate(R.layout.fragment_fragment_settings, container, false);
        nameAvailable = view.findViewById(R.id.checkAvailable);

        btn = view.findViewById(R.id.imageButton);
        mImm = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickImageButton();
            }
        });

        longButton = view.findViewById(R.id.buttonTop);
        longButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickLongButton();
            }
        });

        //when the 3 EditText input changes, calling the function in the TextWatcher
        TextWatcher mTextW = new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (etCName.getText().length() < 1 && etFName.getText().length() < 1 && etPW.getText().length() < 1) {
                    if (longButton.getText() != "I already have an account")
                        longButton.setText("I already have an account");
                } else {
                    if (longButton.getText() != "Clear")
                        longButton.setText("Clear");
                }
                if(etCName.getText().length()<1){
                    nameAvailable.setText("Available?");
                    nameAvailable.setTextColor(Color.parseColor("#000000"));
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
                Log.d("s", actionId+"");
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (view != null)
                        view.clearFocus();
                    hideKeyboard(view);
                    return true;
                }
                return false;
            }
        };

        etCName.setOnEditorActionListener(mEditorActionListener);
        etFName.setOnEditorActionListener(mEditorActionListener);

        etCName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!b && etCName.getText().toString().length()!=0){
                    String url=checkNameUrl+"?name="+etCName.getText().toString();
                    JsonObjectRequest jsObjRequest = new JsonObjectRequest
                            (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                                @Override
                                public void onResponse(JSONObject response) {
                                    //mTxtDisplay.setText("Response: " + response.toString());
                                    try {
                                        Log.d("NAME Available check???",response.getString("avail"));
                                        String res= response.getString("avail");
                                        if(res.equals("false")){
                                            etCName.setError("The name is not available");
                                            nameAvailable.setText("Available?");
                                            nameAvailable.setTextColor(Color.parseColor("#000000"));
                                            isNameValid=false;
                                        }else if(res.equals("true")){
                                            isNameValid=true;
                                            //message to user is not yet display
                                            nameAvailable.setText("Available~~~~~~~~~~");
                                            nameAvailable.setTextColor(Color.parseColor("#08e98f"));
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    // TODO Auto-generated method stub
                                    Log.d("NAME Available check???","Error");
                                }
                            });

                    // Access the RequestQueue through your singleton class.
                    MyVolleySingleton.getInstance(getActivity()).addToRequestQueue(jsObjRequest);
                }
                if(etCName.getText().toString().length()==0)
                    isNameValid=false;
            }
        });



        //trigger dialog for confirmation when the "done" button in keyboard is clicked
        etPW.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                int input_length = etPW.getText().toString().trim().length();
                if (actionId == EditorInfo.IME_ACTION_DONE && input_length != 0) {
                    saveButton.setClickable(false);
                    etPW.clearFocus();
                    return true;
                } else if (actionId == EditorInfo.IME_ACTION_DONE && input_length == 0) {
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

        //trigger tooltip dialog for confirmation when edit field lost focus
        //implemented with 3rd party library EasyDialog.
        //comment this part and enable the part above for the normal dialog implementation
        etPW.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean b) {
                String s = etPW.getText().toString().trim();
                if (!b && s.length() != 0) {
                    if (!pwd_first_time.equals(s)) {
                        updateButtonStatus(false);
                        View content = getActivity().getLayoutInflater().inflate(R.layout.popup_window, null);
                        ed = new EasyDialog(getActivity())
                                .setLayout(content)
                                .setBackgroundColor(getActivity().getResources().getColor(R.color.light_grey))
                                .setLocationByAttachedView(etPW)
                                .setGravity(EasyDialog.GRAVITY_BOTTOM)
                                .setAnimationTranslationShow(EasyDialog.DIRECTION_Y, 1000, -600, 100, -50, 50, 0)
                                .setAnimationAlphaShow(1000, 0.3f, 1.0f)
                                .setAnimationTranslationDismiss(EasyDialog.DIRECTION_Y, 500, -50, 800)
                                .setAnimationAlphaDismiss(500, 1.0f, 0.0f)
                                .setTouchOutsideDismiss(true)
                                .setMatchParent(false)
                                .setMarginLeftAndRight(24, 24);

                        ed.setOnEasyDialogDismissed(new EasyDialog.OnEasyDialogDismissed() {
                            @Override
                            public void onDismissed() {
                                hideKeyboard(getView());
                            }
                        });


                        updateButtonStatus(false);
                        popConfirm = content.findViewById(R.id.popup_confirm);
                        popText = content.findViewById(R.id.popup_edit);
                        popMatch = content.findViewById(R.id.popup_match);
                        popText.setFocusable(true);
                        popConfirm.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                pwd_first_time = popText.getText().toString().trim();
                                String pwd_second_time = etPW.getText().toString().trim();
                                if (!pwd_first_time.equals(pwd_second_time)) {
                                    popMatch.setText(getResources().getText(R.string.pwd_not_match));
                                    popText.setText("");
                                    updateButtonStatus(false);
                                    isPasswordValid=false;
                                } else {
                                    ed.dismiss();
                                    updateButtonStatus(true);
                                    isPasswordValid=true;
                                }
                            }
                        });
                        ed.show();
                    } else {
                        Toast toast = Toast.makeText(getContext(), "Your password has been checked", Toast.LENGTH_SHORT);
                        toast.show();
                        hideKeyboard(view);
                    }
                }
            }
        });

        saveButton = view.findViewById(R.id.buttonSave);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickSaveButton();
            }
        });
        updateButtonStatus(false);

        //loadData();
        return view;
    }

    private void hideKeyboard(View view) {
        if (mImm != null) {

            Log.d("close", "keyboard");
            mImm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void updateButtonStatus(boolean res){
        if(etCName.getText().length()<1 && etFName.getText().length()<1){
            saveButton.setClickable(true);
            saveButton.setBackgroundColor(getResources().getColor(R.color.btn_disable));
        }else {
            saveButton.setClickable(res);
            saveButton.setBackgroundColor(getResources().getColor(res ? R.color.btn_enable : R.color.btn_disable));
        }

    }
    //set the longbutton's text to "Clear"
    public void setClear(){
        if (longButton.getText() != "Clear")
            longButton.setText("Clear");
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


}
