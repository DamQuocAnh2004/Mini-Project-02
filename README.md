# Shopping App - Android (Room Database + SharedPreferences)

## Mo ta
Ung dung quan ly ban hang Android theo dung yeu cau bai tap:
- Dang nhap (luu trang thai bang SharedPreferences)
- Xem danh sach san pham (co tim kiem)
- Xem danh sach danh muc san pham
- Xem chi tiet san pham
- Tao hoa don (phai dang nhap)
- Du lieu luu bang Room Database voi 5 bang

## Tai khoan demo
| Username | Password |
|----------|----------|
| admin    | 123456   |
| user1    | password |

## Cai dat
1. Giai nen ZIP
2. Mo Android Studio -> Open -> chon thu muc ShoppingApp
3. Doi Gradle sync
4. Chay tren thiet bi hoac AVD (minSdk 24)

## Cau truc 5 bang Room DB
- users
- categories
- products (FK -> categories)
- orders (FK -> users)
- order_details (FK -> orders, products)

## Luong chuc nang
Start -> Home -> Dang nhap / Xem san pham / Xem danh muc
San pham -> Chi tiet -> Them gio hang (can dang nhap)
Gio hang -> Thanh toan -> Hoa don
