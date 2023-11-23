package com.darrenJBusRD.jbus_android;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.ListView;

import com.darrenJBusRD.jbus_android.model.Bus;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private BusArrayAdapter busArrayAdapter;
    private ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        busArrayAdapter = new BusArrayAdapter(this, Bus.sampleBusList(10));
        listView = findViewById(R.id.listView);
        listView.setAdapter(busArrayAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar_menu, menu);
        return true;
    }
}