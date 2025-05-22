package com.unasp.atmosweatherapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.unasp.atmosweatherapp.model.WeatherComparisonResponse;
import com.unasp.atmosweatherapp.service.ApiService;
import com.unasp.atmosweatherapp.service.RetrofitClient;
import com.unasp.atmosweatherapp.utils.SessionManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CompareActivity extends AppCompatActivity {

    private static final String TAG = "CompareActivity";

    private EditText etCity1, etCity2;
    private CardView cardCity1, cardCity2;
    private TextView tvCity1Name, tvCity1Temp, tvCity1Humidity, tvCity1Description;
    private TextView tvCity2Name, tvCity2Temp, tvCity2Humidity, tvCity2Description;
    private TextView tvTempDiff, tvHumidityDiff;
    private ProgressBar progressBar;
    private LinearLayout compareContainer;
    SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compare);

        sessionManager = new SessionManager(this);
        if (!sessionManager.isLoggedIn()) {
            redirectToLogin();
            return;
        }

        initViews();
        setupBottomNavigation();
    }

    void initViews() {
        etCity1 = findViewById(R.id.etCity1);
        etCity2 = findViewById(R.id.etCity2);
        cardCity1 = findViewById(R.id.cardCity1);
        cardCity2 = findViewById(R.id.cardCity2);
        tvCity1Name = findViewById(R.id.tvCity1Name);
        tvCity1Temp = findViewById(R.id.tvCity1Temp);
        tvCity1Humidity = findViewById(R.id.tvCity1Humidity);
        tvCity1Description = findViewById(R.id.tvCity1Description);
        tvCity2Name = findViewById(R.id.tvCity2Name);
        tvCity2Temp = findViewById(R.id.tvCity2Temp);
        tvCity2Humidity = findViewById(R.id.tvCity2Humidity);
        tvCity2Description = findViewById(R.id.tvCity2Description);
        tvTempDiff = findViewById(R.id.tvTempDiff);
        tvHumidityDiff = findViewById(R.id.tvHumidityDiff);
        progressBar = findViewById(R.id.progressBar);
        compareContainer = findViewById(R.id.compareContainer);

        findViewById(R.id.btnCompare).setOnClickListener(v -> compareCities());
    }

    void setupBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setSelectedItemId(R.id.nav_compare);

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            Intent intent = null;

            if (id == R.id.nav_weather) {
                intent = new Intent(this, MainActivity.class);
            } else if (id == R.id.nav_forecast) {
                intent = new Intent(this, ForecastActivity.class);
            } else if (id == R.id.nav_compare) {
                return true; // já estamos nesta tela
            } else if (id == R.id.nav_favorite) {
                intent = new Intent(this, FavoritesActivity.class);
            }

            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);
                overridePendingTransition(0, 0);
                return true;
            }
            return false;
        });
    }

    private void compareCities() {
        String city1 = etCity1.getText().toString().trim();
        String city2 = etCity2.getText().toString().trim();

        if (city1.isEmpty() || city2.isEmpty()) {
            Toast.makeText(this, "Digite ambas as cidades para comparar", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        compareContainer.setVisibility(View.GONE);

        List<String> cities = new ArrayList<>();
        cities.add(city1);
        cities.add(city2);

        ApiService apiService = RetrofitClient.getClient(this).create(ApiService.class);
        Call<List<WeatherComparisonResponse>> call = apiService.compareCities(cities);

        call.enqueue(new Callback<List<WeatherComparisonResponse>>() {
            @Override
            public void onResponse(Call<List<WeatherComparisonResponse>> call, Response<List<WeatherComparisonResponse>> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    WeatherComparisonResponse comparison = response.body().get(0);
                    displayComparisonData(comparison);
                    compareContainer.setVisibility(View.VISIBLE);
                } else {
                    String errorMessage = "Erro desconhecido ao comparar cidades.";
                    if (response.errorBody() != null) {
                        try {
                            errorMessage = response.errorBody().string();
                        } catch (IOException e) {
                            Log.e(TAG, "Erro ao ler corpo do erro", e);
                        }
                    }
                    Log.e(TAG, "Falha na resposta: Código " + response.code() + ", Erro: " + errorMessage);
                    Toast.makeText(CompareActivity.this,
                            "Erro ao comparar cidades: " + response.code() + " - " + errorMessage,
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<WeatherComparisonResponse>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.e(TAG, "Falha na requisição", t);
                Toast.makeText(CompareActivity.this,
                        "Falha na conexão: " + t.getClass().getSimpleName() + " - " + t.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    void displayComparisonData(WeatherComparisonResponse comparison) {
        tvCity1Name.setText(comparison.getCityA());
        tvCity1Temp.setText(String.format("Temperatura: %.1f°C", comparison.getTemperatureA()));
        tvCity1Humidity.setText(String.format("Umidade: %d%%", comparison.getHumidityA()));
        tvCity1Description.setText(comparison.getDescriptionA());
        cardCity1.setVisibility(View.VISIBLE);

        tvCity2Name.setText(comparison.getCityB());
        tvCity2Temp.setText(String.format("Temperatura: %.1f°C", comparison.getTemperatureB()));
        tvCity2Humidity.setText(String.format("Umidade: %d%%", comparison.getHumidityB()));
        tvCity2Description.setText(comparison.getDescriptionB());
        cardCity2.setVisibility(View.VISIBLE);

        String tempDiffText = String.format("Diferença de Temparatura: %.1f°C", comparison.getTemperatureDifference());
        String humidityDiffText = String.format("Diferença de Umidade: %d%%", comparison.getHumidityDifference());

        tvTempDiff.setText(tempDiffText);
        tvHumidityDiff.setText(humidityDiffText);

        if (comparison.getTemperatureDifference() > 0) {
            tvTempDiff.setTextColor(getResources().getColor(R.color.accent_red));
        } else if (comparison.getTemperatureDifference() < 0) {
            tvTempDiff.setTextColor(getResources().getColor(R.color.primary_blue));
        } else {
            tvTempDiff.setTextColor(getResources().getColor(R.color.text_secondary));
        }
    }

    void redirectToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}