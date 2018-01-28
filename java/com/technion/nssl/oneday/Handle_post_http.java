package com.technion.nssl.oneday;
import android.app.AlertDialog;
import android.util.Log;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class Handle_post_http extends Handle_http {
  /*  HttpURLConnection client;
    StringBuilder Response = new StringBuilder(); // Using StringBuilder to reduce memory usage (StackOverflow)
    String Temp_Response;*/
    public String handle(String address, String body_param) {
        try {
            URL url = new URL(address);
            client = (HttpURLConnection) url.openConnection();
            client.setRequestMethod("POST");
            client.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            client.setFixedLengthStreamingMode(body_param.getBytes().length);
            client.setDoOutput(true);
            //send req to server
            OutputStreamWriter Request_stream = new OutputStreamWriter(client.getOutputStream());

            Request_stream.write(body_param);

            Request_stream.flush();
            Request_stream.close();
            //read response from server

            InputStream _is;
            if (client.getResponseCode() / 100 == 2) { // 2xx code means success
                _is = client.getInputStream();
                InputStreamReader Response_stream = new InputStreamReader(_is);
                BufferedReader server_rsp = new BufferedReader(Response_stream);
                Temp_Response = server_rsp.readLine();
                while (Temp_Response != null) {
                    Response.append(Temp_Response);
                    Temp_Response =server_rsp.readLine();
                }
//            Response.append('}');

                server_rsp.close();
            } else {
                _is = client.getErrorStream();
                String result = _is.toString();
                Log.i("Error != 2xx", result);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Response.setLength(0);
            Response.append("ERROR");
        } finally {
            client.disconnect();
        }
//        String ret = "{\"UnregisteredEvents\":[{\"activityID\":3,\"name\":\"Animals\",\"startTime\":\"2017-06-15T13:00:00.000Z\",\"endTime\":\"2017-06-15T13:00:00.000Z\",\"capacity\":\"40\",\"currentRegistered\":null,\"registeredUsers\":null,\"isActive\":null,\"organizationID\":null}]}";
/*        if (Response.indexOf("[") != -1) {
            Response.deleteCharAt(Response.indexOf("["));
            Response.deleteCharAt(Response.indexOf("]"));
        }*/
//        return ret;
        return Response.toString();
    }
}
