package com.unasp.atmosweatherapp;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.unasp.atmosweatherapp.model.ForecastResponse;

import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ForecastActivity extends AppCompatActivity {

    private LinearLayout containerForecast;
    private TextInputEditText etSearchForecastCity;
    private Button btnSearchForecast;

    private String cidadeAtual = "Capão Redondo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast);

        containerForecast = findViewById(R.id.containerForecast);
        etSearchForecastCity = findViewById(R.id.etSearchForecastCity);
        btnSearchForecast = findViewById(R.id.btnSearchForecast);

        btnSearchForecast.setOnClickListener(v -> {
            String city = etSearchForecastCity.getText().toString().trim();
            if (!city.isEmpty()) {
                cidadeAtual = city;
                atualizarPrevisao(city);
            } else {
                Toast.makeText(this, "Digite uma cidade", Toast.LENGTH_SHORT).show();
            }
        });

        String cidadeIntent = getIntent().getStringExtra("city");
        if (cidadeIntent != null && !cidadeIntent.isEmpty()) {
            cidadeAtual = cidadeIntent;
        }
        etSearchForecastCity.setText(cidadeAtual);
        atualizarPrevisao(cidadeAtual);
    }

    private void atualizarPrevisao(String cidade) {
        containerForecast.removeAllViews();

        List<ForecastResponse> todas = buscarPrevisoesMockadas();
        List<ForecastResponse> filtradas = filtrarPorCidade(todas, cidade);
        Map<String, List<ForecastResponse>> agrupadas = agruparPorData(filtradas);

        mostrarPrevisoes(agrupadas);
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
                .toLowerCase();
    }

    private Map<String, List<ForecastResponse>> agruparPorData(List<ForecastResponse> lista) {
        Map<String, List<ForecastResponse>> agrupado = new LinkedHashMap<>();
        for (ForecastResponse f : lista) {
            String data = f.getForecastDate().substring(0, 10); // yyyy-MM-dd
            if (!agrupado.containsKey(data)) {
                agrupado.put(data, new ArrayList<>());
            }
            agrupado.get(data).add(f);
        }
        return agrupado;
    }

    private void mostrarPrevisoes(Map<String, List<ForecastResponse>> previsoesPorDia) {
        int diasAdicionados = 0;

        for (Map.Entry<String, List<ForecastResponse>> entry : previsoesPorDia.entrySet()) {
            if (diasAdicionados >= 6) break;

            String data = entry.getKey();
            List<ForecastResponse> previsoes = entry.getValue();

            LinearLayout blocoDia = new LinearLayout(this);
            blocoDia.setOrientation(LinearLayout.VERTICAL);
            blocoDia.setPadding(16, 16, 16, 16);
            blocoDia.setBackgroundResource(R.drawable.button_border);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 0, 0, 32);
            blocoDia.setLayoutParams(params);

            TextView titulo = new TextView(this);
            titulo.setText(formatarData(data));
            titulo.setTextSize(18f);
            titulo.setPadding(0, 0, 0, 12);
            blocoDia.addView(titulo);

            for (ForecastResponse previsao : previsoes) {
                TextView bloco = new TextView(this);
                String hora = previsao.getForecastDate().substring(11, 16);
                String texto = hora + " - " + previsao.getTemperature() + "°C - " + previsao.getDescription();
                bloco.setText(texto);
                bloco.setTextSize(16f);
                bloco.setPadding(8, 4, 8, 4);
                blocoDia.addView(bloco);
            }

            containerForecast.addView(blocoDia);
            diasAdicionados++;
        }
    }

    private String formatarData(String dataIso) {
        try {
            SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date data = isoFormat.parse(dataIso);
            SimpleDateFormat brasileiro = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            return brasileiro.format(data);
        } catch (Exception e) {
            return dataIso;
        }
    }

    // Simula carregamento vindo de API

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
                {"18.98", "85", "chuva leve", "2025-04-14T21:00:00"},
                {"18.55", "86", "chuva leve", "2025-04-15T00:00:00"},
                {"18.20", "88", "chuva leve", "2025-04-15T03:00:00"},
                {"18.24", "82", "chuva leve", "2025-04-15T06:00:00"},
                {"18.85", "76", "nublado", "2025-04-15T09:00:00"},
                {"23.48", "58", "nuvens dispersas", "2025-04-15T12:00:00"},
                {"24.07", "61", "nuvens dispersas", "2025-04-15T15:00:00"},
                {"20.62", "76", "nublado", "2025-04-15T18:00:00"},
                {"19.15", "80", "nublado", "2025-04-15T21:00:00"},
                {"18.56", "85", "chuva leve", "2025-04-16T00:00:00"}
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
}