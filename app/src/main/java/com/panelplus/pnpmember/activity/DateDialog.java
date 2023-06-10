package com.panelplus.pnpmember.activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
//import android.app.DialogFragment;
import java.util.Calendar;
import android.os.Bundle;
//import android.support.v4.app.FragmentManager;
//import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.fragment.app.DialogFragment;

/**
 * Created by DELL on 02/06/60.
 */

public class DateDialog extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    EditText txtDate;
    public DateDialog(View view){
        txtDate = (EditText)view;
    }

    public Dialog onCreateDialog(Bundle savedInstancesState){
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        return new DatePickerDialog(getActivity(),this,year,month,day);
    }

    public void onDateSet(DatePicker view,int year,int month, int day){
        String date = day+"-"+(month+1)+"-"+year;
        txtDate.setText(date);
    }



}
