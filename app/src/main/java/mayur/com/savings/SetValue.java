package mayur.com.savings;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static mayur.com.savings.MainActivity.PREFERENCES_ACTION;
import static mayur.com.savings.MainActivity.PREFERENCES_DATE;
import static mayur.com.savings.MainActivity.PREFERENCES_RUPEES;
import static mayur.com.savings.MainActivity.PREFERENCES_USERINFO;
import static mayur.com.savings.MainActivity.mAction;
import static mayur.com.savings.MainActivity.mDate;
import static mayur.com.savings.MainActivity.mRupees;
import static mayur.com.savings.MainActivity.mUserInfo;
import static mayur.com.savings.ReceiverBroadcastSms.KEY_INT_ACTION;
import static mayur.com.savings.ReceiverBroadcastSms.KEY_INT_DATE;
import static mayur.com.savings.ReceiverBroadcastSms.KEY_INT_RUPEES;

public class SetValue extends AppCompatActivity {

    EditText rupees_editText;
    Button debit, credit, rupees_Button;
    float rupees;
    String action, date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_value);

        mAction = getSharedPreferences(PREFERENCES_ACTION, MODE_PRIVATE);
        mRupees = getSharedPreferences(PREFERENCES_RUPEES, MODE_PRIVATE);
        mDate = getSharedPreferences(PREFERENCES_DATE, MODE_PRIVATE);
        mUserInfo = getSharedPreferences(PREFERENCES_USERINFO, MODE_PRIVATE);

        rupees_editText = (EditText) findViewById(R.id.rupees);
        debit = (Button) findViewById(R.id.setValue_debited);
        credit = (Button) findViewById(R.id.setValue_credited);

        //Date and time...
        Date d = new Date();
        String dateFormat = "dd/MM/yy \t\t hh:mm";
        DateFormat dateFormat1 = new SimpleDateFormat(dateFormat);
        date = dateFormat1.format(d);

    }

    public void setValues(View view) {
        ReceiverBroadcastSms receiverBroadcastSms = new ReceiverBroadcastSms();
        if(!rupees_editText.getText().toString().equals("")){

            rupees = Float.parseFloat(rupees_editText.getText().toString());
            float currentBalance = mUserInfo.getFloat("currentBalance", 0);
            SharedPreferences.Editor editAction = mAction.edit();
            SharedPreferences.Editor editRupees = mRupees.edit();
            SharedPreferences.Editor editDate = mDate.edit();
            SharedPreferences.Editor editCurrentBalance = mUserInfo.edit();

            switch(view.getId()){
                case R.id.setValue_debited :
                    action = debit.getText().toString();
                    currentBalance -= rupees;
                    Log.d("Change", "debited : " + Float.toString(currentBalance));
                    break;
                case R.id.setValue_credited :
                    action = credit.getText().toString();
                    currentBalance += rupees;
                    Log.d("Change", "debited : " + Float.toString(currentBalance));
                    break;
            }

            //extracting values..

                //Date..
                int keyDate = mDate.getInt(KEY_INT_DATE, 0);
                String KEY_DATE = Integer.toString(keyDate);
                editDate.putString(KEY_DATE, date);
                keyDate++;
                editDate.putInt(KEY_INT_DATE, keyDate);

                //Action..
                int keyAction = mAction.getInt(KEY_INT_ACTION, 0);
                String KEY_ACTION = Integer.toString(keyAction);
                editAction.putString(KEY_ACTION, action);
                keyAction++;
                editAction.putInt(KEY_INT_ACTION, keyAction);

                //Rupees..
                int keyRupees = mRupees.getInt(KEY_INT_RUPEES, 0);
                String KEY_RUPEES = Integer.toString(keyRupees);
                editRupees.putFloat(KEY_RUPEES, rupees);
                keyRupees++;
                editRupees.putInt(KEY_INT_RUPEES, keyRupees);

                editAction.apply();
                editRupees.apply();
                editDate.apply();

            //Currentbalance..
            if(currentBalance < 0){
                currentBalance = 0;
            }
            editCurrentBalance.putFloat("currentBalance", currentBalance);
            editCurrentBalance.apply();

            //Starting main activity
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);

            Toast.makeText(this, "Value set", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, "Enter rupees", Toast.LENGTH_SHORT).show();
        }
    }
}
