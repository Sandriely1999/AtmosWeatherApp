package com.unasp.atmosweatherapp.model;


import com.google.gson.annotations.SerializedName;


public class WeatherComparisonResponse {
    @SerializedName("cityA")
    private String cityA;

    @SerializedName("cityB")
    private String cityB;

    @SerializedName("temperatureA")
    private double temperatureA;

    @SerializedName("temperatureB")
    private double temperatureB;

    @SerializedName("temperatureDifference")
    private double temperatureDifference;

    @SerializedName("humidityA")
    private int humidityA;

    @SerializedName("humidityB")
    private int humidityB;

    @SerializedName("humidityDifference")
    private int humidityDifference;

    @SerializedName("descriptionA")
    private String descriptionA;

    @SerializedName("descriptionB")
    private String descriptionB;

    @SerializedName("comparisonDate")
    private String comparisonDate;

    public String getCityA() {
        return cityA;
    }

    public void setCityA(String cityA) {
        this.cityA = cityA;
    }

    public String getCityB() {
        return cityB;
    }

    public void setCityB(String cityB) {
        this.cityB = cityB;
    }

    public double getTemperatureA() {
        return temperatureA;
    }

    public void setTemperatureA(double temperatureA) {
        this.temperatureA = temperatureA;
    }

    public double getTemperatureB() {
        return temperatureB;
    }

    public void setTemperatureB(double temperatureB) {
        this.temperatureB = temperatureB;
    }

    public double getTemperatureDifference() {
        return temperatureDifference;
    }

    public void setTemperatureDifference(double temperatureDifference) {
        this.temperatureDifference = temperatureDifference;
    }

    public int getHumidityA() {
        return humidityA;
    }

    public void setHumidityA(int humidityA) {
        this.humidityA = humidityA;
    }

    public int getHumidityB() {
        return humidityB;
    }

    public void setHumidityB(int humidityB) {
        this.humidityB = humidityB;
    }

    public int getHumidityDifference() {
        return humidityDifference;
    }

    public void setHumidityDifference(int humidityDifference) {
        this.humidityDifference = humidityDifference;
    }

    public String getDescriptionA() {
        return descriptionA;
    }

    public void setDescriptionA(String descriptionA) {
        this.descriptionA = descriptionA;
    }

    public String getDescriptionB() {
        return descriptionB;
    }

    public void setDescriptionB(String descriptionB) {
        this.descriptionB = descriptionB;
    }

    public String getComparisonDate() {
        return comparisonDate;
    }

    public void setComparisonDate(String comparisonDate) {
        this.comparisonDate = comparisonDate;
    }
}