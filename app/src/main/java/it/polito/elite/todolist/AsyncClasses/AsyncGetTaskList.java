/*
Created on May 10, 2016
Copyright (c) 2016 Teodoro Montanaro

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License
@author: Teodoro Montanaro
*/

package it.polito.elite.todolist.AsyncClasses;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import it.polito.elite.todolist.activities.MainActivity;

/**
 * Created by Teo on 10/05/2016.
 */
public class AsyncGetTaskList extends AsyncTask<String, Integer, String> {

    //context and activity of the activity that called the service
    Context mContext;
    MainActivity mActivity;

    ProgressDialog barPD;
    public AsyncGetTaskList(Context context, Activity activity)
    {
        this.mContext = context;
        this.mActivity = (MainActivity) activity;

        barPD = new ProgressDialog(this.mContext);
    }

    //while data is acquired, we show a progress bar
    @Override
    protected void onPreExecute() {
        try {
            barPD = barPD.show(this.mActivity, "Loading", "Please wait");

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    protected String doInBackground(String... urls) {
        String JSONResponse = "";
        try {

            //open an HTTP connection and send request
            HttpURLConnection conn;
            URL urlObj = new URL(urls[0]);

            conn = (HttpURLConnection) urlObj.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(15000);
            conn.connect();

            //get response and convert it to string
            InputStream in = new BufferedInputStream(conn.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            JSONResponse = result.toString();
            conn.disconnect();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return JSONResponse;
    }

    @Override
    protected void onPostExecute(String taskListStrJSON) {
        try {
            mActivity.populateListBackground(taskListStrJSON);
            barPD.dismiss();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
