package com.example.carlos.assignment_one;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    private static final int REQUEST_PET = 0;
    private GoogleMap mMap;

    private ViewSwitcher viewSwitcher;
    private Button petButton;
    private ImageView catImg;
    private TextView catText;
    Animation slide_in_left, slide_out_right;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //check get location permission
        checkPermissions();


        viewSwitcher = (ViewSwitcher) findViewById(R.id.viewSwitcher);

        slide_in_left = AnimationUtils.loadAnimation(this,
                android.R.anim.slide_in_left);
        slide_out_right = AnimationUtils.loadAnimation(this,
                android.R.anim.slide_out_right);

        viewSwitcher.setInAnimation(slide_in_left);
        viewSwitcher.setOutAnimation(slide_out_right);

        catImg = findViewById(R.id.catImg);
        catText = findViewById(R.id.catText);
        petButton = findViewById(R.id.patButton);

        petButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                petButtonClick();
            }
        });

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near the Dartmouth Green.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    boolean trial=true;
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Hanover and move the camera
        double x = Double.parseDouble( getResources().getString(R.string.theGreen_x) );
        double y = Double.parseDouble( getResources().getString(R.string.theGreen_y) );

        LatLng hanover = new LatLng( x, y );
        Log.d("Coords", " " + x + " " + y );

        // Add a marker and move the camera
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        mMap.addMarker(new MarkerOptions().position(hanover).title("Marker in Hanover"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(hanover));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(18f));

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng p0) {
                if( p0 != null ) {
                    Log.d("Map", p0.toString());
                    mMap.addMarker(new MarkerOptions().position(p0).title(p0.toString()));
                }
                if(trial) {
                    viewSwitcher.showNext();
                    trial=false;
                }else{
                    viewSwitcher.showPrevious();
                    trial=true;
                }

            }
        });
    }

    @Override
    public void onLocationChanged(Location location){

    }

    @Override
    public void onProviderDisabled(String provider){
        // required for interface, not used
    }

    @Override
    public void onProviderEnabled (String provider){
        // required for interface, not used
    }

    @Override
    public void onStatusChanged (String provider, int status, Bundle extras){
        // required for interface, not used
    }

    //check if we get the location permission
    private void checkPermissions() {
        if(Build.VERSION.SDK_INT < 23)
            return;

        if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
        }
    }
    //ask for permission
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //
        }else if (grantResults[0] == PackageManager.PERMISSION_DENIED){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_COARSE_LOCATION)) {
                    //Show an explanation to the user *asynchronously*
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("This permission is important for the app.")
                            .setTitle("Important permission required");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
                            }

                        }
                    });
                    requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
                }else{
                    //Never ask again and handle your app without permission.
                }
            }
        }
    }

    //get called when click the pet button
    private void petButtonClick(){
        Intent _intent = new Intent(this,PetSuccessActivity.class);
        startActivityForResult(_intent,REQUEST_PET);
    }

}