package mayur.com.savings;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.content.Context.MODE_PRIVATE;
import static mayur.com.savings.MainActivity.PREFERENCES_USERINFO;
import static mayur.com.savings.MainActivity.PRIMARY_CHANNEL_ID;
import static mayur.com.savings.MainActivity.mAction;
import static mayur.com.savings.MainActivity.mDate;
import static mayur.com.savings.MainActivity.mRupees;
import static mayur.com.savings.MainActivity.mUserInfo;


public class ReceiverBroadcastSms extends BroadcastReceiver {

    //Initializations...
    static String PRIMARY_CHANNEL_ID = "Code_for.NOTIFICATION";
    static String KEY_INT_ACTION = "key_int.for_mAction";
    static String KEY_INT_RUPEES = "key_int.for_mRupees";
    static String KEY_INT_DATE = "key_int.for_mDate";
    String KEY_RUPEES;
    String KEY_ACTION;
    String KEY_DATE;
    String senderNumber, message;
    int Rs_start, Rs_end;
    float rupees;
    String action;
    String date;
    int index;


    @Override
    public void onReceive(Context context, Intent intent) {
        //Initiallizing variables
        mAction = context.getSharedPreferences(MainActivity.PREFERENCES_ACTION, MODE_PRIVATE);
        mRupees = context.getSharedPreferences(MainActivity.PREFERENCES_RUPEES, MODE_PRIVATE);
        mDate = context.getSharedPreferences(MainActivity.PREFERENCES_DATE, MODE_PRIVATE);
        Bundle bundle = intent.getExtras();

        //Reading message
        if (bundle != null) {
            Object[] pdus = (Object[]) bundle.get("pdus");

            assert pdus != null;
            for (int i = 0; i < pdus.length; i++) {
                SmsMessage sms = SmsMessage.createFromPdu((byte[]) pdus[i]);

                senderNumber = sms.getOriginatingAddress();
                message = sms.getDisplayMessageBody();
                //Log.d("Tag", senderNumber+" "+message);
            }

            //Date and time...
            Date d = new Date();
            String dateFormat = "dd/MM/yy \t\t hh:mm";
            DateFormat dateFormat1 = new SimpleDateFormat(dateFormat);
            date = dateFormat1.format(d);
            Log.d("Save", "Date : " + date);

            //Calling function to Extract data...
            dataExtract(message, context);

            //Changing data..
            changeData(context);


            //Saving data..
            saveData(context);


            //Updating in runtime
            Intent runtime_intent = new Intent();
            runtime_intent.setAction("SMS_RECEIVED_FOR_RUNTIME");
            context.sendBroadcast(runtime_intent);

        }
    }

    //Changing the datas....
    public void changeData(Context context) {
        // Changing the data..
        mUserInfo = context.getSharedPreferences(PREFERENCES_USERINFO, MODE_PRIVATE);
        float currentBalance = mUserInfo.getFloat("currentBalance", 0);
        Log.d("Change", "currentBalance : " + currentBalance);
        if (action.equals("DEBITED")) {
            currentBalance -= rupees;
            Log.d("Change", "debited : " + Float.toString(currentBalance));
        }
        if (action.equals("CREDITED")) {
            currentBalance += rupees;
            Log.d("Change", "credited : " + Float.toString(currentBalance));
        }
        if(currentBalance < 0){
            currentBalance = 0;
            Log.d("Change", "currentBalance : " + Float.toString(currentBalance));
        }
        mUserInfo = context.getSharedPreferences(PREFERENCES_USERINFO, MODE_PRIVATE);
        SharedPreferences.Editor editCurrentBalance = mUserInfo.edit();
        editCurrentBalance.putFloat("currentBalance", currentBalance);
        editCurrentBalance.apply();
    }

    //Saving data...
    public void saveData(Context context) {
        //Saving data..
        if (!action.equals("") && rupees != 0.0) {
            Log.d("Save", "saveData");
            SharedPreferences.Editor editAction = mAction.edit();
            SharedPreferences.Editor editRupees = mRupees.edit();
            SharedPreferences.Editor editDate = mDate.edit();

            //Date..
            int keyDate = mDate.getInt(KEY_INT_DATE, 0);
            KEY_DATE = Integer.toString(keyDate);
            editDate.putString(KEY_DATE, date);
            keyDate++;
            editDate.putInt(KEY_INT_DATE, keyDate);

            //Action..
            int keyAction = mAction.getInt(KEY_INT_ACTION, 0);
            KEY_ACTION = Integer.toString(keyAction);
            editAction.putString(KEY_ACTION, action);
            keyAction++;
            editAction.putInt(KEY_INT_ACTION, keyAction);

            //Rupees..
            int keyRupees = mRupees.getInt(KEY_INT_RUPEES, 0);
            KEY_RUPEES = Integer.toString(keyRupees);
            editRupees.putFloat(KEY_RUPEES, rupees);
            keyRupees++;
            editRupees.putInt(KEY_INT_RUPEES, keyRupees);

            editAction.apply();
            editRupees.apply();
            editDate.apply();

            //saveData(context);

            Log.d("Save", "Action, Rupees, Date : " + mAction.getString(KEY_ACTION, " ") + " " + mRupees.getFloat(KEY_ACTION, 0) + " " + mDate.getString(KEY_DATE, ""));
            Log.d("Save", "keyAction, keyRupees, Date : " + mAction.getInt(KEY_INT_ACTION, 0) + " " + mRupees.getInt(KEY_INT_RUPEES, 0) + " " + mDate.getInt(KEY_INT_DATE, 0));
        }

    }


    //Extracting data.....
    public void dataExtract(String message, Context context) {
        message = message.toLowerCase();
//        String xxxx = "xx";
//        String deposit = "deposit";
//        String withdrew = "withdrew";
//        String withdraw = "withdraw";
//        String upiRefNo = "upi ref no";
        String debited = "debited";
        String debit = "debit";
        String credited = "credited";
        String credit = "credit";

        String rs = "rs.";
        String inr = "inr";

        //Checking Dates and rupees..
        int keyDate = mDate.getInt(KEY_INT_DATE, 0);
        int keyRupees = mRupees.getInt(KEY_INT_RUPEES, 0);
        keyRupees --;
        keyDate --;

        String datePrevious = mDate.getString(Integer.toString(keyDate), "");
        Float rupeesPrevious = mRupees.getFloat(Integer.toString(keyRupees), 0);

        Log.d("Save", "date, datePrevious : " + date + " " + datePrevious);

        //Changing id and saving new data in shared preference
        if (message.contains(debited) || message.contains(credited) || message.contains(debit) || message.contains(credit)) {
            if (message.contains(rs) || message.contains(inr)) {
                //Setting action....
                if (message.contains(debited) || message.contains(debit))
                    action = "DEBITED";
                else if (message.contains(credited) || message.contains(credit))
                    action = "CREDITED";

                //Setting rupees..
                if (message.contains(rs)) {
                    index = message.indexOf(rs);

                } else if (message.contains(inr)) {
                    index = message.indexOf(inr);

                }

                boolean flag = true;
                Rs_end = message.length();
                for (int i = index; i < message.length(); i++) {
                    char ch = message.charAt(i);
                    if (flag) {
                        if (Character.isDigit(ch)) {
                            Rs_start = i;
                            flag = false;
                        }
                    } else {
                        if (!Character.isDigit(ch) && ch != '.' && ch != ',') {
                            Rs_end = i;
                            break;
                        }
                    }
                }
                Log.d("Save", "Index : "+Integer.toString(Rs_start) + " " + Integer.toString(Rs_end));

                //Updating rupees with the extracted value
                String new_rs = message.substring(Rs_start, Rs_end);
                if(new_rs.contains(",")){
                    new_rs = new_rs.replace(",","");
                }
//                rupees = Float.parseFloat(message.substring(Rs_start, Rs_end));
                rupees = Float.parseFloat(new_rs);

                Log.d("Save", "Transaction : " + action + " " + Float.toString(rupees));
            }else{
                //Setting everything null
                action = "";
                rupees = 0.0f;
                return;
            }

        } else {
            //Setting everything null
            action = "";
            rupees = 0.0f;
            return;
        }

        if (date.equals(datePrevious) && rupees == rupeesPrevious) {
            action = "";
            rupees = 0;
        }
    }
}
