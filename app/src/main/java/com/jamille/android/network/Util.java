package com.jamille.android.network;

import android.net.wifi.WifiManager;
import android.text.format.Formatter;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Created by Jamille on 07/06/2018.
 */

public class Util {
    private static ArrayList<String> client;
    private static ArrayList<ArpEntry> allClients = null;

    public static ArrayList<ArpEntry> getArpList() {
        ArrayList<ArpEntry> arpList = new ArrayList<>();
        BufferedReader bufferedReader = null;
        String line = null;
        String[] coloumn;
        try {
            bufferedReader = new BufferedReader(new FileReader("/proc/net/arp"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            while ((line = bufferedReader.readLine()) != null) {
                if (line.startsWith("IP"))
                    continue;  //if condition is true, continue makes it jump back (skip current iteration)
                coloumn = line.split(" +");
                String macAdress = coloumn[3];
                AsyncMac task = new AsyncMac();
                String vendor = task.execute(macAdress).get();
                //TimeUnit.SECONDS.sleep(10);
                String online = "online";
                if (Integer.decode(coloumn[2]) != 0) {
                    Log.d("ARP", line);
                    ArpEntry temp = new ArpEntry(coloumn[0], Integer.decode(coloumn[1]),
                            Integer.decode(coloumn[2]), coloumn[3],
                            coloumn[4], coloumn[5], vendor, online);
                    arpList.add(temp);
                }
            }
            Log.d("size", "" + arpList.size());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return arpList;
    }

    public static String getIpPing(WifiManager wm){
        String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
        String [] splitIP = ip.split("\\.", 4);
        String ipPing = splitIP[0] + "." + splitIP[1] + "." + splitIP[2] + ".";
        Log.d("ipPing: ", ipPing);
        return ipPing;
    }

    public static ArrayList getClients(MySqliteHandler databaseHandler){
        allClients = databaseHandler.getConnectedClients();
        client = new ArrayList<>();
        if (allClients.size() > 0) {
            for (int i = 0; i < allClients.size(); i++) {
                ArpEntry entry = allClients.get(i);
                client.add(entry.ipAddress + "  -  " + entry.macAddress + "  -  " + entry.vendor + "  -  " + entry.online);
            }
        }
        Log.d("database count", "" + allClients.size());
        return client;
    }

    public static   ArrayList<ArpEntry> getAllClients(MySqliteHandler databaseHandler){
        allClients = databaseHandler.getConnectedClients();
        return allClients;
    }

    public static ArrayList<ArpEntry> getArpListForMal() {
        ArrayList<ArpEntry> arpList = new ArrayList<>();
        BufferedReader bufferedReader = null;
        String line = null;
        String[] coloumn;
        try {
            bufferedReader = new BufferedReader(new FileReader("/proc/net/arp"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            while ((line = bufferedReader.readLine()) != null) {
                if (line.startsWith("IP"))
                    continue;
                coloumn = line.split(" +");
                String vendor = "";
                String online = "";
                //Log.d("vendor: ", vendor);
                if (Integer.decode(coloumn[2]) != 0) {
                    Log.d("ARP", line);
                    ArpEntry temp = new ArpEntry(coloumn[0], Integer.decode(coloumn[1]),
                            Integer.decode(coloumn[2]), coloumn[3],
                            coloumn[4], coloumn[5], vendor, online);
                    arpList.add(temp);
                }
            }
            Log.d("size", "" + arpList.size());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return arpList;
    }

}
