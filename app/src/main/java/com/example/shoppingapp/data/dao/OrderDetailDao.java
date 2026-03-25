package com.example.shoppingapp.data.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.shoppingapp.data.entity.OrderDetail;

import java.util.List;

@Dao
public interface OrderDetailDao {
    @Insert
    long insert(OrderDetail orderDetail);

    @Query("SELECT * FROM order_details WHERE orderId = :orderId")
    LiveData<List<OrderDetail>> getDetailsByOrder(int orderId);

    @Query("SELECT * FROM order_details WHERE orderId = :orderId")
    List<OrderDetail> getDetailsByOrderSync(int orderId);

    @Query("SELECT SUM(quantity * unitPrice) FROM order_details WHERE orderId = :orderId")
    double getTotalByOrder(int orderId);

    @Query("DELETE FROM order_details WHERE orderId = :orderId AND productId = :productId")
    void deleteItem(int orderId, int productId);

    @Query("SELECT * FROM order_details WHERE orderId = :orderId AND productId = :productId LIMIT 1")
    OrderDetail findByOrderAndProduct(int orderId, int productId);

    @Query("UPDATE order_details SET quantity = :quantity WHERE orderId = :orderId AND productId = :productId")
    void updateQuantity(int orderId, int productId, int quantity);
}
