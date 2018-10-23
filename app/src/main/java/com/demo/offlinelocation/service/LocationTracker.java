package com.demo.offlinelocation.service;

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;

import com.demo.offlinelocation.database.DatabaseHandler;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;

/**
 * Created by Nishi on 4/30/2018.
 */

public class LocationTracker extends Service implements LocationListener {


    // flag for GPS status
    boolean isGPSEnabled = false;

    // flag for network status
    boolean isNetworkEnabled = false;

    // flag for GPS status
    boolean canGetLocation = false;

    Location location; // location
    double latitude; // latitude
    double longitude; // longitude

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 0; // 10 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 0; /*1000 * 60 * 10;*/ // 1 minute 1000 * 60 * 1    900000

    // Declaring a Location Manager
    protected LocationManager locationManager;


    private Handler mHandler = new Handler();
    private Timer mTimer = null;
    long notify_interval = 900000;


    private boolean isRunning;
    private Context context;
    private Thread backgroundThread;
    /*public LocationTracker(Context context) {
        this.mContext = context;
        getLocation();
    }*/

    double myLatitude, myLongitude;

    public LocationTracker() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.context = this;
        this.isRunning = false;
        this.backgroundThread = new Thread(myTask);
        getLocation();
        stopSelf();

    }

    private Runnable myTask = new Runnable() {
        public void run() {
            // Do something here
            getLocation();
            stopSelf();
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (!this.isRunning) {
            this.isRunning = true;
//            this.backgroundThread.start();
        }

      /*  mTimer = new Timer();

        mTimer.schedule(new TimerTaskToGetLocation(), 5, notify_interval);

        mContext = getApplicationContext();*/


      /*  Handler handler = new Handler();
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), "call ", Toast.LENGTH_LONG).show();
                getLocation();
                //performe the deskred task
            }
        }, MIN_TIME_BW_UPDATES);
*/

        return START_STICKY;
    }

  /*  private class TimerTaskToGetLocation extends TimerTask {
        @Override
        public void run() {

            mHandler.post(new Runnable() {
                @Override
                public void run() {

                    Toast.makeText(getApplicationContext(), "call ", Toast.LENGTH_LONG).show();
                    getLocation();

                }
            });

        }
    }*/

    private Boolean RqsLocation(int cid, int lac) {

        Boolean result = false;

        String urlmmap = "http://www.google.com/glm/mmap";

        //   new RetrieveFeedTask().execute(urlmmap);


        try {
            URL url = new URL(urlmmap);
            URLConnection conn = url.openConnection();
            HttpURLConnection httpConn = (HttpURLConnection) conn;
            httpConn.setRequestMethod("POST");
            httpConn.setDoOutput(true);
            httpConn.setDoInput(true);
            httpConn.connect();

            OutputStream outputStream = httpConn.getOutputStream();
            WriteData(outputStream, cid, lac);

            InputStream inputStream = httpConn.getInputStream();
            DataInputStream dataInputStream = new DataInputStream(inputStream);

            dataInputStream.readShort();
            dataInputStream.readByte();
            int code = dataInputStream.readInt();
            if (code == 0) {

                int lat = dataInputStream.readInt();
                myLatitude = ((float) lat / 1000000);
                int longi = dataInputStream.readInt();
                myLongitude = ((float) longi / 1000000);

                result = true;

            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return result;

    }

//    class RetrieveFeedTask extends AsyncTask<String, Void, Boolean> {
//
//        private Exception exception;
//        Boolean result = false;
//
//        protected Boolean doInBackground(String... urls) {
//            try {
//                URL url = new URL(urls[0]);
//                URLConnection conn = url.openConnection();
//                HttpURLConnection httpConn = (HttpURLConnection) conn;
//                httpConn.setRequestMethod("POST");
//                httpConn.setDoOutput(true);
//                httpConn.setDoInput(true);
//                httpConn.connect();
//
//                OutputStream outputStream = httpConn.getOutputStream();
//                WriteData(outputStream, urls[1], urls[2]);
//
//
//                InputStream inputStream = httpConn.getInputStream();
//                DataInputStream dataInputStream = new DataInputStream(inputStream);
//
//                dataInputStream.readShort();
//                dataInputStream.readByte();
//                int code = dataInputStream.readInt();
//                if (code == 0) {
//                    myLatitude = dataInputStream.readDouble();
//                    myLongitude = dataInputStream.readDouble();
//
//                    result = true;
//
//                }
//            } catch (Exception e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//            return result;
//
//
//        }
//
//        protected void onPostExecute(String feed) {
//            // TODO: check this.exception
//            // TODO: do something with the feed
//        }
//
//
//    }

    private void WriteData(OutputStream out, int cid, int lac)
            throws IOException {
        DataOutputStream dataOutputStream = new DataOutputStream(out);
        dataOutputStream.writeShort(21);
        dataOutputStream.writeLong(0);
        dataOutputStream.writeUTF("en");
        dataOutputStream.writeUTF("Android");
        dataOutputStream.writeUTF("1.0");
        dataOutputStream.writeUTF("Web");
        dataOutputStream.writeByte(27);
        dataOutputStream.writeInt(0);
        dataOutputStream.writeInt(0);
        dataOutputStream.writeInt(3);
        dataOutputStream.writeUTF("");

        dataOutputStream.writeInt(cid);
        dataOutputStream.writeInt(lac);

        dataOutputStream.writeInt(0);
        dataOutputStream.writeInt(0);
        dataOutputStream.writeInt(0);
        dataOutputStream.writeInt(0);
        dataOutputStream.flush();
    }

    public Location getLocation() {
        try {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            //   Log.e("location managaer   ", new Gson().toJson(locationManager));

            // getting GPS status
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

//            Log.e(" GPS_PROVIDER   ", String.valueOf(isGPSEnabled));

            // getting network status
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);


//            Log.e(" NETWORK_PROVIDER   ", String.valueOf(isNetworkEnabled));


            LocationListener myLocationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
//                    Log.d("dj", "on location changed: " + location.getLatitude() + " & " + location.getLongitude());
                    // toastLocation(location);
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            };


            if (!isGPSEnabled && !isNetworkEnabled) {

                //retrieve a reference to an instance of TelephonyManager
                TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                }
                try {
                    GsmCellLocation cellLocation = (GsmCellLocation) telephonyManager.getCellLocation();
                    final int cid = cellLocation.getCid();
                    final int lac = cellLocation.getLac();


                    //Doing on background thread
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            final Boolean result = RqsLocation(cid, lac);

                            if (result) {
                                fn_update_sim(myLatitude, myLongitude);
                            } else {
                                // textGeo.setText("Can't find Location!");
                            }

                        }
                    }).start();

                } catch (Exception e) {
                    Log.e("exception ", e.getMessage());
                }
                // showSettingsAlert("NETWORK");

                // no network provider is enabled


            } else {
                this.canGetLocation = true;


                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        //check the network permission
                        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions((Activity) context, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
                        } else {

                            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES,
                                    MIN_DISTANCE_CHANGE_FOR_UPDATES, myLocationListener);

                            Log.d("GPS Enabled", "GPS Enabled");
                            if (locationManager != null) {
                                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                                if (location != null) {
                                    latitude = location.getLatitude();
                                    longitude = location.getLongitude();

                                    fn_update(location);
                                }
                            }

                        }
                    }
                }

                // First get location from Network Provider
                else if (isNetworkEnabled) {


                    //check the network permission
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions((Activity) context, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 101);
                    } else {


                        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES,
                                0/* MIN_DISTANCE_CHANGE_FOR_UPDATES*/, this);

                        Log.e("Network", "Network");

                        if (locationManager != null) {
                            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                                fn_update(location);
                            }
                        }
                    }

                }

            }


        } catch (Exception e) {

            Log.e("exceptionn ", e.getMessage());
            e.printStackTrace();
        }


        return location;
    }


    public void showSettingsAlert(String provider) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(LocationTracker.this);

        alertDialog.setTitle(provider + " SETTINGS");

        alertDialog.setMessage(provider + " is not enabled! Want to go to settings menu?");

        alertDialog.setPositiveButton("Settings",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        LocationTracker.this.startActivity(intent);
                    }
                });

        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();
    }

    private void fn_update(Location location) {


        Geocoder geocoder;
        List<Address> addresses;

        try {

            DatabaseHandler databaseHandler = new DatabaseHandler(context);

            SQLiteDatabase helper = databaseHandler.getWritableDatabase();

            ContentValues contentValues = new ContentValues();


            contentValues.put(DatabaseHandler.L_TIME, getDateTime());
            contentValues.put(DatabaseHandler.L_LAT, location.getLatitude());
            contentValues.put(DatabaseHandler.L_LONG, location.getLongitude());


            geocoder = new Geocoder(this, Locale.getDefault());

            if (geocoder.isPresent()) {

                try {


                    addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                    String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                    String city = addresses.get(0).getLocality();
                    String state = addresses.get(0).getAdminArea();
                    String country = addresses.get(0).getCountryName();
                    String postalCode = addresses.get(0).getPostalCode();
                    String knownName = addresses.get(0).getFeatureName();

                    String final_address = address + ", " + city + ", " + state + " , " + postalCode + ", " + country;

                    contentValues.put(DatabaseHandler.L_ADDRESS, final_address);


                    Log.e("insertdata Geocoder ", "presenet");

                } catch (Exception e) {

                }


            }


            helper.insert(DatabaseHandler.TABLE_LOCATION, null, contentValues);

            Log.e("insert data ", getDateTime());

        } catch (Exception e) {

        }
    }

    private void fn_update_sim(double myLatitude, double myLongitude) {


        Geocoder geocoder;
        List<Address> addresses;

        try {

            DatabaseHandler databaseHandler = new DatabaseHandler(context);

            SQLiteDatabase helper = databaseHandler.getWritableDatabase();

            ContentValues contentValues = new ContentValues();


            contentValues.put(DatabaseHandler.L_TIME, getDateTime());
            contentValues.put(DatabaseHandler.L_LAT, myLatitude);
            contentValues.put(DatabaseHandler.L_LONG, myLongitude);


            geocoder = new Geocoder(this, Locale.getDefault());

            if (geocoder.isPresent()) {

                try {


                    addresses = geocoder.getFromLocation(myLatitude, myLongitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                    String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                    String city = addresses.get(0).getLocality();
                    String state = addresses.get(0).getAdminArea();
                    String country = addresses.get(0).getCountryName();
                    String postalCode = addresses.get(0).getPostalCode();
                    String knownName = addresses.get(0).getFeatureName();

                    String final_address = address + ", " + city + ", " + state + " , " + postalCode + ", " + country;

                    contentValues.put(DatabaseHandler.L_ADDRESS, final_address);


                    Log.e("insertdata Geocoder ", "presenet");

                } catch (Exception e) {

                }


            }


            helper.insert(DatabaseHandler.TABLE_LOCATION, null, contentValues);

            Log.e("insert data ", getDateTime());
        } catch (Exception e) {

        }
    }

    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    /**
     * Stop using GPS listener
     * Calling this function will stop using GPS in your app
     */

    public void stopUsingGPS() {
        if (locationManager != null) {
            locationManager.removeUpdates(LocationTracker.this);
        }
    }

    /**
     * Function to get latitude
     */

    public double getLatitude() {
        if (location != null) {
            latitude = location.getLatitude();
        }

        // return latitude
        return latitude;
    }

    /**
     * Function to get longitude
     */

    public double getLongitude() {
        if (location != null) {
            longitude = location.getLongitude();
        }

        // return longitude
        return longitude;
    }

    /**
     * Function to check GPS/wifi enabled
     *
     * @return boolean
     */

    public boolean canGetLocation() {
        return this.canGetLocation;
    }


    /**
     * Function to show settings alert dialog
     * On pressing Settings button will lauch Settings Options
     */

    public void showSettingsAlert() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings");

        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                context.startActivity(intent);
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        alertDialog.show();
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
}
