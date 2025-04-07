package com.unasp.atmosweatherapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "AtmosWeatherPref";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_USERNAME = "username";

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context context;

    public SessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void saveAuthToken(String token, String username) {
        editor.putString(KEY_TOKEN, token);
        editor.putString(KEY_USERNAME, username);
        editor.commit();
    }

    public String getAuthToken() {
        return pref.getString(KEY_TOKEN, null);
    }

    public String getUsername() {
        return pref.getString(KEY_USERNAME, null);
    }

    public void clear() {
        editor.clear();
        editor.commit();
    }
}