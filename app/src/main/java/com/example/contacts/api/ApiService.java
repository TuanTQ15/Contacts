package com.example.contacts.api;

import com.example.contacts.model.Contact;
import com.example.contacts.model.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    Gson gson = new GsonBuilder().create();

    ApiService apiService = new Retrofit.Builder()
            .baseUrl("https://contact-ptit.herokuapp.com")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build().create(ApiService.class);

    @GET("api/contacts")
    Call<List<Contact>> getAllContact(@Query("accountId") int accountId);

    @GET("api/contacts/{id}")
    Call<Contact> getContact(@Path("id") int id);

    @PUT("api/contacts/{accountId}/{id}")
    Call<Contact> putContact(@Path("accountId") int accountId, @Path("id") int id, @Body Contact contact);

    @POST("api/contacts/{accountId}")
    Call<Contact> postContact(@Path("accountId") int accountId, @Body Contact contact);

    @POST("api/login")
    Call<User> login(@Body User user);

    @POST("api/register")
    Call<User> register(@Body User user);

    @DELETE("api/contacts/{id}")
    Call<Void> deleteContact(@Path("id") int id);
}