package com.jamille.android.network;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomAdapter extends BaseAdapter {
    public ArrayList<ArpEntry> allClients;
    public Context context;

    public CustomAdapter(@NonNull Context context, ArrayList<ArpEntry> allClients) {
        this.allClients = allClients;
        this.context = context;
    }

    @Override
    public int getCount() {
        return allClients.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) { //return custom listveiw
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.listitem, parent, false);
        TextView ipText = (TextView) row.findViewById(R.id.ipText);
        TextView macText = (TextView) row.findViewById(R.id.macText);
        TextView vendorText = (TextView) row.findViewById(R.id.vendorText);
        TextView onlineText = (TextView) row.findViewById(R.id.onlineText);
        Button kick = (Button) row.findViewById(R.id.kick);
        ipText.setText("IP Address: " + allClients.get(position).ipAddress);
        macText.setText("MAC Address: " + allClients.get(position).macAddress);
        vendorText.setText(allClients.get(position).vendor);
        if ((allClients.get(position).online).equals("online")) {
            onlineText.setTextColor(ContextCompat.getColor(context, R.color.online));
        } else {
            onlineText.setTextColor(ContextCompat.getColor(context, R.color.offline));
        }
        onlineText.setText(allClients.get(position).online);
        final String mac = allClients.get(position).macAddress;
        kick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Kicked userKick = new Kicked();
                userKick.kickUser(mac, context);
            }
        });
        return row;
    }

    public void updateList(ArrayList<ArpEntry> newlist) {
        allClients.clear();
        allClients.addAll(newlist);
        this.notifyDataSetChanged();
    }
}
