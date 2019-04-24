package com.iskandar.trainingrecordapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    Context context;
    ImageView btnExit, btnShowRecords, btnInputScreen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setPointers();
        setListeners();
    }
// new branch ongoing check
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
}
