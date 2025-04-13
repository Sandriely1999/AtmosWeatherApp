package com.unasp.atmosweatherapp.model;



public class WeatherResponse {
    private String city;
    private double temperature;
    private int humidity;
    private String description;
    private String forecastDate;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public int getHumidity() {
        return humidity;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getForecastDate() {
        return forecastDate;
    }


}