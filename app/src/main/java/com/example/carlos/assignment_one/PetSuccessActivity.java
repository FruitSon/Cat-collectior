package com.example.carlos.assignment_one;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.soundcloud.android.crop.Crop;

import static com.example.carlos.assignment_one.Fragment_Settings.REQUEST_CODE_TAKE_FROM_CAMERA;

/**
 * Created by Carlos on 17/10/15.
 */

public class PetSuccessActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("Lifecycle", "On Create");
        setContentView(R.layout.activity_petsuccess);

    }


    @Override public void onRestart() {
        super.onRestart();
        Log.d("Lifecycle", "On Restart");
    }

    @Override public void onPause() {
        super.onPause();
        Log.d("Lifecycle", "On Pause");
    }

    @Override public void onStop() {
        super.onStop();
        Log.d("Lifecycle", "On Stop");
    }

    @Override public void onDestroy() {
        super.onDestroy();
        Log.d("Lifecycle", "On Destroy");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

    }

}
