package com.unasp.atmosweatherapp.service;

import com.unasp.atmosweatherapp.model.CadastroRequest;
import com.unasp.atmosweatherapp.model.FavoriteCityResponse;
import com.unasp.atmosweatherapp.model.ForecastResponse;
import com.unasp.atmosweatherapp.model.LoginRequest;
import com.unasp.atmosweatherapp.model.LoginResponse;
import com.unasp.atmosweatherapp.model.WeatherComparisonResponse;
import com.unasp.atmosweatherapp.model.WeatherResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    @POST("/api/auth/login")
    Call<LoginResponse> fazerLogin(@Body LoginRequest request);

    @POST("/api/auth/register")
    Call<LoginResponse> cadastrarUsuario(@Body CadastroRequest request);

    @GET("/api/weather/forecast/current")
    Call<WeatherResponse> getCurrentWeather(@Query("city") String city);

    @GET("/api/weather/favorites")
    Call<List<FavoriteCityResponse>> getFavoriteCities();

    @POST("/api/weather/favorites")
    Call<FavoriteCityResponse> addFavoriteCity(
            @Query("cityName") String cityName,
            @Query("isDefault") boolean isDefault
    );

    @PUT("/api/weather/favorites/{cityId}/default")
    Call<FavoriteCityResponse> setDefaultCity(@Path("cityId") Long cityId);

    @DELETE("/api/weather/favorites/{cityId}")
    Call<Void> removeFavoriteCity(@Path("cityId") Long cityId);

    @GET("/api/weather/favorites/default/weather")
    Call<ForecastResponse> getDefaultCityWeather();

    @GET("/api/weather/compare")
    Call<List<WeatherComparisonResponse>> compareCities(
            @Query("cities") List<String> cities
    );

    @GET("/api/weather/forecast/five-day")
    Call<List<ForecastResponse>> getFiveDayForecast(@Query("city") String city);
}