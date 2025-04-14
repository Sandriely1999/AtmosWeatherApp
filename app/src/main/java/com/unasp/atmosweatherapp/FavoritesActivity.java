package com.unasp.atmosweatherapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.unasp.atmosweatherapp.adapters.FavoriteCitiesAdapter;
import com.unasp.atmosweatherapp.model.FavoriteCityResponse;
import com.unasp.atmosweatherapp.service.ApiService;
import com.unasp.atmosweatherapp.service.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavoritesActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private FavoriteCitiesAdapter adapter;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new FavoriteCitiesAdapter(
                null,
                this::onFavoriteClicked,
                this::setAsDefaultCity,
                this::deleteFavoriteCity
        );
        recyclerView.setAdapter(adapter);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            Intent intent;
            if (id == R.id.nav_weather) {
                intent = new Intent(this, MainActivity.class);
            } else if (id == R.id.nav_forecast) {
                intent = new Intent(this, ForecastActivity.class);
            } else if (id == R.id.nav_compare) {
                intent = new Intent(this, CompareActivity.class);
            } else if (id == R.id.nav_favorite) {
                return true;
            } else {
                return false;
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            overridePendingTransition(0, 0);
            return true;
        });
        bottomNav.setSelectedItemId(R.id.nav_favorite);

        loadFavoriteCities();
    }

    private void loadFavoriteCities() {
        progressBar.setVisibility(View.VISIBLE);

        ApiService apiService = RetrofitClient.getClient(this).create(ApiService.class);
        Call<List<FavoriteCityResponse>> call = apiService.getFavoriteCities();

        call.enqueue(new Callback<List<FavoriteCityResponse>>() {
            @Override
            public void onResponse(Call<List<FavoriteCityResponse>> call, Response<List<FavoriteCityResponse>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    adapter.setCities(response.body());
                }
            }

            @Override
            public void onFailure(Call<List<FavoriteCityResponse>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.e("FavoritesActivity", "Erro ao carregar favoritos", t);
                Toast.makeText(FavoritesActivity.this, "Erro ao carregar favoritos: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }

        });
    }

    private void setAsDefaultCity(FavoriteCityResponse city) {
        if (city == null) return;

        ApiService apiService = RetrofitClient.getClient(this).create(ApiService.class);
        Call<FavoriteCityResponse> call = apiService.setDefaultCity(city.getId());

        call.enqueue(new Callback<FavoriteCityResponse>() {
            @Override
            public void onResponse(Call<FavoriteCityResponse> call, Response<FavoriteCityResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(FavoritesActivity.this, "Cidade padrão definida!", Toast.LENGTH_SHORT).show();
                    loadFavoriteCities();
                }
            }

            @Override
            public void onFailure(Call<FavoriteCityResponse> call, Throwable t) {
                Toast.makeText(FavoritesActivity.this, "Erro ao definir cidade padrão", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteFavoriteCity(FavoriteCityResponse city) {
        if (city == null) return;

        ApiService apiService = RetrofitClient.getClient(this).create(ApiService.class);
        Call<Void> call = apiService.removeFavoriteCity(city.getId());

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(FavoritesActivity.this,
                            "Favorito removido", Toast.LENGTH_SHORT).show();
                    loadFavoriteCities();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(FavoritesActivity.this,
                        "Erro ao remover favorito", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void onFavoriteClicked(FavoriteCityResponse city) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("selectedCity", city.getCityName());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadFavoriteCities();
    }
}