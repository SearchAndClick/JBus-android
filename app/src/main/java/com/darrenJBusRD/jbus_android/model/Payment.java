package com.darrenJBusRD.jbus_android.model;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;

public class Payment extends Invoice{
    public int busId;
    public Timestamp departureDate;
    public List<String> busSeats;

    public Payment(int buyerId, int renterId, int busId, List<String> busSeats, Timestamp departureDate)
    {
        super(buyerId, renterId);
        this.busId = busId;
        this.departureDate = departureDate;
        this.busSeats = busSeats;
    }

    public Payment(Account buyer, Renter renter, int busId, List<String> busSeats, Timestamp departureDate)
    {
        super(buyer, renter);
        this.busId = busId;
        this.departureDate = departureDate;
        this.busSeats = busSeats;
    }
}
