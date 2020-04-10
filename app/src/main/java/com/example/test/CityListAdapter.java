package com.example.test;

import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;

public class CityListAdapter extends RecyclerView.Adapter {


    public static class CityViewHolder extends RecyclerView.ViewHolder{

        CardView cardView;
        TextView placeName;
        TextView latitude;
        TextView longitude;
//        TextView test;

        public CityViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.cardView);
            placeName = itemView.findViewById(R.id.place);
            latitude = itemView.findViewById(R.id.latitude);
            longitude = itemView.findViewById(R.id.longitude);


//            test = itemView.findViewById(R.id.textView_test);
        }
    }

    JSONArray Data;
    Handler queryHandler;

    public CityListAdapter(JSONArray Data, android.os.Handler queryHandler){
        this.Data = Data;
        this.queryHandler = queryHandler;
    }

    @NonNull
    @Override
    public CityListAdapter.CityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_sample_layout, parent, false);
        CityViewHolder cvh = new CityViewHolder(v);

        return cvh;
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        CityViewHolder cvh = (CityViewHolder) holder;
        if (Data == null)
            return;

        try {

//            cvh.test.setText(Data.getJSONObject(position).getString("place_name"));
            cvh.placeName.setText(Data.getJSONObject(position).getString("place_name"));
            final String latitude = Data.getJSONObject(position).getJSONArray("center").getString(1);
            final String longitude = Data.getJSONObject(position).getJSONArray("center").getString(0);

            cvh.latitude.setText("latitude: " + latitude);
            cvh.longitude.setText(" longitude: " + longitude);



            cvh.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Message msg = new Message();
                    msg.what = MainActivity.SHOW_WEATHER_FORECAST;
                    msg.obj = (latitude + "," + longitude).replaceAll(" ","");
                    queryHandler.sendMessage(msg);
                }
            });
            System.out.println(Data.getJSONObject(position).getJSONArray("center").getDouble(1));

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
}
