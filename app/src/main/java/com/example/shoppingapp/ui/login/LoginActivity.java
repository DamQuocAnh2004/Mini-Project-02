package com.example.shoppingapp.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.shoppingapp.data.database.AppDatabase;
import com.example.shoppingapp.data.entity.User;
import com.example.shoppingapp.databinding.ActivityLoginBinding;
import com.example.shoppingapp.ui.home.HomeActivity;
import com.example.shoppingapp.utils.SessionManager;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private SessionManager sessionManager;
    private AppDatabase database;

    // Optional: product to add after login
    private int pendingProductId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sessionManager = new SessionManager(this);
        database = AppDatabase.getDatabase(this);

        if (getIntent().hasExtra("pendingProductId")) {
            pendingProductId = getIntent().getIntExtra("pendingProductId", -1);
        }

        binding.btnLogin.setOnClickListener(v -> attemptLogin());
        binding.tvGoBack.setOnClickListener(v -> finish());
    }

    private void attemptLogin() {
        String username = binding.etUsername.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }

        binding.progressBar.setVisibility(View.VISIBLE);
        binding.btnLogin.setEnabled(false);

        AppDatabase.databaseWriteExecutor.execute(() -> {
            User user = database.userDao().login(username, password);
            runOnUiThread(() -> {
                binding.progressBar.setVisibility(View.GONE);
                binding.btnLogin.setEnabled(true);
                if (user != null) {
                    sessionManager.login(user.id, user.username, user.fullName);
                    Toast.makeText(this, "Chào mừng, " + user.fullName, Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(this, HomeActivity.class);
                    if (pendingProductId != -1) {
                        intent.putExtra("pendingProductId", pendingProductId);
                    }
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(this, "Sai tên đăng nhập hoặc mật khẩu", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
