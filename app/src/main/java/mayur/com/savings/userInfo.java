package mayur.com.savings;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import static mayur.com.savings.MainActivity.PREFERENCES_USERINFO;
import static mayur.com.savings.MainActivity.mUserInfo;

public class userInfo extends AppCompatActivity {

    TextView textView;
    Button next;
    EditText editText;
    int page;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        //Initializations...
        page = 0;
        mUserInfo = getSharedPreferences(PREFERENCES_USERINFO, MODE_PRIVATE);
        textView = (TextView) findViewById(R.id.userInfo);
        editText = (EditText) findViewById(R.id.editText);
        next = (Button) findViewById(R.id.next_userInfo);

    }

    public void next_userInfo_onClick(View view) {
        SharedPreferences.Editor edit = mUserInfo.edit();
        switch (page){
            //Page 1
            case 0 :
                String name = editText.getText().toString();
                if(name.trim().length() != 0) {
                    page++;
                    textView.setText(R.string.enter_current_balance);
                    editText.setHint("Enter your balance");
                    editText.setText("");

                    edit.putString("userName", name);
                    edit.apply();
                }else{
                    Toast.makeText(this, "Enter Your name", Toast.LENGTH_SHORT).show();
                }
                break;

            //Page 2
            case 1 :
                String balance = editText.getText().toString();
                int i;
                if(balance.trim().length() != 0) {
                    for (i = 0; i < balance.length(); i++) {
                        char ch = balance.charAt(i);
                        if (!Character.isDigit(ch) && ch != '.') {
                            editText.setText("");
                            Toast.makeText(this, "Invalid Number", Toast.LENGTH_SHORT).show();
                            break;
                        }
                    }
                    if (i >= balance.length()) {
                        page++;
                        editText.setVisibility(View.INVISIBLE);
                        String s = "Hello!!\n" + mUserInfo.getString("userName", "");
                        textView.setText(s);

                        float balance_float = Float.parseFloat(balance);
                        edit.putFloat("currentBalance", balance_float);
                        edit.apply();
                    }
                }else{
                    Toast.makeText(this, "Enter your current balance", Toast.LENGTH_SHORT).show();
                }
                break;

            //Page 3
            case 2 :
                Intent intent = new Intent(this, MainActivity.class);
                Log.d("userInfo", "new Intent Main Activity");
                startActivity(intent);
        }
    }
}
