package com.anusiewicz.MCForAndroid.controllers;

import android.util.Log;

import java.io.*;

/**
 * Created with IntelliJ IDEA.
 * User: Szymon Anusiewicz
 */
public class FileUtils {

    public static boolean writeToFile(String data,String path) {
        try {
            File yourFile = new File(path);
            if(!yourFile.exists()) {
                  yourFile.createNewFile();
            }

            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(path));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
            return true;
        }
        catch (IOException e) {
            e.printStackTrace();
            Log.e("FileUtils", "File write failed: " + path);
            return false;
        }
    }


    public static String readFromFile(String path) {

        String ret = null;

        try {
            FileInputStream inputStream = new FileInputStream(path);

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString;
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.e("FileUtils", "File not found: " + path);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("FileUtils", "Can't read file: " + path);
        }

        return ret;
    }
}
