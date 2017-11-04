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
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.LinkedList;
import java.util.List;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback,
        LocationListener, GoogleMap.OnMarkerClickListener{

    private static final String getCatUrl = "http://cs65.cs.dartmouth.edu/catlist.pl";
    private static final String patCatUrl = "http://cs65.cs.dartmouth.edu/pat.pl";

    private static final int REQUEST_PET = 0;
    public static final int RESULT_AGAIN = 1;
    public static final int RESULT_DONE = 2;
    private GoogleMap mMap;

    private ViewSwitcher viewSwitcher;
    private Button petButton;
    private NetworkImageView catImg;
    private TextView catText;
    Animation slide_in_left, slide_out_right;

    private Marker lastMarker;
    private Marker myselfMarker;
    private LocationManager locationManager;


    private int visible_radius;
    private List<Marker> catMarkers;

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

        SharedPreferences sp = getSharedPreferences(GlobalValue.SHARED_PREF, 0);
        visible_radius = sp.getInt("radius",200);
        Log.d("visible_radius",visible_radius+"");
        catMarkers = new LinkedList<>();
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
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 5f, this);
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
                if(lastMarker!=null) {
                    lastMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.marker_grey));
                    lastMarker = null;
                    viewSwitcher.showPrevious();
                }
            }
        });

        mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                LatLngBounds bounds;
                if(lastMarker!=null && mMap!=null) {
                    bounds = mMap.getProjection().getVisibleRegion().latLngBounds;
                    if(!bounds.contains(lastMarker.getPosition())){
                        lastMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.marker_grey));
                        lastMarker = null;
                        viewSwitcher.showPrevious();
                    }
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

        displayAllTheCats();
        LatLng newPoint = new LatLng( location.getLatitude(), location.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLng(newPoint));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(17f));
        myselfMarker.setPosition(newPoint);
        updateVisiblity();
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
                displaySignleCatInfo(marker.getSnippet());
            }else{
                // do nothing because user click the same marker
            }
        }else{
            viewSwitcher.showNext();
            marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.marker_green));
            lastMarker = marker;
            displaySignleCatInfo(marker.getSnippet());
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
                //coming back, the current cat has definitely been patted
                petButton.setClickable(false);
                petButton.setBackgroundColor(getResources().getColor(R.color.btn_disable));
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
        if(lastMarker!=null) {
            //pat.pl?name=sergey&password=1234&catid=1&lat=74.2523&lng=74.2134
            String catId = lastMarker.getSnippet();
            String myLat = Double.toString(myselfMarker.getPosition().latitude);
            String myLng = Double.toString(myselfMarker.getPosition().longitude);
            SharedPreferences sp = getSharedPreferences(GlobalValue.SHARED_PREF, 0);
            String username = sp.getString("cName", "Null");
            String password = sp.getString("pW", "Null");
            String url = patCatUrl + "?name=" + username + "&password=" + password + "&catid=" +catId+"&lat="+myLat
                    + "&lng="+myLng;

            StringRequest strRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response){
                            Log.d("Pat Cat",response);
                            Gson gson = new Gson();
                            patCatResponse result = gson.fromJson(response, patCatResponse.class);
                            if(result.status.equals("OK")){
                                startSuccessActivity(result.catId);
                            }else{
                                Toast.makeText(getBaseContext(), result.reason, Toast.LENGTH_LONG).show();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error){
                            Log.d("Pat Cat",error.toString());
                        }
                    }
            );
            MyVolleySingleton.getInstance(this).addToRequestQueue(strRequest);

        }
    }
    //start the success activity && update cat info in local memory (catList)
    private void startSuccessActivity(int patCatId){
        //after pat sucessfully, update the cat info to be petted=true
        for(CatInfo cat:catList){
            if(cat.catId==patCatId){
                cat.petted=true;
                break;
            }
        }
        //update the score
        updateScore();
        //start the success activity
        Intent _intent = new Intent(this, PetSuccessActivity.class);
        startActivityForResult(_intent, REQUEST_PET);
    }
    //update the score because of patting the cat successfully
    private void updateScore(){
        SharedPreferences sp = getSharedPreferences(GlobalValue.SHARED_PREF, 0);
        int cur_score = sp.getInt("score",0)+1;
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("score",cur_score);
        editor.apply();
    }


    private void getAllCatsInfo(){
        SharedPreferences sp = getSharedPreferences(GlobalValue.SHARED_PREF, 0);
        String username = sp.getString("cName", "Null");
        String password = sp.getString("pW","Null");
        Log.d("Name and PW is",username+"    "+password);
        //check if the username and password are in the server
        String url=getCatUrl+"?name="+ username+"&password="+password+"&mode=easy";
        Log.d("URL is",url);

        StringRequest strRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response){
                        Log.d("Get all cats Info",response);
                        Gson gson = new Gson();
                        catList = gson.fromJson(response, new TypeToken<List<CatInfo>>(){}.getType());
                        int score = 0;
                        for(CatInfo cat:catList){
                            if(cat.petted) score++;
                        }
                        SharedPreferences sp = getSharedPreferences(GlobalValue.SHARED_PREF, 0);
                        sp.edit().putInt("score",score);
                        sp.edit().apply();
                        displayAllTheCats();
                        updateVisiblity();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error){
                        Log.d("Get all cats Info",error.toString());
                    }
                }
        );


        MyVolleySingleton.getInstance(this).addToRequestQueue(strRequest);

    }

    private void displayAllTheCats(){
        for(CatInfo cat:catList){
            LatLng catPos = new LatLng(cat.lat, cat.lng);
            Marker temp = mMap.addMarker(new MarkerOptions().position(catPos).title(cat.name).snippet(Integer.toString(cat.catId))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_grey)));
            catMarkers.add(temp);
        }
    }

    private void updateVisiblity(){
        for(Marker m: catMarkers){
            if(calculateDistance(myselfMarker.getPosition(),m.getPosition().latitude,m.getPosition().longitude)>visible_radius) {
                m.setVisible(false);
            }
        }
    }

    private void displaySignleCatInfo(String catId){
        Log.d("Cat Info","Choosed Cat id is "+catId);
        int theCatId = Integer.parseInt(catId);
        double distance=0;
        for(CatInfo cat:catList){
            if(cat.catId==theCatId){
                distance= calculateDistance(myselfMarker.getPosition(),cat.lat,cat.lng);
                catText.setText(cat.name+'\n'+String.format( "%.2f", distance )+" meters");
                catImg.setImageUrl(cat.picUrl, MyVolleySingleton.getInstance(this).getImageLoader());
                if(cat.petted) {
                    petButton.setClickable(false);
                    petButton.setBackgroundColor(getResources().getColor(R.color.btn_disable));
                } else {
                    petButton.setClickable(true);
                    petButton.setBackgroundColor(getResources().getColor(R.color.btn_enable_purple));
                }
                break;
            }
        }
    }

    private double calculateDistance(LatLng myself, double catLat, double catLng) {
        float[] results = new float[1];
        Location.distanceBetween(myself.latitude, myself.longitude, catLat, catLng, results);
        return results[0];
    }
}