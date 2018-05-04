package com.demo.offlinelocation.fragment;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.demo.offlinelocation.R;

/**
 * Created by Nishi on 5/2/2018.
 */

public class SearchFragment extends Fragment implements View.OnClickListener {

    private EditText et_city, et_country, et_state;
    private Button btn_search_add;
    public Context context;
    private RadioButton r_time, r_date, r_week, r_month, r_year;
    private RadioGroup radioData;


    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {

        context = getActivity();


// Inflate the layout for this fragment
        final View view1 = inflater.inflate(R.layout.fragment_search, container, false);


        radioData = (RadioGroup) view1.findViewById(R.id.radioData);
        r_time = (RadioButton) view1.findViewById(R.id.radioTime);
        r_date = (RadioButton) view1.findViewById(R.id.radioDate);
        r_week = (RadioButton) view1.findViewById(R.id.radioWeek);
        r_month = (RadioButton) view1.findViewById(R.id.radioMonth);
        r_year = (RadioButton) view1.findViewById(R.id.radioYear);

        btn_search_add = (Button) view1.findViewById(R.id.btn_search_add);


        btn_search_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int selectedId = radioData.getCheckedRadioButtonId();


                Log.e("text selectedId     ", selectedId + "    a");
                // find the radiobutton by returned id
                final RadioButton radioDataButton = (RadioButton) view1.findViewById(selectedId);


                String selcted = radioDataButton.getText().toString();
                Log.e("text selected text    ", selcted + "    a");
                //Toast.makeText(context, radioDataButton.getText() + "  ada", Toast.LENGTH_SHORT).show();


                if (selcted.equalsIgnoreCase("Date")) {

                    PopupFordate(context);


                }

            }


        });

        return view1;
    }

    private PopupWindow mPopupWindow;

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


       // mPopupWindow.setFocusable(true);
        mPopupWindow.showAsDropDown(dialogView, Gravity.CENTER,Gravity.CENTER);
        // mPopupWindow.showAtLocation(RelativeLayout, Gravity.CENTER, 0, 0);


    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.btn_search_add:


        }

    }
}
