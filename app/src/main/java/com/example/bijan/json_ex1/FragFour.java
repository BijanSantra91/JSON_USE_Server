package com.example.bijan.json_ex1;


import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
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
public class FragFour extends Fragment {
    Button button;
    ListView listView;
    MyTask myTask;
    MyDatabase myDatabase;
    SimpleCursorAdapter simpleCursorAdapter;
    Cursor cursor;

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
                for (int i = 0; i<k.length(); i++){
                    JSONObject m = k.getJSONObject(i);
                    String name = m.getString("name");
                    String email = m.getString("email");
                    JSONObject phone = m.getJSONObject("phone");
                    String mobile = phone.getString("mobile");

                    //now data pushing to database
                    myDatabase.inserData(name, email, mobile);
                }
                cursor.requery();
            } catch (JSONException e) {
                e.printStackTrace();
                Log.d("B-34", "JSON PARSING ERROR");
            }
            super.onPostExecute(s);
        }
    }

    public FragFour() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_frag_four, container, false);

        button = (Button) v.findViewById(R.id.getcon4);
        listView = (ListView) v.findViewById(R.id.showinglist2);
        cursor = myDatabase.quaryData();
        simpleCursorAdapter = new SimpleCursorAdapter(
                getActivity(),
                R.layout.row,
                cursor,
                new String[]{"_id", "name","email","mobile"},
                new int[]{R.id.textview1, R.id.textview2, R.id.textview3, R.id.textview4});
        listView.setAdapter(simpleCursorAdapter);
        myTask = new MyTask();

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
