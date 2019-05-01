package com.iskandar.trainingrecordapp;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

public class InputActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    public static final int MAX_PUSHUPS = 150;
    public static final int MAX_MINUTES = 180;
    public static final double MAX_KILOMETERS = 10;
    public static final int MIN_PUSHUPS = 0;
    public static final int MIN_MINUTES = 0;
    public static final double MIN_KILOMETERS = 0;

    final long DELAY_ON_LONG_CLICK = 100; // in msec

    final int TOAST = Utils.TOAST, SNACKBAR = Utils.SNACKBAR, ALERT = Utils.ALERT;

    String dateToday, chosenDate;

    DataSQLlite dataDb;

    Context context;
    ImageView btnExitActivity, btnSaveData,btnClearText, btnDatePicker;
    TextView counterTreadmillTime, counterTreadmillDistance, counterPushups;
    ImageButton btnTreadmillTimeAdd, btnTreadmillTimeMinus,
                 btnTreadmillDistanceAdd, btnTreadmillDistanceMinus,
                    btnPushupsAdd, btnPushupsMinus;
    TextView txtDateToday;
    EditText txtOtherInput;

    private final String ADD_USER = "... [Add] ...";
    String currentUserSelected;
    String[] usersList;
    Spinner spnUserSelection;

    DatePickerDialog datePickerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);

        setPointers();
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
                if(dateToday.equals(chosenDate)) {
                    // 3 //
                    boolean check = dataDb.addDataEntryToday(currentUserSelected,
                            counterTreadmillTime.getText().toString(),
                            counterTreadmillDistance.getText().toString(),
                            counterPushups.getText().toString(),
                            txtOtherInput.getText().toString());
                    // 4 //
                    if (check)
                        Utils.showMessage(context, TOAST, "Today's data was saved successfully!");
                    else Utils.showMessage(context, TOAST, "ERROR while trying to save data!");
                }
                else
                {
                    // 3 //
                    boolean check = dataDb.addDataEntry(currentUserSelected, chosenDate,
                            counterTreadmillTime.getText().toString(),
                            counterTreadmillDistance.getText().toString(),
                            counterPushups.getText().toString(),
                            txtOtherInput.getText().toString());
                    // 4 //
                    if (check) Utils.showMessage(context, TOAST, "data for chosen date was saved successfully!");
                    else Utils.showMessage(context, TOAST, "ERROR while trying to save data!");
                }
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

        // DATE title and picker //
        txtDateToday.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // RESET to today's date and its DATA, if exist ! //
                setDateForToday();
                loadData();
                return true;
            }
        });

        btnDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
            }
        });

        // USERS SPINNER //
        spnUserSelection.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(usersList[position].equals(ADD_USER))
                {
                    showAddUserDialog();
                }
                else
                {
                    currentUserSelected = usersList[position];
                    loadDataAtDateFor(chosenDate,currentUserSelected);
                }
            }


            private void showAddUserDialog() {
                View v = LayoutInflater.from(context).inflate(R.layout.dialog_add_user,null);
                final EditText edt = v.findViewById(R.id.txtInputUsername);
                final AlertDialog addUser = new AlertDialog.Builder(context,R.style.Theme_AppCompat_Dialog_Alert)
                        .setTitle("Add new user ... ")
                        .setView(v)
                        .setIcon(R.drawable.ic_add_box_green)
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(addNewUser(edt.getText().toString()))
                                {
                                    loadUsersList();
                                }
                                spnUserSelection.setSelection(usersList.length>1?usersList.length-2:0);
                                dialog.dismiss();
                            }

                            private boolean addNewUser(String username) {
                                // if empty
                                if (username.isEmpty())
                                {
                                    Utils.showMessage(context,SNACKBAR,"nothing entered!");
                                    return false;
                                }
                                // if already exist
                                if(Arrays.asList(usersList).contains(username))
                                {
                                    Utils.showMessage(context,SNACKBAR,"user already exists!");
                                    return false;
                                }
                                // all is ok
                                dataDb.addUserTable(username);
                                Utils.showMessage(context,TOAST,"user created successfully!");
                                return true;
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                spnUserSelection.setSelection(usersList.length>1?usersList.length-2:0);
                                dialog.dismiss();
                            }
                        }).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    private void loadDataAtDateFor(String chosenDate, String userName) {
        Cursor tmp = dataDb.getDataAtDateForUser(chosenDate,userName);
        if (tmp.getCount()==0) {
            initializeViewData();
        }
        else {
            tmp.moveToFirst();
            setViewDataFromCursor(tmp);
        }
    }

    private void loadData() {
        // load data for TODAY, FOR DEFAULT USER // if already EXIST ! //
        dataDb = new DataSQLlite(context);
        Cursor tmp = dataDb.getTodaysData();
        if (tmp.getCount()==0) {
            initializeViewData();
        }
        else {
            tmp.moveToFirst();
            setViewDataFromCursor(tmp);
        }
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
        spnUserSelection = findViewById(R.id.spnUserSelection);

        // locale: US //
        //setLocale(Locale.US);

        // date //
        btnDatePicker = findViewById(R.id.imgDatePicker);
        txtDateToday = findViewById(R.id.txtDateToday);
        setDateForToday();
        loadData(); // for today's // for DEFAULT user //
        loadUsersList();
    }

    private void loadUsersList() {
        // for dev. check // START //
        // usersList = new String[] {"abc","def","ghi","jkl","mno","pqr","stu","vwx","yz"};
        // for dev. check // END //
        String[] arrFirst = dataDb.getUsersTableList().toArray(new String[]{});
        usersList = Arrays.copyOf(arrFirst,arrFirst.length+1);
        usersList[arrFirst.length] = ADD_USER;
        ArrayAdapter aa = new ArrayAdapter(context,android.R.layout.simple_spinner_item, usersList);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //Setting the ArrayAdapter data on the Spinner
        spnUserSelection.setAdapter(aa);
        // select default position
        int pos = getDefaultPos(usersList);
        currentUserSelected = usersList[pos];
        spnUserSelection.setSelection(pos,true);
    }

    private int getDefaultPos(String[] arr) {
        for(int i=0; i<arr.length; i+=1)
        {
            if(arr[i].equals(DataSQLlite.DEFAULT_TABLE_NAME)) return i;
        }
        return 0;
    }

    private void setLocale(Locale loc) {
        Configuration configuration = context.getResources().getConfiguration();
        configuration.setLocale(loc);
        configuration.setLayoutDirection(loc);
        context.createConfigurationContext(configuration);
    }

    private void setDateForToday() {
        txtDateToday.setText(getTodaysDateFormatted());
        dateToday = new SimpleDateFormat(DataSQLlite.DATE_FORMAT_PATTERN).format(new Date());
        chosenDate = dateToday; // initially !
        datePickerDialog = new DatePickerDialog(context, this,
                getTodaysYear(), getTodaysMonth(), getTodaysDay());
    }

    private int getTodaysDay() {
        return Integer.parseInt(new SimpleDateFormat("d").format(new Date()));
    }

    private int getTodaysMonth() {
        return Integer.parseInt(new SimpleDateFormat("M").format(new Date()))-1; // 0-11 from 1-12 //
    }

    private int getTodaysYear() {
        return Integer.parseInt(new SimpleDateFormat("yyyy").format(new Date()));
    }

    private String getTodaysDateFormatted() {
        Date dt = new Date();
        String pre = new SimpleDateFormat("EEEE, MMMM d", Locale.US).format(dt);
        String daySuffix = getTodaysSuffix(dt);
        String post = new SimpleDateFormat(", yyyy",Locale.US).format(dt);
        return pre + daySuffix + post;
    }

    private String getTodaysSuffix(Date date) {
        int today = Integer.parseInt(new SimpleDateFormat("d").format(date));
        switch (today)
        {
            case 1: case 21: case 31: return "st";
            case 2: case 22: return "nd";
            case 3: case 23: return "rd";
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

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

        // build "chosenDate" string
        String yearStr = year + ""; // 1 - 4 chars
        while(yearStr.length()<4) { yearStr = "0" + yearStr; } // add leading zeros, if needed
        String monthStr = ((month+1)<10?"0":"")+(month+1); // = 2 chars
        String dayStr = (dayOfMonth<10?"0":"")+dayOfMonth; // = 2 chars
        chosenDate = yearStr+monthStr+dayStr;
        Log.e("date","chosenDate: "+chosenDate);

        // set formatted form of chosenDate for the TextView
        txtDateToday.setText(getDateFormatted(chosenDate));

        // load data for chosen date
        loadDataFor(chosenDate);

    }

    private void loadDataFor(String chosenDate) {
        // load data for chosen DATE, if already EXIST ! // for CURRENT USER selected //
        loadDataAtDateFor(chosenDate,currentUserSelected);
    }

    private void initializeViewData() {
        counterTreadmillTime.setText("0");
        counterTreadmillDistance.setText("0.0");
        counterPushups.setText("0");
        txtOtherInput.setText("");
    }

    private void setViewDataFromCursor(Cursor tmp) {
        counterTreadmillTime.setText(tmp.getString(DataSQLlite.COL_runningTime));
        counterTreadmillDistance.setText(tmp.getString(DataSQLlite.COL_runningDistance));
        counterPushups.setText(tmp.getString(DataSQLlite.COL_pushups));
        txtOtherInput.setText(tmp.getString(DataSQLlite.COL_other));
    }

    private String getDateFormatted(String chosenDate) {

        Date dt;
        try {
            dt = new SimpleDateFormat(DataSQLlite.DATE_FORMAT_PATTERN,Locale.US).parse(chosenDate);
        } catch (ParseException e) {
            e.printStackTrace();
            Log.e("date","parsedDate issue!");
            return "";
        }


        String pre = new SimpleDateFormat("EEEE, MMMM d",Locale.US).format(dt);
        String daySuffix = getTodaysSuffix(dt);
        String post = new SimpleDateFormat(", yyyy",Locale.US).format(dt);
        return pre + daySuffix + post;

    }
}