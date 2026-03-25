package com.example.shoppingapp.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;

import com.example.shoppingapp.R;
import com.example.shoppingapp.databinding.ActivityHomeBinding;
import com.example.shoppingapp.ui.categories.CategoriesActivity;
import com.example.shoppingapp.ui.login.LoginActivity;
import com.example.shoppingapp.ui.order.OrderActivity;
import com.example.shoppingapp.ui.products.ProductsActivity;
import com.example.shoppingapp.utils.SessionManager;

public class HomeActivity extends AppCompatActivity {
    private ActivityHomeBinding binding;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        sessionManager = new SessionManager(this);

        updateUI();

        binding.btnProducts.setOnClickListener(v ->
            startActivity(new Intent(this, ProductsActivity.class)));

        binding.btnCategories.setOnClickListener(v ->
            startActivity(new Intent(this, CategoriesActivity.class)));

        binding.btnMyOrder.setOnClickListener(v -> {
            if (sessionManager.isLoggedIn()) {
                startActivity(new Intent(this, OrderActivity.class));
            } else {
                startActivity(new Intent(this, LoginActivity.class));
            }
        });

        binding.btnLoginLogout.setOnClickListener(v -> {
            if (sessionManager.isLoggedIn()) {
                sessionManager.logout();
                updateUI();
            } else {
                startActivity(new Intent(this, LoginActivity.class));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }

    private void updateUI() {
        if (sessionManager.isLoggedIn()) {
            binding.tvWelcome.setText("Xin chào, " + sessionManager.getFullName());
            binding.btnLoginLogout.setText("Đăng xuất");
            binding.btnMyOrder.setEnabled(true);
        } else {
            binding.tvWelcome.setText("Chào mừng đến với Shopping App!");
            binding.btnLoginLogout.setText("Đăng nhập");
            binding.btnMyOrder.setEnabled(false);
        }
    }
}
