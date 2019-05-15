package com.jamille.android.network;

/**
 * Created by Jamille on 12/06/2018. This class is saved for future iterations
 */

/*public class AsyncPing extends AsyncTask<String, String, String> {
    @Override
    protected String doInBackground(String... ip) {

    for(int i=1; i<10; i++){
            String host = ip[0] + i;

            try {
                InetAddress.getByName(host).isReachable(1000);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        return "done";
        String host = ip[0] + ".179";
        InetAddress ping = null;

        try {
            ping = InetAddress.getByName(host);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        try {
            if (ping.isReachable(8000)){
                return "yes";
            } else {
                return "no";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
   // }

        @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }

}*/
