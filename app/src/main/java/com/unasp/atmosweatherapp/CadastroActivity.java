package com.unasp.atmosweatherapp;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.unasp.atmosweatherapp.model.CadastroRequest;
import com.unasp.atmosweatherapp.model.LoginResponse;
import com.unasp.atmosweatherapp.service.ApiService;
import com.unasp.atmosweatherapp.service.RetrofitClient;
import com.unasp.atmosweatherapp.utils.SessionManager;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CadastroActivity extends AppCompatActivity {

    private EditText editTextUsername, editTextSenha;
    private Button btnCadastrar;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        editTextUsername = findViewById(R.id.editTextUsername);
        editTextSenha = findViewById(R.id.editTextSenhaCadastro);
        btnCadastrar = findViewById(R.id.btnCadastrar);
        progressBar = findViewById(R.id.progressBar);

        btnCadastrar.setOnClickListener(v -> {
            String username = editTextUsername.getText().toString().trim();
            String senha = editTextSenha.getText().toString().trim();

            if (validarCampos(username, senha)) {
                cadastrarUsuario(username, senha);
            }
        });
    }

    private boolean validarCampos(String username, String senha) {
        boolean isValid = true;

        if (username.isEmpty()) {
            editTextUsername.setError("Preencha o username");
            isValid = false;
        }

        if (senha.isEmpty()) {
            editTextSenha.setError("Preencha a senha");
            isValid = false;
        } else if (senha.length() < 6) {
            editTextSenha.setError("Senha deve ter no mínimo 6 caracteres");
            isValid = false;
        }

        return isValid;
    }

    private void cadastrarUsuario(String username, String senha) {
        progressBar.setVisibility(View.VISIBLE);
        btnCadastrar.setEnabled(false);

        ApiService apiService = RetrofitClient.getClient(this).create(ApiService.class);
        CadastroRequest request = new CadastroRequest(username, senha);

        apiService.cadastrarUsuario(request).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                progressBar.setVisibility(View.GONE);
                btnCadastrar.setEnabled(true);

                if (response.isSuccessful()) {
                    LoginResponse loginResponse = response.body();
                    new SessionManager(CadastroActivity.this)
                            .saveAuthToken(loginResponse.getToken(), loginResponse.getUsername());

                    Toast.makeText(CadastroActivity.this,
                            "Cadastro realizado com sucesso! Bem-vindo, " + username + "!",
                            Toast.LENGTH_LONG).show();

                    startActivity(new Intent(CadastroActivity.this, MainActivity.class));
                    finish();
                } else {
                    handleRegistrationError(response, username);
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                btnCadastrar.setEnabled(true);
                Toast.makeText(CadastroActivity.this,
                        "Falha na conexão. Verifique sua internet.", Toast.LENGTH_LONG).show();
                Log.e("CADASTRO", "Falha na requisição: " + t.getMessage());
            }
        });
    }

    private void handleRegistrationError(Response<LoginResponse> response, String username) {
        try {
            int statusCode = response.code();
            String errorMessage;

            if (statusCode == 403) {
                errorMessage = "O usuário '" + username + "' já está cadastrado. Por favor, escolha outro nome.";
            } else {
                errorMessage = "Erro no cadastro (Código: " + statusCode + ")";
            }

            editTextUsername.setError(errorMessage);
            editTextUsername.requestFocus();
            Toast.makeText(CadastroActivity.this, errorMessage, Toast.LENGTH_LONG).show();


            String errorBody = response.errorBody() != null ? response.errorBody().string() : "";
            Log.d("CADASTRO_ERRO", "Status: " + statusCode + " | Response: " + errorBody);

        } catch (IOException e) {
            Toast.makeText(CadastroActivity.this,
                    "Erro ao processar resposta do servidor", Toast.LENGTH_LONG).show();
            Log.e("CADASTRO", "Erro ao ler response", e);
        }
    }
}