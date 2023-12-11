package com.darrenJBusRD.jbus_android;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterViewAnimator;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.darrenJBusRD.jbus_android.model.BaseResponse;
import com.darrenJBusRD.jbus_android.model.City;
import com.darrenJBusRD.jbus_android.model.Station;
import com.darrenJBusRD.jbus_android.request.BaseApiService;
import com.darrenJBusRD.jbus_android.request.UtilsApi;

import java.util.List;

import okhttp3.internal.Util;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddStationActivity extends AppCompatActivity {

    private EditText stationName, address;
    private Spinner citySpinner;
    private City[] city = City.values();
    private Button addStationButton;
    private Context mContext;
    private BaseApiService mApiService;
    private City selectedCity;
    AdapterView.OnItemSelectedListener cityOISL = new AdapterView.OnItemSelectedListener() {

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            ((TextView) adapterView.getChildAt(0)).setTextColor(Color.BLACK);
            selectedCity = city[i];
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_station);

        mContext = this;
        stationName = findViewById(R.id.station_name);
        citySpinner = findViewById(R.id.city_dropdown);
        address = findViewById(R.id.address);
        addStationButton = findViewById(R.id.add_station_button);
        mApiService = UtilsApi.getApiService();

        ArrayAdapter adCity = new ArrayAdapter(mContext, android.R.layout.simple_list_item_1, city);
        citySpinner.setAdapter(adCity);
        citySpinner.setOnItemSelectedListener(cityOISL);

        addStationButton.setOnClickListener(v -> {
            addStation();
        });
    }

    private void addStation() {
        String stationNameS = stationName.getText().toString();
        String cityS = selectedCity.toString();
        String addressS = address.getText().toString();

        mApiService.createStation(stationNameS, cityS, addressS).enqueue(new Callback<BaseResponse<Station>>() {
            @Override
            public void onResponse(Call<BaseResponse<Station>> call, Response<BaseResponse<Station>> response) {
                if(!response.isSuccessful()) {
                    viewToast(mContext, "Application error " + response.code());
                    return;
                }
                BaseResponse<Station> res = response.body();
                if(res.success) finish();
                viewToast(mContext, "Berhasil add station " + res.payload.stationName);
            }

            @Override
            public void onFailure(Call<BaseResponse<Station>> call, Throwable t) {
                viewToast(mContext, "Problem with the Server");
            }
        });
    }

    private void viewToast(Context ctx, String message) {
        Toast.makeText(ctx, message, Toast.LENGTH_SHORT).show();
    }
}