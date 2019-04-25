package com.iskandar.trainingrecordapp;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    final int WELCOME_DELAY = 5; // in seconds


    Context context;
    ImageView btnExit, btnShowRecords, btnInputScreen;

    TextToSpeech tts;


    // TODO mini music player
    // two layout "lines"
        // TOP: current track name (or first in list {make marqueeable})
        //      + button (at end) to change playlist src:[opens files explorer to choose
        //          a folder with "mp3s" ]
        //          then shows the list in a "tmp-dialog" to delete unwanted tracks
        //          then after pressing "OK" starts playing the first track in the MODIFIED list ...
        // BOTTOM: player buttons:  prev, play/pause, next, repeat-one

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
                startActivity(new Intent(context, InputActivity.class));
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
        final TextView txtTop = v.findViewById(R.id.txtWelcomeTop);
        final TextView txtBottom = v.findViewById(R.id.txtWelcomeBottom);
        txtTop.setText(genMsgByTime()[0]);
        txtBottom.setText(genMsgByTime()[1]);

        // say the welcome msg (speech)
        sayHello(txtTop.getText(),txtBottom.getText());

        // set & show the dialog
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
                    public void run() { SystemClock.sleep(WELCOME_DELAY*1000); dialog.dismiss(); }
                }).start();
            }


        });

        welcomeDialog.setCanceledOnTouchOutside(false);
        welcomeDialog.show();
    }

    private void sayHello(final CharSequence txtUp, final CharSequence txtDown) {
        tts=new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                Log.e("tts", status == 0 ? "SUCCESS" : "ERROR");
                if (status != TextToSpeech.ERROR) {
                    if (tts.isLanguageAvailable(Locale.ENGLISH) != TextToSpeech.LANG_MISSING_DATA
                            && tts.isLanguageAvailable(Locale.ENGLISH) != TextToSpeech.LANG_NOT_SUPPORTED)
                    {
                        tts.setLanguage(Locale.ENGLISH);
                        tts.setPitch(1.5f);
                        tts.setSpeechRate(1.5f);
                        String toSpeak = txtUp + "...\n" + txtDown;
                        tts.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
                    }
                    else
                    {
                        Log.e("english", "NOT AVAILABLE");
                    }
                }
            }
        });

    }



    private String[] genMsgByTime()
    {
        int hour = Integer.parseInt(new SimpleDateFormat("HH").format(new Date()));
        if(hour>=4 && hour <=10) return new String[]{"Good Morning","Have a great training session!"};
        else if (hour>=11 && hour<=13) return new String[]{"Hello","Have an easy training!"};
        else if (hour>=14 && hour<=17) return new String[]{"Good Afternoon","Have an enjoyable training!"};
        else if (hour>=18 && hour<=21) return new String[]{"Good Evening","enjoy your session!"};
        else return new String[]{"Good Night","You should be sleeping by now!"};
    }


    @Override
    protected void onDestroy() {
        if(tts != null){
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }
}
