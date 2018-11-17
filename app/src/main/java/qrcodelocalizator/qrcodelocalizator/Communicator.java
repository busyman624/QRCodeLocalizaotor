package qrcodelocalizator.qrcodelocalizator;

import android.os.AsyncTask;
import com.google.gson.Gson;
import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

class Communicator {

    ResponseModel addRoom(RoomModel room) {
        Gson gson = new Gson();
        String jsonBody = gson.toJson(room);

        ResponseModel response = null;
        try{
            response = new PostRoom().execute(jsonBody).get();
        } catch (Exception e){
            e.printStackTrace();
        }

        return response;
    }

    private class PostRoom extends AsyncTask<String, Void, ResponseModel> {

        @Override
        protected ResponseModel doInBackground(String... params) {
            ResponseModel response = null;
            try {
                URL url = new URL("http://" + Config.serverIP + "/room/add");
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

                response = new ResponseModel(connection.getResponseCode(),
                        connection.getResponseMessage());

                connection.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return response;
        }
    }

}
