package com.unasp.atmosweatherapp.service;

import com.unasp.atmosweatherapp.model.CadastroRequest;
import com.unasp.atmosweatherapp.model.CadastroResponse;
import com.unasp.atmosweatherapp.model.LoginRequest;
import com.unasp.atmosweatherapp.model.LoginResponse;
import com.unasp.atmosweatherapp.model.WeatherResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {
    @POST("/api/auth/login")
    Call<LoginResponse> fazerLogin(@Body LoginRequest request);

    @POST("/api/auth/register")
    Call<LoginResponse> cadastrarUsuario(@Body CadastroRequest request);

    @GET("/api/weather/current")
    Call<WeatherResponse> getCurrentWeather(@Query("city") String city);
}