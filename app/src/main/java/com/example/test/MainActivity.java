package com.example.test;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager recyclerViewLayoutManager;

    EditText editText;
    Button searchButton;
    ProgressBar progressBar;

    Handler queryHandler;
    ExecutorService executorService;

    public static final int SEND_QUERY = 0;
    public static final int SHOW_LIST = 1;
    public static final int SHOW_WEATHER_FORECAST = 2;
    public static final int ERROR = 3;
    public static final String COORDINATION_DATA = "COORDINATION_DATA";

    String query;
    JSONArray Data;
    Activity thisActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(getString(R.string.main_activity_title));

        recyclerView = findViewById(R.id.recyclerView);

        recyclerViewLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(recyclerViewLayoutManager);

        editText = findViewById(R.id.editText);
        searchButton = findViewById(R.id.searchButton);
        progressBar = findViewById(R.id.progressBar);

        recyclerView.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.INVISIBLE);

        executorService = Executors.newSingleThreadExecutor();

        thisActivity = this;

        queryHandler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                System.out.println("hi2");

                switch (msg.what){
                    case SEND_QUERY:
                        progressBar.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.INVISIBLE);
                        executorService.execute(new Runnable() {
                            @Override
                            public void run() {
                                fetchResultsForQuery();
                            }
                        });

                        break;
                    case SHOW_LIST:

                        Data = (JSONArray) msg.obj;
                        recyclerView.setAdapter(new CityListAdapter(Data, this));
                        progressBar.setVisibility(View.INVISIBLE);
                        recyclerView.setVisibility(View.VISIBLE);
                        break;

                    case SHOW_WEATHER_FORECAST:
                        Intent intent = new Intent(thisActivity, WeatherForecastActivity.class);
                        intent.putExtra(COORDINATION_DATA, (String) msg.obj);
                        startActivity(intent);
                        break;

                    case ERROR:

                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(getApplicationContext(), (String) msg.obj, Toast.LENGTH_SHORT).show();
                        break;

                }
            }
        };

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initiateSearch();
            }
        });

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH){
                    initiateSearch();
                    return true;
                }
                return false;
            }
        });

    }

    private void initiateSearch(){
        query = editText.getText().toString();
        Message msg = new Message();
        msg.what = SEND_QUERY;
        queryHandler.sendMessage(msg);
    }


    private void fetchResultsForQuery(){
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String url = "https://api.mapbox.com/geocoding/v5/mapbox.places/" + query + ".json?access_token=pk.eyJ1IjoiaHJlemF0YWhlcmkiLCJhIjoiY2s4c3B5dWV1MDF5MjNtbmRsaXkxcnliciJ9.BmScrN_pwKNT0U7ovGnb4w";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try{
                    JSONObject obj = new JSONObject(response);
                    Message msg = new Message();
                    msg.what = SHOW_LIST;
                    msg.obj = obj.getJSONArray("features");
                    queryHandler.sendMessage(msg);
                    System.out.println("hi");
//                    textView.setText(str);

                } catch (JSONException ex){
                    ex.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Message msg = new Message();
                msg.what = ERROR;

                if (error instanceof TimeoutError || error instanceof NoConnectionError){
                    System.out.println("uiahsdhkajsd");
                    msg.obj = "check your internet connection";
                } else if (error instanceof ServerError){
                    msg.obj = "there was a problem with server, please try again later";
                } else if (error instanceof NetworkError){
                    msg.obj = "there was a problem with network, please try again later";
                } else {
                    msg.obj = "unknown error occurred, please try again later";
                }

                queryHandler.sendMessage(msg);

            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }


}
