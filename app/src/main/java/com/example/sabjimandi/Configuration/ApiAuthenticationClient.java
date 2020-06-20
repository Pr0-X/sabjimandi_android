package com.example.sabjimandi.Configuration;

import android.util.Base64;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ApiAuthenticationClient {
    private String baseUrl;
    private String username;
    private String password;
    private String urlResources;
    private String httpMethod;
    private String urlPath;
    private String lastResponse;
    private String payload;
    private HashMap<String, String> parameter;
    private Map<String, List<String>> headerFields;
    private URLEncoder Base64Encoder;

    public ApiAuthenticationClient(String baseUrl, String username,String password){
        setBaseUrl(baseUrl);
        this.username=username;
        this.password=password;
        this.urlPath="";
        this.urlResources="";
        this.httpMethod="GET";
        parameter=new HashMap<>();
        lastResponse="";
        payload="";
        headerFields = new HashMap<>();
        System.setProperty("jsse.enableSNIExtension","false");
    }
    public ApiAuthenticationClient setBaseUrl(String baseUrl){
        this.baseUrl= baseUrl;
        if (!baseUrl.substring(baseUrl.length() - 1).equals("/")){
            this.baseUrl += "/";
        }
        return this;
    }
    public ApiAuthenticationClient setUrlResources(String urlResources){
        this.urlResources = urlResources;
        return this;
    }
    public final ApiAuthenticationClient setUrlPath(String urlPath){
        this.urlPath= urlPath;
        return this;
    }
    public ApiAuthenticationClient setHttpMethod(String httpMethod){
        this.httpMethod= httpMethod;
        return this;
    }

    public String getLastResponse() {
        return lastResponse;
    }

    public Map<String, List<String>> getHeaderFields() {
        return headerFields;
    }

    public ApiAuthenticationClient setParameter(HashMap<String, String> parameter) {
        this.parameter = parameter;
        return this;
    }
    public ApiAuthenticationClient clearParameter(){
        this.parameter.clear();
        return this;
    }
    public ApiAuthenticationClient removeparameter(String key){
        this.parameter.remove(key);
        return this;
    }
    public ApiAuthenticationClient clearAll(){
        parameter.clear();
        baseUrl="";
        this.username="";
        this.password="";
        this.urlResources="";
        this.urlPath="";
        this.httpMethod="";
        lastResponse="";
        payload="";
        headerFields.clear();
        return this;
    }
    public JSONObject getLastResponseAsJsonObject(){
        try {
            return new JSONObject(String.valueOf(lastResponse));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
    public JSONArray getLastResponseAsJsonArray(){
        try {
            return new JSONArray(String.valueOf(lastResponse));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
    public String getPayloadAsString(){
        StringBuilder stringBuffer = new StringBuilder();
        Iterator it = parameter.entrySet().iterator();
        int count=0;
        while (it.hasNext()){
            Map.Entry pair = (Map.Entry) it.next();
            if(count>0){
                stringBuffer.append("&");
            }
            stringBuffer.append(pair.getKey()).append("=").append(pair.getValue());
            it.remove();
            count++;
        }
        return stringBuffer.toString();
    }
    public String execute(){
        String line;
        StringBuilder outputStringBuilder = new StringBuilder();
        try {
            StringBuilder urlString = new StringBuilder(baseUrl + urlResources);

            if (!urlPath.equals("")){
                urlString.append("/" + urlPath);
            }
            if (parameter.size()>0 && httpMethod.equals("GET")){
                payload= getPayloadAsString();
                urlString.append("?"+payload);
            }
            URL url = new URL(urlString.toString());
            String encoding = Base64Encoder.encode(username+":"+password);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod(httpMethod);
            httpURLConnection.setRequestProperty("Authorization", "Basic "+encoding);
            httpURLConnection.setRequestProperty("Accept","application/json");
            httpURLConnection.setRequestProperty("Content-Type","text/plain");
            if (httpMethod.equals("POST") || httpMethod.equals("PUT")){
                payload =getPayloadAsString();
                httpURLConnection.setDoInput(true);
                httpURLConnection.setDoOutput(true);

                try {
                    OutputStreamWriter writer = new OutputStreamWriter(httpURLConnection.getOutputStream(), "UTF-8");
                    writer.write(payload);
                    headerFields = httpURLConnection.getHeaderFields();
                    BufferedReader br = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                    while ((line = br.readLine()) != null) {
                        outputStringBuilder.append(line);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else {
                InputStream content = httpURLConnection.getInputStream();
                headerFields = httpURLConnection.getHeaderFields();
                BufferedReader in = new BufferedReader(new InputStreamReader(content));
                while ((line = in.readLine())!=null){
                    outputStringBuilder.append(line);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!outputStringBuilder.toString().equals("")){
            lastResponse = outputStringBuilder.toString();
        }
        return outputStringBuilder.toString();
    }
}
