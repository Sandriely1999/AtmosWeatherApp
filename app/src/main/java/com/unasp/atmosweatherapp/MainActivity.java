package com.unasp.atmosweatherapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.unasp.atmosweatherapp.model.FavoriteCityResponse;
import com.unasp.atmosweatherapp.model.WeatherResponse;
import com.unasp.atmosweatherapp.service.ApiService;
import com.unasp.atmosweatherapp.service.RetrofitClient;
import com.unasp.atmosweatherapp.utils.SessionManager;

import java.io.IOException;
import java.util.List;

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
    private ImageButton btnFavorite;
    private Long currentFavoriteId = null;
    private boolean isCurrentFavorite = false;
    private BottomNavigationView bottomNavigation;
    private ImageView ivWeatherIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setSelectedItemId(R.id.nav_favorite);

        session = new SessionManager(this);
        if (!session.isLoggedIn()) {
            redirectToLogin();
            return;
        }

        initViews();
        setupListeners();
        handleFavoriteSelection();
    }

    private void handleFavoriteSelection() {
        if (getIntent() != null && getIntent().hasExtra("selectedCity")) {
            String selectedCity = getIntent().getStringExtra("selectedCity");
            etSearchCity.setText(selectedCity);
            fetchWeatherData(selectedCity);
        }
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
        btnFavorite = findViewById(R.id.btnFavorite);
        bottomNavigation = findViewById(R.id.bottom_navigation);
        ivWeatherIcon = findViewById(R.id.ivWeatherIcon);
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

        btnFavorite.setOnClickListener(v -> {
            v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
            v.setSelected(!v.isSelected()); // Atualiza visualmente imediatamente
            if (isCurrentFavorite) {
                removeFavoriteCity();
            } else {
                addFavoriteCity();
            }
        });

        bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            Intent intent;
            if (id == R.id.nav_weather) {
                return true;
            } else if (id == R.id.nav_forecast) {
                intent = new Intent(this, ForecastActivity.class);
            } else if (id == R.id.nav_compare) {
                intent = new Intent(this, CompareActivity.class);
            } else if (id == R.id.nav_favorite) {
                intent = new Intent(this, FavoritesActivity.class);
            } else if (id == R.id.nav_profile) {
                intent = new Intent(this, ProfileActivity.class);
            } else {
                return false;
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            overridePendingTransition(0, 0);
            return true;
        });

        // Define a tela ativa como "Clima" por padrão
        bottomNavigation.setSelectedItemId(R.id.nav_weather);
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
                    try {
                        String errorBody = response.errorBody() != null ?
                                response.errorBody().string() : "Erro vazio";
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
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
       try {
           cardWeather.setVisibility(View.VISIBLE);

           tvCity.setText(weather.getCity());
           tvTemperature.setText(String.format("Temperatura: %.1f°C", weather.getTemperature()));
           tvHumidity.setText(String.format("Umidade: %d%%", weather.getHumidity()));
           tvDescription.setText(weather.getDescription());
           tvDateTime.setText(String.format("Atualizado em: %s",
                   weather.getForecastDate()));

           setWeatherIcon(weather.getDescription().toLowerCase());
           checkIfCityIsFavorite(weather.getCity());
       } catch (Exception e) {
           throw new RuntimeException(e);
       }


    }

    private void setWeatherIcon(String description) {
        int iconResId;
        if (description.contains("chuva")) {
            iconResId = R.drawable.ic_rain;
        } else if (description.contains("nublado")) {
            iconResId = R.drawable.ic_cloudy;
        } else if (description.contains("neblina")) {
            iconResId = R.drawable.ic_cloudy;
        } else if (description.contains("vento")) {
            iconResId = R.drawable.ic_wind;
        } else if (description.contains("algumas nuvens")) {
            iconResId = R.drawable.ic_partly_cloudy_day;
        } else {
            iconResId = R.drawable.ic_sunny;
        }
        ivWeatherIcon.setImageResource(iconResId);
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

    private void addFavoriteCity() {
        String cityName = tvCity.getText().toString().split(",")[0].trim();
        boolean setAsDefault = false; // Ou adicione lógica para definir como padrão

        ApiService apiService = RetrofitClient.getClient(this).create(ApiService.class);
        Call<FavoriteCityResponse> call = apiService.addFavoriteCity(cityName, setAsDefault);

        call.enqueue(new Callback<FavoriteCityResponse>() {
            @Override
            public void onResponse(Call<FavoriteCityResponse> call, Response<FavoriteCityResponse> response) {
                if (response.isSuccessful()) {
                    currentFavoriteId = response.body().getId();
                    isCurrentFavorite = true;
                    updateFavoriteButton();
                    Toast.makeText(MainActivity.this, "Cidade favoritada!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<FavoriteCityResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Erro ao favoritar", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void removeFavoriteCity() {
        if (currentFavoriteId == null) return;

        ApiService apiService = RetrofitClient.getClient(this).create(ApiService.class);
        Call<Void> call = apiService.removeFavoriteCity(currentFavoriteId);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    currentFavoriteId = null;
                    isCurrentFavorite = false;
                    updateFavoriteButton();
                    Toast.makeText(MainActivity.this, "Favorito removido", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Erro ao remover favorito", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateFavoriteButton() {
        runOnUiThread(() -> {
            btnFavorite.setSelected(isCurrentFavorite);
            btnFavorite.invalidate(); // Força a redraw
            btnFavorite.animate()
                    .scaleX(1.2f)
                    .scaleY(1.2f)
                    .setDuration(200)
                    .withEndAction(() -> btnFavorite.animate().scaleX(1f).scaleY(1f).setDuration(200));
        });
    }

    private void checkIfCityIsFavorite(String cityName) {
        ApiService apiService = RetrofitClient.getClient(this).create(ApiService.class);
        Call<List<FavoriteCityResponse>> call = apiService.getFavoriteCities();

        call.enqueue(new Callback<List<FavoriteCityResponse>>() {
            @Override
            public void onResponse(Call<List<FavoriteCityResponse>> call, Response<List<FavoriteCityResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    isCurrentFavorite = false;
                    for (FavoriteCityResponse favorite : response.body()) {
                        if (favorite.getCityName().equalsIgnoreCase(cityName)) {
                            currentFavoriteId = favorite.getId();
                            isCurrentFavorite = true;
                            break;
                        }
                    }
                    runOnUiThread(() -> updateFavoriteButton());
                }
            }

            @Override
            public void onFailure(Call<List<FavoriteCityResponse>> call, Throwable t) {
                Log.e("Favoritos", "Erro ao verificar favoritos", t);
            }
        });
    }

    private void setDefaultCity(Long cityId) {
        ApiService apiService = RetrofitClient.getClient(this).create(ApiService.class);
        Call<FavoriteCityResponse> call = apiService.setDefaultCity(cityId);

        call.enqueue(new Callback<FavoriteCityResponse>() {
            @Override
            public void onResponse(Call<FavoriteCityResponse> call, Response<FavoriteCityResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Cidade padrão definida!", Toast.LENGTH_SHORT).show();
                    // Atualize sua lista de favoritos
                    checkIfCityIsFavorite(tvCity.getText().toString().split(",")[0].trim());
                }
            }

            @Override
            public void onFailure(Call<FavoriteCityResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Erro ao definir cidade padrão", Toast.LENGTH_SHORT).show();
            }
        });
    }

}