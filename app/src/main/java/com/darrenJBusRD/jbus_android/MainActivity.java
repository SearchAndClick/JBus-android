package com.darrenJBusRD.jbus_android;

import com.darrenJBusRD.jbus_android.model.Account;
import com.darrenJBusRD.jbus_android.model.BaseResponse;
import com.darrenJBusRD.jbus_android.request.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

    public static BusArrayAdapter busArrayAdapter;
    private ListView listView;
    private Button[] buttons;
    private int currentPage = 0;
    private int pageSize = 12;
    private int listSize;
    private int noOfPages;
    private List<Bus> listBus = new ArrayList<>();
    private Button prevButton = null;
    private Button nextButton = null;
    private ListView busListView = null;
    private HorizontalScrollView pageScroll = null;
    Context mContext;
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext = this;
        ActionBar actionBar = getActionBar();

        BaseApiService mApiService = UtilsApi.getApiService();
        mApiService.getAllBus().enqueue(new Callback<List<Bus>>() {
            @Override
            public void onResponse(Call<List<Bus>> call, Response<List<Bus>> response) {
                if(!response.isSuccessful()) {
                    viewToast(mContext, "Application error " + response.code());
                    return;
                }
                listBus = response.body();
                busArrayAdapter = new BusArrayAdapter(mContext, listBus);
            }

            @Override
            public void onFailure(Call<List<Bus>> call, Throwable t) {
                viewToast(mContext, "Problem with Server");
            }
        });

        listView = findViewById(R.id.list_view_main);
        listView.setAdapter(busArrayAdapter);

        prevButton = findViewById(R.id.prev_page);
        nextButton = findViewById(R.id.next_page);
        pageScroll = findViewById(R.id.page_number_scroll);

        /*
        listBus = Bus.sampleBusList(20);
        listSize = listBus.size();
        busListView = busArrayAdapter.getView();
        */

        paginationFooter();
        goToPage(currentPage);

        prevButton.setOnClickListener(v -> {
            currentPage = currentPage != 0 ? currentPage - 1 : 0;
            goToPage(currentPage);
        });

        nextButton.setOnClickListener(v -> {
            currentPage = currentPage != noOfPages - 1 ? currentPage + 1 : currentPage;
            goToPage(currentPage);
        });


        /*
        mApiService.getAccountById(LoginActivity.loggedAccount.id).enqueue(new Callback<Account>() {
            @Override
            public void onResponse(Call<Account> call, Response<Account> response) {
                if(!response.isSuccessful()) {
                    viewToast(mContext, "Application error " + response.code());
                    return;
                }
                Account responseAccount = response.body();
            }

            @Override
            public void onFailure(Call<Account> call, Throwable t) {

            }
        });
        */
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
            //moveActivity();
            return true;
        } else return super.onOptionsItemSelected(item);
    }

    private void paginationFooter() {
        int val = listSize % pageSize;
        val = val == 0 ? 0 : 1;
        noOfPages = listSize / pageSize + val;

        LinearLayout ll = findViewById(R.id.btn_layout);
        buttons = new Button[noOfPages];
        if(noOfPages <= 6) {
            ((FrameLayout.LayoutParams) ll.getLayoutParams()).gravity = Gravity.CENTER;
        }

        for(int i = 0; i < noOfPages; i++) {
            buttons[i] = new Button(this);
            buttons[i].setBackgroundColor(getResources().getColor(R.color.black));
            buttons[i].setText(""+(i+1));
            buttons[i].setTextColor(getResources().getColor(R.color.black));
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(100, 100);
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
        
        BusArrayAdapter paginatedAdapater = (BusArrayAdapter) listView.getAdapter();
        busListView.setAdapter(paginatedAdapater);

    }

    private void moveActivity(Context ctx, Class<?> cls) {
        Intent intent = new Intent(ctx, cls);
        startActivity(intent);
    }

    private void viewToast(Context ctx, String message) {
        Toast.makeText(ctx, message, Toast.LENGTH_SHORT).show();
    }
}