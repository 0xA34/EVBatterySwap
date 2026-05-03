# 🔋 EV Battery Swap Management System

Hệ thống đặt lịch và quản lý dịch vụ **hoán đổi pin xe điện** (Electric Vehicle Battery Swap) — web application được xây dựng với **Spring Boot 4** và **Java 21**.

---

## 📋 Mục Lục

- [Giới thiệu](#giới-thiệu)
- [Chức năng theo vai trò](#chức-năng-theo-vai-trò)
- [Công nghệ sử dụng](#công-nghệ-sử-dụng)
- [Kiến trúc dự án](#kiến-trúc-dự-án)
- [Mô hình dữ liệu](#mô-hình-dữ-liệu)
- [Bảo mật](#bảo-mật)
- [Cài đặt & Chạy dự án](#cài-đặt--chạy-dự-án)
- [Cấu hình](#cấu-hình)
- [API Endpoints](#api-endpoints)

---

## Giới thiệu

**EV Battery Swap** là nền tảng cho phép tài xế xe điện đặt lịch hoán đổi pin tại các trạm dịch vụ một cách nhanh chóng, đồng thời cung cấp công cụ quản lý toàn diện cho nhân viên và quản trị viên hệ thống.

### Điểm nổi bật

- 🗺️ **Tìm kiếm trạm theo địa chỉ phân cấp**: Tỉnh/Thành → Quận/Huyện → Phường/Xã
- 🔐 **Bảo mật phân tầng**: JWT stateless + Cookie HttpOnly + Token Blacklist
- ⭐ **Hệ thống đánh giá trạm** theo số sao và nhận xét
- 📊 **Dashboard thống kê** theo thời gian thực cho Admin
- 💳 **Hỗ trợ nhiều hình thức thanh toán**: Tiền mặt, Ví điện tử

---

## Chức năng theo vai trò

### 🚗 Driver (Tài xế)
| Tính năng | Mô tả |
|---|---|
| Đặt lịch swap pin | Tìm kiếm trạm theo tỉnh/huyện/phường, chọn thời gian |
| Xem lịch sử giao dịch | Danh sách tất cả lần swap đã thực hiện |
| Đánh giá trạm | Chấm sao + bình luận sau khi sử dụng dịch vụ |
| Quản lý tài khoản | Cập nhật thông tin cá nhân, nạp tiền ví |
| Theo dõi đặt lịch | Xem trạng thái đặt lịch hiện tại |

### 🧑‍🔧 Staff (Nhân viên trạm)
| Tính năng | Mô tả |
|---|---|
| Quản lý đặt lịch | Xem danh sách reservation của trạm |
| Check-in xác nhận | Xác nhận swap, chọn pin phù hợp, ghi nhận thanh toán |
| Quản lý giao dịch | Xem lịch sử swap transaction tại trạm |
| Kiểm kê pin | Xem trạng thái kho pin tại trạm |
| Đa trạm | Một nhân viên có thể quản lý nhiều trạm cùng lúc |

### 👑 Admin (Quản trị viên)
| Tính năng | Mô tả |
|---|---|
| Dashboard thống kê | Doanh thu, số lượng giao dịch, pin, trạm |
| Quản lý người dùng | CRUD tài khoản Driver/Staff |
| Quản lý trạm | Thêm/sửa/xóa trạm dịch vụ |
| Quản lý pin | Theo dõi tình trạng, lịch sử bảo trì pin |
| Quản lý gói thuê | Cấu hình các gói dịch vụ |
| Quản lý giao dịch | Xem, sửa toàn bộ swap transaction |
| Hỗ trợ khách hàng | Xử lý support ticket |

---

## Công nghệ sử dụng

| Thành phần | Công nghệ | Phiên bản |
|---|---|---|
| **Backend Framework** | Spring Boot | 4.0.5 |
| **Ngôn ngữ** | Java | 21 |
| **ORM** | Spring Data JPA + Hibernate | — |
| **Database** | MySQL | 8+ |
| **Template Engine** | Thymeleaf | — |
| **Security** | Spring Security + JWT (JJWT) | 0.13.0 |
| **Boilerplate** | Lombok | — |
| **Validation** | Spring Boot Starter Validation | — |
| **Build Tool** | Apache Maven | 3.9+ |

---

## Kiến trúc dự án

Dự án tuân theo **Layered Architecture** (Kiến trúc phân tầng):

```
com.ev.batteryswap
├── BatterySwapApplication.java     ← Entry point (@SpringBootApplication)
│
├── controllers/                    ← Tầng xử lý HTTP request
│   ├── AuthController.java         ← Đăng nhập / Đăng xuất
│   ├── IndexController.java        ← Trang chủ công khai
│   ├── admin/                      ← Các controller dành cho Admin
│   ├── staff/                      ← Các controller dành cho Staff
│   └── user/                       ← Các controller dành cho Driver
│
├── services/                       ← Tầng business logic
│   ├── interfaces/                 ← Định nghĩa contract (interface)
│   └── *ServiceImpl.java           ← Hiện thực logic
│
├── repositories/                   ← Tầng truy cập DB (Spring Data JPA)
│
├── pojo/                           ← Entity classes (ánh xạ bảng DB)
│
├── dto/                            ← Data Transfer Objects
│
├── security/                       ← Toàn bộ logic bảo mật
│   ├── JwtTokenProvider.java
│   ├── JwtAuthenticationFilter.java
│   ├── JwtCookieHelper.java
│   ├── TokenBlacklistService.java
│   └── UserDetailsServiceImpl.java
│
└── config/                         ← Cấu hình Spring MVC
    ├── SecurityConfig.java
    └── MvcConfig.java (Interceptors)
```

**Luồng xử lý request:**
```
HTTP Request
    → SecurityFilterChain (JwtAuthenticationFilter)
    → RoleCheckInterceptor (kiểm tra quyền MVC)
    → Controller
    → Service (business logic)
    → Repository
    → Database (MySQL)
```

---

## Mô hình dữ liệu

### Sơ đồ quan hệ chính

```
User ──(ManyToMany)──► Station         (Staff quản lý nhiều trạm)
User ──(OneToOne)────► Battery         (Pin tài xế đang giữ)
Station ──(OneToMany)──► Battery       (Pin thuộc về trạm)
Station ──(ManyToOne)──► Quanhuyen     (Địa chỉ: Quận/Huyện)
Quanhuyen ──(ManyToOne)──► Tinhthanh  (Địa chỉ: Tỉnh/Thành)
Station ──(ManyToOne)──► Phuongxa     (Địa chỉ: Phường/Xã)

Reservation ──(ManyToOne)──► User     (Tài xế đặt lịch)
Reservation ──(ManyToOne)──► Station
Reservation ──(ManyToOne)──► Battery  (Pin muốn nhận)

SwapTransaction ──(ManyToOne)──► User
SwapTransaction ──(ManyToOne)──► Station
SwapTransaction ──(ManyToOne)──► Battery  (batteryOut — pin trả lại)
SwapTransaction ──(ManyToOne)──► Battery  (batteryIn  — pin nhận mới)
```

### Các Entity chính

| Entity | Bảng DB | Mô tả |
|---|---|---|
| `User` | `users` | Tài khoản hệ thống (Driver/Staff/Admin) |
| `Station` | `stations` | Trạm swap pin |
| `Battery` | `batteries` | Pin xe điện |
| `Reservation` | `reservations` | Đặt lịch swap |
| `SwapTransaction` | `swap_transactions` | Giao dịch swap thực tế |
| `StationReview` | `station_reviews` | Đánh giá trạm |
| `RentalPackage` | `rental_packages` | Gói thuê pin |
| `Rental` | `rentals` | Hợp đồng thuê pin |
| `MaintenanceLog` | `maintenance_logs` | Lịch sử bảo trì pin |
| `SupportTicket` | `support_tickets` | Yêu cầu hỗ trợ |
| `Tinhthanh` | `tinhthanh` | Tỉnh/Thành phố |
| `Quanhuyen` | `quanhuyen` | Quận/Huyện |
| `Phuongxa` | `phuongxa` | Phường/Xã |

---

## Bảo mật

Hệ thống bảo mật được thiết kế **stateless**, kết hợp JWT và Cookie.

### Tổng quan luồng xác thực

```
[Client] POST /api/login
    → Xác thực username/password (BCrypt)
    → Tạo JWT chứa role claim
    → Set-Cookie: HttpOnly + SameSite=Strict
    ↓
[Mọi request tiếp theo]
    → RoleCheckInterceptor đọc cookie, xác thực JWT
    → Cho phép hoặc redirect về trang login
```

### Phân quyền Cookie theo vai trò

| Cookie | Path | Vai trò |
|---|---|---|
| `admin_token` | `/admin/**` | Admin |
| `staff_token` | `/staff/**` | Staff |
| `driver_token` | `/` | Driver |

### Cơ chế bảo vệ

| Cơ chế | Mô tả |
|---|---|
| **BCrypt** | Hash mật khẩu với salt ngẫu nhiên |
| **JWT + HMAC-SHA256** | Ký token với secret key, không thể giả mạo |
| **HttpOnly Cookie** | JavaScript không đọc được → chống XSS |
| **SameSite=Strict** | Chỉ gửi cookie cùng domain → chống CSRF |
| **Token Blacklist** | Vô hiệu hóa token ngay khi logout |
| **SessionCreationPolicy.STATELESS** | Không lưu session phía server |

---

## Cài đặt & Chạy dự án

### Yêu cầu hệ thống

- **Java 21** (JDK)
- **Maven 3.9+**
- **MySQL 8+**

### Bước 1: Clone repository

```bash
git clone <repository-url>
cd EVBatterySwap
```

### Bước 2: Tạo database MySQL

```sql
CREATE DATABASE bsm CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

> Hibernate sẽ tự động tạo/cập nhật bảng nhờ `spring.jpa.hibernate.ddl-auto=update`.

### Bước 3: Cấu hình `application.properties`

Chỉnh sửa file `src/main/resources/application.properties`:

```properties
# Thay thế thông tin kết nối DB của bạn
spring.datasource.url=jdbc:mysql://localhost:3306/bsm?allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=Asia/Ho_Chi_Minh
spring.datasource.username=YOUR_DB_USERNAME
spring.datasource.password=YOUR_DB_PASSWORD

# Thay bằng secret key đủ dài và bảo mật
jwt.secret=YourSuperSecretKeyForJWTGeneratingMustBeLongEnough
jwt.expiration=3600000          # 1 giờ (ms)
jwt.refresh-expiration=604800000 # 7 ngày (ms)
```

### Bước 4: Build & Chạy

```bash
# Sử dụng Maven Wrapper (không cần cài Maven)
./mvnw spring-boot:run

# Hoặc trên Windows
mvnw.cmd spring-boot:run
```

Ứng dụng sẽ chạy tại: **http://localhost:8080**

---

## Cấu hình

| Thuộc tính | Mô tả | Mặc định |
|---|---|---|
| `spring.datasource.url` | JDBC URL kết nối MySQL | `jdbc:mysql://localhost:3306/bsm` |
| `spring.datasource.username` | Tên đăng nhập DB | `root` |
| `spring.datasource.password` | Mật khẩu DB | — |
| `spring.jpa.hibernate.ddl-auto` | Chiến lược quản lý schema | `update` |
| `jwt.secret` | Secret key ký JWT | — |
| `jwt.expiration` | Thời gian hết hạn Access Token (ms) | `3600000` (1h) |
| `jwt.refresh-expiration` | Thời gian hết hạn Refresh Token (ms) | `604800000` (7d) |

---

## API Endpoints

### Authentication

| Method | Endpoint | Mô tả |
|---|---|---|
| `POST` | `/api/login` | Đăng nhập, nhận JWT cookie |
| `GET` | `/api/logout` | Đăng xuất, thu hồi token |
| `POST` | `/api/register` | Đăng ký tài khoản mới |

### User (Driver)

| Method | Endpoint | Mô tả |
|---|---|---|
| `GET` | `/user/dashboard` | Dashboard tài xế |
| `GET` | `/user/book` | Trang đặt lịch swap |
| `POST` | `/user/reservations` | Tạo đặt lịch mới |
| `GET` | `/user/history` | Lịch sử giao dịch |
| `GET` | `/user/profile` | Thông tin cá nhân |
| `GET` | `/user/station/{id}` | Chi tiết trạm & đánh giá |
| `POST` | `/api/rating` | Gửi đánh giá trạm |

### Staff

| Method | Endpoint | Mô tả |
|---|---|---|
| `GET` | `/staff/dashboard` | Dashboard nhân viên |
| `GET` | `/staff/reservations` | Danh sách đặt lịch |
| `POST` | `/staff/reservations/complete` | Xác nhận check-in swap |
| `GET` | `/staff/transactions` | Lịch sử giao dịch tại trạm |
| `GET` | `/staff/batteries` | Kiểm kê pin tại trạm |

### Admin

| Method | Endpoint | Mô tả |
|---|---|---|
| `GET` | `/admin/dashboard` | Dashboard thống kê |
| `GET/POST` | `/admin/users` | Quản lý người dùng |
| `GET/POST` | `/admin/stations` | Quản lý trạm |
| `GET/POST` | `/admin/batteries` | Quản lý pin |
| `GET/POST` | `/admin/transactions` | Quản lý giao dịch |
| `GET/POST` | `/admin/rental-packages` | Quản lý gói thuê |
| `GET` | `/admin/reservations` | Xem tất cả đặt lịch |
| `GET` | `/admin/tickets` | Hỗ trợ khách hàng |

### REST API (JSON)

| Method | Endpoint | Mô tả |
|---|---|---|
| `GET` | `/api/provinces` | Danh sách tỉnh/thành |
| `GET` | `/api/districts/{provinceId}` | Quận/huyện theo tỉnh |
| `GET` | `/api/wards/{districtId}` | Phường/xã theo quận |
| `GET` | `/api/stations` | Danh sách trạm |

---

## 📁 Cấu trúc thư mục đầy đủ

```
EVBatterySwap/
├── src/
│   ├── main/
│   │   ├── java/com/ev/batteryswap/
│   │   │   ├── BatterySwapApplication.java
│   │   │   ├── config/
│   │   │   ├── controllers/
│   │   │   │   ├── admin/
│   │   │   │   ├── staff/
│   │   │   │   └── user/
│   │   │   ├── dto/
│   │   │   ├── pojo/
│   │   │   ├── repositories/
│   │   │   ├── security/
│   │   │   └── services/
│   │   │       └── interfaces/
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── static/
│   │       └── templates/
│   │           ├── admin/
│   │           ├── staff/
│   │           └── user/
│   └── test/
├── pom.xml
├── mvnw / mvnw.cmd
└── README.md
```

---

## 🛠️ Phát triển thêm

Một số hướng cải tiến trong tương lai:

- [ ] Thay **in-memory Token Blacklist** bằng **Redis** (hỗ trợ multi-instance)
- [ ] Tích hợp **thông báo real-time** (WebSocket/SSE) cho check-in
- [ ] Triển khai **Refresh Token** đầy đủ
- [ ] Thêm **unit test** và **integration test** với JUnit 5
- [ ] Đóng gói Docker và triển khai CI/CD

---
