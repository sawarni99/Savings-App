package mayur.com.savings;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import static mayur.com.savings.ReceiverBroadcastSms.KEY_INT_ACTION;
import static mayur.com.savings.ReceiverBroadcastSms.KEY_INT_DATE;
import static mayur.com.savings.ReceiverBroadcastSms.KEY_INT_RUPEES;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    //Initializing Variables
    TextView balance, header_text;
    NotificationManager mNotify;
    LinearLayout layout;
    View divider;
    SmsManager smsManager;
    int PERMISSION_SMS = 1;
    static String PRIMARY_CHANNEL_ID = "Code_for.NOTIFICATION";
    float currentBalance;
    static SharedPreferences mUserInfo;
    static String PREFERENCES_USERINFO = "_code_preference_userInfo";
    static SharedPreferences mAction, mRupees, mDate;
    static String PREFERENCES_ACTION = "_code_preference_action";
    static String PREFERENCES_RUPEES = "_code_preference_rupees";
    static String PREFERENCES_DATE = "_code_preference_date";
    IntentFilter runtime_intentFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initializations..
        balance = (TextView) findViewById(R.id.balance);
        layout = (LinearLayout) findViewById(R.id.layout);
        divider = (View) findViewById(R.id.divider);
        mAction = getSharedPreferences(PREFERENCES_ACTION, MODE_PRIVATE);
        mRupees = getSharedPreferences(PREFERENCES_RUPEES, MODE_PRIVATE);
        mDate = getSharedPreferences(PREFERENCES_DATE, MODE_PRIVATE);
        mUserInfo = getSharedPreferences(PREFERENCES_USERINFO, MODE_PRIVATE);
        runtime_intentFilter = new IntentFilter();
        runtime_intentFilter.addAction("SMS_RECEIVED_FOR_RUNTIME");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        setSupportActionBar(toolbar);

        //Setting up initial values : User name and current values
        String userName = mUserInfo.getString("userName", "");
        currentBalance = mUserInfo.getFloat("currentBalance", -1);
        if (userName.equals("") || currentBalance == -1) {
            Intent intent = new Intent(this, userInfo.class);
            Log.d("Tag", "userInfo activity");
            startActivity(intent);
        }

        //Cheacking permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Grant permission to run in background", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS); //Opening settings to grant background running process
            Uri uri = Uri.fromParts("package", getPackageName(), null);
            intent.setData(uri);
            startActivity(intent);

            Log.d("tag", "Permission Is Not Granted For Receiving SMS");
            //Asking to grant permission for receiving SMS
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_SMS}, PERMISSION_SMS);
        } else {
            Log.d("tag", "Permission Granted For Receiving SMS");
        }

        //Opening navigation bar
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //Changing drawer text_header : changing name.
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View username = navigationView.getHeaderView(0);
        header_text = (TextView) username.findViewById(R.id.header_text);
        header_text.setText(userName);
        navigationView.setNavigationItemSelectedListener(this);

        //Calling function to Displaying saved data..
        displayData();

    }

    //Displaying data
    private void displayData() {
        layout.removeAllViews();
        //Getting rupees id
        int keyRupees = mRupees.getInt(KEY_INT_RUPEES, 0);
        float rupees = mRupees.getFloat(Integer.toString(keyRupees - 1), 0);

        //Getting action id
        int keyAction = mAction.getInt(KEY_INT_ACTION, 0);
        String action = mAction.getString(Integer.toString(keyAction - 1), "");

        //Getting date id
        int keyDate = mDate.getInt(KEY_INT_DATE, 0);
        String date = mDate.getString(Integer.toString(keyDate - 1), "dd/MM/yy hh:mm");

        //Getting current balance
        currentBalance = mUserInfo.getFloat("currentBalance", -1);

        if (currentBalance != -1) {
            balance.setText(Float.toString(currentBalance));
        }

        //Displaying all datas : dd/mm/yy hh:mm     000.00      ACTION
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


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    //Navgation bar
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Intent itemSelect;
        if (id == R.id.allTransaction) {
            //Opening transaction activity
            itemSelect = new Intent(this, Transactions.class);
            startActivity(itemSelect);
            Log.d("Tag", "Transactions");
        } else if (id == R.id.setting) {
            //Opening Setteing activity
            itemSelect = new Intent(this, Setting.class);
            startActivity(itemSelect);
            Log.d("Tag", "Setting");
        }
        //Cloasing drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //setValue
    public void setValue_onClick(View view) {
        //opening SetValue Activity
        Intent value = new Intent(this, SetValue.class);
        startActivity(value);
    }

    //Transactions
    public void Transaction_onClick(View view) {
        //Opening transaction activity
        Intent itemSelect;
        itemSelect = new Intent(this, Transactions.class);
        startActivity(itemSelect);
        Log.d("Tag", "Transactions");
    }

    //Displaying in runtime....
    BroadcastReceiver RunimeDisplay_Receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            displayData();
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(RunimeDisplay_Receiver, runtime_intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(RunimeDisplay_Receiver);
    }

}
