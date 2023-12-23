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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.darrenJBusRD.jbus_android.model.BaseResponse;
import com.darrenJBusRD.jbus_android.model.Bus;
import com.darrenJBusRD.jbus_android.model.BusType;
import com.darrenJBusRD.jbus_android.model.Facility;
import com.darrenJBusRD.jbus_android.model.Station;
import com.darrenJBusRD.jbus_android.request.BaseApiService;
import com.darrenJBusRD.jbus_android.request.UtilsApi;

import java.security.AlgorithmConstraints;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddBusActivity extends AppCompatActivity {

    EditText busName, capacity, price;
    Spinner busTypeSpinner, departureSpinner, arrivalSpinner;
    CheckBox ac, wifi, toilet, lcdTv, coolbox, lunch, largeBaggage, electricSocket;
    Button add;
    private BusType[] busType = BusType.values();
    private BusType selectedBusType;
    private List<Station> stationList;
    private int selectedDeptStationID;
    private int selectedArrStationID;
    private List<Facility> selectedFacilities = new ArrayList<>();
    BaseApiService mApiService = null;
    Context mContext;


    AdapterView.OnItemSelectedListener busTypeOISL = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
            selectedBusType = busType[position];
        }
        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };

    AdapterView.OnItemSelectedListener departureOISL = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
            selectedDeptStationID = stationList.get(position).id;
        }
        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };

    AdapterView.OnItemSelectedListener arrivalOISL = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
            selectedArrStationID = stationList.get(position).id;
        }
        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bus);

        mContext = this;
        busName = findViewById(R.id.bus_name);
        capacity = findViewById(R.id.capacity);
        price = findViewById(R.id.price);
        busTypeSpinner = findViewById(R.id.bus_type_dropdown);
        departureSpinner = findViewById(R.id.departure_dropdown);
        arrivalSpinner = findViewById(R.id.arrival_dropdown);
        ac = findViewById(R.id.ac_box);
        wifi = findViewById(R.id.wifi_box);
        toilet = findViewById(R.id.toilet_box);
        lcdTv = findViewById(R.id.lcd_tv_box);
        coolbox = findViewById(R.id.coolbox_box);
        lunch = findViewById(R.id.lunch_box);
        largeBaggage = findViewById(R.id.large_baggage_box);
        electricSocket = findViewById(R.id.electric_socket_box);
        add = findViewById(R.id.add_button);

        mApiService = UtilsApi.getApiService();

        add.setOnClickListener(v -> {
            if(busName.getText().toString().isEmpty() || capacity.getText().toString().isEmpty() || price.getText().toString().isEmpty()) {
                viewToast(mContext, "No field can be empty");
                return;
            }
            addBus();
        });

        ArrayAdapter adBus = new ArrayAdapter(mContext, android.R.layout.simple_list_item_1, busType);
        adBus.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
        busTypeSpinner.setAdapter(adBus);
        busTypeSpinner.setOnItemSelectedListener(busTypeOISL);

        BaseApiService mApiService = UtilsApi.getApiService();

        mApiService.getAllStation().enqueue(new Callback<List<Station>>() {
            @Override
            public void onResponse(Call<List<Station>> call, Response<List<Station>> response) {
                if(!response.isSuccessful()) {
                    viewToast(mContext, "Application error " + response.code());
                    return;
                } else if(response.body().isEmpty()) {
                    viewToast(mContext, "Tidak ada station yang tersedia");
                    return;
                }
                stationList = response.body();
                String[] stationName = new String[stationList.size()];
                for(int i = 0; i < stationList.size(); i++) {
                    stationName[i] = stationList.get(i).stationName;
                }
                ArrayAdapter adStation = new ArrayAdapter(mContext, android.R.layout.simple_list_item_1, stationName);
                departureSpinner.setAdapter(adStation);
                departureSpinner.setOnItemSelectedListener(departureOISL);
                arrivalSpinner.setAdapter(adStation);
                arrivalSpinner.setOnItemSelectedListener(arrivalOISL);
            }

            @Override
            public void onFailure(Call<List<Station>> call, Throwable t) {
                viewToast(mContext, "Problem with the Server");
            }
        });

    }

    void addBus() {
        if(selectedDeptStationID == selectedArrStationID) {
            viewToast(mContext, "The arrival station must be different");
            return;
        }
        selectedFacilities.clear();
        if (ac.isChecked()) { selectedFacilities.add(Facility.AC); }
        if (wifi.isChecked()) { selectedFacilities.add(Facility.WIFI); }
        if (toilet.isChecked()) { selectedFacilities.add(Facility.TOILET); }
        if (lcdTv.isChecked()) { selectedFacilities.add(Facility.LCD_TV); }
        if (coolbox.isChecked()) { selectedFacilities.add(Facility.COOL_BOX); }
        if (lunch.isChecked()) { selectedFacilities.add(Facility.LUNCH); }
        if (largeBaggage.isChecked()) { selectedFacilities.add(Facility.LARGE_BAGGAGE); }
        if (electricSocket.isChecked()) { selectedFacilities.add(Facility.ELECTRIC_SOCKET); }

        if(selectedFacilities.isEmpty()) {
            viewToast(mContext, "Bus must have at least 1 facility");
            return;
        }

        mApiService.createBus(LoginActivity.loggedAccount.id, busName.getText().toString(), Integer.parseInt(capacity.getText().toString()),
                selectedFacilities, selectedBusType, Double.parseDouble(price.getText().toString()),
                selectedDeptStationID, selectedArrStationID).enqueue(new Callback<BaseResponse<Bus>>() {
            @Override
            public void onResponse(Call<BaseResponse<Bus>> call, Response<BaseResponse<Bus>> response) {
                if(!response.isSuccessful()) {
                    viewToast(mContext, "Application error " + response.code());
                    return;
                }
                BaseResponse<Bus> res = response.body();
                if(res.success) finish();
                viewToast(mContext, "Berhasil Add Bus");
            }

            @Override
            public void onFailure(Call<BaseResponse<Bus>> call, Throwable t) {
                viewToast(mContext, "Problem with the Server");
            }
        });
    }

    private void viewToast(Context ctx, String message) {
        Toast.makeText(ctx, message, Toast.LENGTH_SHORT).show();
    }
}