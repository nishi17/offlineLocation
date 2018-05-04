package com.demo.offlinelocation.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.demo.offlinelocation.R;
import com.demo.offlinelocation.fragment.SearchResultFragment;
import com.demo.offlinelocation.service.AlarmReceiver;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static Context context;


    public String[] permission_location;

    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 1;

    private SharedPreferences sharedPreferences;

    private boolean isShowGpsDialog = false;

    private boolean doubleBackToExitPressedOnce = false;

    private DrawerLayout dLayout;
    private ImageView ev_navIcon;

    public String SearchResultFragment;
    private TextView txtTitle;



    public String getSearchResultFragment() {
        return SearchResultFragment;
    }

    public void setSearchResultFragment(String searchResultFragment) {
        SearchResultFragment = searchResultFragment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = MainActivity.this;

        sharedPreferences = getPreferences(Context.MODE_PRIVATE);

        permission_location = new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};


        if (android.os.Build.VERSION.SDK_INT >= 23) {
            //            /*Ask  Permissions here*/
            verifyPermission();

        } else {
            //            /*After Permission Granted*/

            proceedAfterPermission();

        }

        init();


    }

    @Override
    protected void onResume() {
        super.onResume();
        //getdataFromDatabase();


    }


    private void init() {


        dLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        ev_navIcon = (ImageView) findViewById(R.id.iv_navicon);
        ev_navIcon.setOnClickListener(this);


        txtTitle = (TextView) findViewById(R.id.txt_title);

        // txtTitle.setText("Search Activity");

        txtTitle.setText("Dashboard");
    }


    private JSONObject getLocationFormGoogle(double lat, double lng) {

        // aps.googleapis.com/maps/api/place/nearbysearch/json?location=23.0127068,72.5118581&radius=100&type=airport|amusement_park&key=AIzaSyDf4OEkOzbmPIhPweGGzdkJw0KxPApxZ4g
        // String apiRequest = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + placesName; //+ "&ka&sensor=false"
        String latVal = String.valueOf(lat);
        String lngVal = String.valueOf(lng);
        String apiRequest = null;//+ "&ka&sensor=false"
        try {
            apiRequest = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="
                    + URLEncoder.encode(latVal, "UTF-8")
                    + ","
                    + URLEncoder.encode(lngVal, "UTF-8")
                    + "&radius="
                    + URLEncoder.encode("5000", "UTF-8")
                    + "&sensor="
                    + URLEncoder.encode("true", "UTF-8")
                    + "&types="
                    + URLEncoder.encode("food|bar|church|museum|art_gallery", "UTF-8")
                    + "&key="
                    + URLEncoder.encode("AIzaSyDdMnQpqT9pr-k6VhwesT1OBAg_qkvflxU", "UTF-8");
        } catch (UnsupportedEncodingException e) {


        }
        HttpGet httpGet = new HttpGet(apiRequest);
        HttpClient client = new DefaultHttpClient();
        org.apache.http.HttpResponse response;
        StringBuilder stringBuilder = new StringBuilder();

        try {
            response = client.execute(httpGet);
            HttpEntity entity = response.getEntity();
            InputStream stream = entity.getContent();
            int b;
            while ((b = stream.read()) != -1) {
                stringBuilder.append((char) b);
            }
        } catch (ClientProtocolException e) {
        } catch (IOException e) {
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject = new JSONObject(stringBuilder.toString());
        } catch (JSONException e) {

            e.printStackTrace();
        }

        return jsonObject;
    }


    private void verifyPermission() {

        int permissionCheck = ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_COARSE_LOCATION);

        switch (permissionCheck) {

            case PackageManager.PERMISSION_GRANTED:

                sharedPreferences.edit().putBoolean
                        (Manifest.permission.ACCESS_COARSE_LOCATION, true)
                        .commit();

                proceedAfterPermission();

                break;

            case PackageManager.PERMISSION_DENIED:

    /*this if done till dont ask again check box
      not click after that goToFetting call*/

                if (ActivityCompat.shouldShowRequestPermissionRationale
                        ((Activity) context, Manifest.permission.ACCESS_COARSE_LOCATION)) {

                    ActivityCompat.requestPermissions(this, permission_location,
                            MY_PERMISSIONS_REQUEST_LOCATION);

                } else if (sharedPreferences.getBoolean
                        (Manifest.permission.ACCESS_COARSE_LOCATION, false)) {

                    goToSettings("get Location", 300);

                } else {

                    ActivityCompat.requestPermissions(this, permission_location,
                            MY_PERMISSIONS_REQUEST_LOCATION);

                }

                break;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {

            case MY_PERMISSIONS_REQUEST_LOCATION: {

                if (grantResults.length > 0 && grantResults[0]
                        == PackageManager.PERMISSION_GRANTED) {

                    sharedPreferences.edit()
                            .putBoolean(Manifest.permission.ACCESS_COARSE_LOCATION, true)
                            .commit();

                    proceedAfterPermission();

                } else {

                    // permission denied, boo!
                    //Disable the functionality that depends on this permission.

                    if (ActivityCompat.shouldShowRequestPermissionRationale
                            ((Activity) context, Manifest.permission.ACCESS_COARSE_LOCATION)) {

                        ActivityCompat.requestPermissions(this, permission_location,
                                MY_PERMISSIONS_REQUEST_LOCATION);

                    } else {

                        goToSettings("get Location", 300);

                    }
                }

            }
            break;

            default:

                super.onRequestPermissionsResult(requestCode, permissions,
                        grantResults);
        }

    }


    //grant permission of app needs in the phone setting

    private void goToSettings(final String title, final int per) {

        android.support.v7.app.AlertDialog.Builder builder = new
                android.support.v7.app.AlertDialog.Builder(MainActivity.this);

        builder.setTitle("App Permission");

        builder.setMessage("This app needs " + title + " permission.");

        builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();

                Intent intent = new Intent
                        (Settings.ACTION_APPLICATION_DETAILS_SETTINGS);

                Uri uri = Uri.fromParts("package", getPackageName(), null);

                intent.setData(uri);

                startActivityForResult(intent, per);
                //Toast.makeText(getBaseContext(),"Go to Permissions to Grant Storage", Toast.LENGTH_LONG).show();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();

                goToSettings(title, per);

            }
        });

        builder.show();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 300) {

            if (ContextCompat.checkSelfPermission(MainActivity.this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                proceedAfterPermission();

            } else {

                goToSettings("get Location", 300);

            }

        } else if (requestCode == 400) {

            proceedAfterPermission();
        }

    }


    private void proceedAfterPermission() {


        boolean isGpsEnable = getGPSStatus();//manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!isGpsEnable) {

            isShowGpsDialog = false;
            buildAlertMessageNoGps();
        } else {

            Intent alarm = new Intent(this.context, AlarmReceiver.class);
            boolean alarmRunning = (PendingIntent.getBroadcast(this.context, 0, alarm, PendingIntent.FLAG_NO_CREATE) != null);
            if (alarmRunning == false) {
                PendingIntent pendingIntent = PendingIntent.getBroadcast(this.context, 0, alarm, 0);
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), /*10000*/600000, pendingIntent);/* /10 min*/
            }

            isShowGpsDialog = true;
        }
    }

    private boolean getGPSStatus() {
        String allowedLocationProviders =
                Settings.System.getString(getContentResolver(),
                        Settings.System.LOCATION_PROVIDERS_ALLOWED);

        if (allowedLocationProviders == null) {
            allowedLocationProviders = "";
        }

        return allowedLocationProviders.contains(LocationManager.GPS_PROVIDER);
    }

    //display  dailog if location is off
    private void buildAlertMessageNoGps() {


        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        // startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        startActivityForResult(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS), 400);


                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                        isShowGpsDialog = false;
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }


    @Override
    public void onClick(View view) {
        switch (R.id.iv_navicon) {

            case R.id.iv_navicon:
                setNavigationDrawer();
                dLayout.openDrawer(Gravity.LEFT);


                break;
        }
    }


    private void setNavigationDrawer() {

        dLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navView = (NavigationView) findViewById(R.id.navigation);
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

/*                Fragment frag = null; // create a Fragment Object
                int itemId = item.getItemId(); // get selected menu item's id
// check selected menu item's id and replace a Fragment Accordingly
                if (itemId == R.id.it_search) {

                    frag = new SearchFragment();
                } else if (itemId == R.id.it_all_data) {
                    frag = new SearchResultFragment();
                }

                Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
                if (frag != null) {

                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.frame, frag); // replace a Fragment with Frame Layout
                    transaction.addToBackStack(null);
                    transaction.commit(); // commit the changes
                    dLayout.closeDrawers(); // close the all open Drawer Views
                    return true;
                }*/


                int itemId = item.getItemId();
                if (itemId == R.id.it_search) {
                    dLayout.closeDrawers();
                    Intent i = new Intent(MainActivity.this, SearchActivity.class);
                    startActivity(i);

                } else if (itemId == R.id.it_all_data) {
                    dLayout.closeDrawers();
                    Intent i = new Intent(MainActivity.this, SearchResultActivity.class);
                    startActivity(i);

                }


                return false;
            }
        });


    }


    public void insertAddressRefrencce() {

        String my = getSearchResultFragment();

        SearchResultFragment fragmentA = (SearchResultFragment) MainActivity.this.getSupportFragmentManager().findFragmentByTag(my);
        fragmentA.insertAddress();

    }

 /*   @Override
    public void onBackPressed() {
        int count = getFragmentManager().getBackStackEntryCount();

        if (count == 0) {
            super.onBackPressed();
            //additional code
        } else {
            getFragmentManager().popBackStack();
        }
    }*/

    @Override
    public void onBackPressed() {

      /*  int count = getFragmentManager().getBackStackEntryCount();

        if (count == 0) {

            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce=false;
                }
            }, 1000);
            //additional code
        } else {
            getFragmentManager().popBackStack();
        }*/

        if (dLayout.isDrawerOpen(Gravity.LEFT)) {

            dLayout.closeDrawers();
        }

     /*   if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else*/
        if (!doubleBackToExitPressedOnce) {
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please click BACK again to exit.", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        } else {
            super.onBackPressed();
            return;
        }

    }

}
