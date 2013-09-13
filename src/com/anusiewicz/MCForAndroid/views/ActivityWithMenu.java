package com.anusiewicz.MCForAndroid.views;

import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.anusiewicz.MCForAndroid.R;
import com.anusiewicz.MCForAndroid.controllers.ConnectionManager;
import com.anusiewicz.MCForAndroid.controllers.MCForAndroidApplication;

/**
 * Created with IntelliJ IDEA.
 * User: Szymon Anusiewicz
 */
public class ActivityWithMenu extends Activity {

    protected ConnectionManager connectionManager = MCForAndroidApplication.getConnectionManager();

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if(item.getItemId() == R.id.menu_item_exit){
            //connectionManager.disconnectFromRemoteHost();
            this.finish();
            return true;
        } else if (item.getItemId() == R.id.menu_item_conn) {
            connectionManager.showConnectionActivity(this);
        }
        return false;
    }

}
