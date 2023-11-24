package com.darrenJBusRD.jbus_android;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.darrenJBusRD.jbus_android.request.BaseApiService;

public class AboutMeActivity extends AppCompatActivity {

    private TextView username;
    private TextView email;
    private TextView balance;
    private EditText topUpAmount;
    private Button topUp;
    private BaseApiService mApiService;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_me);

        username.findViewById(R.id.username);
        email.findViewById(R.id.email);
        balance.findViewById(R.id.balance);
        topUpAmount.findViewById(R.id.top_amount_amount);
        topUp.findViewById(R.id.top_up);


        username.setText("Darren Adam Dewantoro");
        email.setText("darren.adam@ui.ac.id");
        balance.setText("1000");

        topUp.setOnClickListener(v -> {
            updateBalance(Integer.parseInt(this.topUpAmount.getText().toString()));
        });


    }

    private void updateBalance(int amount) {
        int balanceS = Integer.parseInt(this.balance.getText().toString());
        balanceS = balanceS + amount;
        this.balance.setText("" + balanceS);
    }
}