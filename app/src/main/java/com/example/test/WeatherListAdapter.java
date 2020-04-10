package com.example.test;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class WeatherListAdapter extends RecyclerView.Adapter<WeatherListAdapter.WeatherViewHolder> {

    public static class WeatherViewHolder extends RecyclerView.ViewHolder{

        ImageView weatherIcon;
        TextView date;
        TextView summary;
        TextView tempMin;
        TextView tempMax;
        TextView humidity;


        public WeatherViewHolder(@NonNull View itemView) {
            super(itemView);

            weatherIcon = itemView.findViewById(R.id.weatherIcon);
            date = itemView.findViewById(R.id.date);
            summary = itemView.findViewById(R.id.summary);
            tempMin = itemView.findViewById(R.id.tempMin);
            tempMax = itemView.findViewById(R.id.tempMax);
            humidity = itemView.findViewById(R.id.humidity);
        }
    }

    JSONArray Data;
    Handler queryHandler;
    Calendar calendar;
    DecimalFormat decimalFormat;
    HashMap<String, Integer> hashMap;

    public WeatherListAdapter(JSONArray Data, Handler queryHandler){
        this.Data = Data;
        this.queryHandler = queryHandler;
        this.calendar = Calendar.getInstance();
        this.decimalFormat = new DecimalFormat("#.00");
        hashMap = new HashMap<>();

        hashMap.put("clear-day", R.drawable.clear_day);
        hashMap.put("clear-night", R.drawable.clear_night);
        hashMap.put("cloudy", R.drawable.cloudy);
        hashMap.put("fog", R.drawable.fog);
        hashMap.put("rain", R.drawable.rain);
        hashMap.put("snow", R.drawable.snow);
        hashMap.put("sleet", R.drawable.sleet);
        hashMap.put("wind", R.drawable.wind);
        hashMap.put("partly-cloudy-night", R.drawable.partly_cloudy_night);
        hashMap.put("partly-cloudy-day", R.drawable.partly_cloudy_day);

    }

    @NonNull
    @Override
    public WeatherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_weather_layout, parent, false);
        WeatherViewHolder wvh = new WeatherViewHolder(v);

        return wvh;
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherViewHolder holder, int position) {

        if (Data == null)
            return;

        try {
            JSONObject dailyRecord = Data.getJSONObject(position);

            holder.weatherIcon.setImageResource(hashMap.get(dailyRecord.getString("icon")));

            holder.date.setText(dateByPos(position));

            holder.summary.setText(dailyRecord.getString("summary"));

            holder.tempMin.setText(toCelsuis(dailyRecord.getDouble("temperatureLow")));

            holder.tempMax.setText(toCelsuis(dailyRecord.getDouble("temperatureHigh")));

            holder.humidity.setText(String.format("%s %%", dailyRecord.getString("humidity")));

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    @Override
    public int getItemCount() {
        if (Data == null)
            return 0;
        System.out.println(Data.length());
        return Data.length();
    }

    private String dateByPos(int position){
        Date date = new Date();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, position);
        date = calendar.getTime();

        String str = date.toString();
        return str.substring(0,10) + str.substring(str.length()-5, str.length());
    }

    private String toCelsuis(double fahrenheit){

        return decimalFormat.format((fahrenheit - 32)/1.8) + " °C";
    }



}
