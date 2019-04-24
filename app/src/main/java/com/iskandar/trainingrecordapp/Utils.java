package com.iskandar.trainingrecordapp;


import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {


    public static String[] genMsgByTime()
    {
        int hour = Integer.parseInt(new SimpleDateFormat("HH").format(new Date()));
        if(hour>=4 && hour <=10) return new String[]{"Good Morning","Have a great training session!"};
        else if (hour>=11 && hour<=13) return new String[]{"Hello","Have an easy training!"};
        else if (hour>=14 && hour<=17) return new String[]{"Good Afternoon","Have an enjoyable training!"};
        else if (hour>=18 && hour<=21) return new String[]{"Good Evening","enjoy your session!"};
        else return new String[]{"Good Night","You should be sleeping by now!"};
    }
}
