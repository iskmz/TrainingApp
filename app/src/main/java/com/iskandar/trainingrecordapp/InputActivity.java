package com.iskandar.trainingrecordapp;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class InputActivity extends AppCompatActivity {

    public static final int MAX_PUSHUPS = 150;
    public static final int MAX_MINUTES = 180;
    public static final double MAX_KILOMETERS = 10;
    public static final int MIN_PUSHUPS = 0;
    public static final int MIN_MINUTES = 0;
    public static final double MIN_KILOMETERS = 0;

    final long DELAY_ON_LONG_CLICK = 100; // in msec

    final int TOAST = Utils.TOAST, SNACKBAR = Utils.SNACKBAR, ALERT = Utils.ALERT;

    DataSQLlite dataDb;

    Context context;
    ImageView btnExitActivity, btnSaveData,btnClearText;
    TextView counterTreadmillTime, counterTreadmillDistance, counterPushups;
    ImageButton btnTreadmillTimeAdd, btnTreadmillTimeMinus,
                 btnTreadmillDistanceAdd, btnTreadmillDistanceMinus,
                    btnPushupsAdd, btnPushupsMinus;
    TextView txtDateToday;
    EditText txtOtherInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);

        setPointers();
        loadData();
        setListeners();
    }

    private void setListeners() {

        // counters' plus & minus // on Click
        btnPushupsAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { incrementCounter(counterPushups,MAX_PUSHUPS); }
        });
        btnTreadmillDistanceAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { incrementCounter(counterTreadmillDistance,MAX_KILOMETERS); }
        });
        btnTreadmillTimeAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { incrementCounter(counterTreadmillTime,MAX_MINUTES); }
        });
        btnPushupsMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { decrementCounter(counterPushups,MIN_PUSHUPS); }
        });
        btnTreadmillDistanceMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { decrementCounter(counterTreadmillDistance,MIN_KILOMETERS); }
        });
        btnTreadmillTimeMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { decrementCounter(counterTreadmillTime,MIN_MINUTES); }
        });
        // counters' plus and minus // on LONG click
        btnPushupsAdd.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View v) { doIt(1,v,counterPushups,MAX_PUSHUPS); return false; }
        });
        btnTreadmillDistanceAdd.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) { doIt(1,v,counterTreadmillDistance,MAX_KILOMETERS); return false; }
        });
        btnTreadmillTimeAdd.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) { doIt(1,v,counterTreadmillTime,MAX_MINUTES); return false; }
        });
        btnPushupsMinus.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) { doIt(-1,v,counterPushups,MIN_PUSHUPS); return false; }
        });
        btnTreadmillDistanceMinus.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) { doIt(-1,v,counterTreadmillDistance,MIN_KILOMETERS); return false; }
        });
        btnTreadmillTimeMinus.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) { doIt(-1,v,counterTreadmillTime,MIN_MINUTES); return false; }
        });
        // counters themselves! // clear on LONG CLICK
        counterTreadmillTime.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) { counterTreadmillTime.setText("0"); return true; }
        });
        counterTreadmillDistance.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) { counterTreadmillDistance.setText("0.0"); return true; }
        });
        counterPushups.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) { counterPushups.setText("0"); return true; }
        });
        // CLEAR (other text) //
        btnClearText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { txtOtherInput.setText(""); }
        });
        // EXIT //
        btnExitActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); dataDb.close();
            }
        });
        // SAVE //
        btnSaveData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 1. check fields , empty or not ?
                // 2. check conditions (if distance, then time, or vice versa) ..etc ...
                // 3. save to local sql db using the "helper"
                // 4. if successful show message to user "data was successfully saved !"

                if(!fieldsCheckOK()) { return;} // 1 + 2 //
                // 3 //
                boolean check = dataDb.addDataEntryToday(
                                    counterTreadmillTime.getText().toString(),
                                    counterTreadmillDistance.getText().toString(),
                                    counterPushups.getText().toString(),
                                    txtOtherInput.getText().toString());
                // 4 //
                if(check) Utils.showMessage(context,TOAST,"Today's data was saved successfully!");
                else Utils.showMessage(context,TOAST,"ERROR while trying to save data!");
            }

            private boolean fieldsCheckOK() {
                // check 1 // ALL FIELDS ARE EMPTY //
                if (counterPushups.getText().equals("0")
                        && counterTreadmillTime.getText().equals("0")
                        && counterTreadmillDistance.getText().equals("0.0")
                        && txtOtherInput.getText().toString().isEmpty())
                {
                    Utils.showMessage(context,ALERT,"All fields are empty, nothing to save, nothing was saved!");
                    return false;
                }

                // check 2 // BOTH distance & time are entered ! //
                if(counterTreadmillDistance.getText().equals("0.0") && !counterTreadmillTime.getText().equals("0")
                    || counterTreadmillTime.getText().equals("0") && !counterTreadmillDistance.getText().equals("0.0"))
                {
                    Utils.showMessage(context,ALERT,"Both Time & Distance fields must be non-zero!");
                    return false;
                }

                return true;
            }
        });

    }

    private void loadData() {
        // load data for TODAY, if already EXIST ! //
        dataDb = new DataSQLlite(context);
        Cursor tmp = dataDb.getTodaysData();
        if (tmp.getCount()==0) return;
        tmp.moveToFirst();
        counterTreadmillTime.setText(tmp.getString(DataSQLlite.COL_runningTime));
        counterTreadmillDistance.setText(tmp.getString(DataSQLlite.COL_runningDistance));
        counterPushups.setText(tmp.getString(DataSQLlite.COL_pushups));
        txtOtherInput.setText(tmp.getString(DataSQLlite.COL_other));
    }

    private void setPointers() {

        this.context=this;
        btnExitActivity = findViewById(R.id.btnCloseInput);
        btnSaveData = findViewById(R.id.btnSaveInput);
        counterTreadmillTime = findViewById(R.id.txtCounterTreadmillMinutes);
        counterTreadmillDistance = findViewById(R.id.txtCounterTreadmillKilometers);
        counterPushups = findViewById(R.id.txtCounterPushups);
        btnTreadmillTimeAdd = findViewById(R.id.btnMoreTreadmillMinutes);
        btnTreadmillTimeMinus = findViewById(R.id.btnLessTreadmillMinutes);
        btnTreadmillDistanceAdd = findViewById(R.id.btnMoreTreadmillKilometers);
        btnTreadmillDistanceMinus = findViewById(R.id.btnLessTreadmillKilometers);
        btnPushupsAdd = findViewById(R.id.btnMorePushups);
        btnPushupsMinus = findViewById(R.id.btnLessPushups);
        txtOtherInput = findViewById(R.id.txtInputOther);
        btnClearText = findViewById(R.id.btnClearText);

        txtDateToday = findViewById(R.id.txtDateToday);
        txtDateToday.setText(getTodaysDate());
    }

    private String getTodaysDate() {
        Date dt = new Date();
        String pre = new SimpleDateFormat("EEEE, MMMM d").format(dt);
        String daySuffix = getTodaysSuffix(dt);
        String post = new SimpleDateFormat(", yyyy").format(dt);
        return pre + daySuffix + post;
    }

    private String getTodaysSuffix(Date date) {
        int today = Integer.parseInt(new SimpleDateFormat("d").format(date));
        switch (today%10)
        {
            case 1: return "st";
            case 2: return "nd";
            case 3: return "rd";
            default: return "th";
        }
    }


    private void doIt(final int switcher, final View v, final TextView counter, final double valueMaxMin) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    SystemClock.sleep(DELAY_ON_LONG_CLICK);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(switcher==1) incrementCounter(counter,valueMaxMin);
                            else decrementCounter(counter,valueMaxMin);
                        }
                    });
                    if (!v.isPressed()) break;
                }
            }
        }).start();
    }

    private void incrementCounter(TextView counter,double maxValue)
    {
        StringBuilder tmp = new StringBuilder(counter.getText());
        if(tmp.indexOf(".")!=-1) // if has decimal point ... keep it
        {
            double tmpNum = Double.parseDouble(tmp.toString());
            tmp = new StringBuilder((tmpNum>=maxValue?maxValue:plusPointOne(tmpNum))+"");
        }
        else
        {
            int tmpNum = Integer.parseInt(tmp.toString());
            tmp = new StringBuilder((int)(tmpNum>=maxValue?maxValue:tmpNum+1)+"");
        }
        counter.setText(tmp);
    }

    private void decrementCounter(TextView counter, double minValue)
    {
        StringBuilder tmp = new StringBuilder(counter.getText());
        if(tmp.indexOf(".")!=-1) // if has decimal point ... keep it
        {
            double tmpNum = Double.parseDouble(tmp.toString());
            tmp = new StringBuilder((tmpNum<=minValue?minValue:minusPointOne(tmpNum))+"");
        }
        else
        {
            int tmpNum = Integer.parseInt(tmp.toString());
            tmp = new StringBuilder((int)(tmpNum<=minValue?minValue:tmpNum-1)+"");
        }
        counter.setText(tmp);
    }

    private double plusPointOne(double num)
    {
        int tmp = ((int)(num*10))+1;
        return ((double)tmp/10);
    }

    private double minusPointOne(double num)
    {
        int tmp = ((int)(num*10))-1;
        return ((double)tmp/10);
    }

}