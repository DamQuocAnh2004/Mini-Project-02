package com.example.shoppingapp.ui.productdetail;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.shoppingapp.data.database.AppDatabase;
import com.example.shoppingapp.data.entity.Category;
import com.example.shoppingapp.data.entity.Order;
import com.example.shoppingapp.data.entity.OrderDetail;
import com.example.shoppingapp.data.entity.Product;
import com.example.shoppingapp.databinding.ActivityProductDetailBinding;
import com.example.shoppingapp.ui.login.LoginActivity;
import com.example.shoppingapp.ui.order.OrderActivity;
import com.example.shoppingapp.utils.FormatUtils;
import com.example.shoppingapp.utils.SessionManager;

public class ProductDetailActivity extends AppCompatActivity {
    private ActivityProductDetailBinding binding;
    private SessionManager sessionManager;
    private AppDatabase database;
    private Product currentProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProductDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sessionManager = new SessionManager(this);
        database = AppDatabase.getDatabase(this);

        int productId = getIntent().getIntExtra("productId", -1);
        if (productId == -1) { finish(); return; }

        loadProduct(productId);

        binding.btnAddToOrder.setOnClickListener(v -> addToOrder());
    }

    private void loadProduct(int productId) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            currentProduct = database.productDao().findById(productId);
            if (currentProduct == null) { runOnUiThread(this::finish); return; }

            String categoryName = "";
            if (currentProduct.categoryId != null) {
                Category cat = database.categoryDao().findById(currentProduct.categoryId);
                if (cat != null) categoryName = cat.name;
            }
            final String catName = categoryName;

            runOnUiThread(() -> {
                setTitle(currentProduct.name);
                binding.tvProductName.setText(currentProduct.name);
                binding.tvProductPrice.setText(FormatUtils.formatCurrency(currentProduct.price));
                binding.tvProductStock.setText("Tồn kho: " + currentProduct.stock);
                binding.tvProductCategory.setText("Danh mục: " + catName);
                binding.tvProductDescription.setText(currentProduct.description);
            });
        });
    }

    private void addToOrder() {
        if (!sessionManager.isLoggedIn()) {
            Toast.makeText(this, "Vui lòng đăng nhập để đặt hàng", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            return;
        }

        if (currentProduct == null) return;

        AppDatabase.databaseWriteExecutor.execute(() -> {
            int userId = sessionManager.getUserId();

            // Tạo Order nếu chưa có order pending
            Order pendingOrder = database.orderDao().getPendingOrderByUser(userId);
            if (pendingOrder == null) {
                Order newOrder = new Order(userId, FormatUtils.getCurrentDateTime(), "pending");
                long orderId = database.orderDao().insert(newOrder);
                pendingOrder = database.orderDao().findById((int) orderId);
            }

            final int orderId = pendingOrder.id;

            // Kiểm tra sản phẩm đã có trong order chưa
            OrderDetail existing = database.orderDetailDao().findByOrderAndProduct(orderId, currentProduct.id);
            if (existing != null) {
                database.orderDetailDao().updateQuantity(orderId, currentProduct.id, existing.quantity + 1);
            } else {
                OrderDetail detail = new OrderDetail(orderId, currentProduct.id, 1, currentProduct.price);
                database.orderDetailDao().insert(detail);
            }

            // Cập nhật total
            double total = database.orderDetailDao().getTotalByOrder(orderId);
            database.orderDao().updateTotal(orderId, total);

            runOnUiThread(() -> {
                Toast.makeText(this, "Đã thêm vào giỏ hàng!", Toast.LENGTH_SHORT).show();
                binding.btnViewOrder.setVisibility(android.view.View.VISIBLE);
                binding.btnViewOrder.setOnClickListener(v -> {
                    startActivity(new Intent(this, OrderActivity.class));
                });
            });
        });
    }

    @Override
    public boolean onSupportNavigateUp() { finish(); return true; }
}
