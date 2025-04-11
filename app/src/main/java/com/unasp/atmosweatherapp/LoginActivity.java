package com.unasp.atmosweatherapp;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.unasp.atmosweatherapp.model.LoginRequest;
import com.unasp.atmosweatherapp.model.LoginResponse;
import com.unasp.atmosweatherapp.service.ApiService;
import com.unasp.atmosweatherapp.service.RetrofitClient;
import com.unasp.atmosweatherapp.utils.SessionManager;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private EditText editTextUsername, editTextSenha;
    private Button btnLogin, btnCriarConta;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Inicializa views
        editTextUsername = findViewById(R.id.editTextUsername);
        editTextSenha = findViewById(R.id.editTextSenha);
        btnLogin = findViewById(R.id.btnLogin);
        btnCriarConta = findViewById(R.id.btnCriarConta);
        progressBar = findViewById(R.id.progressBar);

        btnLogin.setOnClickListener(v -> {
            String username = editTextUsername.getText().toString().trim();
            String senha = editTextSenha.getText().toString().trim();

            if (validarCamposLogin(username, senha)) {
                fazerLogin(username, senha);
            }
        });

        btnCriarConta.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, CadastroActivity.class));
        });
    }

    private boolean validarCamposLogin(String username, String password) {
        boolean isValid = true;

        if (username.isEmpty()) {
            editTextUsername.setError("Username é obrigatório");
            isValid = false;
        }

        if (password.isEmpty()) {
            editTextSenha.setError("Senha é obrigatória");
            isValid = false;
        } else if (password.length() < 6) {
            editTextSenha.setError("Senha deve ter no mínimo 6 caracteres");
            isValid = false;
        }

        return isValid;
    }

    private void fazerLogin(String username, String password) {
        progressBar.setVisibility(View.VISIBLE);
        btnLogin.setEnabled(false);

        ApiService apiService = RetrofitClient.getClient(this).create(ApiService.class);
        LoginRequest request = new LoginRequest(username, password);

        apiService.fazerLogin(request).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                progressBar.setVisibility(View.GONE);
                btnLogin.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    // Login bem-sucedido
                    LoginResponse loginResponse = response.body();
                    new SessionManager(LoginActivity.this)
                            .saveAuthToken(loginResponse.getToken(), loginResponse.getUsername());

                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                } else {
                    // Tratamento de erro
                    try {
                        String errorBody = response.errorBody().string();
                        Toast.makeText(LoginActivity.this,
                                "Erro: " + errorBody, Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        Toast.makeText(LoginActivity.this,
                                "Erro no login", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                btnLogin.setEnabled(true);
                Toast.makeText(LoginActivity.this,
                        "Falha na conexão: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}