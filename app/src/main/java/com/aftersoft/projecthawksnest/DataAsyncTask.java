package com.aftersoft.projecthawksnest;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by lars on 08-06-17.
 */

public class DataAsyncTask extends AsyncTask<String,Void,String> {

    private DataTaskListener listener;

    public DataAsyncTask(DataTaskListener listener) {
        this.listener = listener;
    }

    @Override
    protected String doInBackground(String... params) {
        BufferedReader reader = null;
        String response = "";

        try {
            URL url = new URL(params[0]);
            URLConnection connection = url.openConnection();

            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            response = reader.readLine();
            String line;
            while ( (line = reader.readLine()) != null ) {
                response += line;
            }

        } catch (MalformedURLException e) {
            Log.e("TAG", e.getLocalizedMessage());
            return null;
        } catch (IOException e) {
            Log.e("TAG", e.getLocalizedMessage());
            return null;
        } catch ( Exception e) {
            Log.e("TAG", e.getLocalizedMessage());
            return null;
        } finally {
            if( reader != null ) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e("TAG", e.getLocalizedMessage());
                    return null;
                }
            }
        }

        return response;
    }

    @Override
    protected void onPostExecute(String s) {
        try {
            JSONObject jsonObject = new JSONObject(s);
            Double xAxis = Double.parseDouble(jsonObject.getString("xAxis"));
            Double yAxis = Double.parseDouble(jsonObject.getString("yAxis"));
            Double zAxis = Double.parseDouble(jsonObject.getString("zAxis"));
            listener.onDataReceived(xAxis, yAxis, zAxis);

        } catch (Exception e) {
            e.printStackTrace();
            listener.onExceptionThrown();
        }
    }
}
