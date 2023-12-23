package com.darrenJBusRD.jbus_android;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
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
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BusDetailActivity extends AppCompatActivity {
    public static Bus busDetail;
    TextView busName, capacity, price, busType, departure, arrival, balance;
    TextView ac, wifi, toilet, lcdTv, coolbox, lunch, largeBaggage, electricSocket;
    Button book;
    Spinner scheduleSpinner, seatSpinner;
    private String departureDate, seat;
    private List<String> availableSeat;
    private int schedulePosition;
    private List<String> busSeats = new ArrayList<>();
    private List<Facility> selectedFacilities = new ArrayList<>();
    SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy hh:mm:ss");
    BaseApiService mApiService = null;
    Context mContext;

    AdapterView.OnItemSelectedListener scheduleOISL = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
            departureDate = dateFormat.format(busDetail.schedules.get(position).departureSchedule);
            schedulePosition = position;
            updateAvailableSeats();
        }
        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };

    AdapterView.OnItemSelectedListener seatOISL = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
            seat = availableSeat.get(position);
        }
        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_detail);
        Bundle extras = getIntent().getExtras();
        mContext = this;
        busName = findViewById(R.id.bus_name_book);
        capacity = findViewById(R.id.capacity_book);
        price = findViewById(R.id.price_book);
        busType = findViewById(R.id.bus_type_book);
        departure = findViewById(R.id.departure_book);
        arrival = findViewById(R.id.arrival_book);
        balance = findViewById(R.id.balance_book);
        ac = findViewById(R.id.ac_box);
        wifi = findViewById(R.id.wifi_box);
        toilet = findViewById(R.id.toilet_box);
        lcdTv = findViewById(R.id.lcd_tv_box);
        coolbox = findViewById(R.id.coolbox_box);
        lunch = findViewById(R.id.lunch_box);
        largeBaggage = findViewById(R.id.large_baggage_box);
        electricSocket = findViewById(R.id.electric_socket_box);
        book = findViewById(R.id.book_button);
        scheduleSpinner = findViewById(R.id.schedule_dropdown);
        seatSpinner = findViewById(R.id.available_seat_dropdown);
        mApiService = UtilsApi.getApiService();
        viewBus();

        String[] scheduleArray = new String[busDetail.schedules.size()];
        int i = 0;
        for(Schedule s: busDetail.schedules) {
            scheduleArray[i++] = dateFormat.format(s.departureSchedule);
        }
        ArrayAdapter adSchedule = new ArrayAdapter(mContext, android.R.layout.simple_list_item_1, scheduleArray);
        adSchedule.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
        scheduleSpinner.setAdapter(adSchedule);
        scheduleSpinner.setOnItemSelectedListener(scheduleOISL);

    }
    void viewBus() {
        selectedFacilities = busDetail.facilities;
        busName.setText(busDetail.name);
        capacity.setText(String.valueOf(busDetail.capacity));
        price.setText(String.valueOf(busDetail.price.price));
        busType.setText(busDetail.busType.toString());
        departure.setText(busDetail.departure.stationName);
        arrival.setText(busDetail.arrival.stationName);
        balance.setText(String.valueOf(LoginActivity.loggedAccount.balance));

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

        book.setOnClickListener(b -> {
            if(LoginActivity.loggedAccount.balance < busDetail.price.price) {
                viewToast(mContext, "Balance Anda tidak cukup");
                return;
            }
            busSeats.clear();
            busSeats.add(seat);
            mApiService.makeBooking(LoginActivity.loggedAccount.id, busDetail.accountId, busDetail.id, busSeats, departureDate).enqueue(new Callback<BaseResponse<Payment>>() {
                @Override
                public void onResponse(Call<BaseResponse<Payment>> call, Response<BaseResponse<Payment>> response) {
                    if(!response.isSuccessful()) {
                        viewToast(mContext, "Application error " + response.code());
                        return;
                    }
                    BaseResponse<Payment> res = response.body();
                    LoginActivity.loggedAccount.balance -= busDetail.price.price;
                    if(res.success) finish();
                    Intent intent = new Intent(mContext, MainActivity.class);
                    startActivity(intent);
                    viewToast(mContext, "Berhasil Booking");
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

    private void updateAvailableSeats() {
        Set<String> keys = busDetail.schedules.get(schedulePosition).seatAvailability.keySet();
        String[] available = new String[keys.size()];
        System.out.println(available);
        int i = 0;
        for(String s: keys) {
            if(busDetail.schedules.get(schedulePosition).seatAvailability.get(s)) {
                available[i++] = (s);
            }
        }
        availableSeat = Arrays.asList(available);
        ArrayAdapter adSeat = new ArrayAdapter(mContext, android.R.layout.simple_list_item_1, available);
        adSeat.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
        seatSpinner.setAdapter(adSeat);
        seatSpinner.setOnItemSelectedListener(seatOISL);
    }
}