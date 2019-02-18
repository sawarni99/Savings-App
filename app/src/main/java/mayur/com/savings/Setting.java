package mayur.com.savings;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import static mayur.com.savings.MainActivity.PREFERENCES_ACTION;
import static mayur.com.savings.MainActivity.PREFERENCES_DATE;
import static mayur.com.savings.MainActivity.PREFERENCES_RUPEES;
import static mayur.com.savings.MainActivity.PREFERENCES_USERINFO;
import static mayur.com.savings.MainActivity.*;

public class Setting extends AppCompatActivity {

    EditText name_edit, balance_edit;
    Button name_button, balance_button, delete_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        //Initiallizing variables
        name_edit = (EditText) findViewById(R.id.name_edit_setting);
        name_button = (Button) findViewById(R.id.name_button_setting);
        balance_edit = (EditText) findViewById(R.id.balance_edit_setting);
        balance_button = (Button) findViewById(R.id.balance_button_setting);
        delete_button = (Button) findViewById(R.id.delete_button_setting);


    }

    //Clicking button
    public void button_onclick(View view) {
        mAction = getSharedPreferences(PREFERENCES_ACTION, MODE_PRIVATE);
        mRupees = getSharedPreferences(PREFERENCES_RUPEES, MODE_PRIVATE);
        mDate = getSharedPreferences(PREFERENCES_DATE, MODE_PRIVATE);
        mUserInfo = getSharedPreferences(PREFERENCES_USERINFO, MODE_PRIVATE);

        SharedPreferences.Editor editAction = mAction.edit();
        SharedPreferences.Editor editRupees = mRupees.edit();
        SharedPreferences.Editor editDate = mDate.edit();
        SharedPreferences.Editor editUserInfo = mUserInfo.edit();

        switch(view.getId()){
            //if name button is pressed
            case R.id.name_button_setting :
                String name = name_edit.getText().toString();
                editUserInfo.putString("userName", name);
                editUserInfo.apply();
                name_edit.setText("");
                Toast.makeText(this, "Name updated", Toast.LENGTH_SHORT).show();
                break;
            //if balance button is pressed
            case R.id.balance_button_setting :
                float balance = Float.parseFloat(balance_edit.getText().toString());
                editUserInfo.putFloat("currentBalance", balance);
                editUserInfo.apply();
                balance_edit.setText("");
                Toast.makeText(this, "Balance updated", Toast.LENGTH_SHORT).show();
                break;
            //if delete button is pressed
            case R.id.delete_button_setting :
                editAction.clear().apply();
                editDate.clear().apply();
                editRupees.clear().apply();
                Toast.makeText(this, "Data removed", Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
