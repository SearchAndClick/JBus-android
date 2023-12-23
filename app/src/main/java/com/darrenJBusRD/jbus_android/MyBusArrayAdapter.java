package com.darrenJBusRD.jbus_android;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.darrenJBusRD.jbus_android.model.Bus;

import java.util.List;


public class MyBusArrayAdapter extends ArrayAdapter<Bus> {
    public MyBusArrayAdapter(@NonNull Context ctx, List<Bus> arrayList) {
        super(ctx, 0, arrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
         View currentBusView = convertView;

         if(currentBusView == null) {
             currentBusView = LayoutInflater.from(getContext()).inflate(R.layout.my_bus_list_view, parent, false);
         }

         Bus currentBusPosition = getItem(position);

         TextView name = currentBusView.findViewById(R.id.my_name_bus);
         name.setText(currentBusPosition.name);

         TextView price = currentBusView.findViewById(R.id.my_price_bus);
         price.setText(String.valueOf(currentBusPosition.price.price));

         ImageView calendar = currentBusView.findViewById(R.id.my_calendar_bus);
         calendar.setOnClickListener(c -> {
             Intent intent = new Intent(ManageBusActivity.mContext, AddBusScheduleActivity.class);
             AddBusScheduleActivity.busDetail = currentBusPosition;
             startActivity(ManageBusActivity.mContext, intent, null);
         });

//         LinearLayout linearLayout = currentBusView.findViewById(R.id.my_bus_layout);
//         linearLayout.setOnClickListener(v -> {
//             Intent intent = new Intent(ManageBusActivity.mContext, BusDetailActivity.class);
//             BusDetailActivity.busDetail = currentBusPosition;
//             startActivity(MainActivity.mContext, intent, null);
//         });

         return currentBusView;
    }
}
