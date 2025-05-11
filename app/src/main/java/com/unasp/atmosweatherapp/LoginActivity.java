package com.unasp.atmosweatherapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

        try {
            ApiService apiService = RetrofitClient.getClient(this).create(ApiService.class);
            LoginRequest request = new LoginRequest(username, password);

            apiService.fazerLogin(request).enqueue(new Callback<LoginResponse>() {
                @Override
                public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                    progressBar.setVisibility(View.GONE);
                    btnLogin.setEnabled(true);

                    try {
                        if (response.isSuccessful() && response.body() != null) {
                            // Login bem-sucedido
                            LoginResponse loginResponse = response.body();
                            new SessionManager(LoginActivity.this)
                                    .saveAuthToken(loginResponse.getToken(), loginResponse.getUsername());

                            // Mensagem de sucesso adicionada aqui
                            Toast.makeText(LoginActivity.this,
                                    "Login realizado com sucesso! Bem-vindo, " + username + "!",
                                    Toast.LENGTH_LONG).show();

                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish();
                        } else {
                            String errorMessage = "Credenciais inválidas. Verifique seu usuário e senha.";

                            // Tentativa de obter mais detalhes do erro
                            try {
                                String errorBody = response.errorBody() != null ?
                                        response.errorBody().string().toLowerCase() : "";

                                if (errorBody.contains("user not found") || errorBody.contains("usuário não encontrado")) {
                                    errorMessage = "Usuário não encontrado. Verifique ou crie uma conta.";
                                    editTextUsername.setError(errorMessage);
                                    editTextUsername.requestFocus();
                                } else if (errorBody.contains("invalid password") || errorBody.contains("senha incorreta")) {
                                    errorMessage = "Senha incorreta. Tente novamente.";
                                    editTextSenha.setError(errorMessage);
                                    editTextSenha.requestFocus();
                                }
                            } catch (IOException e) {
                                // Se não conseguir ler o errorBody, mantém a mensagem genérica
                            }

                            // Mostra o Toast e mensagem no campo
                            Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        Toast.makeText(LoginActivity.this,
                                "Erro ao processar resposta: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<LoginResponse> call, Throwable t) {
                    progressBar.setVisibility(View.GONE);
                    btnLogin.setEnabled(true);
                    Toast.makeText(LoginActivity.this,
                            "Falha na conexão. Verifique sua internet.", Toast.LENGTH_LONG).show();
                    Log.e("LoginError", "Erro na requisição: " + t.getMessage());
                }
            });
        } catch (Exception e) {
            progressBar.setVisibility(View.GONE);
            btnLogin.setEnabled(true);
            Toast.makeText(LoginActivity.this,
                    "Erro ao tentar login. Tente novamente.", Toast.LENGTH_LONG).show();
            Log.e("LoginError", "Erro geral: " + e.getMessage());
        }
    }
}