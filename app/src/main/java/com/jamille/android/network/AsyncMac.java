package com.jamille.android.network;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Jamille on 12/06/2018.
 */

public class AsyncMac extends AsyncTask<String, String, String> {
    @Override
    protected String doInBackground(String... macUrl) {
        String result = "";
        URL url;
        String info = null;
        String vendorName = null;
        HttpURLConnection urlConnection;

        try { //open browser and fetch content of the url
            url = new URL("https://macvendors.co/api/" + macUrl[0]);
            Log.d("here :", " " + url);
            urlConnection = (HttpURLConnection) url.openConnection(); //open browser
            InputStream is = urlConnection.getInputStream();
            Log.d("inputStream: ", String.valueOf(is));
            InputStreamReader r = new InputStreamReader(is);
            int data = r.read();

            while (data != -1) {
                char current = (char) data;
                result += current;
                data = r.read();
            }

            JSONObject jsonObject = new JSONObject(result);
            info = jsonObject.getString("result");
            JSONObject jsonObject1 = new JSONObject(info);
            vendorName = jsonObject1.getString("company");
            Log.d("vendor: ", "party");
            return vendorName;

        } catch (Exception e) {
            Log.d("ex: ", String.valueOf(e));
        }
        return vendorName;
    }
}
