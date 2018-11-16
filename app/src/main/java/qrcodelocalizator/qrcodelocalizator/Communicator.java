package qrcodelocalizator.qrcodelocalizator;

import android.os.AsyncTask;
import com.google.gson.Gson;
import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

class Communicator {

    private String serverIP;

    void setServerIP(String serverIP){
        this.serverIP = serverIP;
    }

    String addRoom(RoomModel room) {
        Gson gson = new Gson();
        String jsonBody = gson.toJson(room);

        String statusCode = "-1";
        try{
            statusCode = new PostRoom().execute(jsonBody).get();
        } catch (Exception e){
            e.printStackTrace();
        }

        return statusCode;
    }

    private class PostRoom extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String statusCode="-1";
            try {
                URL url = new URL("http://" + serverIP + "/room/add");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                connection.setRequestProperty("Accept","application/json");
                connection.setDoOutput(true);
                connection.setDoInput(true);

                DataOutputStream os = new DataOutputStream(connection.getOutputStream());
                os.writeBytes(params[0]);

                os.flush();
                os.close();

                statusCode = Integer.toString(connection.getResponseCode());

                connection.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return statusCode;
        }
    }

}
