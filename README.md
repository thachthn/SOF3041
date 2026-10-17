# Testing-ASM — Ứng dụng quản lý sản phẩm (Spring Boot)

Dự án nền (Y1) cho Assignment "Kiểm thử nâng cao cùng AI (Java)".

## Chức năng
- Đăng ký / Đăng nhập (session-based, mật khẩu mã hoá BCrypt)
- CRUD sản phẩm (thêm / xem / sửa / xoá)
- Dữ liệu lưu H2 in-memory (tự reset mỗi lần chạy lại app — thuận tiện cho việc test lặp lại)

## Yêu cầu môi trường
- JDK 25+
- Maven 3.9+
- Google Chrome (để chạy Selenium test)

## Chạy ứng dụng
```bash
cd webapp
mvn spring-boot:run
```
Mở trình duyệt: http://localhost:8080

## Chạy test

### Unit/Integration test (JUnit, tầng Service + Controller)
```bash
mvn test
```
(Mặc định loại trừ nhóm `ui` — xem `pom.xml`, để chạy nhanh trong vòng lặp code thường ngày.)

### Chỉ chạy nhóm smoke
```bash
mvn test -Dgroups=smoke
```

### Chạy Selenium UI test
```bash
mvn test -Dgroups=ui
```
Yêu cầu máy có Google Chrome cài sẵn; WebDriverManager sẽ tự tải ChromeDriver tương ứng.

### Xem report
Sau khi chạy, report nằm tại: `target/surefire-reports/`

## Cấu trúc thư mục
```
webapp/
├── src/main/java/com/fpt/testingasm/
│   ├── model/        # User, Product (entity + validation)
│   ├── repository/   # Spring Data JPA repository
│   ├── service/      # Business rule (BR1-BR12, xem comment trong code)
│   └── controller/   # AuthController, ProductController
├── src/main/resources/templates/   # Giao diện Thymeleaf
├── src/test/java/com/fpt/testingasm/
│   ├── service/      # Unit test cho UserService, ProductService
│   ├── controller/   # Integration test (MockMvc) cho 2 controller
│   └── selenium/     # Page Object + Selenium UI test
└── .github/workflows/ci.yml   # GitHub Actions: smoke / regression / ui
```

## Tài khoản demo
Không có tài khoản dựng sẵn — hãy vào `/register` để tạo tài khoản mới (DB reset mỗi lần restart app).
