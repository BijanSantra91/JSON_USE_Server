package com.example.bijan.json_ex1;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
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
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class FragOne extends Fragment {

    Button button;
    ListView listView;
    MyTask myTask;
    ArrayList<Contacts> arrayList;
    MyAdpater myAdpater;

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
                    //let put this data to arraylist

                    Contacts contacts = new Contacts();
                    contacts.setName(name);
                    contacts.setEmail(email);
                    contacts.setMobile(mobile);
                    contacts.setSno(i+1);

                    //now push contact to arraylist
                    arrayList.add(contacts);
                }
                // calling adapter
                myAdpater.notifyDataSetChanged();

            } catch (JSONException e) {
                e.printStackTrace();
                Log.d("B-34", "JSON PARSING ERROR");
            }
            super.onPostExecute(s);
        }
    }

    public class MyAdpater extends BaseAdapter {

        @Override
        public int getCount() {
            return arrayList.size();
        }

        @Override
        public Object getItem(int i) {
            return arrayList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View v = getActivity().getLayoutInflater().inflate(R.layout.row, null);

            Contacts contacts = arrayList.get(i);

            TextView tv1 = (TextView) v.findViewById(R.id.textview1);
            TextView tv2 = (TextView) v.findViewById(R.id.textview2);
            TextView tv3 = (TextView) v.findViewById(R.id.textview3);
            TextView tv4 = (TextView) v.findViewById(R.id.textview4);

            tv1.setText(""+contacts.getSno());
            tv2.setText(""+contacts.getName());
            tv3.setText(""+contacts.getEmail());
            tv4.setText(""+contacts.getMobile());

            return v;
        }
    }

    public FragOne() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_frag_one, container, false);

        button = (Button) v.findViewById(R.id.getcon1);
        listView = (ListView) v.findViewById(R.id.showinglist);
        arrayList = new ArrayList<Contacts>();
        myAdpater =  new MyAdpater();
        listView.setAdapter(myAdpater);
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
