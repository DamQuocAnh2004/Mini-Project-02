package com.example.shoppingapp.ui.order;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shoppingapp.R;
import com.example.shoppingapp.data.entity.OrderDetail;
import com.example.shoppingapp.data.entity.Product;
import com.example.shoppingapp.utils.FormatUtils;

import java.util.List;

public class OrderDetailAdapter extends RecyclerView.Adapter<OrderDetailAdapter.VH> {
    private List<OrderItem> items;

    public static class OrderItem {
        public OrderDetail detail;
        public Product product;
        public OrderItem(OrderDetail detail, Product product) {
            this.detail = detail;
            this.product = product;
        }
    }

    public OrderDetailAdapter(List<OrderItem> items) { this.items = items; }

    public void updateList(List<OrderItem> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_detail, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        OrderItem item = items.get(pos);
        h.tvName.setText(item.product.name);
        h.tvQty.setText("Số lượng: " + item.detail.quantity);
        h.tvUnitPrice.setText("Đơn giá: " + FormatUtils.formatCurrency(item.detail.unitPrice));
        h.tvSubtotal.setText("Thành tiền: " + FormatUtils.formatCurrency(item.detail.quantity * item.detail.unitPrice));
    }

    @Override
    public int getItemCount() { return items.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvName, tvQty, tvUnitPrice, tvSubtotal;
        VH(@NonNull View v) {
            super(v);
            tvName = v.findViewById(R.id.tvItemName);
            tvQty = v.findViewById(R.id.tvItemQty);
            tvUnitPrice = v.findViewById(R.id.tvItemUnitPrice);
            tvSubtotal = v.findViewById(R.id.tvItemSubtotal);
        }
    }
}
