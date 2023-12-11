package com.darrenJBusRD.jbus_android;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.darrenJBusRD.jbus_android.model.BaseResponse;
import com.darrenJBusRD.jbus_android.model.Bus;
import com.darrenJBusRD.jbus_android.model.Facility;
import com.darrenJBusRD.jbus_android.model.Payment;
import com.darrenJBusRD.jbus_android.model.Schedule;
import com.darrenJBusRD.jbus_android.request.BaseApiService;
import com.darrenJBusRD.jbus_android.request.UtilsApi;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddBusScheduleActivity extends AppCompatActivity {
    Bus busDetail;
    TextView busName, capacity, price, busType, departure, arrival;
    EditText schedule;
    Button add;
    private int position;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    BaseApiService mApiService = null;
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bus_schedule);
        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            position = extras.getInt("Bus");
        }
        mContext = this;
        busName = findViewById(R.id.bus_name_book);
        capacity = findViewById(R.id.capacity_book);
        price = findViewById(R.id.price_book);
        busType = findViewById(R.id.bus_type_book);
        departure = findViewById(R.id.departure_book);
        arrival = findViewById(R.id.arrival_book);
        add = findViewById(R.id.add_schedule_button);
        mApiService = UtilsApi.getApiService();


        mApiService.getAllBus().enqueue(new Callback<List<Bus>>() {
            @Override
            public void onResponse(Call<List<Bus>> call, Response<List<Bus>> response) {
                if(!response.isSuccessful()) {
                    viewToast(mContext, "Application error " + response.code());
                    return;
                }else if(response.body().isEmpty()) {
                    viewToast(mContext, "Tidak ada bus yang tersedia");
                    return;
                }
                busDetail = response.body().get(position);
                viewBus();
            }

            @Override
            public void onFailure(Call<List<Bus>> call, Throwable t) {
                viewToast(mContext, "Problem with the Server");
            }
        });
    }

    void viewBus() {
        busName.setText(busDetail.name);
        capacity.setText(String.valueOf(busDetail.capacity));
        price.setText(String.valueOf(busDetail.price.price));
        busType.setText(busDetail.busType.toString());
        departure.setText(busDetail.departure.stationName);
        arrival.setText(busDetail.arrival.stationName);


        add.setOnClickListener(a -> {
            mApiService.addSchedule(busDetail.id, schedule.getText().toString()).enqueue(new Callback<BaseResponse<Bus>>() {
                @Override
                public void onResponse(Call<BaseResponse<Bus>> call, Response<BaseResponse<Bus>> response) {
                    if(!response.isSuccessful()) {
                        viewToast(mContext, "Application error " + response.code());
                        return;
                    }
                    BaseResponse<Bus> res = response.body();
                    if(res.success) finish();
                    viewToast(mContext, "Berhasil Booking");
                }

                @Override
                public void onFailure(Call<BaseResponse<Bus>> call, Throwable t) {
                    viewToast(mContext, "Problem with the Server");
                }
            });
        });
    }

    private void viewToast(Context ctx, String message) {
        Toast.makeText(ctx, message, Toast.LENGTH_SHORT).show();
    }
}
