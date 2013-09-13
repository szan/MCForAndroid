package com.anusiewicz.MCForAndroid.views;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import com.anusiewicz.MCForAndroid.R;
import com.anusiewicz.MCForAndroid.model.Constants;

/**
 * Created with IntelliJ IDEA.
 * User: Szymon Anusiewicz
 */
public class NewScreenActivity extends Activity {

    private static final String TAG = "NewScreenActivity";

    private String screenName;
    private String refreshTime;

    EditText screenNameText, refreshTimeText;
    Button bCreate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_screen_layout);

        refreshTimeText = (EditText) findViewById(R.id.refreshTimeText);
        refreshTimeText.setText("");
        screenNameText = (EditText) findViewById(R.id.screenNameText);
        screenNameText.setText("");
        bCreate = (Button) findViewById(R.id.buttonCreate);
        bCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                screenName = screenNameText.getText().toString();
                refreshTime = refreshTimeText.getText().toString();

                if (screenName.matches("")) {
                    Toast.makeText(NewScreenActivity.this,"Specify screen name", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (refreshTime.matches("")) {
                    Toast.makeText(NewScreenActivity.this,"Specify refresh time", Toast.LENGTH_SHORT).show();
                    return;
                }

                Integer refTime;

                try {
                    refTime = new Integer(refreshTimeText.getText().toString());
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    Toast.makeText(NewScreenActivity.this,"Insert numeric value",Toast.LENGTH_SHORT).show();
                    return;
                }

                if (refTime <= 0 ) {
                    Toast.makeText(NewScreenActivity.this,"Invalid refresh time",Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent i = new Intent(NewScreenActivity.this,DeviceControlActivity.class);
                i.putExtra(Constants.SCREEN_NAME_TAG, screenName);
                i.putExtra(Constants.REFRESH_TIME_TAG, refTime);
                startActivity(i);
            }
        });
    }
}
