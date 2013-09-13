package com.anusiewicz.MCForAndroid.views;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.anusiewicz.MCForAndroid.R;
import com.anusiewicz.MCForAndroid.controllers.MCForAndroidApplication;

/**
 * Created with IntelliJ IDEA.
 * User: Szymon Anusiewicz
 */
public class MenuActivity  extends Activity implements View.OnClickListener {


    private Button bConnection, bCustomCommand, bControlScreen, bNewScreen, bExit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu_layout);

        bConnection = (Button) findViewById(R.id.buttonConnection);
        bConnection.setOnClickListener(this);
        bCustomCommand = (Button) findViewById(R.id.buttonCustom);
        bCustomCommand.setOnClickListener(this);
        bControlScreen = (Button) findViewById(R.id.buttonControlScreens);
        bControlScreen.setOnClickListener(this);
        bNewScreen = (Button) findViewById(R.id.buttonNewScreen);
        bNewScreen.setOnClickListener(this);
        bExit = (Button) findViewById(R.id.buttonExit);
        bExit.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MCForAndroidApplication.getConnectionManager().disconnectFromRemoteHost();
    }

    @Override
    public void onClick(View view) {

        switch(view.getId()) {
            case R.id.buttonConnection:
                MCForAndroidApplication.getConnectionManager().showConnectionActivity(this);
                break;
            case R.id.buttonCustom:
                Intent i = new Intent(this,CustomCommandActivity.class);
                startActivity(i);
                break;
            case R.id.buttonControlScreens:
                Intent j = new Intent(this,ScreenListActivity.class);
                startActivity(j);
                break;
            case R.id.buttonNewScreen:
                Intent k = new Intent(this,NewScreenActivity.class);
                startActivity(k);
                break;
            case R.id.buttonExit:
                finish();
                break;
            default:
                break;
        }

    }
}
