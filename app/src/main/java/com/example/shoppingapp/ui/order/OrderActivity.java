package com.example.shoppingapp.ui.order;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.shoppingapp.data.database.AppDatabase;
import com.example.shoppingapp.data.entity.Order;
import com.example.shoppingapp.data.entity.OrderDetail;
import com.example.shoppingapp.data.entity.Product;
import com.example.shoppingapp.databinding.ActivityOrderBinding;
import com.example.shoppingapp.utils.FormatUtils;
import com.example.shoppingapp.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

public class OrderActivity extends AppCompatActivity {
    private ActivityOrderBinding binding;
    private SessionManager sessionManager;
    private AppDatabase database;
    private OrderDetailAdapter adapter;
    private Order currentOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Giỏ hàng");

        sessionManager = new SessionManager(this);
        database = AppDatabase.getDatabase(this);

        adapter = new OrderDetailAdapter(new ArrayList<>());
        binding.rvOrderDetails.setLayoutManager(new LinearLayoutManager(this));
        binding.rvOrderDetails.setAdapter(adapter);

        loadOrder();

        binding.btnCheckout.setOnClickListener(v -> checkout());
        binding.btnContinueShopping.setOnClickListener(v -> finish());
    }

    private void loadOrder() {
        int userId = sessionManager.getUserId();
        AppDatabase.databaseWriteExecutor.execute(() -> {
            currentOrder = database.orderDao().getPendingOrderByUser(userId);
            if (currentOrder == null) {
                runOnUiThread(() -> showEmptyState());
                return;
            }

            final int orderId = currentOrder.id;
            runOnUiThread(() -> {
                database.orderDetailDao().getDetailsByOrder(orderId).observe(this, details -> {
                    if (details == null || details.isEmpty()) {
                        showEmptyState();
                        return;
                    }
                    showOrderState();
                    loadDetailWithProducts(details, orderId);
                });
            });
        });
    }

    private void loadDetailWithProducts(List<OrderDetail> details, int orderId) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            List<OrderDetailAdapter.OrderItem> items = new ArrayList<>();
            for (OrderDetail d : details) {
                Product p = database.productDao().findById(d.productId);
                if (p != null) {
                    items.add(new OrderDetailAdapter.OrderItem(d, p));
                }
            }
            double total = database.orderDetailDao().getTotalByOrder(orderId);
            runOnUiThread(() -> {
                adapter.updateList(items);
                binding.tvTotal.setText("Tổng cộng: " + FormatUtils.formatCurrency(total));
            });
        });
    }

    private void showEmptyState() {
        binding.layoutEmpty.setVisibility(View.VISIBLE);
        binding.layoutOrder.setVisibility(View.GONE);
    }

    private void showOrderState() {
        binding.layoutEmpty.setVisibility(View.GONE);
        binding.layoutOrder.setVisibility(View.VISIBLE);
    }

    private void checkout() {
        if (currentOrder == null) return;
        new AlertDialog.Builder(this)
            .setTitle("Xác nhận thanh toán")
            .setMessage("Bạn có chắc muốn thanh toán đơn hàng này?")
            .setPositiveButton("Thanh toán", (d, w) -> doCheckout())
            .setNegativeButton("Hủy", null)
            .show();
    }

    private void doCheckout() {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            database.orderDao().markAsPaid(currentOrder.id);
            final int paidOrderId = currentOrder.id;
            currentOrder = null;
            runOnUiThread(() -> {
                Toast.makeText(this, "Thanh toán thành công!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, InvoiceActivity.class);
                intent.putExtra("orderId", paidOrderId);
                startActivity(intent);
                finish();
            });
        });
    }

    @Override
    public boolean onSupportNavigateUp() { finish(); return true; }
}
