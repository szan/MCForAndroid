package com.anusiewicz.MCForAndroid.views;

import android.app.LauncherActivity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.anusiewicz.MCForAndroid.controllers.FileUtils;
import com.anusiewicz.MCForAndroid.model.Constants;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Szymon Anusiewicz
 */
public class ScreenListActivity extends ListActivity {

    private ArrayAdapter<String> directoryList;
    private List<String> fileList = new ArrayList<String>();
    private String fileDir = Environment.getExternalStorageDirectory().getPath() + "/Android/data/com.szymonanusiewicz.MCForAndroid/files/";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        registerForContextMenu(getListView());

        File directory = new File(Constants.FILES_DIRECTORY);
        directory.mkdirs();

        File yourFile = new File(Constants.FILES_DIRECTORY + "Engine Control");
        if (!yourFile.exists()) {
            FileUtils.writeToFile(getExampleFile(),yourFile.getPath());
        }

        File root = new File (Constants.FILES_DIRECTORY);
        ListDir(root);

    }

    void ListDir(File f){
        File[] files = f.listFiles();
        fileList.clear();
        if (files != null) {
            for (File file : files){
                fileList.add(file.getName());
            }
        }

        directoryList = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, fileList);
        setListAdapter(directoryList);
    }


    private String getExampleFile() {

        JSONObject file = new JSONObject();
        try {
            file.put(Constants.TITLE_TAG, "Engine Control");
            file.put(Constants.REFRESH_TIME_TAG,3);
            JSONArray devices = new JSONArray();
            JSONObject device1 = new JSONObject();
            device1.put(Constants.DEVICE_NAME_TAG,"Enkoder");
            device1.put(Constants.DEVICE_TYPE_TAG, "D");
            device1.put(Constants.DEVICE_NUMBER_TAG, 100);
            devices.put(device1);

            JSONObject device2 = new JSONObject();
            device2.put(Constants.DEVICE_NAME_TAG,"Kierunek");
            device2.put(Constants.DEVICE_TYPE_TAG, "X");
            device2.put(Constants.DEVICE_NUMBER_TAG,1);
            devices.put(device2);

            file.put(Constants.DEVICES_TAG,devices);

            return file.toString();

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }


    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        AdapterView.AdapterContextMenuInfo info;
        try {
            info = (AdapterView.AdapterContextMenuInfo) menuInfo;
        } catch (ClassCastException e) {
            e.printStackTrace();
            return;
        }
        String item = (String) getListAdapter().getItem(info.position);

        menu.setHeaderTitle(item);
        menu.add(Menu.NONE, info.position, Menu.NONE, "Delete");
        menu.add(Menu.NONE, info.position, Menu.NONE, "Open");
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle() == "Delete" ) {
            String name = (String) getListAdapter().getItem(item.getItemId());
            File file = new File(Constants.FILES_DIRECTORY + name);
            file.delete();
            View v = getListView().getChildAt(item.getItemId());
            //getListView().removeViewAt(item.getItemId());
            directoryList.remove(name);
            directoryList.notifyDataSetChanged();
        } else if (item.getTitle() == "Open" ) {
            String name = (String) getListAdapter().getItem(item.getItemId());
            Intent i = new Intent(this,DeviceControlActivity.class);
            i.putExtra(Constants.TITLE_TAG,name);
            startActivity(i);
        }
        return true;
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        TextView item = (TextView) l.getChildAt(position);

        Intent i = new Intent(this,DeviceControlActivity.class);
        i.putExtra(Constants.TITLE_TAG,item.getText());
        startActivity(i);

    }

}