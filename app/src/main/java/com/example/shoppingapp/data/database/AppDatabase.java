package com.example.shoppingapp.data.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.shoppingapp.data.dao.CategoryDao;
import com.example.shoppingapp.data.dao.OrderDao;
import com.example.shoppingapp.data.dao.OrderDetailDao;
import com.example.shoppingapp.data.dao.ProductDao;
import com.example.shoppingapp.data.dao.UserDao;
import com.example.shoppingapp.data.entity.Category;
import com.example.shoppingapp.data.entity.Order;
import com.example.shoppingapp.data.entity.OrderDetail;
import com.example.shoppingapp.data.entity.Product;
import com.example.shoppingapp.data.entity.User;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(
    entities = {User.class, Category.class, Product.class, Order.class, OrderDetail.class},
    version = 1,
    exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {
    public abstract UserDao userDao();
    public abstract CategoryDao categoryDao();
    public abstract ProductDao productDao();
    public abstract OrderDao orderDao();
    public abstract OrderDetailDao orderDetailDao();

    private static volatile AppDatabase INSTANCE;
    public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(4);

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                        context.getApplicationContext(),
                        AppDatabase.class, "shopping_db"
                    )
                    .addCallback(new RoomDatabase.Callback() {
                        @Override
                        public void onCreate(@NonNull SupportSQLiteDatabase db) {
                            super.onCreate(db);
                            databaseWriteExecutor.execute(() -> seedData(context));
                        }
                    })
                    .build();
                }
            }
        }
        return INSTANCE;
    }

    private static void seedData(Context context) {
        AppDatabase database = getDatabase(context);
        UserDao userDao = database.userDao();
        CategoryDao categoryDao = database.categoryDao();
        ProductDao productDao = database.productDao();

        userDao.insert(new User("admin", "123456", "Admin User", "admin@shop.com"));
        userDao.insert(new User("user1", "password", "Nguyen Van A", "user1@shop.com"));

        long catElec  = categoryDao.insert(new Category("Dien tu", "Thiet bi dien tu, dien gia dung"));
        long catCloth = categoryDao.insert(new Category("Thoi trang", "Quan ao, giay dep, phu kien"));
        long catFood  = categoryDao.insert(new Category("Thuc pham", "Do an, thuc uong"));
        long catBook  = categoryDao.insert(new Category("Sach", "Sach giao khoa, sach tham khao"));

        productDao.insert(new Product("iPhone 15", "Dien thoai Apple iPhone 15 128GB", 22990000, 50, (int) catElec));
        productDao.insert(new Product("Samsung Galaxy S24", "Samsung Galaxy S24 256GB", 18990000, 30, (int) catElec));
        productDao.insert(new Product("Laptop Dell XPS 13", "Laptop Dell XPS 13 Intel Core i7", 35990000, 20, (int) catElec));
        productDao.insert(new Product("Tai nghe Sony WH-1000XM5", "Tai nghe chong on cao cap", 8990000, 40, (int) catElec));
        productDao.insert(new Product("Ao Thun Nam Basic", "Ao thun cotton 100%, nhieu mau", 199000, 200, (int) catCloth));
        productDao.insert(new Product("Quan Jeans Levis 501", "Quan jeans co dien dang straight", 1290000, 100, (int) catCloth));
        productDao.insert(new Product("Giay Nike Air Force 1", "Giay the thao classic trang", 2590000, 80, (int) catCloth));
        productDao.insert(new Product("Ca phe Highlands", "Ca phe rang xay dac biet 250g", 89000, 500, (int) catFood));
        productDao.insert(new Product("Banh Trung Thu Kinh Do", "Hop banh trung thu dac biet 4 cai", 380000, 150, (int) catFood));
        productDao.insert(new Product("Lap trinh Android can ban", "Sach hoc lap trinh Android tu co ban", 250000, 100, (int) catBook));
        productDao.insert(new Product("Clean Code", "Sach Clean Code - Robert C. Martin", 320000, 75, (int) catBook));
    }
}
