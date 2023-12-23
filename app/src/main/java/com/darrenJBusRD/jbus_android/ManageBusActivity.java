package com.darrenJBusRD.jbus_android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import android.content.Context;
import android.os.Bundle;
import android.widget.Button;

import com.darrenJBusRD.jbus_android.model.Bus;
import com.darrenJBusRD.jbus_android.request.BaseApiService;
import com.darrenJBusRD.jbus_android.request.UtilsApi;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManageBusActivity extends AppCompatActivity {

    public static Context mContext;
    public static List<Bus> listBus = new ArrayList<>();
    ListView listView = null;
    private MyBusArrayAdapter busArrayAdapter = null;
    private Button[] buttons;
    private int currentPage = 0;
    private int pageSize = 16;
    private int listSize;
    private int noOfPages;
    private Button prevButton = null;
    private Button nextButton = null;
    private HorizontalScrollView pageScroll = null;
    private BaseApiService mApiService = UtilsApi.getApiService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_bus);

        mContext = this;
        prevButton = findViewById(R.id.prev_page_manage_bus);
        nextButton = findViewById(R.id.next_page_manage_bus);
        pageScroll = findViewById(R.id.page_number_scroll_manage_bus);
        listView = findViewById(R.id.list_view_manage_bus);

        mApiService.getMyBus(LoginActivity.loggedAccount.id).enqueue(new Callback<List<Bus>>() {
            @Override
            public void onResponse(Call<List<Bus>> call, Response<List<Bus>> response) {
                if(!response.isSuccessful()) {
                    viewToast(mContext, "Application error " + response.code());
                    return;
                }
                listBus = response.body();
                busArrayAdapter = new MyBusArrayAdapter(mContext, listBus);
                listView.setAdapter(busArrayAdapter);
                listSize = listBus.size();

                paginationFooter();
                if(buttons == null) return;
                goToPage(currentPage);

                prevButton.setOnClickListener(v -> {
                    currentPage = currentPage != 0 ? currentPage - 1 : 0;
                    goToPage(currentPage);
                });

                nextButton.setOnClickListener(v -> {
                    currentPage = currentPage != noOfPages - 1 ? currentPage + 1 : currentPage;
                    goToPage(currentPage);
                });
            }

            @Override
            public void onFailure(Call<List<Bus>> call, Throwable t) {
                viewToast(mContext, "Problem with Server");
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        mApiService.getMyBus(LoginActivity.loggedAccount.id).enqueue(new Callback<List<Bus>>() {
            @Override
            public void onResponse(Call<List<Bus>> call, Response<List<Bus>> response) {
                if(!response.isSuccessful()) {
                    viewToast(mContext, "Application error " + response.code());
                    return;
                }
                listBus = response.body();
                busArrayAdapter = new MyBusArrayAdapter(mContext, listBus);
                listView.setAdapter(busArrayAdapter);
                listSize = listBus.size();

                paginationFooter();
                if(buttons == null) return;
                goToPage(currentPage);

                prevButton.setOnClickListener(v -> {
                    currentPage = currentPage != 0 ? currentPage - 1 : 0;
                    goToPage(currentPage);
                });

                nextButton.setOnClickListener(v -> {
                    currentPage = currentPage != noOfPages - 1 ? currentPage + 1 : currentPage;
                    goToPage(currentPage);
                });
            }

            @Override
            public void onFailure(Call<List<Bus>> call, Throwable t) {
                viewToast(mContext, "Problem with Server");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.manage_bus_bar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.add_bus_menu) {
            moveActivity(mContext, AddBusActivity.class);
        }
        else if(item.getItemId() == R.id.add_station_menu) {
            moveActivity(mContext, AddStationActivity.class);
        }
        return super.onOptionsItemSelected(item);
    }

    private void paginationFooter() {
        int val = listSize % pageSize;
        val = val == 0 ? 0 : 1;
        noOfPages = listSize / pageSize + val;

        if(noOfPages <= 1) {
            prevButton.setVisibility(View.GONE);
            nextButton.setVisibility(View.GONE);
            pageScroll.setVisibility(View.GONE);
            return;
        }
        prevButton.setVisibility(View.VISIBLE);
        nextButton.setVisibility(View.VISIBLE);
        pageScroll.setVisibility(View.VISIBLE);

        LinearLayout ll = findViewById(R.id.btn_layout_manage_bus);
        buttons = new Button[noOfPages];
        if(noOfPages <= 6) {
            ((FrameLayout.LayoutParams) ll.getLayoutParams()).gravity = Gravity.CENTER;
        }

        for(int i = 0; i < noOfPages; i++) {
            buttons[i] = new Button(mContext);
            buttons[i].setBackgroundColor(getResources().getColor(R.color.black));
            buttons[i].setText(""+(i+1));
            buttons[i].setTextColor(getResources().getColor(R.color.black));
            buttons[i].setTextSize(16);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(150, 150);
            ll.addView(buttons[i], lp);
            final int j = i;
            buttons[j].setOnClickListener(v -> {
                currentPage = j;
                goToPage(j);
            });
        }
    }

    private void goToPage(int index) {
        for(int i = 0; i < noOfPages; i++) {
            if(i == index) {
                buttons[index].setBackgroundDrawable(getResources().getDrawable(R.drawable.oval));
                buttons[i].setTextColor(getResources().getColor(android.R.color.white));
                scrollToFrom(buttons[index]);
                viewPaginatedList(listBus, currentPage);
            }
            else {
                buttons[i].setBackgroundColor(getResources().getColor(android.R.color.transparent));
                buttons[i].setTextColor(getResources().getColor(android.R.color.black));
            }
        }
    }

    private void scrollToFrom(Button item) {
        int scrollX = item.getLeft() - (pageScroll.getWidth() - item.getWidth()) / 2;
        pageScroll.smoothScrollTo(scrollX, 0);
    }

    private void viewPaginatedList(List<Bus> listBus, int page) {
        int startIndex = page * pageSize;
        int endIndex = Math.min(startIndex + pageSize, listBus.size());
        List<Bus> paginatedList = listBus.subList(startIndex, endIndex);

        busArrayAdapter = null;
        busArrayAdapter = new MyBusArrayAdapter(mContext, paginatedList);
        listView.setAdapter(busArrayAdapter);
//        MyBusArrayAdapter paginatedAdapater = (MyBusArrayAdapter) listView.getAdapter();
//        busListView.setAdapter(paginatedAdapater);

    }

    private void moveActivity(Context ctx, Class<?> cls) {
        Intent intent = new Intent(ctx, cls);
        startActivity(intent);
    }

    private void viewToast(Context ctx, String message) {
        Toast.makeText(ctx, message, Toast.LENGTH_SHORT).show();
    }
}