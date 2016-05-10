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

package it.polito.elite.todolist.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;

import it.polito.elite.todolist.AsyncClasses.AsyncDeleteTask;
import it.polito.elite.todolist.AsyncClasses.AsyncGetTaskList;
import it.polito.elite.todolist.R;
import it.polito.elite.todolist.model.Task;

public class MainActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //set the content view
        setContentView(R.layout.activity_main);

        //run an async process to get the list of tasks
        AsyncGetTaskList task = new AsyncGetTaskList(this.getBaseContext(),this);
        task.execute("http://10.0.2.2:5000/api/v1.0/tasks");
        /*why we use 10.0.2.2 instead of 127.0.0.1:
        Each instance of the emulator runs behind a virtual router/firewall service that isolates it
        from our development machine's network interfaces and settings and from the internet.
        An emulated device can not see your development machine or other emulator instances on the network.
        Instead, it sees only that it is connected through Ethernet to a router/firewall.

        The virtual router for each instance manages the 10.0.2/24 network address space
        — all addresses managed by the router are in the form of 10.0.2.<xx>, where <xx> is a number.
        */

    }

    protected void onResume() {
        super.onResume();
        //we want to load again the list

        //run an async process to get the list of tasks
        AsyncGetTaskList task = new AsyncGetTaskList(this.getBaseContext(),this);
        task.execute("http://10.0.2.2:5000/api/v1.0/tasks");
        /*why we use 10.0.2.2 instead of 127.0.0.1:
        Each instance of the emulator runs behind a virtual router/firewall service that isolates it
        from our development machine's network interfaces and settings and from the internet.
        An emulated device can not see your development machine or other emulator instances on the network.
        Instead, it sees only that it is connected through Ethernet to a router/firewall.

        The virtual router for each instance manages the 10.0.2/24 network address space
        — all addresses managed by the router are in the form of 10.0.2.<xx>, where <xx> is a number.
        */

    }

    //this method will be invoked when the async process finishes returning the acquired list of tasks
    public void populateListBackground(String JSONResponse)
    {
        try {
            //parse the obtained list of tasks (in JSON)
            JSONObject tasksListObj = new JSONObject(JSONResponse);
            JSONArray taskList = tasksListObj.getJSONArray("tasks");

            //create the list of tasks for the adapter
            ArrayList<Task> tasks = new ArrayList<Task>();
            for (int i = 0; i < taskList.length(); i++) {
                JSONObject singleTaskObject = taskList.getJSONObject(i);
                Task task = new Task();
                task.setDescription(singleTaskObject.getString("description"));
                task.setId(Integer.parseInt(singleTaskObject.getString("id")));
                tasks.add(task);
            }

            //insert the list in the listView
            ListView todoListView = (ListView) this.findViewById(R.id.todoListView);
            ArrayAdapter adapter = new ArrayAdapter(this,
                    android.R.layout.simple_list_item_1, tasks);
            todoListView.setAdapter(adapter);

                    /*
                    * It is possible to customize the Array adapter: more info can be found at the following links
                    * 1. http://developer.android.com/reference/android/widget/ArrayAdapter.html
                    * 2. TUTORIAL -> http://www.vogella.com/tutorials/AndroidListView/article.html
                    */

            //when we click on an item the detail page will be open
            //another way of doing it: create a list with handled swipe actions:
            //more info:
            //1. https://github.com/baoyongzhang/SwipeMenuListView
            //2. http://www.tutecentral.com/android-swipe-listview/

            todoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, final View view,
                                        int position, long id) {
                    final Task item = (Task) parent.getItemAtPosition(position);
                    Intent intent = new Intent(parent.getContext(), TaskDetailsActivity.class);
                    //pass the task Id as parameter to the view
                    Bundle b = new Bundle();
                    b.putInt("taskId", item.getId());
                    intent.putExtras(b);

                    startActivity(intent);
                    //if you want to completely close the activity decomment the following line (if you press the back button it will close the app instead of coming back home)
                    // finish();
                }

            });
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }



    //function executed when the "Delete" button is pressed
    public void openNewTaskActivity(View view)
    {
        Intent intent = new Intent(this, NewTaskActivity.class);
        startActivity(intent);
    }

/* HOW TO GET DATA FROM A FILE (tasks.json) stored in res/raw folder
    public String getTaskListJSON()
    {
        String taskListInJSON = "";

        try {
            StringBuilder text = new StringBuilder();
            InputStream inputStream = this.getResources().openRawResource(R.raw.tasks);
            InputStreamReader inputreader = new InputStreamReader(inputStream);
            BufferedReader buffreader = new BufferedReader(inputreader);

            while ((taskListInJSON = buffreader.readLine()) != null) {
                text.append(taskListInJSON);
                text.append('\n');
            }
            taskListInJSON = text.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return taskListInJSON;
    }
    */


}
