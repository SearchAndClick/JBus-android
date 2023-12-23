package com.darrenJBusRD.jbus_android.model;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class Invoice extends Serializable
{
    public enum BusRating
    {
        NONE,
        NEUTRAL,
        GOOD,
        BAD;
    }

    public enum PaymentStatus
    {
        FAILED,
        WAITING,
        SUCCESS;
    }

    public Timestamp time;
    public int buyerId;
    public int renterId;
    public BusRating rating = BusRating.NONE;
    public PaymentStatus status = PaymentStatus.WAITING;

    protected Invoice(int buyerId, int renterId)
    {
        super();
        this.buyerId = buyerId;
        this.renterId = renterId;
        this.time = Timestamp.valueOf(LocalDateTime.now().toString());
    }

    public Invoice(Account buyer, Renter renter)
    {
        super();
        this.buyerId = buyer.id ;
        this.renterId = renter.id ;
        this.time = Timestamp.valueOf(LocalDateTime.now().toString());
    }
}
