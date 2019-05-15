package com.jamille.android.network;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import java.io.DataOutputStream;
import java.net.Socket;

public class Kicked {
    String macToKick;
    Context context;

    public Kicked() { // empty constructor
    }

    public void kickUser(String macToKick, Context context) {
        this.macToKick = macToKick;
        this.context = context;
        AsyncKickUser task = new AsyncKickUser();
        task.execute(macToKick);
    }

    public class AsyncKickUser extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {
            SharedPreferences sharedPreferences = context.getSharedPreferences("Linux info", Context.MODE_PRIVATE);
            String linuxIP = sharedPreferences.getString("linux ip", ""); //first paramter is key, if key doesnt exist then second parameter is returned, which is empty string
            String linuxPort = sharedPreferences.getString("linux port", "");
            Socket s = null;
            try {
                s = new Socket(linuxIP, Integer.parseInt(linuxPort));
                DataOutputStream out = new DataOutputStream(s.getOutputStream());
                out.writeBytes(macToKick);
                out.close();
            } catch (
                    Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}

