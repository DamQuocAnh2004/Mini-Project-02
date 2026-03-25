package com.example.shoppingapp.ui.categories;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shoppingapp.R;
import com.example.shoppingapp.data.database.AppDatabase;
import com.example.shoppingapp.data.entity.Category;
import com.example.shoppingapp.databinding.ActivityCategoriesBinding;
import com.example.shoppingapp.ui.products.ProductsActivity;

import java.util.ArrayList;
import java.util.List;

public class CategoriesActivity extends AppCompatActivity {
    private ActivityCategoriesBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCategoriesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("Danh mục sản phẩm");

        CategoryAdapter adapter = new CategoryAdapter(new ArrayList<>(), category -> {
            Intent intent = new Intent(this, ProductsActivity.class);
            intent.putExtra("categoryId", category.id);
            intent.putExtra("categoryName", category.name);
            startActivity(intent);
        });

        binding.rvCategories.setLayoutManager(new LinearLayoutManager(this));
        binding.rvCategories.setAdapter(adapter);

        AppDatabase.getDatabase(this).categoryDao().getAllCategories().observe(this, categories -> {
            if (categories != null) adapter.updateList(categories);
        });
    }

    @Override
    public boolean onSupportNavigateUp() { finish(); return true; }

    // Inner Adapter
    static class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.VH> {
        private List<Category> list;
        private final OnCategoryClick listener;

        interface OnCategoryClick { void onClick(Category c); }

        CategoryAdapter(List<Category> list, OnCategoryClick listener) {
            this.list = list;
            this.listener = listener;
        }

        void updateList(List<Category> newList) { this.list = newList; notifyDataSetChanged(); }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull VH h, int pos) {
            Category c = list.get(pos);
            h.tvName.setText(c.name);
            h.tvDesc.setText(c.description);
            h.itemView.setOnClickListener(v -> listener.onClick(c));
        }

        @Override
        public int getItemCount() { return list.size(); }

        static class VH extends RecyclerView.ViewHolder {
            TextView tvName, tvDesc;
            VH(@NonNull View v) {
                super(v);
                tvName = v.findViewById(R.id.tvCategoryName);
                tvDesc = v.findViewById(R.id.tvCategoryDesc);
            }
        }
    }
}
