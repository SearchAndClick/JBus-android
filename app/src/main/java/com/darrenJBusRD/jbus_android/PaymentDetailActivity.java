package com.darrenJBusRD.jbus_android;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
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
import com.darrenJBusRD.jbus_android.model.Invoice;
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

    public static Payment paymentDetail;
    private Bus busDetail;
    TextView busName, price, busType, departure, arrival, seatTextView, schedule;
    TextView ac, wifi, toilet, lcdTv, coolbox, lunch, largeBaggage, electricSocket, note;
    Button accept, cancel;
    private String departureDate;
    private int position;
    private List<String> scheduleList = new ArrayList<>();
    private List<Facility> selectedFacilities = new ArrayList<>();
    SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy hh:mm:ss");
    BaseApiService mApiService = null;
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_detail);
        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            position = extras.getInt("Payment");
        }
        mContext = this;
        busName = findViewById(R.id.bus_name_payment);
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
        note = findViewById(R.id.note_payment_detail);
        accept = findViewById(R.id.accept_button);
        cancel = findViewById(R.id.cancel_button);
        schedule = findViewById(R.id.schedule_payment);
        mApiService = UtilsApi.getApiService();

        confirmPayment();
    }

    void confirmPayment() {
        for(Bus b: MainActivity.listBus) {
            if(b.id == paymentDetail.busId) {
                busDetail = b;
                break;
            }
        }

        selectedFacilities = busDetail.facilities;
        busName.setText(busDetail.name);
        price.setText(String.valueOf(busDetail.price.price));
        busType.setText(busDetail.busType.toString());
        departure.setText(busDetail.departure.stationName);
        arrival.setText(busDetail.arrival.stationName);
        schedule.setText(dateFormat.format(paymentDetail.departureDate));
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

        if(paymentDetail.status == Invoice.PaymentStatus.SUCCESS || paymentDetail.status == Invoice.PaymentStatus.FAILED) {
            accept.setVisibility(View.GONE);
            cancel.setVisibility(View.GONE);
            note.setVisibility(View.GONE);
        }

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
                    moveActivity(mContext, PaymentActivity.class);
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
                    LoginActivity.loggedAccount.balance += busDetail.price.price * 0.7;
                    if(res.success) finish();
                    moveActivity(mContext, PaymentActivity.class);
                    viewToast(mContext, "Cancel Payment Berhasil");
                }

                @Override
                public void onFailure(Call<BaseResponse<Payment>> call, Throwable t) {
                    viewToast(mContext, "Problem with the Server");
                }
            });
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