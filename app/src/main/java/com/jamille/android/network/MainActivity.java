package com.jamille.android.network;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static java.net.InetAddress.getByName;

public class MainActivity extends AppCompatActivity {
    int i = 1;
    Handler intruderHandler;
    public Boolean tableExists;
    ArrayList<String> client;
    MySqliteHandler databaseHandler;
    //ArrayAdapter adapter;
    ArrayList<ArpEntry> allClients;
    ArrayList<ArpEntry> checkClients;
    String connection;
    public CustomAdapter customAdapter;
    ListView listView;
    SimpleDateFormat formatter;
    ArrayList<String> arpListMac;
    ArrayList<String> databaseMac;
    String intruder;
    ProgressBar progressBar;
    MediaPlayer mediaPlayer;
    Handler handler;
    TextView action;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        action = (TextView) findViewById(R.id.action);
        handler = new Handler();
        intruderHandler = new Handler();
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        mediaPlayer = MediaPlayer.create(this, R.raw.intruder);
        formatter = new SimpleDateFormat("HH:mm    dd/MM/yyyy");
        listView = (ListView) findViewById(R.id.list);
        client = new ArrayList<>();
        databaseHandler = new MySqliteHandler(MainActivity.this);
        allClients = Util.getAllClients(databaseHandler);
        customAdapter = new CustomAdapter(this, allClients);
        listView.setAdapter(customAdapter);
        Button button = (Button) findViewById(R.id.button);
        if (!allClients.isEmpty()) {
            runnable.run();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(this, Settings.class);
        stopRun(); //stop runnable
        this.startActivity(intent);
        return true;
    }

    public void online(View view) {
        stopRun();
    }

    public void stopRun() {
        handler.removeCallbacks(runnable);
    }

    private Runnable runnable = new Runnable() {

        @Override
        public void run() {
            handler.postDelayed(this, 15000);
            Log.d("", "online");
            AsyncOnline task = new AsyncOnline();
            allClients = Util.getAllClients(databaseHandler);
            task.execute(allClients);
            ArrayList<ArpEntry> upDate = databaseHandler.getConnectedClients();
            customAdapter.updateList(upDate);
        }
    };

    public Runnable newRunnable = new Runnable() {
        @Override
        public void run() {
            AsyncIntruder task = new AsyncIntruder();
            WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);
            String ip = Util.getIpPing(wm);
            task.execute(ip);
        }
    };

    public void intruder(View view) {
        progressBar.setVisibility(View.VISIBLE);
        action.setText("");
        handler.removeCallbacks(runnable);
        intruderHandler.postDelayed(newRunnable, 15000);
    }

    public void mal(View view) {
        action.setText("");
        Log.d("selected", "mal");
        AsyncMal taskMal = new AsyncMal();
        taskMal.execute("");
    }

    public void dropTable() {
        SQLiteDatabase db = databaseHandler.getWritableDatabase();
        String sql = "drop table " + "clients";
        db.execSQL(sql);
    }

    public void click(View view) { //initial scan
        AsyncPing task = new AsyncPing();
        WifiManager wm = (WifiManager) getSystemService(WIFI_SERVICE);
        String ip = Util.getIpPing(wm);
        task.execute(ip);
    }

    public class AsyncPing extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... ip) {
            for (int i = 1; i < 255; i++) {
                String host = ip[0] + i;
                try {
                    publishProgress(host + " " + getByName(host).isReachable(10)); //300 seems the best but no apple
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return "done";
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            //TODO log pings
            Log.d("PING", "onProgressUpdate: " + values[0]);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Toast.makeText(MainActivity.this, "done", Toast.LENGTH_LONG).show();
            progressBar.setVisibility(View.INVISIBLE);
            try {
                TimeUnit.SECONDS.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ArrayList<ArpEntry> arpList = Util.getArpList();
            databaseHandler.addClient(arpList);
            allClients.clear();
            allClients.addAll(Util.getAllClients(databaseHandler));
            customAdapter.notifyDataSetChanged();
            runnable.run();
        }
    }


    public class AsyncOnline extends AsyncTask<ArrayList<ArpEntry>, String, String> {

        @Override
        protected String doInBackground(ArrayList<ArpEntry>... input) {
            client.clear();
            int id = 1;
            ArrayList<ArpEntry> clients = input[0];
            if (clients.size() > 0) {
                for (int i = 0; i < clients.size(); i++) {
                    ArpEntry entry = clients.get(i);
                    String host = entry.ipAddress;
                    try {
                        getByName(host).isReachable(1000);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    ArrayList<ArpEntry> arpList = null;
                    try {
                        arpList = Online.onlineArp();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    for (int j = 0; j < arpList.size(); j++) {
                        if (host.equals(arpList.get(j).ipAddress) && arpList.get(j).flag == 2) {
                            entry.online = "online";
                        }
                        if (host.equals(arpList.get(j).ipAddress) && arpList.get(j).flag == 0) {
                            if (entry.online.equals("online")) {
                                Date date = new Date();
                                String time = formatter.format(date);
                                entry.online = time;
                            }
                        }
                    }
                    arpList.clear();
                    databaseHandler.updateClient(id, entry.online);
                    publishProgress(host + " " + entry.online); //call here to update online immediatley
                    id++;
                }
            }
            return "done";
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            Log.d("PING", "onProgressUpdate: " + values[0]);
            ArrayList<ArpEntry> show = databaseHandler.getConnectedClients();
            allClients.clear();
            allClients.addAll(show);
            customAdapter.updateList(allClients);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d("", "done");
            ArrayList<ArpEntry> show = databaseHandler.getConnectedClients();
            for (int i = 0; i < show.size(); i++) {
                Log.d(show.get(i).ipAddress, show.get(i).online);
            }
            allClients.clear();
            allClients.addAll(show);
            customAdapter.updateList(allClients);
        }
    }


    public class AsyncIntruder extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {
            for (int i = 1; i < 255; i++) {
                String host = strings[0] + i;
                try {
                    publishProgress(host + " " + getByName(host).isReachable(10));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                TimeUnit.SECONDS.sleep(15);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            ArrayList<ArpEntry> arpListClients = Util.getArpList();
            ArrayList<ArpEntry> databaseClients = Util.getAllClients(databaseHandler);
            arpListMac = new ArrayList<String>();
            databaseMac = new ArrayList<String>();
            intruder = new String();
            for (int i = 0; i < arpListClients.size(); i++) {
                arpListMac.add(arpListClients.get(i).macAddress);
            }
            for (int p = 0; p < databaseClients.size(); p++) {
                databaseMac.add(databaseClients.get(p).macAddress);
            }
            for (int j = 0; j < arpListMac.size(); j++) {
                if (databaseMac.contains(arpListMac.get(j))) {
                } else {
                    intruder = arpListMac.get(j);
                }
            }
            if (!intruder.isEmpty()) {
                Log.i("intruder: ", intruder);
                //play intruder sound
                mediaPlayer.start();
                AsyncSocket task = new AsyncSocket();
                task.execute(intruder);
                action.setText("Intruder detected");
            } else {
                action.setText("System Secure");
                Log.i("intruder: ", "none");
            }
            arpListClients.clear();
            databaseClients.clear();
            arpListMac.clear();
            databaseMac.clear();
            intruder = null;
            progressBar.setVisibility(View.INVISIBLE);
            runnable.run();
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }
    }


    public class AsyncSocket extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            String intruder = strings[0];
            SharedPreferences sharedPreferences = getSharedPreferences("Linux info", Context.MODE_PRIVATE);
            String linuxIP = sharedPreferences.getString("linux ip", ""); //first paramter is key, if key doesnt exist then second parameter is returned, which is empty string
            String linuxPort = sharedPreferences.getString("linux port", "");
            try {
                Socket s = new Socket(linuxIP, Integer.parseInt(linuxPort));
                DataOutputStream out = new DataOutputStream(s.getOutputStream());
                out.writeBytes(intruder);
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }


    public class AsyncMal extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d("", "MAL ACT");
            int mal = 0;
            ArrayList<ArpEntry> arpListCheck = Util.getArpListForMal();
            ArrayList<String> list = new ArrayList<>();
            if (arpListCheck.size() > 0) {
                for (int i = 0; i < arpListCheck.size(); i++) {
                    ArpEntry entry = arpListCheck.get(i);
                    String macAdd = entry.macAddress;
                    list.add(macAdd);
                }
                for (int j = 0; j < list.size(); j++) {
                    for (int k = 0; k < list.size(); k++) {
                        if (j != k && list.get(j).equals(list.get(k))) {
                            Log.d("", "Found ARP spoof");
                            mal = 1;
                        } else {
                            Log.d("", "System secure");
                        }
                    }
                }
                if (mal == 1) {
                    action.setText("Malicious Activity Detected");
                    mediaPlayer.start();
                } else {
                    action.setText("System Secure");
                }
            }
            return;
        }
    }


}
