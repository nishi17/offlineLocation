package com.demo.offlinelocation.fragment;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TextView;

import com.demo.offlinelocation.R;
import com.demo.offlinelocation.activity.MainActivity;
import com.demo.offlinelocation.database.DatabaseHandler;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

/**
 * Created by Nishi on 5/3/2018.
 */

public class SearchResultFragment extends Fragment implements View.OnClickListener {

    public static Context context;

    private static TableLayout tableLayout;

    private static TextView t_lat, t_long, t_time, t_address;


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {

        context = getActivity();


        String myTag = getTag();

        ((MainActivity) getActivity()).setSearchResultFragment(myTag);

// Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_result_search, container, false);

        tableLayout = (TableLayout) view.findViewById(R.id.table);


        if (isConnectingToInternet(context)) {

            insertAddress();
            getdataFromDatabase();

        } else {
            getdataFromDatabase();
        }


        return view;
    }


    public static void insertAddress() {

        try {


            DatabaseHandler databaseHandler = new DatabaseHandler(context);


            SQLiteDatabase database = databaseHandler.getReadableDatabase();

            String[] Colums = {DatabaseHandler.L_LAT,
                    DatabaseHandler.L_LONG, DatabaseHandler.l_ID};

            String whereClause = /*DatabaseHandler.L_ADDRESS + " = ?"*/"ADDRESS is null or ADDRESS = ?";
            String[] whereArgs = new String[]{""};

            // cursor = db.query(DBHelper.TABLE_NAME, COLUMNS, "folder_name is null or folder_name = ?", new String[]{""}, null, null, DBHelper.TIMESTAMP_COL + " DESC");

            Cursor cursor = database.query
                    (DatabaseHandler.TABLE_LOCATION, Colums, whereClause, whereArgs, null, null, null);


            Log.e("insertdata count ", String.valueOf(cursor.getCount()));

            if (cursor.getCount() >= 0) {
                if (cursor.moveToFirst()) {
                    do {


                        Log.e("google api ", "insertdata call  ");


                        Double L_LAT = cursor.getDouble((cursor.getColumnIndex(DatabaseHandler.L_LAT)));

                        Double L_LONG = cursor.getDouble((cursor.getColumnIndex(DatabaseHandler.L_LONG)));

                        int L_ID = cursor.getInt((cursor.getColumnIndex(DatabaseHandler.l_ID)));


                        getLocationCityName(L_LAT, L_LONG, L_ID);


                    } while (cursor.moveToNext());

                } else {

                }

            }
        } catch (Exception e) {

        }

    }

    public static String getLocationCityName(double lat, double lon, int l_ID) {


        List<Address> addresses;

        String latVal = String.valueOf(lat);
        String lngVal = String.valueOf(lon);
        String idVal = String.valueOf(l_ID);

        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        if (geocoder.isPresent()) {

            try {


                addresses = geocoder.getFromLocation(lat, lon, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
                String postalCode = addresses.get(0).getPostalCode();
                String knownName = addresses.get(0).getFeatureName();

                String final_address = address + ", " + city + ", " + state + " , " + postalCode + ", " + country;

                String id = String.valueOf(l_ID);

                insertDatabaseQuery(final_address, id);
            } catch (Exception e) {

            }


        } else {
            new RetrieveFeedTask().execute(/*urlToRssFeed);getLocationFormGoogle(*/latVal, lngVal, idVal);
            //  String city = getCityAddress(result);
        }
        return null;
    }


    private static void insertDatabaseQuery(String city, String l_ID) {

        DatabaseHandler databaseHandler = new DatabaseHandler(context);

        SQLiteDatabase helper = databaseHandler.getWritableDatabase();

        ContentValues contentValues = new ContentValues();


        contentValues.put(DatabaseHandler.L_ADDRESS, city);

        helper.update(DatabaseHandler.TABLE_LOCATION, contentValues, DatabaseHandler.l_ID + " = ?", new String[]{l_ID});
        // helper.insert(DatabaseHandler.TABLE_LOCATION, null, contentValues);
        ///getdataFromDatabase();
    }


    private static void getdataFromDatabase() {

        DatabaseHandler databaseHandler = new DatabaseHandler(context);


        SQLiteDatabase database = databaseHandler.getReadableDatabase();

        String[] Colums = {DatabaseHandler.L_LAT,
                DatabaseHandler.L_LONG,
                DatabaseHandler.L_TIME, DatabaseHandler.L_ADDRESS};


        Cursor cursor = database.query
                (DatabaseHandler.TABLE_LOCATION, Colums, null, null, null, null, null);
        //tableLayout.removeAllViews();

        if (cursor.getCount() >= 0) {
            if (cursor.moveToFirst()) {
                do {
                    View childAddons = LayoutInflater.from(context).inflate(R.layout.child_table, null);
                    t_lat = (TextView) childAddons.findViewById(R.id.t_lat);
                    t_long = (TextView) childAddons.findViewById(R.id.t_long);
                    t_time = (TextView) childAddons.findViewById(R.id.t_time);
                    t_address = (TextView) childAddons.findViewById(R.id.t_address);


                    Double L_LAT = cursor.getDouble((cursor.getColumnIndex(DatabaseHandler.L_LAT)));

                    Double L_LONG = cursor.getDouble((cursor.getColumnIndex(DatabaseHandler.L_LONG)));
                    Log.e("lat long ", L_LAT + "                 " + L_LONG);

                    String L_TIME = cursor.getString
                            (cursor.getColumnIndex(DatabaseHandler.L_TIME));

                    String L_ADDRESS = cursor.getString
                            (cursor.getColumnIndex(DatabaseHandler.L_ADDRESS));


                    t_lat.setText(new DecimalFormat("##.####").format(L_LAT));
                    t_long.setText(new DecimalFormat("##.####").format(L_LONG));
                    t_time.setText(L_TIME);
                    t_address.setText(L_ADDRESS);

                    tableLayout.addView(childAddons);

                } while (cursor.moveToNext());

            } else {

            }

        }


    }

    public static boolean isConnectingToInternet(Context mContext) {
        ConnectivityManager connectivity = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
        }
        return false;
    }

    @Override
    public void onClick(View view) {

    }


    private static class RetrieveFeedTask extends AsyncTask<String, Void, String> {

        org.apache.http.HttpResponse response;
        HttpClient client = new DefaultHttpClient();

        StringBuilder stringBuilder = new StringBuilder();

        String id;


        @Override
        protected String doInBackground(String... strings) {

            id = strings[2];
            String apiRequest = null;//+ "&ka&sensor=false"
            try {
                apiRequest = "https://maps.googleapis.com/maps/api/geocode/json?latlng="
                        + URLEncoder.encode(strings[0], "UTF-8")
                        + ","
                        + URLEncoder.encode(strings[1], "UTF-8")
                        + "&radius="
                     /*   + URLEncoder.encode("5000", "UTF-8")
                        + "&sensor="
                        + URLEncoder.encode("true", "UTF-8")*/
                      /*  + "&types="
                        + URLEncoder.encode("food|bar|church|museum|art_gallery", "UTF-8")*/
                        + "&key="
                        + URLEncoder.encode("AIzaSyDf4OEkOzbmPIhPweGGzdkJw0KxPApxZ4g", "UTF-8");
            } catch (UnsupportedEncodingException e) {


            }
            HttpGet httpGet = new HttpGet(apiRequest);


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
            return String.valueOf(response);

        }


        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);


            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject = new JSONObject(stringBuilder.toString());
            } catch (JSONException e) {

                e.printStackTrace();
            }

            //   Log.e("cittyuyuuyu     ", new Gson().toJson(jsonObject));

            String city = getCityAddress(jsonObject);

            // Log.e("cittyuyuuyu     ", city);

            insertDatabaseQuery(city, id);


        }

    }

    protected static String getCityAddress(JSONObject result) {

        if (result.has("results")) {
            try {
                JSONArray array = result.getJSONArray("results");
                if (array.length() > 0) {


                    JSONObject place = array.getJSONObject(0);
                    String s = place.getString("formatted_address");


                    Log.e(" adddreeeaaaaaaaa      ", s);
                    return s;

                  /*  JSONArray components = place.getJSONArray("address_components");

                    for (int i = 0; i < components.length(); i++) {
                        JSONObject component = components.getJSONObject(i);
                        JSONArray types = component.getJSONArray("types");
                        for (int j = 0; j < types.length(); j++) {
                            String street_number, route, sublocality_level_1, locality, administrative_area_level_2,
                                    administrative_area_level_1, country;
                            if (types.getString(j).equals("street_number")) {//city
                                String street_numbe = component.getString("long_name");
                                Log.d("street_number", street_numbe);

                                // return component.getString("long_name");
                            }


                            if (types.getString(j).equals("locality")) {//city
                                String city = component.getString("long_name");
                                Log.d("city", city);

                                return component.getString("long_name");
                            }

                            if (types.getString(j).equals("postal_code")) {//pin
                                return component.getString("long_name");
                            }

                            if (types.getString(j).equals("country")) {//country
                                return component.getString("long_name");
                            }

                            if (types.getString(j).equals("administrative_area_level_1")) {//state
                                return component.getString("long_name");
                            }

                            if (types.getString(j).equals("administrative_area_level_2")) {
                                return component.getString("long_name");
                            }
                        }


                    }*/
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return null;
    }


}
