package com.technion.nssl.oneday;


import java.net.HttpURLConnection;

public abstract class Handle_http {
    HttpURLConnection client;
    StringBuilder Response = new StringBuilder(); // Using StringBuilder to reduce memory usage (StackOverflow)
    String Temp_Response;
    abstract String handle(String address, String body_param);
}
