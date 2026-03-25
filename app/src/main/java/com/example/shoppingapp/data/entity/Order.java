package com.example.shoppingapp.data.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
    tableName = "orders",
    foreignKeys = @ForeignKey(
        entity = User.class,
        parentColumns = "id",
        childColumns = "userId",
        onDelete = ForeignKey.CASCADE
    ),
    indices = {@Index("userId")}
)
public class Order {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public int userId;
    public String orderDate;
    public String status; // "pending", "paid"
    public double totalAmount;

    public Order() {}

    public Order(int userId, String orderDate, String status) {
        this.userId = userId;
        this.orderDate = orderDate;
        this.status = status;
        this.totalAmount = 0;
    }
}
