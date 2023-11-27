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
import com.darrenJBusRD.jbus_android.request.BaseApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterRenterActivity extends AppCompatActivity {

    private EditText companyName, address, phoneNumber;
    private Button register;
    private Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_renter);

        companyName.findViewById(R.id.company_name);
        address.findViewById(R.id.address);
        phoneNumber.findViewById(R.id.phone_number);
        register.findViewById(R.id.register);

        register.setOnClickListener(v -> {

        });
    }

    private void register(){
        String companyNameS = companyName.getText().toString();
        String addressS = address.getText().toString();
        String phoneNumberS = phoneNumber.getText().toString();

        if(companyNameS.isEmpty() || addressS.isEmpty() || phoneNumberS.isEmpty()) {
            viewToast(mContext, "Field cannot be empty");
            return;
        }

        mApiService.register(companyNameS, addressS, phoneNumberS).enqueue(new Callback<BaseResponse<Account>>() {
            @Override
            public void onResponse(Call<BaseResponse<Account>> call, Response<BaseResponse<Account>> response) {
                if(!response.isSuccessful()) {
                    viewToast(mContext, "Application error " + response.code());
                    return;
                }

                BaseResponse<Account> res = response.body();
                if(res.success) finish();
                LoginActivity.loggedAccount.company = res.payload;
                moveActivity(mContext, AboutMeActivity.class);
                viewToast(mContext, "Berhasil register renter");
            }

            @Override
            public void onFailure(Call<BaseResponse<Account>> call, Throwable t) {
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