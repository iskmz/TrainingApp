package com.iskandar.trainingrecordapp;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    final int WELCOME_DELAY = 5; // in seconds


    Context context;
    ImageView btnExit, btnShowRecords, btnInputScreen;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        setPointers();
        showWelcomeDialog();
        setListeners();
    }


    private void setListeners() {

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnShowRecords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context,RecordListActivity.class));
            }
        });

        btnInputScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context,InputAcitivity.class));
            }
        });
    }

    private void setPointers() {

        this.context = this;
        btnExit = findViewById(R.id.btnCloseMain);
        btnInputScreen = findViewById(R.id.btnInputScreen);
        btnShowRecords = findViewById(R.id.btnShowRecords);
    }


    private void showWelcomeDialog() {

        // set the views to inflate from ...
        View v = LayoutInflater.from(context).inflate(R.layout.dialog_welcome,null);
        TextView txtTop = v.findViewById(R.id.txtWelcomeTop);
        TextView txtBottom = v.findViewById(R.id.txtWelcomeBottom);
        txtTop.setText(Utils.genMsgByTime()[0]);
        txtBottom.setText(Utils.genMsgByTime()[1]);

        // set the dialog
        final Dialog welcomeDialog = new Dialog(context,R.style.Theme_AppCompat_Dialog);
        welcomeDialog.setContentView(v);
        welcomeDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        WindowManager.LayoutParams wmlp = welcomeDialog.getWindow().getAttributes();
        wmlp.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        //wmlp.x = 100;   //x position
        wmlp.y = 100;   //y position
        welcomeDialog.getWindow().setAttributes(wmlp);

        welcomeDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                // wait for "WELCOME_DELAY" seconds and then close dialog //
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        SystemClock.sleep(WELCOME_DELAY*1000);
                        dialog.dismiss();
                    }
                }).start();
            }
        });


        welcomeDialog.setCanceledOnTouchOutside(false);
        welcomeDialog.show();
    }



}
