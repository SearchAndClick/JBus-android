package com.darrenJBusRD.jbus_android;

import com.darrenJBusRD.jbus_android.request.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.content.Context;
import android.widget.Toast;

import com.darrenJBusRD.jbus_android.model.Bus;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    public static Context mContext;
    public BusArrayAdapter busArrayAdapter;
    private ListView listView;
    private Button[] buttons;
    private int currentPage = 0;
    private int pageSize = 16;
    private int listSize;
    private int noOfPages;
    public static List<Bus> listBus = new ArrayList<>();
    private Button prevButton = null;
    private Button nextButton = null;
    private HorizontalScrollView pageScroll = null;
    private BaseApiService mApiService = UtilsApi.getApiService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle(Html.fromHtml("<font color=\"white\">" + getString(R.string.app_name) + "</font>"));

        mContext = this;
        prevButton = findViewById(R.id.prev_page);
        nextButton = findViewById(R.id.next_page);
        pageScroll = findViewById(R.id.page_number_scroll);
        listView = findViewById(R.id.list_view_main);

        mApiService.getAllBus().enqueue(new Callback<List<Bus>>() {
            @Override
            public void onResponse(Call<List<Bus>> call, Response<List<Bus>> response) {
                if(!response.isSuccessful()) {
                    viewToast(mContext, "Application error " + response.code());
                    return;
                }
                listBus = response.body();
                for(int i = 0; i < listBus.size(); i++) {
                    if(listBus.get(i).schedules.isEmpty()) {
                        listBus.remove(i);
                        i--;
                    }
                }
//                listBus = Bus.sampleBusList(18);
                busArrayAdapter = new BusArrayAdapter(mContext, listBus);
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
        mApiService.getAllBus().enqueue(new Callback<List<Bus>>() {
            @Override
            public void onResponse(Call<List<Bus>> call, Response<List<Bus>> response) {
                if(!response.isSuccessful()) {
                    viewToast(mContext, "Application error " + response.code());
                    return;
                }
                listBus = response.body();
                for(int i = 0; i < listBus.size(); i++) {
                    if(listBus.get(i).schedules.isEmpty()) {
                        listBus.remove(i);
                        i--;
                    }
                }
                busArrayAdapter = new BusArrayAdapter(mContext, listBus);
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
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Handle item selection.
        if(item.getItemId() == R.id.search_menu) {
            return true;
        } else if (item.getItemId() == R.id.profile_menu) {
            moveActivity(mContext, AboutMeActivity.class);
            return true;
        } else if (item.getItemId() == R.id.payment_menu) {
            moveActivity(mContext, PaymentActivity.class);
            return true;
        } else return super.onOptionsItemSelected(item);
    }

    private void paginationFooter() {
        int val = listSize % pageSize;
        val = val == 0 ? 0 : 1;
        noOfPages = listSize / pageSize + val;
//        System.out.println(noOfPages + " " + listSize + " " + pageSize + " " + val);
        if(noOfPages <= 1) {
            prevButton.setVisibility(View.GONE);
            nextButton.setVisibility(View.GONE);
            pageScroll.setVisibility(View.GONE);
            return;
        }
        prevButton.setVisibility(View.VISIBLE);
        nextButton.setVisibility(View.VISIBLE);
        pageScroll.setVisibility(View.VISIBLE);

        LinearLayout ll = findViewById(R.id.btn_layout);
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
        busArrayAdapter = new BusArrayAdapter(mContext, paginatedList);
        listView.setAdapter(busArrayAdapter);
//        BusArrayAdapter paginatedAdapater = (BusArrayAdapter) listView.getAdapter();
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