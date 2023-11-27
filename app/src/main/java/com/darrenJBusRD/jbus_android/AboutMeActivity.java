package com.darrenJBusRD.jbus_android;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.darrenJBusRD.jbus_android.request.BaseApiService;

public class AboutMeActivity extends AppCompatActivity {

    private TextView username, email, balance, renterStatus, registerCompany;
    private EditText topUpAmount;
    private Button topUp, manageBus;
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
        renterStatus.findViewById(R.id.renter_status);
        registerCompany.findViewById(R.id.register_your_company);
        manageBus.findViewById(R.id.manage_bus);


        username.setText(LoginActivity.loggedAccount.name);
        email.setText(LoginActivity.loggedAccount.email);
        balance.setText(""+ LoginActivity.loggedAccount.balance);
        if(LoginActivity.loggedAccount.company == null) {
            renterStatus.setText("You're not registered as a renter");
            manageBus.setVisibility(View.GONE);
            registerCompany.setVisibility(View.VISIBLE);
        } else {
            renterStatus.setText("You're already registered as a renter");
            manageBus.setVisibility(View.VISIBLE);
            registerCompany.setVisibility(View.GONE);
        }

        registerCompany.setOnClickListener(v -> {
            moveActivity(this, RegisterRenterActivity.class);
        });
        manageBus.setOnClickListener(v -> {
            moveActivity(this, ManageBusActivity.class);
        });
        topUp.setOnClickListener(v -> {
            updateBalance(Integer.parseInt(this.topUpAmount.getText().toString()));
        });


    }

    private void moveActivity(Context ctx, Class<?> cls) {
        Intent intent = new Intent(ctx, cls);
        startActivity(intent);
    }

    private void updateBalance(int amount) {
        LoginActivity.loggedAccount.balance = LoginActivity.loggedAccount.balance + amount;
        this.balance.setText("" + LoginActivity.loggedAccount.balance);
    }
}