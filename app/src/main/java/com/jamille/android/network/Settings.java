package com.jamille.android.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class Settings extends AppCompatActivity {
    EditText linuxIP;
    public SharedPreferences sharedPreferences;
    EditText linuxPort;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ActionBar actionBar = getSupportActionBar();
        //actionBar.setTitle("Settings");  //not changing in manifest, so i hard coded it here
        actionBar.setDisplayHomeAsUpEnabled(true); //show the go back arrow
        linuxIP = (EditText) findViewById(R.id.linuxIP);
        linuxPort = (EditText) findViewById(R.id.linuxPort);
    }

    public void save(View view){
         sharedPreferences = getSharedPreferences("Linux info", Context.MODE_PRIVATE);
        //private so only this app can access this sharedpref file, linux info file
        SharedPreferences.Editor editor = sharedPreferences.edit(); //this object allows us to write to sharedpref file
        editor.putString("linux ip", linuxIP.getText().toString());   //shared pref is simple database with 2 columns, key and value
        editor.putString("linux port", linuxPort.getText().toString());
        editor.apply(); //add the shared prefrence strings to shared pref file
        Toast.makeText(this, "Saved", Toast.LENGTH_LONG).show();
    }

    public void clear(View view){
        getApplicationContext().deleteDatabase("database.db"); //clear the database
    }

    public Settings(){
    }

    public String getLinuxIP(){
        SharedPreferences sharedPreferences = getSharedPreferences("Linux info", Context.MODE_PRIVATE);
        String linuxIP = sharedPreferences.getString("linux ip", ""); //first paramter is key, if key doesnt exist then second parameter is returned, which is empty string
        Log.d("shared pref: ", linuxIP);
        return linuxIP;
    }

    public String getLinuxPort(){
        SharedPreferences sharedPreferences = getSharedPreferences("Linux info", Context.MODE_PRIVATE);
        //first paramter is key, if key doesnt exist then second parameter is returned, which is empty string
        String linuxPort = sharedPreferences.getString("linux port", "");
        Log.d("shared pref: ", linuxPort);
        return linuxPort;
    }

}
