package com.jamille.android.network;

import android.util.Log;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Online {

    public static ArrayList<ArpEntry> onlineArp() throws IOException {
        ArrayList<ArpEntry> arpList = new ArrayList<>();
        BufferedReader bufferedReader = null;
        String line = null;
        String[] coloumn;
        try {
            bufferedReader = new BufferedReader(new FileReader("/proc/net/arp"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        while ((line = bufferedReader.readLine()) != null) {
            //Log.d("Read", line);
            if (line.startsWith("IP"))
                continue;  //if condition is true, continue makes it jump back (skip current iteration)
            coloumn = line.split(" +");
            String vendor = "";
            String online = "online";
            //Log.d("vendor: ", vendor);
            if (Integer.decode(coloumn[2]) != 0) {
            }
            ArpEntry temp = new ArpEntry(coloumn[0], Integer.decode(coloumn[1]),
                        Integer.decode(coloumn[2]), coloumn[3],
                        coloumn[4], coloumn[5], vendor, online);
                arpList.add(temp);
        }
        Log.d("size", "" + arpList.size());
        return arpList;
    }
}
