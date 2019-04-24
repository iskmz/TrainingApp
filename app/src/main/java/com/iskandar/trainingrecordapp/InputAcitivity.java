package com.iskandar.trainingrecordapp;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class InputAcitivity extends AppCompatActivity {

    final int MAX_PUSHUPS = 150;
    final int MAX_MINUTES = 180;
    final double MAX_KILOMETERS = 10;
    final int MIN_PUSHUPS = 0;
    final int MIN_MINUTES = 0;
    final double MIN_KILOMETERS = 0;


    Context context;
    ImageView btnExitActivity, btnSaveData;
    TextView counterTreadmillTime, counterTreadmillDistance, counterPushups;
    ImageButton btnTreadmillTimeAdd, btnTreadmillTimeMinus,
                 btnTreadmillDistanceAdd, btnTreadmillDistanceMinus,
                    btnPushupsAdd, btnPushupsMinus;
    TextView txtDateToday;
    EditText txtOtherInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_acitivity);

        setPointers();
        loadData();
        setListeners();
    }

    private void setListeners() {

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
    }

    private void loadData() {
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
        txtDateToday = findViewById(R.id.txtDateToday);
        txtOtherInput = findViewById(R.id.txtInputOther);

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
