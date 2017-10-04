package com.example.carlos.assignment_one;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.Toast;

import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.io.FileNotFoundException;

import static com.example.carlos.assignment_one.Fragment_Settings.REQUEST_CODE_TAKE_FROM_CAMERA;


/**
 * Created by Carlos on 17/10/3.
 */

public class SignupActivity extends FragmentActivity {

    public  Fragment_Settings fragmentSetting;
    public Uri mImageCaptureUri;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d("Lifecycle", "On Create");
        setContentView(R.layout.activity_ignup);

        //fragmentSetting=findViewById(R.id.frag);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        //步骤二：用add()方法加上Fragment的对象rightFragment
        fragmentSetting = new Fragment_Settings();
        transaction.replace(android.R.id.content, fragmentSetting);

        //步骤三：调用commit()方法使得FragmentTransaction实例的改变生效
        transaction.commit();

        attachFragments();
    }

    private void attachFragments()
    {

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
        Log.d("Fragment Settings", "--------onActivityResult---MainActivity----");
        if (resultCode != RESULT_OK)
            return;
        Log.d("Fragment Settings", "--------onActivityResult of OK-----MainActivity--");
        switch (requestCode) {
            case REQUEST_CODE_TAKE_FROM_CAMERA:
                // Send image taken from camera for cropping
                beginCrop(mImageCaptureUri);
                break;
            case Crop.REQUEST_CROP:
                // Update image view after image crop
                handleCrop(resultCode, data);

                break;
        }
    }

    public void onClickImageButtonSetting() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Construct temporary image path and name to save the taken
        // photo
        ContentValues values = new ContentValues(1);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
        mImageCaptureUri = this.getContentResolver()
                .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                mImageCaptureUri);
        intent.putExtra("return-data", true);
        try {
            startActivityForResult(intent, REQUEST_CODE_TAKE_FROM_CAMERA);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void beginCrop(Uri source) {
        Uri destination = Uri.fromFile(new File(getCacheDir(), "cropped"));
        Crop.of(source, destination).asSquare().start(this);
    }

    private void handleCrop(int resultCode, Intent result) {
        Log.d("Fragment Settings", "handleing Crop--------");
        if (resultCode == RESULT_OK) {
            if(fragmentSetting!=null) {
                Bitmap bitmap = decodeUriAsBitmap(Crop.getOutput(result));
                fragmentSetting.btn.setImageBitmap(bitmap);
                fragmentSetting.setClear();
                //((Fragment_Settings) fragSetting).btn.refreshDrawableState();
            }
            Log.d("Fragment Settings", "handle Crop Result_Ok!!!!!!!");
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private Bitmap decodeUriAsBitmap(Uri uri){
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        return bitmap;
    }



}


