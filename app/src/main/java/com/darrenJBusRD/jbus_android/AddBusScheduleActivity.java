package com.darrenJBusRD.jbus_android;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
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

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddBusScheduleActivity extends AppCompatActivity {

    public static Bus  busDetail;
    TextView busName, capacity, price, busType, departure, arrival;
    EditText schedule;
    Button add;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    BaseApiService mApiService = null;
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bus_schedule);

        mContext = this;
        busName = findViewById(R.id.bus_name_schedule);
        capacity = findViewById(R.id.capacity_schedule);
        price = findViewById(R.id.price_schedule);
        busType = findViewById(R.id.bus_type_schedule);
        departure = findViewById(R.id.departure_schedule);
        arrival = findViewById(R.id.arrival_schedule);
        schedule = findViewById(R.id.add_schedule);
        add = findViewById(R.id.add_schedule_button);
        mApiService = UtilsApi.getApiService();

        viewBus();
    }

    void viewBus() {
        busName.setText(busDetail.name);
        capacity.setText(String.valueOf(busDetail.capacity));
        price.setText(String.valueOf(busDetail.price.price));
        busType.setText(busDetail.busType.toString());
        departure.setText(busDetail.departure.stationName);
        arrival.setText(busDetail.arrival.stationName);


        add.setOnClickListener(a -> {
            String scheduleS = schedule.getText().toString();
            if(scheduleS.isEmpty()) {
                viewToast(mContext, "Field can't be empty!");
                return;
            }
            else if(!validateSchedule(scheduleS)) {
                viewToast(mContext, "No new lines and follow the format!");
                return;
            }
            try {
                Date parsedDate = dateFormat.parse(scheduleS);
                if(parsedDate.before(new Date())) {
                    viewToast(mContext, "The Date must be in the future");
                    return;
                }
            }
            catch (ParseException e) {
                System.out.println(e);
            }
            mApiService.addSchedule(busDetail.id, scheduleS).enqueue(new Callback<BaseResponse<Bus>>() {
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

    public boolean validateSchedule(String schedule) {
        String REGEX_SCHEDULE = "^([0-9]{4})-(1[0-2]|0?[1-9])-(3[01]|[12][0-9]|0?[1-9]) ([0-1]?[0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]$";
        Pattern pattern = Pattern.compile(REGEX_SCHEDULE);
        return pattern.matcher(schedule).find();
    }

    private void viewToast(Context ctx, String message) {
        Toast.makeText(ctx, message, Toast.LENGTH_SHORT).show();
    }
}
