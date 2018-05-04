package com.demo.offlinelocation.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.demo.offlinelocation.Common;
import com.demo.offlinelocation.R;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private Context context;
    private TextView txtTitle, text_selected;
    private ImageView ev_navIcon;
    private Button btn_search_final;
    private Button btn_search_add;

    private RadioButton r_time, r_date, r_week, r_month, r_year;
    private RadioGroup radioData;
    private PopupWindow mPopupWindow;

    private LinearLayout linear;

    public String search_date,
            search_time_date, search_time_start, search_time_end,
            search_week,
            search_month,
            search_year;

    private String item_week_Selected;

    public SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        context = SearchActivity.this;
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
     //   preferences = getPreferences(Context.MODE_PRIVATE);

        linear = (LinearLayout) findViewById(R.id.linear);

        txtTitle = (TextView) findViewById(R.id.txt_title);
        txtTitle.setText("Search Activity");

        ev_navIcon = (ImageView) findViewById(R.id.iv_navicon);
        ev_navIcon.setVisibility(View.GONE);

        init();


    }

    private void init() {


        text_selected = (TextView) findViewById(R.id.text_selected);

        btn_search_final = (Button) findViewById(R.id.btn_search_final);

        radioData = (RadioGroup) findViewById(R.id.radioData);
        r_time = (RadioButton) findViewById(R.id.radioTime);
        r_date = (RadioButton) findViewById(R.id.radioDate);
        r_week = (RadioButton) findViewById(R.id.radioWeek);
        r_month = (RadioButton) findViewById(R.id.radioMonth);
        r_year = (RadioButton) findViewById(R.id.radioYear);
        btn_search_add = (Button) findViewById(R.id.btn_search_add);

        btn_search_add.setOnClickListener(this);
        btn_search_final.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {


        switch (view.getId()) {

            case R.id.btn_search_add:

                ButtonPress();

                break;

            case R.id.btn_search_final:


                String search_type = preferences.getString(Common.Type_Search, "");


                Intent intent = new Intent(this, SearchResultActivity.class);

                if (search_type == null) {

                } else if (search_type.equalsIgnoreCase("Date")) {

                    intent.putExtra("search_date", search_date);
                    Log.e("putt date ", search_date);

                } else if (search_type.equalsIgnoreCase("Time")) {

                    intent.putExtra("search_time_date", search_time_date);
                    intent.putExtra("search_time_start", search_time_start);
                    intent.putExtra("search_time_end", search_time_end);

                    Log.e("putt Time ", search_time_date + "   " + search_time_start + "   " + search_time_end);


                } else if (search_type.equalsIgnoreCase("Week")) {


                    intent.putExtra("search_week", search_week);
                    Log.e("putt Week ", search_week);


                } else if (search_type.equalsIgnoreCase("Month")) {


                    intent.putExtra("search_month", search_month);
                    Log.e("putt Month ", search_month);

                } else if (search_type.equalsIgnoreCase("Year")) {

                    intent.putExtra("search_year", search_year);
                    Log.e("putt Year ", search_year);


                }

                intent.putExtra("selectedSTRING", "its_has_value");
                startActivity(intent);

                break;

        }
    }


    private void ButtonPress() {

        int selectedId = radioData.getCheckedRadioButtonId();

        Log.e("text selectedId     ", selectedId + "    a");
        // find the radiobutton by returned id
        final RadioButton radioDataButton = (RadioButton) findViewById(selectedId);

        String selcted = radioDataButton.getText().toString();
        Log.e("text selected text    ", selcted + "    a");
        //Toast.makeText(context, radioDataButton.getText() + "  ada", Toast.LENGTH_SHORT).show();

        if (selcted.equalsIgnoreCase("Date")) {
            radioData.setVisibility(View.INVISIBLE);
            btn_search_add.setVisibility(View.INVISIBLE);
            text_selected.setVisibility(View.INVISIBLE);
            btn_search_final.setVisibility(View.INVISIBLE);
            PopupFordate(context);

        } else if (selcted.equalsIgnoreCase("Time")) {
            radioData.setVisibility(View.INVISIBLE);
            btn_search_add.setVisibility(View.INVISIBLE);
            text_selected.setVisibility(View.INVISIBLE);
            btn_search_final.setVisibility(View.INVISIBLE);
            PopupForTime(context);

        } else if (selcted.equalsIgnoreCase("Week")) {
            radioData.setVisibility(View.INVISIBLE);
            btn_search_add.setVisibility(View.INVISIBLE);
            text_selected.setVisibility(View.INVISIBLE);
            btn_search_final.setVisibility(View.INVISIBLE);
            PopupFor_Week(context);

        } else if (selcted.equalsIgnoreCase("Month")) {
            radioData.setVisibility(View.INVISIBLE);
            btn_search_add.setVisibility(View.INVISIBLE);
            text_selected.setVisibility(View.INVISIBLE);
            btn_search_final.setVisibility(View.INVISIBLE);
            PopupFor_Month(context);

        } else if (selcted.equalsIgnoreCase("Year")) {
            radioData.setVisibility(View.INVISIBLE);
            btn_search_add.setVisibility(View.INVISIBLE);
            text_selected.setVisibility(View.INVISIBLE);
            btn_search_final.setVisibility(View.INVISIBLE);
            PopupFor_Year(context);

        }


    }

    private void PopupFor_Year(Context context) {


        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.fragment_week, null);


        mPopupWindow = new PopupWindow(
                dialogView,
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT
        );

        if (Build.VERSION.SDK_INT >= 21) {
            mPopupWindow.setElevation(5.0f);
        }


        mPopupWindow.setFocusable(false);
        mPopupWindow.showAtLocation(linear, Gravity.NO_GRAVITY, 0, 1000);

        TextView textView = (TextView) dialogView.findViewById(R.id.textView);
        textView.setText("Year :  ");

        // Spinner element
        Spinner spinner = (Spinner) dialogView.findViewById(R.id.spinner);

        // Spinner click listener
        spinner.setOnItemSelectedListener(this);

        // Spinner Drop down elements
        List<String> categories = new ArrayList<String>();
        categories.add("1 year");
        categories.add("2 year");
        categories.add("3 year");
        categories.add("4 year");
        categories.add("5 year");
        categories.add("10 year");


        Button btnGet = (Button) dialogView.findViewById(R.id.button_time);
        btnGet.setText("Get Year");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(/*android.R.layout.simple_spinner_dropdown_item*/R.layout.dropdown_spinner);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);


        btnGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mPopupWindow.dismiss();

                preferences.edit().putString(Common.Type_Search, "Year").commit();


                radioData.setVisibility(View.VISIBLE);
                btn_search_add.setVisibility(View.VISIBLE);
                text_selected.setVisibility(View.VISIBLE);
                btn_search_final.setVisibility(View.VISIBLE);


                search_year = item_week_Selected;

                text_selected.setText("Selected Year :   " + search_year);

            }
        });


    }

    private void PopupFor_Month(Context context) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.fragment_week, null);


        mPopupWindow = new PopupWindow(
                dialogView,
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT
        );

        if (Build.VERSION.SDK_INT >= 21) {
            mPopupWindow.setElevation(5.0f);
        }


        mPopupWindow.setFocusable(false);
        mPopupWindow.showAtLocation(linear, Gravity.NO_GRAVITY, 0, 1000);

        TextView textView = (TextView) dialogView.findViewById(R.id.textView);
        textView.setText("Month :  ");

        // Spinner element
        Spinner spinner = (Spinner) dialogView.findViewById(R.id.spinner);

        // Spinner click listener
        spinner.setOnItemSelectedListener(this);

        // Spinner Drop down elements
        List<String> categories = new ArrayList<String>();
        categories.add("January");
        categories.add("February");
        categories.add("March");
        categories.add("April");
        categories.add("May");
        categories.add("June");
        categories.add("July");
        categories.add("August");
        categories.add("September");
        categories.add("October");
        categories.add("November");
        categories.add("December");


        Button btnGet = (Button) dialogView.findViewById(R.id.button_time);
        btnGet.setText("Get Month");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(/*android.R.layout.simple_spinner_dropdown_item*/R.layout.dropdown_spinner);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);


        btnGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mPopupWindow.dismiss();

                preferences.edit().putString(Common.Type_Search, "Month").commit();


                radioData.setVisibility(View.VISIBLE);
                btn_search_add.setVisibility(View.VISIBLE);
                text_selected.setVisibility(View.VISIBLE);
                btn_search_final.setVisibility(View.VISIBLE);


                search_month = item_week_Selected;

                text_selected.setText("Selected Month :   " + search_month);

            }
        });


    }

    private void PopupFor_Week(Context context) {


        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.fragment_week, null);


        mPopupWindow = new PopupWindow(
                dialogView,
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT
        );

        if (Build.VERSION.SDK_INT >= 21) {
            mPopupWindow.setElevation(5.0f);
        }


        mPopupWindow.setFocusable(false);
        mPopupWindow.showAtLocation(linear, Gravity.NO_GRAVITY, 0, 1000);

        // Spinner element
        Spinner spinner = (Spinner) dialogView.findViewById(R.id.spinner);

        // Spinner click listener
        spinner.setOnItemSelectedListener(this);

        // Spinner Drop down elements
        List<String> categories = new ArrayList<String>();
        categories.add("Last Week");
        categories.add("Last 2 Week");
        categories.add("Last 3 Week");
        categories.add("Last 4 Week");
        categories.add("Last Month");


        Button btnGet = (Button) dialogView.findViewById(R.id.button_time);


        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);


        btnGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mPopupWindow.dismiss();

                preferences.edit().putString(Common.Type_Search, "Week").commit();


                radioData.setVisibility(View.VISIBLE);
                btn_search_add.setVisibility(View.VISIBLE);
                text_selected.setVisibility(View.VISIBLE);
                btn_search_final.setVisibility(View.VISIBLE);


                search_week = item_week_Selected;

                text_selected.setText("Selected Week :   " + search_week);

            }
        });


    }

    private void PopupForTime(Context context) {


        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.fragment_time, null);


        mPopupWindow = new PopupWindow(
                dialogView,
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT
        );

        if (Build.VERSION.SDK_INT >= 21) {
            mPopupWindow.setElevation(5.0f);
        }


        mPopupWindow.setFocusable(false);
        mPopupWindow.showAtLocation(linear, Gravity.NO_GRAVITY, 0, 1000);

        final TimePicker pickerStart, pickerEnd;
        final DatePicker datePicker_time;
        Button btnGet;
        final TextView tvw;
        tvw = (TextView) dialogView.findViewById(R.id.textView1);
        pickerStart = (TimePicker) dialogView.findViewById(R.id.timePicker_start);
        pickerStart.setIs24HourView(true);

        pickerEnd = (TimePicker) dialogView.findViewById(R.id.timePicker_end);
        pickerEnd.setIs24HourView(true);

        datePicker_time = (DatePicker) dialogView.findViewById(R.id.datePicker_time);

        btnGet = (Button) dialogView.findViewById(R.id.button_time);


        btnGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mPopupWindow.dismiss();

                preferences.edit().putString(Common.Type_Search, "Time").commit();


                Log.e("fteee   " , preferences.getString(Common.Type_Search,""));

                radioData.setVisibility(View.VISIBLE);
                btn_search_add.setVisibility(View.VISIBLE);
                text_selected.setVisibility(View.VISIBLE);
                btn_search_final.setVisibility(View.VISIBLE);

                int hourStart, minuteStart, hourEnd, minuteEnd;
                String start_am_pm, end_am_pm;
                if (Build.VERSION.SDK_INT >= 23) {
                    hourStart = pickerStart.getHour();
                    minuteStart = pickerStart.getMinute();
                } else {
                    hourStart = pickerStart.getCurrentHour();
                    minuteStart = pickerStart.getCurrentMinute();
                }
                if (hourStart > 12) {
                    start_am_pm = "PM";
                    hourStart = hourStart - 12;
                } else {
                    start_am_pm = "AM";
                }

                if (Build.VERSION.SDK_INT >= 23) {
                    hourEnd = pickerEnd.getHour();
                    minuteEnd = pickerEnd.getMinute();
                } else {
                    hourEnd = pickerEnd.getCurrentHour();
                    minuteEnd = pickerEnd.getCurrentMinute();
                }
                if (hourEnd > 12) {
                    end_am_pm = "PM";
                    hourEnd = hourEnd - 12;
                } else {
                    end_am_pm = "AM";
                }


                search_time_date = datePicker_time.getDayOfMonth() + "/" + (datePicker_time.getMonth() + 1) + "/" + datePicker_time.getYear();

                search_time_start = hourStart + ":" + minuteStart + " " + start_am_pm;
                search_time_end = hourEnd + ":" + minuteEnd + " " + end_am_pm;

                text_selected.setText("Selected Date: " + search_time_date + "   \n" +
                        "Selected Time: " + search_time_start + " to " + search_time_end);

            }
        });

    }

    private void PopupFordate(Context context) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.fragment_date, null);


        mPopupWindow = new PopupWindow(
                dialogView,
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT
        );

        if (Build.VERSION.SDK_INT >= 21) {
            mPopupWindow.setElevation(5.0f);
        }


        mPopupWindow.setFocusable(false);
        mPopupWindow.showAtLocation(linear, Gravity.NO_GRAVITY, 0, 1000);

        final DatePicker picker;
        Button btnGet;
        final TextView tvw;
        tvw = (TextView) dialogView.findViewById(R.id.textView1);
        picker = (DatePicker) dialogView.findViewById(R.id.datePicker1);
        btnGet = (Button) dialogView.findViewById(R.id.button1);


        btnGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mPopupWindow.dismiss();


                preferences.edit().putString(Common.Type_Search, "Date").commit();

                radioData.setVisibility(View.VISIBLE);
                btn_search_add.setVisibility(View.VISIBLE);
                text_selected.setVisibility(View.VISIBLE);
                btn_search_final.setVisibility(View.VISIBLE);


         /*       String sear = picker.getDayOfMonth() + "/" + (picker.getMonth() + 1) + "/" + picker.getYear();
                String myFormat = "yyyy-MM-DD HH:MM:SS.SSS";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                search_date = sdf.format(sear);*/

                search_date = picker.getDayOfMonth() + "/" + (picker.getMonth() + 1) + "/" + picker.getYear();
                text_selected.setText("Selected Date: " + search_date);

            }
        });


    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        item_week_Selected = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
