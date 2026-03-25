package com.example.shoppingapp.ui.order;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.shoppingapp.data.database.AppDatabase;
import com.example.shoppingapp.data.entity.Order;
import com.example.shoppingapp.data.entity.OrderDetail;
import com.example.shoppingapp.data.entity.Product;
import com.example.shoppingapp.data.entity.User;
import com.example.shoppingapp.databinding.ActivityInvoiceBinding;
import com.example.shoppingapp.ui.home.HomeActivity;
import com.example.shoppingapp.utils.FormatUtils;

import java.util.ArrayList;
import java.util.List;

public class InvoiceActivity extends AppCompatActivity {
    private ActivityInvoiceBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInvoiceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        setTitle("Hóa đơn");

        int orderId = getIntent().getIntExtra("orderId", -1);
        if (orderId == -1) { finish(); return; }

        OrderDetailAdapter adapter = new OrderDetailAdapter(new ArrayList<>());
        binding.rvInvoiceDetails.setLayoutManager(new LinearLayoutManager(this));
        binding.rvInvoiceDetails.setAdapter(adapter);

        AppDatabase db = AppDatabase.getDatabase(this);
        AppDatabase.databaseWriteExecutor.execute(() -> {
            Order order = db.orderDao().findById(orderId);
            if (order == null) { runOnUiThread(this::finish); return; }

            User user = db.userDao().findById(order.userId);
            List<OrderDetail> details = db.orderDetailDao().getDetailsByOrderSync(orderId);

            List<OrderDetailAdapter.OrderItem> items = new ArrayList<>();
            for (OrderDetail d : details) {
                Product p = db.productDao().findById(d.productId);
                if (p != null) items.add(new OrderDetailAdapter.OrderItem(d, p));
            }

            runOnUiThread(() -> {
                binding.tvInvoiceId.setText("Mã hóa đơn: #" + orderId);
                binding.tvInvoiceDate.setText("Ngày: " + FormatUtils.formatDisplayDate(order.orderDate));
                binding.tvInvoiceCustomer.setText("Khách hàng: " + (user != null ? user.fullName : "N/A"));
                binding.tvInvoiceStatus.setText("Trạng thái: Đã thanh toán ✓");
                binding.tvInvoiceTotal.setText("Tổng cộng: " + FormatUtils.formatCurrency(order.totalAmount));
                adapter.updateList(items);
            });
        });

        binding.btnBackHome.setOnClickListener(v -> {
            Intent intent = new Intent(this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });
    }
}
