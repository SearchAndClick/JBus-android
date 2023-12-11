package com.darrenJBusRD.jbus_android;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.darrenJBusRD.jbus_android.model.Bus;

import java.util.List;


public class BusArrayAdapter extends ArrayAdapter<Bus> {
    public BusArrayAdapter(@NonNull Context ctx, List<Bus> arrayList) {
        super(ctx, 0, arrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
         View currentBusView = convertView;

         if(currentBusView == null) {
             currentBusView = LayoutInflater.from(getContext()).inflate(R.layout.bus_list_view, parent, false);
         }

         Bus currentBusPosition = getItem(position);

         TextView name = currentBusView.findViewById(R.id.name_bus);
         name.setText(currentBusPosition.name);

         TextView price = currentBusView.findViewById(R.id.price_bus);
         price.setText(String.valueOf(currentBusPosition.price.price));

         return currentBusView;
    }
}
