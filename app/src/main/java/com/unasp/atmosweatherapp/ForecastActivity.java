package com.unasp.atmosweatherapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import android.widget.EditText;
import com.unasp.atmosweatherapp.adapters.ForecastPagerAdapter;
import com.unasp.atmosweatherapp.model.ForecastResponse;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ForecastActivity extends AppCompatActivity {

    private EditText etSearchForecastCity;
    private Button btnSearchForecast;
    private ViewPager2 viewPagerForecast;
    private TabLayout tabLayout;

    private String cidadeAtual = "Capão Redondo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast);

        etSearchForecastCity = findViewById(R.id.etSearchForecastCity);
        btnSearchForecast = findViewById(R.id.btnSearchForecast);
        viewPagerForecast = findViewById(R.id.viewPagerForecast);
        tabLayout = findViewById(R.id.tabLayout);

        String cidadeIntent = getIntent().getStringExtra("city");
        if (cidadeIntent != null && !cidadeIntent.isEmpty()) {
            cidadeAtual = cidadeIntent;
        }
        etSearchForecastCity.setText(cidadeAtual);
        atualizarPrevisao(cidadeAtual);

        BottomNavigationView bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setSelectedItemId(R.id.nav_forecast);

        bottomNavigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            Intent intent = null;

            if (id == R.id.nav_weather) {
                intent = new Intent(this, MainActivity.class);
            } else if (id == R.id.nav_forecast) {
                return true; // já estamos nesta tela
            } else if (id == R.id.nav_compare) {
                intent = new Intent(this, CompareActivity.class);
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


        btnSearchForecast.setOnClickListener(v -> {
            String city = etSearchForecastCity.getText().toString().trim();
            if (!city.isEmpty()) {
                cidadeAtual = city;
                atualizarPrevisao(city);
            } else {
                Toast.makeText(this, "Digite uma cidade", Toast.LENGTH_SHORT).show();
            }
        });

        if (cidadeIntent != null && !cidadeIntent.isEmpty()) {
            cidadeAtual = cidadeIntent;
        }
        etSearchForecastCity.setText(cidadeAtual);
        atualizarPrevisao(cidadeAtual);
    }

    private void atualizarPrevisao(String cidade) {
        List<ForecastResponse> todas = buscarPrevisoesMockadas();
        List<ForecastResponse> filtradas = filtrarPorCidade(todas, cidade);
        Map<String, List<ForecastResponse>> agrupadas = agruparPorData(filtradas);
        configurarViewPager(agrupadas);
    }

    private void configurarViewPager(Map<String, List<ForecastResponse>> previsoesPorDia) {
        List<String> dias = new ArrayList<>(previsoesPorDia.keySet());

        if (dias.size() > 6) {
            dias = dias.subList(0, 6);
        }

        ForecastPagerAdapter adapter = new ForecastPagerAdapter(this, dias, previsoesPorDia);
        viewPagerForecast.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPagerForecast,
                (tab, position) -> {
                    // Não precisa de texto, os dots funcionam sozinhos
                }).attach();
    }

    private List<ForecastResponse> filtrarPorCidade(List<ForecastResponse> lista, String cidade) {
        List<ForecastResponse> filtrada = new ArrayList<>();
        for (ForecastResponse f : lista) {
            if (normalizar(f.getCity()).equals(normalizar(cidade))) {
                filtrada.add(f);
            }
        }
        return filtrada;
    }

    private String normalizar(String texto) {
        return Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("[\\p{InCombiningDiacriticalMarks}]", "")
                .toLowerCase(Locale.ROOT);
    }

    private Map<String, List<ForecastResponse>> agruparPorData(List<ForecastResponse> lista) {
        Map<String, List<ForecastResponse>> agrupado = new LinkedHashMap<>();
        for (ForecastResponse f : lista) {
            String data = f.getForecastDate().substring(0, 10);
            agrupado.computeIfAbsent(data, k -> new ArrayList<>()).add(f);
        }
        return agrupado;
    }

    private List<ForecastResponse> buscarPrevisoesMockadas() {
        List<ForecastResponse> lista = new ArrayList<>();

        lista.addAll(criarPrevisoes("São Paulo", new String[][]{
                {"22.92", "76", "chuva leve", "2025-04-13T21:00:00"},
                {"22.44", "79", "chuva moderada", "2025-04-14T00:00:00"},
                {"21.33", "87", "chuva leve", "2025-04-14T03:00:00"},
                {"19.98", "94", "algumas nuvens", "2025-04-14T06:00:00"},
                {"21.67", "86", "chuva leve", "2025-04-14T09:00:00"},
                {"22.42", "70", "chuva leve", "2025-04-14T12:00:00"},
                {"22.00", "70", "chuva leve", "2025-04-14T15:00:00"},
                {"19.80", "78", "nublado", "2025-04-14T18:00:00"},
                {"18.98", "85", "chuva leve", "2025-04-14T21:00:00"}
        }));

        lista.addAll(criarPrevisoes("Capão Redondo", new String[][]{
                {"20.5", "80", "nublado", "2025-04-13T09:00:00"},
                {"22.3", "70", "chuva leve", "2025-04-13T12:00:00"},
                {"23.8", "65", "chuva moderada", "2025-04-13T15:00:00"}
        }));

        lista.addAll(criarPrevisoes("Paris", new String[][]{
                {"12.4", "75", "nuvens dispersas", "2025-04-13T09:00:00"},
                {"14.1", "68", "ensolarado", "2025-04-13T12:00:00"},
                {"15.7", "60", "ensolarado", "2025-04-13T15:00:00"}
        }));

        return lista;
    }

    private List<ForecastResponse> criarPrevisoes(String cidade, String[][] dados) {
        List<ForecastResponse> lista = new ArrayList<>();
        for (String[] registro : dados) {
            ForecastResponse f = new ForecastResponse();
            f.setCity(cidade);
            f.setTemperature(Double.parseDouble(registro[0]));
            f.setHumidity(Integer.parseInt(registro[1]));
            f.setDescription(registro[2]);
            f.setForecastDate(registro[3]);
            lista.add(f);
        }
        return lista;
    }
}
