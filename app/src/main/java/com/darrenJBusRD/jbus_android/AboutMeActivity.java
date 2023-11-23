package com.darrenJBusRD.jbus_android;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class AboutMeActivity extends AppCompatActivity {

    private TextView username;
    private TextView email;
    private TextView balance;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_me);

        username.findViewById(R.id.username);
        email.findViewById(R.id.email);
        balance.findViewById(R.id.balance);
        username.setText("Darren Adam Dewantoro");
        email.setText("darren.adam@ui.ac.id");
        balance.setText("IDR 10.000.000.000,00");

    }
}