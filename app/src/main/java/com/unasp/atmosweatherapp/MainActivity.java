package com.unasp.atmosweatherapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.unasp.atmosweatherapp.model.WeatherResponse;
import com.unasp.atmosweatherapp.service.ApiService;
import com.unasp.atmosweatherapp.service.RetrofitClient;
import com.unasp.atmosweatherapp.utils.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private SessionManager session;
    private EditText etSearchCity;
    private Button btnSearch, btnLogout;
    private CardView cardWeather;
    private TextView tvCity, tvTemperature, tvHumidity, tvDescription, tvDateTime;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        session = new SessionManager(this);
        if (!session.isLoggedIn()) {
            redirectToLogin();
            return;
        }

        initViews();
        setupListeners();
    }

    private void initViews() {
        etSearchCity = findViewById(R.id.etSearchCity);
        btnSearch = findViewById(R.id.btnSearch);
        btnLogout = findViewById(R.id.btnLogout);
        cardWeather = findViewById(R.id.cardWeather);
        tvCity = findViewById(R.id.tvCity);
        tvTemperature = findViewById(R.id.tvTemperature);
        tvHumidity = findViewById(R.id.tvHumidity);
        tvDescription = findViewById(R.id.tvDescription);
        tvDateTime = findViewById(R.id.tvDateTime);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupListeners() {
        btnSearch.setOnClickListener(v -> {
            String city = etSearchCity.getText().toString().trim();
            if (!city.isEmpty()) {
                fetchWeatherData(city);
            } else {
                Toast.makeText(this, "Digite uma cidade", Toast.LENGTH_SHORT).show();
            }
        });

        btnLogout.setOnClickListener(v -> logout());
    }

    private void fetchWeatherData(String city) {
        progressBar.setVisibility(View.VISIBLE);
        cardWeather.setVisibility(View.GONE);

        ApiService apiService = RetrofitClient.getClient(this).create(ApiService.class);
        Call<WeatherResponse> call = apiService.getCurrentWeather(city);

        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    displayWeatherData(response.body());
                } else {
                    Toast.makeText(MainActivity.this,
                            "Não foi possível obter dados para esta cidade",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this,
                        "Erro de conexão: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayWeatherData(WeatherResponse weather) {
        cardWeather.setVisibility(View.VISIBLE);

        tvCity.setText(weather.getCity());
        tvTemperature.setText(String.format("Temperatura: %.1f°C", weather.getTemperature()));
        tvHumidity.setText(String.format("Umidade: %d%%", weather.getHumidity()));
        tvDescription.setText(weather.getDescription());
        tvDateTime.setText(String.format("Atualizado em: %s",
                weather.getForecastDate().toString()));
    }

    private void logout() {
        session.clear();
        redirectToLogin();
        Toast.makeText(this, "Você saiu da sua conta", Toast.LENGTH_SHORT).show();
    }

    private void redirectToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}