package com.unasp.atmosweatherapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.unasp.atmosweatherapp.R;
import com.unasp.atmosweatherapp.model.ForecastResponse;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ForecastPagerAdapter extends RecyclerView.Adapter<ForecastPagerAdapter.ForecastViewHolder> {

    private final List<String> dias;
    private final Map<String, List<ForecastResponse>> previsoesPorDia;
    private final Context context;

    public ForecastPagerAdapter(Context context, List<String> dias, Map<String, List<ForecastResponse>> previsoesPorDia) {
        this.context = context;
        this.dias = dias;
        this.previsoesPorDia = previsoesPorDia;
    }

    @NonNull
    @Override
    public ForecastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_forecast_day, parent, false);
        return new ForecastViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ForecastViewHolder holder, int position) {
        String dia = dias.get(position);
        List<ForecastResponse> lista = previsoesPorDia.get(dia);

        holder.tvForecastDate.setText(formatarData(dia));
        holder.forecastDetailsContainer.removeAllViews();

        if (lista != null) {
            for (ForecastResponse previsao : lista) {
                View item = LayoutInflater.from(context).inflate(R.layout.item_forecast_hour, holder.forecastDetailsContainer, false);

                ImageView icon = item.findViewById(R.id.ivForecastIcon);
                TextView hora = item.findViewById(R.id.tvForecastHour);
                TextView temp = item.findViewById(R.id.tvForecastTemp);
                TextView desc = item.findViewById(R.id.tvForecastDesc);

                icon.setImageResource(obterIcone(previsao.getDescription()));
                hora.setText(previsao.getForecastDate().substring(11, 16));
                temp.setText(String.format(Locale.getDefault(), "%.1f°C", previsao.getTemperature()));
                desc.setText(previsao.getDescription());

                holder.forecastDetailsContainer.addView(item);
            }
        }
    }

    @Override
    public int getItemCount() {
        return dias.size();
    }

    static class ForecastViewHolder extends RecyclerView.ViewHolder {
        TextView tvForecastDate;
        LinearLayout forecastDetailsContainer;

        public ForecastViewHolder(@NonNull View itemView) {
            super(itemView);
            tvForecastDate = itemView.findViewById(R.id.tvForecastDate);
            forecastDetailsContainer = itemView.findViewById(R.id.forecastDetailsContainer);
        }
    }

    private String formatarData(String dataIso) {
        try {
            SimpleDateFormat iso = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date data = iso.parse(dataIso);
            SimpleDateFormat br = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            return br.format(data);
        } catch (Exception e) {
            return dataIso;
        }
    }

    private int obterIcone(String descricao) {
        descricao = descricao.toLowerCase();

        if (descricao.contains("chuva")) return R.drawable.ic_rain;
        if (descricao.contains("nublado")) return R.drawable.ic_cloudy;
        if (descricao.contains("nuvem")) return R.drawable.ic_partly_cloudy_day;
        if (descricao.contains("sol") || descricao.contains("ensolarado")) return R.drawable.ic_sunny;
        if (descricao.contains("vento")) return R.drawable.ic_wind;

        return R.drawable.ic_sunny;
    }
}
