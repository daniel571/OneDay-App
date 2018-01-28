package com.technion.nssl.oneday;
import android.util.Log;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class Handle_get_http extends Handle_http {

    public String handle(String address, String body_param) {
        Response.setLength(0);
        try {
            URL url = new URL(address+"?"+body_param);
            client = (HttpURLConnection) url.openConnection();
            client.setRequestMethod("GET");
            //read response from server
            InputStreamReader Response_stream = new InputStreamReader(client.getInputStream());
            BufferedReader server_rsp = new BufferedReader(Response_stream);
            Temp_Response = server_rsp.readLine();
            while (Temp_Response != null) {
                Response.append(Temp_Response);
                Temp_Response =server_rsp.readLine();
            }
//            Response.append('}');
            server_rsp.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            client.disconnect();
        }

//        return ret;
        return Response.toString();
    }
}
