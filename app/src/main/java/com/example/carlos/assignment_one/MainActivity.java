package com.example.carlos.assignment_one;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements ConfirmDialog.DialogListener {

    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private CGFragmentPagerAdapter myFragmentPagerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getSupportActionBar().hide();
        Log.d("activity","onCreat");
        setContentView(R.layout.activity_main);

        //when the main activity start, display the login in screen first
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);

        CGInitViews();
    }


    //initialize the tablayout and fragments in it
    private void CGInitViews() {
        ArrayList<Fragment> fragments = new ArrayList<>();
        fragments.add(new PlayFragment());
        fragments.add(new Fragment1());//these 3 now do not have any function now
        fragments.add(new Fragment1());
        fragments.add(new PrefsFragment());

        //bind viewPager to pagerAdapater
        mViewPager= (ViewPager) findViewById(R.id.viewPager);
        myFragmentPagerAdapter = new CGFragmentPagerAdapter(getFragmentManager(),fragments);
        mViewPager.setAdapter(myFragmentPagerAdapter);

        //bind tablayout to viewpager
        mTabLayout = (TabLayout) findViewById(R.id.tabLayout);
        mTabLayout.setupWithViewPager(mViewPager);



    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("MainActivity", "--------onActivityResult---MainActivity----");

    }


    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        Log.d("MAIN", "positive clicked");
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        Log.d("MAIN", "negative clicked");
    }
}
