package com.unasp.atmosweatherapp.model;

public class ForecastResponse {
    private String city;
    private double temperature;
    private int humidity;
    private String description;
    private String forecastDate; // formato: "2025-04-14T12:00:00"

    public String getCity() {
        return city;
    }

    public double getTemperature() {
        return temperature;
    }

    public int getHumidity() {
        return humidity;
    }

    public String getDescription() {
        return description;
    }

    public String getForecastDate() {
        return forecastDate;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setForecastDate(String forecastDate) {
        this.forecastDate = forecastDate;
    }
}