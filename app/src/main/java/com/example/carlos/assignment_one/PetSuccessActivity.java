package com.example.carlos.assignment_one;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.soundcloud.android.crop.Crop;

import static com.example.carlos.assignment_one.Fragment_Settings.REQUEST_CODE_TAKE_FROM_CAMERA;
import static com.example.carlos.assignment_one.MapActivity.RESULT_AGAIN;
import static com.example.carlos.assignment_one.MapActivity.RESULT_DONE;

/**
 * Created by Carlos on 17/10/15.
 */

public class PetSuccessActivity extends Activity {

    private Button againButton;
    private Button doneButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("Lifecycle", "On Create");
        setContentView(R.layout.activity_petsuccess);

        againButton= findViewById(R.id.AgainButton);
        doneButton = findViewById(R.id.DoneButton);
        againButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //go back to map activity
                Intent data = new Intent();
                setResult(RESULT_AGAIN, data);  // the result is going to be passed to onActivityResult()
                // Actually finish; this closes the activity and restores the calling Activity
                finish();
            }
        });

        doneButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //go back to map activity
                Intent data = new Intent();
                setResult(RESULT_DONE, data);  // the result is going to be passed to onActivityResult()
                // Actually finish; this closes the activity and restores the calling Activity
                finish();
            }
        });
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
