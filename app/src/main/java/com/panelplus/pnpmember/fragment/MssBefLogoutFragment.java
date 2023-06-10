package com.panelplus.pnpmember.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.panelplus.pnpmember.R;
import com.panelplus.pnpmember.activity.MainActivity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;


/**
 * Created by DELL on 14/06/60.
 */

public class MssBefLogoutFragment extends AppCompatDialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //Create the view show
        View v = LayoutInflater.from(getActivity())
                .inflate(R.layout.message_popup_bflogout, null);

        //create button listener
       DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
           @Override
           public void onClick(DialogInterface dialog, int which) {
               switch (which) {
                   case DialogInterface.BUTTON_POSITIVE:
                       Log.i("TAG", "Clicked The Button");

                       ((MainActivity) getActivity()).myOnResume();

                       /*Intent intent = new Intent(getActivity().getApplication(), EvaMemberActivity.class);
                       startActivity(intent);*/

                    break;


                   case DialogInterface.BUTTON_NEGATIVE:
                       //do not anything
                       break;
               }
           }
       };



        //Build the alert dialog
        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setPositiveButton(android.R.string.ok,listener)
                .setNegativeButton(android.R.string.cancel,listener)
                .create();
    }
}
