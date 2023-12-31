package com.darrenJBusRD.jbus_android.request;

import com.darrenJBusRD.jbus_android.model.Account;

import com.darrenJBusRD.jbus_android.model.BaseResponse;
import com.darrenJBusRD.jbus_android.model.Bus;
import com.darrenJBusRD.jbus_android.model.BusType;
import com.darrenJBusRD.jbus_android.model.Facility;
import com.darrenJBusRD.jbus_android.model.Payment;
import com.darrenJBusRD.jbus_android.model.Renter;
import com.darrenJBusRD.jbus_android.model.Station;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface BaseApiService {
    @GET("account/{id}")
    Call<Account> getAccountById (@Path("id") int id);

    @POST("account/register")
    Call<BaseResponse<Account>> register (
            @Query("name") String name,
            @Query("email") String email,
            @Query("password") String password);

    @POST("account/login")
    Call<BaseResponse<Account>> login (
            @Query("email") String email,
            @Query("password") String password);

    @GET("station/getAllStation")
    Call<List<Station>> getAllStation();

    @GET("bus/getMyBus")
    Call<List<Bus>> getMyBus(
            @Query("accountId") int accountId);

    @POST("account/{id}/registerRenter")
    Call<BaseResponse<Renter>> registerRenter (
            @Path("id") int id,
            @Query("companyName") String companyName,
            @Query("address") String address,
            @Query("phoneNumber") String phoneNumber);

    @POST("account/{id}/topUp")
    Call<BaseResponse<Double>> topUp(
            @Path("id") int id,
            @Query("amount") double amount);

    @GET("bus/getAllBus")
    Call<List<Bus>> getAllBus();

    @POST("bus/createBus")
    Call<BaseResponse<Bus>> createBus (
            @Query("accountId") int accountId,
            @Query("name") String name,
            @Query("capacity") int capacity,
            @Query("facilities") List<Facility> facilities,
            @Query("busType") BusType busType,
            @Query("price") double price,
            @Query("stationDepartureId") int stationDepartureId,
            @Query("stationArrivalId") int stationArrivalId);

    @POST("station/createStation")
    Call<BaseResponse<Station>> createStation(
            @Query("stationName") String stationName,
            @Query("city") String city,
            @Query("address") String address);

    @POST("payment/makeBooking")
    Call<BaseResponse<Payment>> makeBooking (
            @Query("buyerId") int buyerId,
            @Query("renterId") int renterId,
            @Query("busId") int busId,
            @Query("busSeats") List<String> busSeats,
            @Query("departureDate") String departureDate);

    @GET("payment/getMyPayment")
    Call<List<Payment>> getMyPayment(
            @Query("buyerId") int buyerId);

    @POST("payment/{id}/cancel")
    Call<BaseResponse<Payment>> cancel (
            @Path("id") int id);

    @POST("payment/{id}/accept")
    Call<BaseResponse<Payment>> accept (
            @Path("id") int id);

    @POST("bus/addSchedule")
    Call<BaseResponse<Bus>> addSchedule (
            @Query("busId") int busId,
            @Query("time") String time);
}
