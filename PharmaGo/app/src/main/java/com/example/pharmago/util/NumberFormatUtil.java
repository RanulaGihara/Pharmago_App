package com.example.pharmago.util;

import java.text.DecimalFormat;

public class NumberFormatUtil {

    public static int getRandomIntBetweenRange(int min, int max){
        int x =(int) (Math.random()*((max-min)+1))+min;
        return x;
    }


    public static String getCurrencyFormat(double amount) {
        DecimalFormat formatter;

        formatter = new DecimalFormat("###,###,##0.00");


        return formatter.format(amount);
    }

    public static String getPercentageFormat(double percentage) {
        DecimalFormat formatter;
        formatter = new DecimalFormat("###,###.00");
        return formatter.format(percentage);
    }

    public static String getNumSeqFormat(int num){
        DecimalFormat formatter;
        formatter = new DecimalFormat("00");
        return formatter.format(num);
    }
}
