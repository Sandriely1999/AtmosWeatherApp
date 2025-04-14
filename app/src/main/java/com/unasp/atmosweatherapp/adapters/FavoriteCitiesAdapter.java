package com.unasp.atmosweatherapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.unasp.atmosweatherapp.R;
import com.unasp.atmosweatherapp.model.FavoriteCityResponse;
import com.unasp.atmosweatherapp.model.WeatherResponse;
import com.unasp.atmosweatherapp.service.ApiService;
import com.unasp.atmosweatherapp.service.RetrofitClient;

import java.util.List;
import java.util.function.Consumer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavoriteCitiesAdapter extends RecyclerView.Adapter<FavoriteCitiesAdapter.ViewHolder> {

    private List<FavoriteCityResponse> cities;
    private final Consumer<FavoriteCityResponse> onItemClickListener;
    private final Consumer<FavoriteCityResponse> onSetDefaultListener;
    private final Consumer<FavoriteCityResponse> onDeleteListener;

    public FavoriteCitiesAdapter(
            List<FavoriteCityResponse> cities,
            Consumer<FavoriteCityResponse> onItemClickListener,
            Consumer<FavoriteCityResponse> onSetDefaultListener,
            Consumer<FavoriteCityResponse> onDeleteListener) {
        this.cities = cities;
        this.onItemClickListener = onItemClickListener;
        this.onSetDefaultListener = onSetDefaultListener;
        this.onDeleteListener = onDeleteListener;
    }

    public void setCities(List<FavoriteCityResponse> cities) {
        this.cities = cities;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_favorite_city, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (cities == null || position >= cities.size()) return;

        FavoriteCityResponse city = cities.get(position);
        holder.tvCityName.setText(city.getCityName());

        // Busca os dados do tempo para cada cidade
        fetchWeatherForCity(city, holder);

        holder.ivStar.setVisibility(city.isDefault() ? View.VISIBLE : View.GONE);

        // Configurar ouvintes de clique
        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.accept(city);
            }
        });

        holder.btnSetDefault.setOnClickListener(v -> {
            if (onSetDefaultListener != null) {
                onSetDefaultListener.accept(city);
            }
        });

        holder.btnRemove.setOnClickListener(v -> {
            if (onDeleteListener != null) {
                onDeleteListener.accept(city);
            }
        });
    }

    @Override
    public int getItemCount() {
        return cities == null ? 0 : cities.size();
    }

    private void fetchWeatherForCity(FavoriteCityResponse city, ViewHolder holder) {
        ApiService apiService = RetrofitClient.getClient(holder.itemView.getContext()).create(ApiService.class);
        Call<WeatherResponse> call = apiService.getCurrentWeather(city.getCityName());

        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    WeatherResponse weather = response.body();
                    holder.tvTemperature.setText(String.format("Temperatura: %.1f°C", weather.getTemperature()));
                    holder.tvCondition.setText(weather.getDescription());

                    // Definir o ícone do tempo
                    String description = weather.getDescription().toLowerCase();
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
                    holder.ivWeatherIcon.setImageResource(iconResId);
                } else {
                    holder.tvTemperature.setText("Temperatura: --");
                    holder.tvCondition.setText("Informações não disponíveis");
                    holder.ivWeatherIcon.setImageResource(R.drawable.ic_sunny);
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                holder.tvTemperature.setText("Temperatura: --");
                holder.tvCondition.setText("Erro ao carregar");
                holder.ivWeatherIcon.setImageResource(R.drawable.ic_sunny);
            }
        });
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView tvCityName;
        final TextView tvTemperature;
        final TextView tvCondition;
        final ImageView ivWeatherIcon;
        final ImageView ivStar;
        final Button btnSetDefault;
        final Button btnRemove;

        ViewHolder(View itemView) {
            super(itemView);
            tvCityName = itemView.findViewById(R.id.tvCityName);
            tvTemperature = itemView.findViewById(R.id.tvTemperature);
            tvCondition = itemView.findViewById(R.id.tvCondition);
            ivWeatherIcon = itemView.findViewById(R.id.ivWeatherIcon);
            ivStar = itemView.findViewById(R.id.ivStar);
            btnSetDefault = itemView.findViewById(R.id.btnSetDefault);
            btnRemove = itemView.findViewById(R.id.btnRemove);
        }
    }
}