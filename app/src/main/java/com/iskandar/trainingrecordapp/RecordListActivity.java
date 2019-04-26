package com.iskandar.trainingrecordapp;

import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class RecordListActivity extends AppCompatActivity {

    Context context;
    // MVC //
    DataSQLlite dataDB;
    List<DataItem> dataItemList;
    ListView lstView;
    // other views //
    ImageView btnCloseList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_list);


        setPointers();
        setListeners();
    }

    private void setListeners() {
        btnCloseList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { finish(); }
        });
    }

    private void setPointers() {
        this.context = this;
        btnCloseList = findViewById(R.id.btnCloseList);
        lstView = findViewById(R.id.lstData);
        dataItemList = loadDataToList();
        lstView.setAdapter(new DataListAdapter());
    }

    private List<DataItem> loadDataToList() {
        // get data from SQL db using Cursor //
        List<DataItem> tmp = new ArrayList<>();
        dataDB = new DataSQLlite(context);
        Cursor res = dataDB.getAllData();
        if (res.getCount()==0) return tmp;
        while(res.moveToNext())
        {
            tmp.add(new DataItem(
                    res.getString(DataSQLlite.COL_DATE),
                    res.getString(DataSQLlite.COL_runningDistance),
                    res.getString(DataSQLlite.COL_runningTime),
                    res.getString(DataSQLlite.COL_pushups),
                    res.getString(DataSQLlite.COL_other))
            );
        }
        return tmp;
    }

    /////////////////////////////////////////////////////////////////////////////////////////////
    // inner class for DATA to make life easier !
    public class DataItem {
        String date, treadmill, pushups, other;
        public DataItem(String date, String run_distance, String run_time, String pushups, String other) {
            this.date = date.substring(0, 4) + "." + date.substring(4, 6) + "." + date.substring(6, 8);
            this.treadmill = run_distance + "/" + run_time;
            this.pushups = pushups;
            this.other = other;
        }
    }
    /////////////////////////////////////////////////////////////////////////////////////////////
    // inner class for ADAPTER to make life even more easier !
    public class DataListAdapter extends BaseAdapter {
        public DataListAdapter() {} // no need to pass context or dataList, cause of INNER class ! //
        @Override public int getCount() { return dataItemList.size(); }
        @Override public Object getItem(int position) { return null; }
        @Override public long getItemId(int position) { return 0; }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return convertView!=null?convertView:getItemView(position);
        }
        private View getItemView(int pos) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_in_list, null);
            final DataItem current = dataItemList.get(pos);
            ((TextView) view.findViewById(R.id.txtDateValue)).setText(current.date);
            ((TextView) view.findViewById(R.id.txtTreadmillValue)).setText(current.treadmill);
            ((TextView) view.findViewById(R.id.txtPushupsValue)).setText(current.pushups);
            TextView txtOtherNA = view.findViewById(R.id.txtOtherNA);
            ImageView imgOtherShow = view.findViewById(R.id.imgOtherShow);
            if(current.other.isEmpty())
            {
                txtOtherNA.setVisibility(View.VISIBLE);
                imgOtherShow.setVisibility(View.GONE);
            }
            else
            {
                txtOtherNA.setVisibility(View.GONE);
                imgOtherShow.setVisibility(View.VISIBLE);
                imgOtherShow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) { showInfoDialog(current.other); }
                    private void showInfoDialog(String msg) {
                        Dialog infoDialog = new Dialog(context,R.style.Theme_AppCompat_Dialog);
                        View v = LayoutInflater.from(context).inflate(R.layout.dialog_other_info,null);
                        ((TextView) v.findViewById(R.id.txtDialogOtherInfo)).setText(msg);
                        infoDialog.setContentView(v);
                        infoDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                        infoDialog.show();
                    }
                });
            }
            return view;
        }
    }
}