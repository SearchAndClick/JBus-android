package com.darrenJBusRD.jbus_android;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

public class PaymentDetailActivity extends AppCompatActivity {

    Bus busDetail;
    TextView busName, capacity, price, busType, departure, arrival, seatTextView;
    TextView ac, wifi, toilet, lcdTv, coolbox, lunch, largeBaggage, electricSocket;
    Button accept, cancel;
    Spinner scheduleSpinner;
    private String departureDate;
    private int position;
    private Payment paymentDetail;
    private List<String> scheduleList = new ArrayList<>();
    private List<Facility> selectedFacilities = new ArrayList<>();
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    BaseApiService mApiService = null;
    Context mContext;

    AdapterView.OnItemSelectedListener scheduleOISL = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
            departureDate = dateFormat.format(busDetail.schedules.get(position).departureSchedule);
        }
        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_detail);
        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            position = extras.getInt("Bus");
        }
        mContext = this;
        busName = findViewById(R.id.bus_name_payment);
        capacity = findViewById(R.id.capacity_payment);
        price = findViewById(R.id.price_payment);
        busType = findViewById(R.id.bus_type_payment);
        departure = findViewById(R.id.departure_payment);
        arrival = findViewById(R.id.arrival_payment);
        seatTextView = findViewById(R.id.seat_payment);
        ac = findViewById(R.id.ac_box);
        wifi = findViewById(R.id.wifi_box);
        toilet = findViewById(R.id.toilet_box);
        lcdTv = findViewById(R.id.lcd_tv_box);
        coolbox = findViewById(R.id.coolbox_box);
        lunch = findViewById(R.id.lunch_box);
        largeBaggage = findViewById(R.id.large_baggage_box);
        electricSocket = findViewById(R.id.electric_socket_box);
        accept = findViewById(R.id.accept_button);
        cancel = findViewById(R.id.cancel_button);
        scheduleSpinner = findViewById(R.id.schedule_dropdown);
        mApiService = UtilsApi.getApiService();

        for(Schedule s: busDetail.schedules) {
            scheduleList.add(dateFormat.format(s.departureSchedule));
        }
        ArrayAdapter adSchedule = new ArrayAdapter(mContext, android.R.layout.simple_list_item_1, scheduleList);
        adSchedule.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
        scheduleSpinner.setAdapter(adSchedule);
        scheduleSpinner.setOnItemSelectedListener(scheduleOISL);

        mApiService.getMyPayment(LoginActivity.loggedAccount.id).enqueue(new Callback<List<Payment>>() {
            @Override
            public void onResponse(Call<List<Payment>> call, Response<List<Payment>> response) {
                if(!response.isSuccessful()) {
                    viewToast(mContext, "Application error " + response.code());
                    return;
                }else if(response.body().isEmpty()) {
                    viewToast(mContext, "Tidak ada bus yang tersedia");
                    return;
                }
                paymentDetail = response.body().get(position);
                getBus();
            }

            @Override
            public void onFailure(Call<List<Payment>> call, Throwable t) {

            }
        });

    }

    void getBus() {
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
                for(Bus b: response.body()) {
                    if(b.id == paymentDetail.busId) {
                        busDetail = b;
                        break;
                    }
                }
                confirmPayment();
            }

            @Override
            public void onFailure(Call<List<Bus>> call, Throwable t) {
                viewToast(mContext, "Problem with the Server");
            }
        });
    }

    void confirmPayment() {
        selectedFacilities = busDetail.facilities;
        busName.setText(busDetail.name);
        capacity.setText(String.valueOf(busDetail.capacity));
        price.setText(String.valueOf(busDetail.price.price));
        busType.setText(busDetail.busType.toString());
        departure.setText(busDetail.departure.stationName);
        arrival.setText(busDetail.arrival.stationName);
        departure.setText(dateFormat.format(paymentDetail.departureDate));
        seatTextView.setText(paymentDetail.busSeats.toString());

        if (selectedFacilities.contains(Facility.AC)) { ac.setVisibility(View.VISIBLE); }
        else { ac.setVisibility(View.GONE); }
        if (selectedFacilities.contains(Facility.WIFI)) { wifi.setVisibility(View.VISIBLE); }
        else { wifi.setVisibility(View.GONE); }
        if (selectedFacilities.contains(Facility.TOILET)) { toilet.setVisibility(View.VISIBLE); }
        else { toilet.setVisibility(View.GONE); }
        if (selectedFacilities.contains(Facility.LCD_TV)) { lcdTv.setVisibility(View.VISIBLE); }
        else { lcdTv.setVisibility(View.GONE); }
        if (selectedFacilities.contains(Facility.COOL_BOX)) { coolbox.setVisibility(View.VISIBLE); }
        else { coolbox.setVisibility(View.GONE); }
        if (selectedFacilities.contains(Facility.LUNCH)) { lunch.setVisibility(View.VISIBLE); }
        else { lunch.setVisibility(View.VISIBLE); }
        if (selectedFacilities.contains(Facility.LARGE_BAGGAGE)) { largeBaggage.setVisibility(View.VISIBLE); }
        else { largeBaggage.setVisibility(View.GONE); }
        if (selectedFacilities.contains(Facility.ELECTRIC_SOCKET)) { electricSocket.setVisibility(View.VISIBLE); }
        else { largeBaggage.setVisibility(View.GONE); }

        accept.setOnClickListener(a -> {
            mApiService.accept(paymentDetail.id).enqueue(new Callback<BaseResponse<Payment>>() {
                @Override
                public void onResponse(Call<BaseResponse<Payment>> call, Response<BaseResponse<Payment>> response) {
                    if(!response.isSuccessful()) {
                        viewToast(mContext, "Application error " + response.code());
                        return;
                    }
                    BaseResponse res = response.body();
                    if(res.success) finish();
                    viewToast(mContext, "Accept Payment Berhasil");
                }

                @Override
                public void onFailure(Call<BaseResponse<Payment>> call, Throwable t) {
                    viewToast(mContext, "Problem with the Server");
                }
            });
        });

        cancel.setOnClickListener(c -> {
            mApiService.cancel(paymentDetail.id).enqueue(new Callback<BaseResponse<Payment>>() {
                @Override
                public void onResponse(Call<BaseResponse<Payment>> call, Response<BaseResponse<Payment>> response) {
                    if(!response.isSuccessful()) {
                        viewToast(mContext, "Application error " + response.code());
                        return;
                    }
                    BaseResponse res = response.body();
                    if(res.success) finish();
                    viewToast(mContext, "Cancel Payment Berhasil");
                }

                @Override
                public void onFailure(Call<BaseResponse<Payment>> call, Throwable t) {
                    viewToast(mContext, "Problem with the Server");
                }
            });
        });

    }

    private void viewToast(Context ctx, String message) {
        Toast.makeText(ctx, message, Toast.LENGTH_SHORT).show();
    }
}