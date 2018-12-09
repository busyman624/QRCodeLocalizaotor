package qrcodelocalizator.qrcodelocalizator;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

//klasa odpowiadająca za komunikację z API
class Communicator {

    //metoda tworząca nowy obiekt klasy odpowiedzialnej za wywołanie zapytania GET /room/ping
    //na wcisniecie przycisku CHECK na MainActivity
    ResponseModel getAvailability(){
        ResponseModel response = null;
        try{
            response = new GetAvailability().execute().get();
        } catch (Exception e){
            e.printStackTrace();
        }
        return response;
    }

    //metoda tworzaca nowy obiekt klasy odpowiedzialnej za wywolanie zapytania POST /room
    //na wcisniecie przycisku ADD na AddRoomActivity
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

    //metoda tworzaca nowy obiekt klasy odpowiedzialnej za wywolanie zapytania GET /room/{roomNumber}
    //na wcisniecie przycisku GET na GetRoomActivity
    ResponseModel getRoom(String roomNumber){
        ResponseModel response = null;
        try{
            response = new GetRoom().execute(roomNumber).get();
            Gson gson = new Gson();
            response.setRoom(gson.fromJson(response.getMessage(), RoomModel.class));
        } catch (Exception e){
            e.printStackTrace();
        }
        return response;
    }

    //metoda tworzaca nowy obiekt klasy odpowiedzialnej za wywolanie zapytania POST /room/{roomNumber}/qrCode
    //na wcisniecie przycisku ADD na AddRoomActivity, zaraz wykonaniu requesta na POST /room jesli OK
    ResponseModel addQRCode(String roomId, byte[] qrCode){
        ResponseModel response = null;
        try{
            response = new PostQRCode(roomId).execute(qrCode).get();
        } catch (Exception e){
            e.printStackTrace();
        }

        return response;
    }

    //metoda tworzaca nowy obiekt klasy odpowiedzialnej za wywolanie zapytania GET /room/{roomNumber}/qrCode + GET /room/{roomNumber}/map
    //na wcisniecie przycisku GET na GetRoomActivity, zaraz po wykonaniu GET /room/{roomNumber} jesli OK
    Bitmap getAttachment(String roomNumber, String attachmentType){
        Bitmap attachment = null;
        try{
            attachment = new GetAttachment().execute(roomNumber, attachmentType).get();
        } catch (Exception e){
            e.printStackTrace();
        }

        return attachment;
    }

    //klasa umożliwiajaca asynchroniczne wywolanie requestu GET /room/ping do API, czyli żeby to się działo w tle
    private class GetAvailability extends AsyncTask<Void, Void, ResponseModel> {

        @Override
        protected ResponseModel doInBackground(Void ... params) {
            ResponseModel response = null;
            try {
                URL url = new URL("http://" + Config.serverIP + "/room/" + "ping");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                connection.setRequestProperty("Accept","application/json");
                connection.setConnectTimeout(5000);
                connection.setDoInput(true);

                response = new ResponseModel(connection.getResponseCode(), null);

                connection.disconnect();
            }
            catch (Exception e) {
                response = new ResponseModel(HttpURLConnection.HTTP_NOT_FOUND, null);
            }
            return response;
        }
    }


    //klasa umożliwiajaca asynchroniczne wywolanie requestu POST /room do API, czyli żeby to się działo w tle
    private class PostRoom extends AsyncTask<String, Void, ResponseModel> {

        @Override
        protected ResponseModel doInBackground(String... params) {
            ResponseModel response = null;
            try {
                URL url = new URL("http://" + Config.serverIP + "/room");
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

                String message="";
                if (connection.getResponseCode() == HttpsURLConnection.HTTP_CREATED) {
                    String line;
                    BufferedReader br=new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    while ((line=br.readLine()) != null) {
                        message+=line;
                    }
                }
                response = new ResponseModel(connection.getResponseCode(), message);

                connection.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return response;
        }
    }

    //klasa umożliwiajaca asynchroniczne wywolanie requestu GET /room/{roomNumber} do API, czyli żeby to się działo w tle
    private class GetRoom extends AsyncTask<String, Void, ResponseModel> {

        @Override
        protected ResponseModel doInBackground(String... params) {
            ResponseModel response = null;
            try {
                URL url = new URL("http://" + Config.serverIP + "/room/" + params[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                connection.setRequestProperty("Accept","application/json");
                connection.setDoInput(true);

                String message="";
                if (connection.getResponseCode() == HttpsURLConnection.HTTP_OK) {
                    String line;
                    BufferedReader br=new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    while ((line=br.readLine()) != null) {
                        message+=line;
                    }
                }
                response = new ResponseModel(connection.getResponseCode(), message);

                connection.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return response;
        }
    }

    //klasa umożliwiajaca asynchroniczne wywolanie requestu POST /room/{roomNumber}/qrCode do API, czyli żeby to się działo w tle
    private class PostQRCode extends AsyncTask<byte [], Void, ResponseModel> {

        private String roomId;

        String lineEnd  = "\r\n";
        String twoHyphens = "--";
        String boundary =  "*****";

        PostQRCode(String roomId){
            this.roomId = roomId;
        }

        @Override
        protected ResponseModel doInBackground(byte[]... params) {
            ResponseModel response = null;
            try {
                URL url = new URL("http://" + Config.serverIP + "/room/" + roomId + "/qrCode");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoOutput(true);
                connection.setDoInput(true);
                connection.setUseCaches(false);

                connection.setRequestMethod("POST");
                connection.setRequestProperty("Connection", "Keep-Alive");
                connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

                DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
                outputStream.writeBytes(twoHyphens + boundary + lineEnd);
                outputStream.writeBytes("Content-Disposition: form-data;" +
                        " name=\"" + "qrCode" + "\"; filename=\"" + "qrCode.jpg" +"\"" + lineEnd);
                outputStream.writeBytes("Content-Type: image/jpeg" + lineEnd);

                outputStream.writeBytes(lineEnd);
                outputStream.write(params[0]);
                outputStream.writeBytes(lineEnd);

                outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                outputStream.flush();
                outputStream.close();

                String message="";
                if (connection.getResponseCode() == HttpsURLConnection.HTTP_OK) {
                    String line;
                    BufferedReader br=new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    while ((line=br.readLine()) != null) {
                        message+=line;
                    }
                }
                response = new ResponseModel(connection.getResponseCode(), message);

                connection.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return response;
        }
    }

    //klasa umożliwiajaca asynchroniczne wywolanie requestu GET /room/{roomNumber}/(qrCode lub /map) do API, czyli żeby to się działo w tle
    private class GetAttachment extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap bitmap = null;
            try {
                URL url = new URL("http://" + Config.serverIP + "/room/" + params[0]
                        + "/" + params[1]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                connection.setRequestProperty("Accept","application/json");
                connection.setDoInput(true);

                if (connection.getResponseCode() == HttpsURLConnection.HTTP_OK) {
                    InputStream is = connection.getInputStream();
                    bitmap = BitmapFactory.decodeStream(is);
                }

                connection.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }
    }
}
