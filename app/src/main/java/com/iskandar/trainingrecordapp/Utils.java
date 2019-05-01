package com.iskandar.trainingrecordapp;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Toast;

public class Utils {


    ///////////////////////////////  show message stuff //////////////////////////////////////////

    public static final int TOAST = 0, SNACKBAR = 1, ALERT = 2;

    public static void showMessage(Context context, int switcher, String msg) {
        switch(switcher) // 0: toast, 1: snackbar, 2: alert-dialog //
        {
            case 1:
                final Snackbar sn = Snackbar.make(((Activity)context).findViewById(android.R.id.content)
                        , msg, Snackbar.LENGTH_LONG);
                sn.setAction("OK", new View.OnClickListener() {
                    @Override public void onClick(View v) { sn.dismiss(); }});
                sn.setActionTextColor(Color.RED);
                sn.show();
                break;
            case 2:
                AlertDialog alert = new AlertDialog.Builder(context,R.style.Theme_AppCompat_Dialog_Alert)
                        .setIcon(R.drawable.ic_warning_red)
                        .setTitle("Oops!")
                        .setMessage(msg)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) { dialog.dismiss(); }})
                        .show();
                break;
            default: // = 0
                Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
        }
    }


    ///////////////////////////////  hidden taps stuff //////////////////////////////////////////

    private static int tapsCounter;

    public static void initializeTaps()
    {
        tapsCounter = 0;
    }

    public static void addTapsCounter()
    {
        tapsCounter += 1;
    }

    public static boolean checkTapsStatus()
    {
        return tapsCounter>=11;
    }
}
