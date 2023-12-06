package com.darrenJBusRD.jbus_android;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;

import com.darrenJBusRD.jbus_android.model.Account;
import com.darrenJBusRD.jbus_android.model.BaseResponse;
import com.darrenJBusRD.jbus_android.model.Renter;
import com.darrenJBusRD.jbus_android.request.BaseApiService;
import com.darrenJBusRD.jbus_android.request.UtilsApi;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterRenterActivity extends AppCompatActivity {

    private EditText companyName, address, phoneNumber;
    private Button registerButton;
    private Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_renter);

        mContext = this;

        companyName = findViewById(R.id.company_name);
        address = findViewById(R.id.address);
        phoneNumber = findViewById(R.id.phone_number);
        registerButton = findViewById(R.id.register_button);

        registerButton.setOnClickListener(v -> {
            registerRenter();
        });
    }

    protected void registerRenter(){
        int id = LoginActivity.loggedAccount.id;
        /*String companyNameS = companyName.getText().toString();
        String addressS = address.getText().toString();
        String phoneNumberS = phoneNumber.getText().toString();*/
        String companyNameS = "DoubleD25";
        String addressS = "Jl A W Syahranie";
        String phoneNumberS = "12345678";

        if(companyNameS.isEmpty() || addressS.isEmpty() || phoneNumberS.isEmpty()) {
            viewToast(mContext, "Field cannot be empty");
            return;
        }

        BaseApiService mApiService = UtilsApi.getApiService();

        mApiService.registerRenter(id, companyNameS, "addressS", phoneNumberS).enqueue(new Callback<BaseResponse<Renter>>() {
            @Override
            public void onResponse(Call<BaseResponse<Renter>> call, Response<BaseResponse<Renter>> response) {
                if(!response.isSuccessful()) {
                    viewToast(mContext, "Application error " + response.code());
                    return;
                }

                BaseResponse<Renter> res = response.body();
                LoginActivity.loggedAccount.company = res.payload;
                if(res.success) finish();
                viewToast(mContext, "Berhasil register renter");
            }

            @Override
            public void onFailure(Call<BaseResponse<Renter>> call, Throwable t) {
                viewToast(mContext, "Problem with the server");
            }
        });
    }

    private void moveActivity(Context ctx, Class<?> cls) {
        Intent intent = new Intent(ctx, cls);
        startActivity(intent);
    }


    private void viewToast(Context ctx, String message) {
        Toast.makeText(ctx, message, Toast.LENGTH_SHORT).show();
    }
}