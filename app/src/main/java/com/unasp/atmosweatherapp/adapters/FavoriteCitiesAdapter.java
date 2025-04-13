package com.unasp.atmosweatherapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.unasp.atmosweatherapp.R;
import com.unasp.atmosweatherapp.model.FavoriteCityResponse;

import java.util.List;
import java.util.function.Consumer;

public class FavoriteCitiesAdapter extends RecyclerView.Adapter<FavoriteCitiesAdapter.ViewHolder> {

    private List<FavoriteCityResponse> cities;
    private final Consumer<FavoriteCityResponse> onClickListener;
    private final Consumer<FavoriteCityResponse> onSetDefaultListener;
    private final Consumer<FavoriteCityResponse> onDeleteListener;

    public FavoriteCitiesAdapter(List<FavoriteCityResponse> cities,
                                 Consumer<FavoriteCityResponse> onClickListener,
                                 Consumer<FavoriteCityResponse> onSetDefaultListener,
                                 Consumer<FavoriteCityResponse> onDeleteListener) {
        this.cities = cities;
        this.onClickListener = onClickListener;
        this.onSetDefaultListener = onSetDefaultListener;
        this.onDeleteListener = onDeleteListener;
    }

    public void updateList(List<FavoriteCityResponse> newCities) {
        this.cities = newCities;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_favorite_city, parent, false);
        return new ViewHolder(view, onSetDefaultListener, onDeleteListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FavoriteCityResponse city = cities.get(position);
        holder.bind(city);

        holder.itemView.setOnClickListener(v -> {
            if (onClickListener != null) {
                onClickListener.accept(city);
            }
        });
    }

    @Override
    public int getItemCount() {
        return cities != null ? cities.size() : 0;
    }

    public void setCities(List<FavoriteCityResponse> cities) {
        this.cities = cities;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvCityName;
        private final ImageButton btnSetDefault;
        private final ImageButton btnDelete;
        private final ImageView ivDefaultIndicator;
        private FavoriteCityResponse currentCity;

        public ViewHolder(@NonNull View itemView,
                          Consumer<FavoriteCityResponse> onSetDefaultListener,
                          Consumer<FavoriteCityResponse> onDeleteListener) {
            super(itemView);
            tvCityName = itemView.findViewById(R.id.tvCityName);
            btnSetDefault = itemView.findViewById(R.id.btnSetDefault);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            ivDefaultIndicator = itemView.findViewById(R.id.ivDefaultIndicator);

            btnSetDefault.setOnClickListener(v -> {
                if (currentCity != null) {
                    onSetDefaultListener.accept(currentCity);
                }
            });

            btnDelete.setOnClickListener(v -> {
                if (currentCity != null) {
                    onDeleteListener.accept(currentCity);
                }
            });
        }

        public void bind(FavoriteCityResponse city) {
            currentCity = city;
            tvCityName.setText(city.getCityName());

            if (city.isDefault()) {
                ivDefaultIndicator.setVisibility(View.VISIBLE);
                btnSetDefault.setVisibility(View.GONE);
            } else {
                ivDefaultIndicator.setVisibility(View.GONE);
                btnSetDefault.setVisibility(View.VISIBLE);
            }
        }
    }
}