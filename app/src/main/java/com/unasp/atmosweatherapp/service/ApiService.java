package com.unasp.atmosweatherapp.service;

import com.unasp.atmosweatherapp.model.CadastroRequest;
import com.unasp.atmosweatherapp.model.CadastroResponse;
import com.unasp.atmosweatherapp.model.LoginRequest;
import com.unasp.atmosweatherapp.model.LoginResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {
    @POST("/api/auth/login")
    Call<LoginResponse> fazerLogin(@Body LoginRequest request);

    @POST("/api/auth/register")
    Call<LoginResponse> cadastrarUsuario(@Body CadastroRequest request); // Mudar o retorno para LoginResponse
}