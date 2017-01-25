package com.example.bijan.json_ex1;


import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragThree extends Fragment {

    Button button;
    MyTask myTask;
    MyDatabase myDatabase;
    Cursor cursor;
    RecyclerView recyclerView;
    MyRecyclerViewAdaprter myRecyclerViewAdaprter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myDatabase = new MyDatabase(getActivity());
        myDatabase.open();
    }

    @Override
    public void onDestroy() {
        myDatabase.close();
        super.onDestroy();
    }

    public boolean checkInternet(){
        ConnectivityManager manager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if (networkInfo == null || networkInfo.isConnected() == false){
            return false;
        }
        return true;
    }

    public class MyTask extends AsyncTask<String, Void, String> {

        URL myURL;
        HttpURLConnection httpURLConnection;
        InputStream inputStream;
        InputStreamReader inputStreamReader;
        BufferedReader bufferedReader;
        String line;
        StringBuilder result;

        @Override
        protected String doInBackground(String... strings) {
            try {
                myURL = new URL(strings[0]);
                httpURLConnection = (HttpURLConnection) myURL.openConnection();
                inputStream = httpURLConnection.getInputStream();
                inputStreamReader = new InputStreamReader(inputStream);
                bufferedReader = new BufferedReader(inputStreamReader);
                line = bufferedReader.readLine();
                result = new StringBuilder();
                while (line != null){
                    result.append(line);
                    line = bufferedReader.readLine();
                }
                return result.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
                Log.d("B-34", "URL IS IMPROPER");
            } catch (IOException e) {
                e.printStackTrace();
                Log.d("B-34", "NETWORK PROBLEM");
            }
            return "SOME THING WENT WRONG";
        }

        @Override
        protected void onPostExecute(String s) {
            //reverse json passing
            try {
                JSONObject j = new JSONObject(s);
                JSONArray k = j.getJSONArray("contacts");
                for (int i = 0; i<k.length(); i++) {
                    JSONObject m = k.getJSONObject(i);
                    String name = m.getString("name");
                    String email = m.getString("email");
                    JSONObject phone = m.getJSONObject("phone");
                    String mobile = phone.getString("mobile");

                    //now data pushing to database
                    myDatabase.inserData(name, email, mobile);
                }
                myRecyclerViewAdaprter.notifyDataSetChanged();
                cursor.requery();
            } catch (JSONException e) {
                e.printStackTrace();
                Log.d("B-34", "JSON PARSING ERROR");
            }
            super.onPostExecute(s);
        }
    }

    //create recyclerview adapter
    public  class MyRecyclerViewAdaprter extends RecyclerView.Adapter<MyRecyclerViewAdaprter.ViewHolder>{

        @Override
        public MyRecyclerViewAdaprter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            //load xml
            View v = getActivity().getLayoutInflater().inflate(R.layout.row, parent, false);
            //ViewHolder viewHolder = new ViewHolder(v);
            ViewHolder viewHolder=new ViewHolder(v);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(MyRecyclerViewAdaprter.ViewHolder holder, int position) {

            //get data from cursor base on position
            cursor.moveToPosition(position);
            int sno = cursor.getInt(0);
            String name = cursor.getString(1);
            String email = cursor.getString(2);
            String mobile = cursor.getString(3);

            //apphy data on viewholder using setter
            holder.tv1.setText(""+sno);
            holder.tv2.setText(name);
            holder.tv3.setText(email);
            holder.tv4.setText(mobile);

        }

        @Override
        public int getItemCount() {
            return cursor.getCount();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView tv1, tv2, tv3, tv4;
            public ViewHolder(View itemView) {
                super(itemView);
                tv1 = (TextView) itemView.findViewById(R.id.textview1);
                tv2 = (TextView) itemView.findViewById(R.id.textview2);
                tv3 = (TextView) itemView.findViewById(R.id.textview3);
                tv4 = (TextView) itemView.findViewById(R.id.textview4);
            }
        }
    }


    public FragThree() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_frag_three, container, false);
        button = (Button) v.findViewById(R.id.getcon3);
        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView2);
        cursor = myDatabase.quaryData();
        myTask = new MyTask();
        myRecyclerViewAdaprter = new MyRecyclerViewAdaprter();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(myRecyclerViewAdaprter);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (checkInternet()){
                    if (myTask.getStatus() == AsyncTask.Status.RUNNING || myTask.getStatus() == AsyncTask.Status.FINISHED){
                        Toast.makeText(getActivity(), "ALREADY RUNNING PLEASE WAIT", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    myTask.execute("http://api.androidhive.info/contacts");
                }
                else {
                    Toast.makeText(getActivity(), "NETWORK IS NOT AVAILABLE", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return  v;
    }
}
