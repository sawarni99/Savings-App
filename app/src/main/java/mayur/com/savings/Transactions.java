package mayur.com.savings;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

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

public class Transactions extends AppCompatActivity {

    TextView name;
    LinearLayout layout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transactions);

        name = (TextView) findViewById(R.id.name_transaction);
        layout = (LinearLayout) findViewById(R.id.layout_transaction);
        mAction = getSharedPreferences(PREFERENCES_ACTION, MODE_PRIVATE);
        mRupees = getSharedPreferences(PREFERENCES_RUPEES, MODE_PRIVATE);
        mDate = getSharedPreferences(PREFERENCES_DATE, MODE_PRIVATE);
        mUserInfo = getSharedPreferences(PREFERENCES_USERINFO, MODE_PRIVATE);

        String nameUserInfo = mUserInfo.getString("userName", "NAME");
        String s = "Hello!! "+nameUserInfo+"\nYour transactions are : ";
        name.setText(s);

        //calling function to display the datas...
        displayAllData();
    }

    private void displayAllData() {
        layout.removeAllViews();
        int keyRupees = mRupees.getInt(KEY_INT_RUPEES, 0);
        float rupees ;

        int keyAction = mAction.getInt(KEY_INT_ACTION, 0);
        String action ;

        int keyDate = mDate.getInt(KEY_INT_DATE, 0);
        String date ;

        //Displaying data : dd/mm/yy hh:mm      000.00      ACTION
        for (int i = (keyAction - 1); i >= 0; i--) {
            TextView stats = new TextView(this);
            stats.setTextSize(18);
            String s;
            action = mAction.getString(Integer.toString(i), "");
            rupees = mRupees.getFloat(Integer.toString(i), 0);
            date = mDate.getString(Integer.toString(i), "dd/MM/yy hh:mm");
            if (action.equals("DEBITED")) {
                s = date + "  \t\t\t " + action + " \t\t\t " + rupees;
            } else {
                s = date + "  \t\t\t " + action + " \t " + rupees;
            }
            stats.setText(s);
            layout.addView(stats);
        }
    }
}
