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

import java.util.ArrayList;
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
    private List<Station> stationList = new ArrayList<>();
    private int selectedDeptStationID;
    private int selectedArrStationID;
    private List<Facility> selectedFacilities = new ArrayList<>();
    BaseApiService mApiService = null;
    Context mContext;


    AdapterView.OnItemSelectedListener busTypeOISL = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
        // mengisi field selectedBusType sesuai dengan item yang dipilih
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
// mengisi field selectedBusType sesuai dengan item yang dipilih
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
            addBus();
        });

        ArrayAdapter adBus = new ArrayAdapter(this, android.R.layout.simple_list_item_1, busType);
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
                }
                stationList = response.body();
            }

            @Override
            public void onFailure(Call<List<Station>> call, Throwable t) {
                viewToast(mContext, "Tidak ada station yang tersedia");
            }
        });

    }

    void addBus() {
        Bus createBus = new Bus();
        selectedFacilities.clear();
        if (ac.isChecked()) { selectedFacilities.add(Facility.AC); }
        if (wifi.isChecked()) { selectedFacilities.add(Facility.WIFI); }
        if (toilet.isChecked()) { selectedFacilities.add(Facility.TOILET); }
        if (lcdTv.isChecked()) { selectedFacilities.add(Facility.LCD_TV); }
        if (coolbox.isChecked()) { selectedFacilities.add(Facility.COOL_BOX); }
        if (lunch.isChecked()) { selectedFacilities.add(Facility.LUNCH); }
        if (largeBaggage.isChecked()) { selectedFacilities.add(Facility.LARGE_BAGGAGE); }
        if (electricSocket.isChecked()) { selectedFacilities.add(Facility.ELECTRIC_SOCKET); }

        createBus.accountId = LoginActivity.loggedAccount.id;
        createBus.name = busName.getText().toString();
        createBus.capacity = Integer.parseInt(capacity.getText().toString());
        createBus.price.price = Double.parseDouble(price.getText().toString());
        createBus.busType = selectedBusType;
        createBus.departure.id = selectedDeptStationID;
        createBus.arrival.id = selectedArrStationID;
        createBus.facilities = selectedFacilities;

        mApiService.createBus(createBus.accountId, createBus.name, createBus.capacity,
                createBus.facilities, createBus.busType, createBus.price.price,
                createBus.departure.id, createBus.arrival.id).enqueue(new Callback<BaseResponse<Bus>>() {
            @Override
            public void onResponse(Call<BaseResponse<Bus>> call, Response<BaseResponse<Bus>> response) {
                if(!response.isSuccessful()) {
                    viewToast(mContext, "Application error " + response.code());
                    return;
                }
                BaseResponse<Bus> res = response.body();
                if(res.success) finish();
            }

            @Override
            public void onFailure(Call<BaseResponse<Bus>> call, Throwable t) {
                    viewToast(mContext, "Problem with Server");
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