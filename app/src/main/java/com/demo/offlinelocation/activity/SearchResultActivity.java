package com.demo.offlinelocation.activity;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;

import com.demo.offlinelocation.Common;
import com.demo.offlinelocation.R;
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
import java.sql.Time;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SearchResultActivity extends AppCompatActivity {

    private static Context context;
    private TextView txtTitle;
    private ImageView ev_navIcon;

    private static TableLayout tableLayout;
    public String querySTRING;
    private static TextView t_lat, t_long, t_time, t_address;

    public SharedPreferences preferences;

    private static DatabaseHandler databaseHandler;

    private static String[] Colums;

    private static SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);


        context = SearchResultActivity.this;

        preferences = PreferenceManager.getDefaultSharedPreferences(context);

        txtTitle = (TextView) findViewById(R.id.txt_title);
        txtTitle.setText("Result Activity");

        ev_navIcon = (ImageView) findViewById(R.id.iv_navicon);
        ev_navIcon.setVisibility(View.GONE);

        tableLayout = (TableLayout) findViewById(R.id.table);


        try {
            querySTRING = getIntent().getExtras().getString("selectedSTRING");
        } catch (Exception e) {

        }


        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (querySTRING != null) {

                    String search_type = preferences.getString(Common.Type_Search, "");

                    Log.e("get  ", search_type + "     ");

                    if (search_type == null) {


                    } else if (search_type.equalsIgnoreCase("Date")) {

                        String get_search_date = getIntent().getExtras().getString("search_date");

                        Log.e("get date ", get_search_date);


                        try {
                            Get_Date_data(get_search_date);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                    } else if (search_type.equalsIgnoreCase("Time")) {


                        String get_search_time_date_start = getIntent().getExtras().getString("search_time_date_start");
                        String get_search_time_date_end = getIntent().getExtras().getString("search_time_date_end");
                        String get_search_time_start = getIntent().getExtras().getString("search_time_start");
                        String get_search_time_end = getIntent().getExtras().getString("search_time_end");


                        Log.e("get Time ", get_search_time_date_start + "   " + get_search_time_date_end + "   " + get_search_time_start + "   " + get_search_time_end);

                        try {
                            Get_Time_data(get_search_time_date_start, get_search_time_date_end, get_search_time_start, get_search_time_end);
                        } catch (ParseException e) {
                            Log.e("exceprionn ", e.getMessage());
                        }

                    } else if (search_type.equalsIgnoreCase("Week")) {

                        String get_search_week = getIntent().getExtras().getString("search_week");


                        try {
                            Get_Week_data(get_search_week);
                        } catch (ParseException e) {
                            Log.e("exceprionn ", e.getMessage());
                        }

                        Log.e("get Week ", get_search_week);


                    } else if (search_type.equalsIgnoreCase("Month & Year")) {

                        String get_search_month = getIntent().getExtras().getString("search_month");
                        String get_search_year = getIntent().getExtras().getString("search_year");


                        try {

                            Get_MONTH_YEAR_data(get_search_month, get_search_year);

                            Log.e("get Month year ", get_search_month + "  " + get_search_year);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }


                    }/* else if (search_type.equalsIgnoreCase("Year")) {

                        String get_search_year = getIntent().getExtras().getString("search_year");

                        Log.e("get Year ", get_search_year);


                    }*/


                    Log.e("quey  string  ", querySTRING);

                } else {
                    if (isConnectingToInternet(context)) {

                        insertAddress();
                        getAlldataFromDatabase();

                    } else {
                        getAlldataFromDatabase();
                    }

                }
            }
        }, 500);


    }

    private void Get_MONTH_YEAR_data(String get_search_month, String lastWeekDatee) throws ParseException {

        /*   IN THIS METHOD COMMENT SECTION IS FOR THE datetime FORMAT GET DATE.. ITS USE FOR FURTHER
         *   USE FOR FIND DIFFRENCE BETWEEN YEAR AND MONTH OF SELECTED AND CURRENT   */

        Cursor cursor = null;
        String month_se = null;

        int year_select = Integer.parseInt(lastWeekDatee);

     /*   int year_current = Calendar.getInstance().get(Calendar.YEAR);
        int pass_year = year_select - year_current;
        Calendar c = Calendar.getInstance();
        // Log.e("lasttaas fdsc     ", year_select + "  " + year_current + "   " + pass_year);
        SimpleDateFormat newFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:SS");
        c.add(Calendar.YEAR, pass_year);*/


        R_data_common_Before_curser();


        if (get_search_month == null) {

            cursor = database.rawQuery("SELECT * FROM " + DatabaseHandler.TABLE_LOCATION
                    + " WHERE strftime('%Y', TIME) =  '" + year_select + "'", null);
        } else {

            Date date = new SimpleDateFormat("MMMM").parse(get_search_month);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);

            int month_select = cal.get(Calendar.MONTH) + 1;// if you put month_select date in to calender.add then remove + 1 so you get exact month

            if (month_select < 10) {
                month_se = "0" + month_select;
            }

           /* int month_current = Calendar.getInstance().get(Calendar.MONTH);
            int pass_current = month_select - month_current;
            c.add(Calendar.MONTH, pass_current);*/

            Log.e("montthh ****  fdsc     ", String.valueOf(month_select));

            cursor = database.rawQuery("SELECT * FROM " + DatabaseHandler.TABLE_LOCATION
                    + " WHERE strftime('%Y', TIME) = '" + year_select + "'  AND strftime('%m', TIME) = '" + month_se + "' ", null);


        }

      /*  String lastWeekD = newFormat.format(c.getTime());
        Log.e("lasttaas fdsc     ", lastWeekD + "cursor size  " + cursor.getCount());*/


        R_data_common_AFTER_curser(cursor);


    }

    private void Get_Week_data(String get_search_week) throws ParseException {
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat newFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:SS");

        Date date1 = new Date();
        String CurrentDate = newFormat.format(date1);

        Calendar c = Calendar.getInstance();
        c.setTime(date1);

        if (get_search_week.equalsIgnoreCase("Last Week")) {


            c.add(Calendar.DATE, -7);

        } else if (get_search_week.equalsIgnoreCase("Last 2 Week")) {


            c.add(Calendar.DATE, -14);


        } else if (get_search_week.equalsIgnoreCase("Last 3 Week")) {


            c.add(Calendar.DATE, -21);

        } else if (get_search_week.equalsIgnoreCase("Last 4 Week")) {


            c.add(Calendar.DATE, -28);


        } else if (get_search_week.equalsIgnoreCase("Last Month")) {


            c.add(Calendar.DATE, -30);

        }
        String lastWeekDatee = formatter.format(c.getTime());

        R_data_common_Before_curser();


        try {
            Date date = (Date) formatter.parse(lastWeekDatee);
            String lastweekDate = newFormat.format(date);


            Log.e(" lastdate and Current ", lastweekDate + "    " + CurrentDate);

            Cursor cursor = database.rawQuery("select LATITUDE, LONGITUTE, ADDRESS, TIME from "
                    + DatabaseHandler.TABLE_LOCATION + " where TIME BETWEEN '"
                    + lastweekDate + "' AND '"
                    + CurrentDate + "' ORDER BY TIME ASC", null);

            R_data_common_AFTER_curser(cursor);
        } catch (Exception e) {

            Log.e("excpetion  ", e.getMessage());

        }


    }

    private void Get_Time_data(String get_search_time_date_start, String get_search_time_date_end, String get_search_time_start, String get_search_time_end) throws ParseException {

        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

        Calendar cStart = Calendar.getInstance();

        cStart.setTime(formatter.parse(get_search_time_date_start));

        Time t = Time.valueOf(get_search_time_start);
        cStart.add(Calendar.HOUR, t.getHours());
        cStart.add(Calendar.MINUTE, t.getMinutes());
        cStart.add(Calendar.SECOND, t.getSeconds());


        Calendar cEndStart = Calendar.getInstance();
        cEndStart.setTime(formatter.parse(get_search_time_date_end));

        Time t1 = Time.valueOf(get_search_time_end);
        cEndStart.add(Calendar.HOUR, t1.getHours());
        cEndStart.add(Calendar.MINUTE, t1.getMinutes());
        cEndStart.add(Calendar.SECOND, t1.getSeconds());


        SimpleDateFormat newFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:SS");


        String StartFrom = newFormat.format(cStart.getTime());
        String ENDFrom = newFormat.format(cEndStart.getTime());


        Log.e("start  ", StartFrom + "  end  " + ENDFrom);

        R_data_common_Before_curser();
        try {
            Cursor cursor = database.rawQuery("select LATITUDE, LONGITUTE, ADDRESS, TIME from " + DatabaseHandler.TABLE_LOCATION + " where TIME BETWEEN '" + StartFrom + "' AND '" + ENDFrom + "' ORDER BY TIME ASC", null);
            R_data_common_AFTER_curser(cursor);
        } catch (Exception e) {

        }

    }

    private void Get_Date_data(String get_search_date) throws ParseException {


        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        Log.e("first  ", get_search_date);

        Calendar c = Calendar.getInstance();
        c.setTime(formatter.parse(get_search_date));
        c.add(Calendar.DATE, 1);
        String nextday = formatter.format(c.getTime());
        Log.e("plus one datye  ", nextday);

        R_data_common_Before_curser();

        try {
            Date date = (Date) formatter.parse(get_search_date);
            Date date2 = (Date) formatter.parse(nextday);


            Log.e("second  ", String.valueOf(date));
            SimpleDateFormat newFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:SS");
            String finalString = newFormat.format(date);
            String nextdayfinalstring = newFormat.format(date2);
            Log.e("final  ", finalString + "     " + nextdayfinalstring);

            Cursor cursor = database.rawQuery("select LATITUDE, LONGITUTE, ADDRESS, TIME from " + DatabaseHandler.TABLE_LOCATION + " where TIME BETWEEN '" + finalString + "' AND '" + nextdayfinalstring + "' ORDER BY TIME ASC", null);


            R_data_common_AFTER_curser(cursor);
        } catch (ParseException e) {
            e.printStackTrace();
            Log.e("error  ", e.getMessage());
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


    private static void getAlldataFromDatabase() {

        R_data_common_Before_curser();

        Cursor cursor = database.query
                (DatabaseHandler.TABLE_LOCATION, Colums, null, null, null, null, null);
        //tableLayout.removeAllViews();

        R_data_common_AFTER_curser(cursor);
    }

    private static void R_data_common_Before_curser() {

        databaseHandler = new DatabaseHandler(context);


        database = databaseHandler.getReadableDatabase();

        Colums = new String[]{DatabaseHandler.L_LAT,
                DatabaseHandler.L_LONG,
                DatabaseHandler.L_TIME, DatabaseHandler.L_ADDRESS};


    }

    private static void R_data_common_AFTER_curser(Cursor cursor) {
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
                    // Log.e("lat long ", L_LAT + "                 " + L_LONG);

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
}
