package com.example.shoppingapp.ui.products;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.shoppingapp.data.database.AppDatabase;
import com.example.shoppingapp.data.entity.Product;
import com.example.shoppingapp.databinding.ActivityProductsBinding;
import com.example.shoppingapp.ui.productdetail.ProductDetailActivity;

import java.util.ArrayList;
import java.util.List;

public class ProductsActivity extends AppCompatActivity implements ProductAdapter.OnProductClickListener {
    private ActivityProductsBinding binding;
    private ProductAdapter adapter;
    private List<Product> allProducts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProductsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Sản phẩm");

        adapter = new ProductAdapter(this, new ArrayList<>(), this);
        binding.rvProducts.setLayoutManager(new GridLayoutManager(this, 2));
        binding.rvProducts.setAdapter(adapter);

        // Filter by categoryId if passed
        int categoryId = getIntent().getIntExtra("categoryId", -1);

        AppDatabase db = AppDatabase.getDatabase(this);
        if (categoryId != -1) {
            db.productDao().getProductsByCategory(categoryId).observe(this, products -> {
                allProducts = products != null ? products : new ArrayList<>();
                adapter.updateList(allProducts);
            });
            String catName = getIntent().getStringExtra("categoryName");
            if (catName != null) setTitle(catName);
        } else {
            db.productDao().getAllProducts().observe(this, products -> {
                allProducts = products != null ? products : new ArrayList<>();
                adapter.updateList(allProducts);
            });
        }

        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterProducts(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void filterProducts(String query) {
        if (query.isEmpty()) {
            adapter.updateList(allProducts);
            return;
        }
        List<Product> filtered = new ArrayList<>();
        for (Product p : allProducts) {
            if (p.name.toLowerCase().contains(query.toLowerCase())) {
                filtered.add(p);
            }
        }
        adapter.updateList(filtered);
    }

    @Override
    public void onProductClick(Product product) {
        Intent intent = new Intent(this, ProductDetailActivity.class);
        intent.putExtra("productId", product.id);
        startActivity(intent);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
