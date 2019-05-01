package com.iskandar.trainingrecordapp;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
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
    ImageView btnCloseList, btnDeleteUser;

    String currentUserSelected;
    String[] usersList;
    Spinner spnUserSelection;

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
            public void onClick(View v) { finish(); dataDB.close(); }
        });
        lstView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                // DELETE: are you sure ? alert ! //
                AlertDialog alert = new AlertDialog.Builder(context,R.style.Theme_AppCompat_Dialog_Alert)
                        .setIcon(R.drawable.ic_warning_red).setTitle(" DELETE ?!")
                        .setMessage("Pressing OK would delete the"
                                + "\nrecord at: " + dataItemList.get(position).date+" !!"
                                +"\n\nAre you sure ?")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // remove
                                dataDB.deleteDataAtDate(currentUserSelected, dataItemList.get(position)
                                                .date.replace(".","")); // from SQL DB
                                dataItemList.remove(position);              // from dataList
                                lstView.setAdapter(new DataListAdapter()); // re-set adapter
                            }
                        })
                        .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) { dialog.dismiss(); }})
                        .show();
                return true;
            }
        });

        btnDeleteUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteUserDialog();
            }

            private void showDeleteUserDialog() {
                View v = LayoutInflater.from(context).inflate(R.layout.dialog_delete_user,null);
                final EditText edt = v.findViewById(R.id.txtInputMagic);
                final ImageView taps = v.findViewById(R.id.imgTaps);

                taps.setImageResource(R.drawable.ic_warning_red);
                Utils.initializeTaps();
                taps.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Utils.addTapsCounter();
                        if(Utils.checkTapsStatus())
                        {
                            taps.setImageResource(R.drawable.ic_greenlit);
                        }
                    }
                });


                final AlertDialog delete = new AlertDialog.Builder(context,R.style.Theme_AppCompat_Dialog_Alert)
                        .setTitle("DELETE selected user with all their records !!")
                        .setView(v)
                        .setIcon(R.drawable.ic_delete_red)
                        .setPositiveButton("Do it !!", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.e("del","\tmagic: "+edt.getText().toString()+
                                        "\t taps: "+Utils.checkTapsStatus());
                                if(edt.getText().toString().equals("!!!!") && Utils.checkTapsStatus())
                                {
                                    Log.e("del","pass & taps confirmed!");
                                    dataDB.deleteUser(currentUserSelected);
                                    loadUsersList();
                                }
                                spnUserSelection.setSelection(0,true);
                                dialog.dismiss();
                            }


                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                spnUserSelection.setSelection(0,true);
                                dialog.dismiss();
                            }
                        }).show();

            }
        });

        // USERS SPINNER //
        spnUserSelection.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    currentUserSelected = usersList[position];
                    dataItemList = loadDataToList(currentUserSelected);
                    lstView.setAdapter(new DataListAdapter());
            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // for DEVELOPER check purpose // "secret" button
        // UNCOMMENT when needed to adjust data in db // or anything else ...
        /*
                btnCloseList.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        dataDB.deleteAllRandomData(); // delete all random data inserted
                        dataItemList = loadDataToList(); // reload data to list from the "new" DB //
                        lstView.setAdapter(new DataListAdapter()); // reset adapter
                        return true;
                    }
                });
        */
    }

    private void setPointers() {
        this.context = this;
        btnCloseList = findViewById(R.id.btnCloseList);
        btnDeleteUser = findViewById(R.id.btnDeleteUser);
        lstView = findViewById(R.id.lstData);
        dataDB = new DataSQLlite(context);
        // dataDB.insertRandomData(35); // for DEVELOPER checks //
        dataItemList = loadDataToList();
        lstView.setAdapter(new DataListAdapter());
        // users list //
        spnUserSelection = findViewById(R.id.spnUserSelection_RecordAct);
        loadUsersList();
    }

    private void loadUsersList() {
        usersList = dataDB.getUsersTableList().toArray(new String[]{});
        ArrayAdapter aa = new ArrayAdapter(context,android.R.layout.simple_spinner_item, usersList);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spnUserSelection.setAdapter(aa);
        // select default position
        currentUserSelected = usersList[0];
        spnUserSelection.setSelection(0,true);
    }


    private List<DataItem> loadDataToList(String username) {
        // get data from SQL db using Cursor //
        List<DataItem> tmp = new ArrayList<>();
        Cursor res = dataDB.getAllDataForUser(username);
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
        tmp = sortListByDate(tmp);
        return tmp;
    }


    private List<DataItem> loadDataToList() {
        // get data from SQL db using Cursor //
        List<DataItem> tmp = new ArrayList<>();
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
        tmp = sortListByDate(tmp);
        return tmp;
    }

    private List<DataItem> sortListByDate(List<DataItem> lst) {
        List<DataItem> sortedList = new ArrayList<>();
        while (!lst.isEmpty()) // repeat until emptied
        {
            // find the item with the SMALLEST rawDate value
            DataItem smallest = lst.get(0);
            for (DataItem item : lst) {
                if (item.getRawDate() < smallest.getRawDate()) {
                    smallest = item;
                }
            }
            // add smallest item to new sorted list
            sortedList.add(smallest);
            // remove the smallest item from the given lst
            lst.remove(smallest);
        }
        return sortedList;
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
        public int getRawDate() { return Integer.parseInt(this.date.replace(".","")); }
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
            return getItemView(position);
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
                        final Dialog infoDialog = new Dialog(context,R.style.Theme_AppCompat_Dialog);
                        View v = LayoutInflater.from(context).inflate(R.layout.dialog_other_info,null);
                        ((LinearLayout) v.findViewById(R.id.layDialogInfo)).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                infoDialog.dismiss();
                            }
                        });
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