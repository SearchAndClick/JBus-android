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
import com.darrenJBusRD.jbus_android.model.BusType;
import com.darrenJBusRD.jbus_android.model.Facility;
import com.darrenJBusRD.jbus_android.model.Payment;
import com.darrenJBusRD.jbus_android.model.Schedule;
import com.darrenJBusRD.jbus_android.model.Station;
import com.darrenJBusRD.jbus_android.request.BaseApiService;
import com.darrenJBusRD.jbus_android.request.UtilsApi;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BusDetailActivity extends AppCompatActivity {
    Bus busDetail;
    TextView busName, capacity, price, busType, departure, arrival, balance;
    TextView ac, wifi, toilet, lcdTv, coolbox, lunch, largeBaggage, electricSocket;
    Button book;
    Spinner scheduleSpinner;
    private String departureDate, seat;
    private int position, schedulePosition;
    private List<String> busSeats = new ArrayList<>();
    private List<String> availableSeat = new ArrayList<>();
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

    AdapterView.OnItemSelectedListener seatOISL = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
            schedulePosition = position;
            seat = availableSeat.get(position);
        }
        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            availableSeat.clear();
            Set<String> keys = busDetail.schedules.get(schedulePosition).seatAvailability.keySet();
            for(String s: keys) {
                availableSeat.add(s);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bus_detail);
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
        mApiService = UtilsApi.getApiService();

        for(Schedule s: busDetail.schedules) {
            scheduleList.add(dateFormat.format(s.departureSchedule));
        }
        ArrayAdapter adSchedule = new ArrayAdapter(mContext, android.R.layout.simple_list_item_1, scheduleList);
        adSchedule.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
        scheduleSpinner.setAdapter(adSchedule);
        scheduleSpinner.setOnItemSelectedListener(scheduleOISL);

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
                    if(res.success) finish();
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
}