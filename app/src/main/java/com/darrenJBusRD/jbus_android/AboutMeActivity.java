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
import android.widget.Toast;

import com.darrenJBusRD.jbus_android.model.BaseResponse;
import com.darrenJBusRD.jbus_android.request.BaseApiService;
import com.darrenJBusRD.jbus_android.request.UtilsApi;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

        mContext = this;
        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        balance = findViewById(R.id.balance);
        topUpAmount = findViewById(R.id.top_amount_amount);
        topUp = findViewById(R.id.top_up);
        renterStatus = findViewById(R.id.renter_status);
        registerCompany = findViewById(R.id.register_your_company);
        manageBus = findViewById(R.id.manage_bus);
        mApiService = UtilsApi.getApiService();

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
            String topUpS = this.topUpAmount.getText().toString();
            if(topUpS.isEmpty()) {
                viewToast(mContext, "field cannot be empty");
                return;
            }
            updateBalance(Double.parseDouble(this.topUpAmount.getText().toString()));
        });


    }

    private void moveActivity(Context ctx, Class<?> cls) {
        Intent intent = new Intent(ctx, cls);
        startActivity(intent);
    }

    private void updateBalance(double amount) {
        mApiService.topUp(LoginActivity.loggedAccount.id, amount).enqueue(new Callback<BaseResponse<Double>>() {
            @Override
            public void onResponse(Call<BaseResponse<Double>> call, Response<BaseResponse<Double>> response) {
                if(!response.isSuccessful()) {
                    viewToast(mContext, "Application error " + response.code());
                    return;
                }
                BaseResponse res = response.body();
                LoginActivity.loggedAccount.balance = LoginActivity.loggedAccount.balance + (double)(res.payload);
                balance.setText("" + (int) (LoginActivity.loggedAccount.balance));
            }

            @Override
            public void onFailure(Call<BaseResponse<Double>> call, Throwable t) {
                viewToast(mContext, "Problem with Server");
            }
        });
    }

    private void viewToast(Context ctx, String message) {
        Toast.makeText(ctx, message, Toast.LENGTH_SHORT).show();
    }
}