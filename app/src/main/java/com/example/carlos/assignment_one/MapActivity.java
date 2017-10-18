package com.example.carlos.assignment_one;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener, GoogleMap.OnMarkerClickListener {

    private static final String getCatUrl = "http://cs65.cs.dartmouth.edu/catlist.pl";

    private static final int REQUEST_PET = 0;
    public static final int RESULT_AGAIN = 1;
    public static final int RESULT_DONE = 2;
    private GoogleMap mMap;

    private ViewSwitcher viewSwitcher;
    private Button petButton;
    private ImageView catImg;
    private TextView catText;
    Animation slide_in_left, slide_out_right;

    private Marker lastMarker;
    private Marker myselfMarker;
    private LocationManager locationManager;


    private List<CatInfo> catList;

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

        getAllCatsInfo();

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

        //default location
        LatLng hanover = new LatLng( x, y );
        Log.d("Coords", " " + x + " " + y );

        //get last known location
        Location temp=null;
        LatLng loc;
        try{
            if (locationManager==null&&ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5f, this);
            }
            if(locationManager!=null)
                temp=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }catch (SecurityException e){
            Log.d("PERM", "Security Exception getting last known location. Using Hanover.");
        }
        if(temp!=null){
            loc = new LatLng(temp.getLatitude(),temp.getLongitude());
        }else{
            loc=hanover;
        }

        // Add a marker and move the camera
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        myselfMarker = mMap.addMarker(new MarkerOptions().position(hanover).title("Marker in Hanover")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_self)));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(loc));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(18f));
        //!!!!!!!!!!!!!!!!!!!need to change
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng p0) {
                if( p0 != null ) {
                    Log.d("Map", p0.toString());
                    mMap.addMarker(new MarkerOptions().position(p0).title(p0.toString())
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_grey)));
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

        mMap.setOnMarkerClickListener(this);
    }

    @Override
    public void onLocationChanged(Location location){
        Log.d("LOCATION", "CHANGED: " + location.getLatitude() + " " + location.getLongitude());
        Toast.makeText(this, "LOC: " + location.getLatitude() + " " + location.getLongitude(),
                Toast.LENGTH_LONG).show();

        LatLng newPoint = new LatLng( location.getLatitude(), location.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLng(newPoint));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(17f));
        myselfMarker.setPosition(newPoint);

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

    @Override
    public boolean onMarkerClick(final Marker marker) {
        if(marker.equals(myselfMarker))
            return true;
        if(lastMarker!=null) {// now it has a clicked(green) marker
            if(!lastMarker.equals(marker)) {
                lastMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.marker_grey));
                marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.marker_green));
                lastMarker = marker;
            }else{
                // do nothing because user click the same marker
            }
        }else{
            marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.marker_green));
            lastMarker = marker;
        }

        return true;
    }

    //ask for permission
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED && locationManager==null) {
                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5f, this);
            }
        }else if (grantResults[0] == PackageManager.PERMISSION_DENIED){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    //Show an explanation to the user *asynchronously*
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("This permission is important for the app.")
                            .setTitle("Important permission required");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
                            }

                        }
                    });
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
                }else{
                    //Never ask again and handle your app without permission.
                }
            }
        }
    }

    //deal with the result from petSuccessActivity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_PET) {
            if (resultCode == RESULT_AGAIN) {
                //do what????????????
            }else{
                this.finish();
            }
        }
    }


    //------------------------Non-Override  Function---------------------------------
    //check if we get the location permission
    private void checkPermissions() {
        if(Build.VERSION.SDK_INT < 23)
            return;
        /*
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }else{
            //get the location to update
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED && locationManager==null) {
                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5f, this);
            }
        }*/

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }else{
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5f, this);
        }
    }

    //get called when click the pet button
    private void petButtonClick(){
        Intent _intent = new Intent(this,PetSuccessActivity.class);
        startActivityForResult(_intent,REQUEST_PET);
    }


    private void getAllCatsInfo(){
        SharedPreferences sp = getSharedPreferences(GlobalValue.SHARED_PREF, 0);
        String username = sp.getString("cName", "Null");
        String password = sp.getString("pW","Null");
        Log.d("Name and PW is",username+"    "+password);
        //check if the username and password are in the server
        String url=getCatUrl+"?name="+ username+"&password="+password+"&mode=easy";
        Log.d("URL is",url);

        /*
        JsonArrayRequest jsArrayRequest= new JsonArrayRequest(Request.Method.GET, url,null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response){
                        Log.d("Get all cats Info",response.toString());
                        Gson gson = new Gson();
                        catList = gson.fromJson(response.toString(), new TypeToken<List<CatInfo>>(){}.getType());
                        Log.d("Get all cats Info","for a pause");
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error){
                        Log.d("Get all cats Info",error.toString());
                    }
                });*/

        StringRequest strRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response){
                        Log.d("Get all cats Info",response);
                        Gson gson = new Gson();
                        catList = gson.fromJson(response, new TypeToken<List<CatInfo>>(){}.getType());
                        DisplayAllTheCats();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error){
                Log.d("Get all cats Info",error.toString());
            }
        });


        MyVolleySingleton.getInstance(this).addToRequestQueue(strRequest);

    }

    private void DisplayAllTheCats(){
        for(CatInfo cat:catList){
            LatLng catPos = new LatLng( cat.lat, cat.lng );
            mMap.addMarker(new MarkerOptions().position(catPos).title(cat.name).snippet(Integer.toString(cat.catId))
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_grey)));
        }
    }

}