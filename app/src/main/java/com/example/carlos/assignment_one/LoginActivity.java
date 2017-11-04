package com.example.carlos.assignment_one;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Carlos on 17/10/3.
 */


public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;

    public EditText _usernameText;
    public EditText _passwordText;
    public Button _loginButton;
    public TextView _signupLink;

    //public SharedPreferences sp;
    private static final String checkProfileUrl = "http://cs65.cs.dartmouth.edu/profile.pl";

    private String password;
    private String username;

    //onCreate we bind the view and set up the button's click listener
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        checkPermissions();

        _usernameText = findViewById(R.id.input_username);
        _passwordText = findViewById(R.id.input_password);
        _loginButton = findViewById(R.id.btn_login);
        _signupLink = findViewById(R.id.link_signup);

        _loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
            }
        });

    }

    //check if we get the camera and storage permission
    private void checkPermissions() {
        if(Build.VERSION.SDK_INT < 23)
            return;

        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 0);
        }
    }
    //ask for permission
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            //
        }else if (grantResults[0] == PackageManager.PERMISSION_DENIED || grantResults[1] == PackageManager.PERMISSION_DENIED){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)||shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    //Show an explanation to the user *asynchronously*
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("This permission is important for the app.")
                            .setTitle("Important permission required");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 0);
                            }

                        }
                    });
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 0);
                }else{
                    //Never ask again and handle your app without permission.
                }
            }
        }
    }

    //after clicking login button, we check the name and password here and display a loading dialog
    public void login() {
        Log.d(TAG, "Login");
        //check if the input(name, password) are valid, not checking if they have already signed up
        if (!validate()) {
            onLoginFailed("");
            return;
        }
        //set login button to false, preventing user's multiple clicking
        _loginButton.setEnabled(false);
        //display a dialog to tell user to wait
        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this); //R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();
        //get the username and password
        username = _usernameText.getText().toString();
        password = _passwordText.getText().toString();

        //check if the username and password are in the server
        String url=checkProfileUrl+"?name="+URLEncoder.encode(username)+"&password="+URLEncoder.encode(password);
        //+URLEncoder.encode(password);
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        //mTxtDisplay.setText("Response: " + response.toString());
                        try {
                            Log.d("NAME Available check???",response.toString());
                            progressDialog.dismiss();
                            if(!response.has("error")){
                                onLoginSuccess(response);

                            }else{
                                onLoginFailed(response.getString("error"));
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

        MyVolleySingleton.getInstance(this).addToRequestQueue(jsObjRequest);
    }

    //if the user's sign up successfully, go to the main activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {
                this.finish();
            }
        }
    }
    //do not allow go back button
    @Override
    public void onBackPressed() {
        // disable going back to the MainActivity
        moveTaskToBack(true);
    }
    //if login in successfully, set the user's profile to local storage and go to the main activity
    public void onLoginSuccess(JSONObject response) {
        _loginButton.setEnabled(true);
        Log.d("response when login",response.toString());
        SharedPreferences sp = getSharedPreferences(GlobalValue.SHARED_PREF, 0);
        SharedPreferences.Editor editor = sp.edit();
        try {
            editor.putString("cName", username);
            editor.putString("pW", URLEncoder.encode(password));
            Log.d("password put into sp",URLEncoder.encode(password));
            if(response.getString("full_name")!=null){
                editor.putString("fName", response.getString("full_name"));
            }
            if(response.getString("link")!=null) {
                editor.putString("link", response.getString("link"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        editor.apply();

        finish();
    }
    //if login failed, stay on this screen
    public void onLoginFailed(String error) {
        if(!error.equals(""))
            Toast.makeText(getBaseContext(), error, Toast.LENGTH_LONG).show();

        _loginButton.setEnabled(true);
    }
    // check if the name and password is not empty, otherwise display error message
    public boolean validate() {
        boolean valid = true;

        String username = _usernameText.getText().toString();
        String password = _passwordText.getText().toString();

        if (username.isEmpty()) {
            _usernameText.setError("need to enter a user name");
            valid = false;
        } else {
            _usernameText.setError(null);
        }

        if (password.isEmpty()) {
            _passwordText.setError("need to enter a password");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }
}
