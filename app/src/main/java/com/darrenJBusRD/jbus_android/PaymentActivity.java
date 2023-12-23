package com.darrenJBusRD.jbus_android;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.darrenJBusRD.jbus_android.model.Payment;
import com.darrenJBusRD.jbus_android.request.BaseApiService;
import com.darrenJBusRD.jbus_android.request.UtilsApi;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentActivity extends AppCompatActivity {

    public static Context mContext;
    private List<Payment> listPayment = new ArrayList<>();
    ListView listView = null;
    private PaymentArrayAdapter paymentArrayAdapter = null;
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
        setContentView(R.layout.activity_payment);

        mContext = this;
        prevButton = findViewById(R.id.prev_page_payment);
        nextButton = findViewById(R.id.next_page_payment);
        pageScroll = findViewById(R.id.page_number_scroll_payment);
        listView = findViewById(R.id.list_view_payment);

        mApiService.getMyPayment(LoginActivity.loggedAccount.id).enqueue(new Callback<List<Payment>>() {
            @Override
            public void onResponse(Call<List<Payment>> call, Response<List<Payment>> response) {
                if(!response.isSuccessful()) {
                    viewToast(mContext, "Application error " + response.code());
                    return;
                }
                listPayment = response.body();
                paymentArrayAdapter = new PaymentArrayAdapter(mContext, listPayment);
                listView.setAdapter(paymentArrayAdapter);
                listSize = listPayment.size();

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
            public void onFailure(Call<List<Payment>> call, Throwable t) {
                Log.e(TAG, "Network request failed", t);
                viewToast(mContext, "Problem with Server");
            }
        });
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

        LinearLayout ll = findViewById(R.id.btn_layout_payment);
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
                viewPaginatedList(listPayment, currentPage);
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

    private void viewPaginatedList(List<Payment> listPayment, int page) {
        int startIndex = page * pageSize;
        int endIndex = Math.min(startIndex + pageSize, listPayment.size());
        List<Payment> paginatedList = listPayment.subList(startIndex, endIndex);

        paymentArrayAdapter = null;
        paymentArrayAdapter = new PaymentArrayAdapter(mContext, paginatedList);
        listView.setAdapter(paymentArrayAdapter);
    }

    private void viewToast(Context ctx, String message) {
        Toast.makeText(ctx, message, Toast.LENGTH_SHORT).show();
    }
}