package com.darrenJBusRD.jbus_android;

import static androidx.core.content.ContextCompat.startActivity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.darrenJBusRD.jbus_android.model.Payment;

import java.text.SimpleDateFormat;
import java.util.List;

public class PaymentArrayAdapter extends ArrayAdapter<Payment> {

    public PaymentArrayAdapter(@NonNull Context ctx, List<Payment> arrayList) {
        super(ctx, 0, arrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View currentPaymentView = convertView;

        if(currentPaymentView == null) {
            currentPaymentView = LayoutInflater.from(getContext()).inflate(R.layout.payment_list_view, parent, false);
        }

        Payment currentPaymentPosition = getItem(position);

        TextView status = currentPaymentView.findViewById(R.id.status_payment);
        status.setText(currentPaymentPosition.status.toString());

        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy hh:mm:ss");
        TextView departureDate = currentPaymentView.findViewById(R.id.departure_payment);
        departureDate.setText(dateFormat.format(currentPaymentPosition.departureDate));

        LinearLayout linearLayout = currentPaymentView.findViewById(R.id.layout_payment);
        linearLayout.setOnClickListener(ll -> {
            Intent intent = new Intent(PaymentActivity.mContext, PaymentDetailActivity.class);
            PaymentDetailActivity.paymentDetail = currentPaymentPosition;
            startActivity(PaymentActivity.mContext, intent, null);
        });

        return currentPaymentView;
    }
}