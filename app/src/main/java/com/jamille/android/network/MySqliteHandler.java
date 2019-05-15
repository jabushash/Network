package com.jamille.android.network;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by Jamille on 15/06/2018.
 */

public class MySqliteHandler extends SQLiteOpenHelper {
    private static final int DATABSE_VERSION = 1; //first database for network app
    private static final String DATABASE_NAME = "database.db"; //database name
    public static final String TABLE_CLIENTS = "clients";  // table name
    private static final String COLUMN_ID = " id";
    private static final String COLUMN_IP_ADDRESS = "ipAddress";
    private static final String COLUMN_MAC_ADDRESS = "macAddress";
    private static final String COLUMN_VENDOR = "vendor";
    private static final String COLUMN_ONLINE = "online";
    SQLiteDatabase mDatabase;
    //create the table
    String CREATE_CLIENT_TABLE = "CREATE TABLE " + TABLE_CLIENTS + "(" + COLUMN_ID +
            " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_IP_ADDRESS + " TEXT, " + COLUMN_MAC_ADDRESS +
            " TEXT, " + COLUMN_VENDOR + " TEXT, " + COLUMN_ONLINE + " TEXT);";

    public MySqliteHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABSE_VERSION);  //call contructor of superclass
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_CLIENT_TABLE); //create table
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CLIENTS);
        onCreate(db);
    }

    public void updateClient(int id, String connection) {
        SQLiteDatabase database = MySqliteHandler.this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ONLINE, connection);
        database.update(TABLE_CLIENTS, values, "id=" + id, null);
    }

    public void addClient(ArrayList<ArpEntry> arpList) {
        SQLiteDatabase databse = MySqliteHandler.this.getWritableDatabase();
        ContentValues values = new ContentValues();
        for (int i = 0; i < arpList.size(); i++) {
            values.put(COLUMN_IP_ADDRESS, arpList.get(i).ipAddress);
            values.put(COLUMN_MAC_ADDRESS, arpList.get(i).macAddress);
            values.put(COLUMN_VENDOR, arpList.get(i).vendor);
            values.put(COLUMN_ONLINE, arpList.get(i).online);
            databse.insert(TABLE_CLIENTS, null, values);
        }
        databse.close();
    }

    public ArrayList<ArpEntry> getConnectedClients() {
        ArrayList<ArpEntry> clientList = new ArrayList<>();
        String selectAllQuery = "SELECT * FROM " + TABLE_CLIENTS;
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery(selectAllQuery, null);
        if (cursor.moveToFirst()) {
            do {
                ArpEntry client = new ArpEntry();
                client.setId(Integer.parseInt(cursor.getString(0)));
                client.setIpAddress(cursor.getString(1));
                client.setMacAddress(cursor.getString(2));
                client.setVendor(cursor.getString(3));
                client.setOnline(cursor.getString(4));
                clientList.add(client);
            } while (cursor.moveToNext());
        }
        return clientList;
    }

    public int getClientCount() {
        String clientCountQuery = "SELECT * FROM " + TABLE_CLIENTS;
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery(clientCountQuery, null);
        cursor.close();
        return cursor.getCount();
    }
}
