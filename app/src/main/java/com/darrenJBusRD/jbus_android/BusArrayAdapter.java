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
         View currentItemView = convertView;

         if(currentItemView == null) {
             currentItemView = LayoutInflater.from(getContext()).inflate(R.layout.bus_view, parent, false);
         }

         Bus currentBusPosition = getItem(position);

         TextView name = currentItemView.findViewById(R.id.name);

         return currentItemView;
    }
}
