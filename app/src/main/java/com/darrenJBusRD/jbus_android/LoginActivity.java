package com.darrenJBusRD.jbus_android;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.darrenJBusRD.jbus_android.model.Account;
import com.darrenJBusRD.jbus_android.model.BaseResponse;
import com.darrenJBusRD.jbus_android.request.BaseApiService;
import com.darrenJBusRD.jbus_android.request.RetrofitClient;
import com.darrenJBusRD.jbus_android.request.UtilsApi;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private TextView registerNow = null;
    private Button loginButton = null;
    private BaseApiService mApiService;
    private Context mContext = this;
    private EditText email, password;
    public static Account loggedAccount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        registerNow = findViewById(R.id.register_now);
        loginButton = findViewById(R.id.login_button);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);

        registerNow.setOnClickListener(v -> { moveActivity(this, RegisterActivity.class); });
        loginButton.setOnClickListener(v -> { requestLogin(); });


    }

    private void requestLogin()  {
        String emailS = email.getText().toString();
        String passwordS = password.getText().toString();

        if(emailS.isEmpty() || passwordS.isEmpty()) {
            viewToast(mContext, "Field cannot be empty");
            return;
        }

        mApiService = UtilsApi.getApiService();
        mApiService.login(emailS, passwordS).enqueue(new Callback<BaseResponse<Account>>() {
            @Override
            public void onResponse(Call<BaseResponse<Account>> call, Response<BaseResponse<Account>> response) {
                if(!response.isSuccessful()) {
                    viewToast(mContext, "Application error " + response.code());
                    return;
                }

                BaseResponse<Account> res = response.body();
                if(res.success) finish();
                loggedAccount = res.payload;
                moveActivity(mContext, MainActivity.class);
                viewToast(mContext, "Welcome!");
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