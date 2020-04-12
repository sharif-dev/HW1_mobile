package com.example.test;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ProgressBar;
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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WeatherForecastActivity extends AppCompatActivity {

    final String DarkSkyToken = "a6b8c7b90a261e493e22279291026462";
    public static final int SEND_QUERY = 0;
    public static final int SHOW_LIST = 1;
    public static final int ERROR = 2;

    ExecutorService executorService;

    Handler queryHandler;
    String url;

    JSONArray Data;

    RecyclerView recyclerView;
    RecyclerView.LayoutManager recyclerViewLayoutManager;
    ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_forecast);
        setTitle(getString(R.string.weather_activity_title));

        url = "https://api.darksky.net/forecast/a6b8c7b90a261e493e22279291026462/" + getIntent().getStringExtra(MainActivity.COORDINATION_DATA);
        System.out.println(getIntent().getStringExtra(MainActivity.COORDINATION_DATA));
        executorService = Executors.newSingleThreadExecutor();

        recyclerView = findViewById(R.id.recyclerView_weather);
        recyclerViewLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(recyclerViewLayoutManager);

        progressBar = findViewById(R.id.progressBar2);
        recyclerView.setVisibility(View.INVISIBLE);

        queryHandler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);

                switch (msg.what){
                    case SEND_QUERY:
                        progressBar.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.INVISIBLE);
                        executorService.execute(new Runnable() {
                            @Override
                            public void run() {
                                fetchWeatherForecastResultls();
                            }
                        });
                        break;
                    case SHOW_LIST:

                        Data = (JSONArray) msg.obj;
                        recyclerView.setAdapter(new WeatherListAdapter(Data,queryHandler));

                        progressBar.setVisibility(View.INVISIBLE);
                        recyclerView.setVisibility(View.VISIBLE);

                        break;

                    case ERROR:
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(getApplicationContext(), (String) msg.obj, Toast.LENGTH_SHORT).show();
                        break;

                }
            }
        };

        Message msg = new Message();
        msg.what = SEND_QUERY;
        queryHandler.sendMessage(msg);
    }


    private void fetchWeatherForecastResultls(){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try{
                    JSONObject obj = new JSONObject(response);
                    Message msg = new Message();
                    msg.what = SHOW_LIST;
                    msg.obj = obj.getJSONObject("daily").getJSONArray("data");
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
