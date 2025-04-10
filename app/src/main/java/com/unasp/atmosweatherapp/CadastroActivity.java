package com.unasp.atmosweatherapp;


import android.content.Intent;
import android.os.Bundle;
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
import com.unasp.atmosweatherapp.utils.MainActivity;
import com.unasp.atmosweatherapp.utils.SessionManager;

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
        if (username.isEmpty()) {
            editTextUsername.setError("Preencha o username");
            return false;
        }
        if (senha.isEmpty()) {
            editTextSenha.setError("Preencha a senha");
            return false;
        }
        return true;
    }

    private void cadastrarUsuario(String username, String senha) {
        // Mostra ProgressBar e desabilita botão
        progressBar.setVisibility(View.VISIBLE);
        btnCadastrar.setEnabled(false);

        ApiService apiService = RetrofitClient.getClient(this).create(ApiService.class);
        CadastroRequest request = new CadastroRequest(username, senha);

        apiService.cadastrarUsuario(request).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                progressBar.setVisibility(View.GONE);
                btnCadastrar.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    // Login automático (Melhoria #5)
                    String token = response.body().getToken();
                    String username = response.body().getUsername();

                    SessionManager session = new SessionManager(CadastroActivity.this);
                    session.saveAuthToken(token, username);

                    // Redireciona para MainActivity
                    startActivity(new Intent(CadastroActivity.this, MainActivity.class));
                    finish();

                } else {
                    try {
                        String errorBody = response.errorBody().string();
                        Toast.makeText(CadastroActivity.this, "Erro: " + errorBody, Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(CadastroActivity.this, "Erro ao cadastrar", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                btnCadastrar.setEnabled(true);
                Toast.makeText(CadastroActivity.this, "Falha na conexão: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}