# BOOKSTORE — FINAL INTEGRATION REPORT

**Ngày:** 01/10/2026
**Nhánh:** `integration/cart-order-final`
**Phạm vi:** Cart, partial checkout, COD order, inventory, order history/cancel, admin order management.

## 1. Executive Summary

**Kết quả: PASS WITH WARNINGS.** Backend và web layer đã được merge, sửa các lỗi phát hiện khi chạy Tomcat thực tế. Lần kiểm tra cuối: **21/21 JUnit tests** và các kịch bản HTTP trên Tomcat 10.1.44 + SQL Server tạm đều đạt. Database `bookstore_db` hiện tại **chưa áp dụng migration**; ứng dụng không thể chạy tính năng Order trên database đó cho đến khi backup, chạy migration và kiểm tra lại. Không có migration nào được chạy trên `bookstore_db`.

## 2. Git Integration

| Mục | Commit/nhánh |
|---|---|
| Base | `7393d10` (`origin/main` trước tích hợp) |
| Backend Codex | `codex/order-backend` — `d45c4e1` |
| Web Antigravity | `antigravity/order-web` — `28e4253` |
| Merge | `68be0db` — Antigravity vào backend |
| Integration trước lượt kiểm tra này | `50d374f` |
| Sửa và kiểm chứng cuối | `46e6f7c` |
| Nhánh giao kết quả | `integration/cart-order-final`; không merge thêm vào `main` |

Lúc bắt đầu lượt kiểm tra, `main` và nhánh integration cùng trỏ tới `50d374f`. Tôi chuyển sang nhánh integration rồi commit các sửa đổi ở đó. Các tệp tài liệu, ảnh và output `target/` đã được stage bởi thao tác khác trong workspace; commit `46e6f7c` dùng `git commit --only` để không đưa chúng vào commit và không xóa chúng.

## 3. Merge Conflicts

Merge `68be0db` đã giải quyết xung đột tại `pom.xml`: giữ dependency JUnit 5, Surefire và thêm pattern `**/*Test*.java` để chạy `CartTest_24162073`. Không có conflict Git mới trong lượt kiểm tra này. Đã soát annotation servlet với `web.xml`; không có mapping servlet trùng. Bỏ `@WebFilter` ở EncodingFilter vì filter đã khai báo trong `web.xml`.

## 4. Files Added / Modified

- Backend: `sql/20261001_order_backend.sql`, `OrderDAO_24162073`, `OrderItemDAO_24162073`, `OrderService_24162073`, `Order_24162073`, `OrderItem_24162073`, `OrderStatus_24162073`, `CsrfFilter_24162073`, `ORDER_BACKEND_HANDOFF.md`.
- Web: `CartServlet_24162073`, `CheckoutServlet_24162073`, `OrderHistoryServlet_24162073`, `OrderDetailServlet_24162073`, `CancelOrderServlet_24162073`, `AdminOrderServlet_24162073`, `Cart_24162073`, `CartItem_24162073` và bảy JSP cart/checkout/order/admin order.
- Tích hợp trên các file cũ: `BookDAO`, `BookService`, `Book`, `User`, các filter, `web.xml`, `pom.xml`, trang product/book detail/admin book.
- Commit `46e6f7c`: migration thêm ràng buộc quantity; sửa Cart/Checkout/Admin servlet, DBConnection, EncodingFilter, Cart model; escape các trang home/product/detail/cart/checkout/admin book và layout; thêm test migration có dữ liệu cũ và overflow.

## 5. Final Architecture

`sessionScope.user` xác định người dùng; `sessionScope.cart` giữ Cart; `checkoutSelectedBookIds` giữ danh sách sách được chọn; `csrfToken` bảo vệ mọi POST. Cart kiểm tra sách active và tồn kho. Checkout lấy ID từ session selection và quantity từ session cart; `OrderService` lấy giá/title hiện tại từ SQL Server. Chỉ sau commit mới xóa các mục đã mua khỏi cart, nên partial checkout giữ lại sách không chọn. Order history/detail dùng snapshot `book_title`, `unit_price`; admin quản lý trạng thái qua service.

## 6. Database Changes

Migration `sql/20261001_order_backend.sql` dùng đúng `users.id` và `books.quantity`:

| Thành phần | Kết quả |
|---|---|
| `users.phone` | `VARCHAR(20)`; số 0 đầu đã mất khi còn `INT` không thể khôi phục |
| `books.price` | `DECIMAL(18,2)` |
| `books.quantity` | `INT NOT NULL`, `CHECK quantity >= 0`; migration báo lỗi nếu dữ liệu cũ NULL/âm |
| `books.is_active` | `BIT NOT NULL DEFAULT 1` cho soft delete |
| `orders` | FK `userid → users.id`; receiver name/phone/email/address/note; `total_amount DECIMAL(18,2)`; COD; status; `DATETIME2`; CHECK total/payment/status |
| `order_items` | FK order/book; snapshot `book_title`, `unit_price`; CHECK quantity > 0 và unit_price >= 0 |
| Index | `IX_orders_user_created`, `IX_order_items_order` |

Migration được chạy trên database tạm tạo theo schema cũ và có user/book cũ trước khi nâng cấp; các dòng cũ còn nguyên, quantity âm bị constraint từ chối. Kiểm tra read-only `bookstore_db` hiện tại: có 9 sách, không có quantity NULL/âm; bảng `orders` và cột `is_active` chưa có.

## 7. Routes

| Method | URL | Auth | CSRF | Mục đích |
|---|---|---|---|---|
| GET | `/cart` | User | — | Xem cart |
| POST | `/cart/add` | User | Có | Thêm sách |
| POST | `/cart/update` | User | Có | Sửa quantity |
| POST | `/cart/remove` | User | Có | Xóa mục |
| POST | `/cart/clear` | User | Có | Làm trống cart |
| POST | `/checkout/prepare` | User | Có | Chọn sách |
| GET | `/checkout` | User | — | Xem checkout |
| POST | `/checkout` | User | Có | Tạo COD order |
| GET | `/checkout/success` | User | — | Kết quả |
| GET | `/order-history` | User | — | Lịch sử |
| GET | `/order-detail` | User | — | Chi tiết theo owner |
| POST | `/order/cancel` | User | Có | Hủy PENDING |
| GET | `/admin/orders` | Admin | — | Danh sách |
| GET | `/admin/order-detail` | Admin | — | Chi tiết |
| POST | `/admin/order/update-status` | Admin | Có | Chuyển trạng thái |
| POST | `/admin/order/cancel` | Admin | Có | Hủy và hoàn kho |
| POST | `/admin/books?action=delete` | Admin | Có | Soft delete sách |

AuthFilter bao phủ route user, AdminFilter bao phủ `/admin/*`, CsrfFilter bao phủ `/*`. Các URL mutation GET không sửa dữ liệu.

## 8. Transaction Review

`createOrder` dùng một JDBC Connection với `autoCommit=false`, khóa sách active bằng `UPDLOCK, HOLDLOCK`, trừ kho nguyên tử với `WHERE quantity >= ? AND is_active=1`, ghi order/items, tính tổng từ DB rồi commit. SQL/runtime exception rollback. Hai phương thức cancel dùng conditional status update trước khi hoàn kho trong cùng transaction; cancel lại không cộng kho lần hai. Status advance dùng expected-current-status trong UPDATE. DAO tham gia transaction nhận Connection từ service và không nuốt `SQLException`.

## 9. Security Review

| Vấn đề | Bằng chứng/kết quả |
|---|---|
| Authentication/authorization | AuthFilter/AdminFilter và admin check ở service; HTTP login ba vai trò đạt |
| IDOR | User B yêu cầu detail đơn của User A nhận HTTP 403; DAO lọc `orderid` + `userid` |
| CSRF | HTTP thiếu/sai token ở cart remove và admin update nhận 403; token đúng thao tác thành công |
| XSS | HTTP dùng book title/description và receiver/note chứa `<script>`; HTML đã escape. Bỏ title khỏi inline JS cart và escape các view/layout liên quan |
| Price/quantity tampering | POST `price=1,total=1,subtotal=1` vẫn lưu tổng 1100.00 và unit price DB; quantity âm/vượt kho bị từ chối ở web |
| State transitions | User không hủy CONFIRMED; admin không hủy SHIPPING và không đưa DELIVERED về PENDING |
| Inactive book | Không hiển thị cho khách, không thêm mới, cart cũ checkout bị rollback |

Các bảo vệ này áp dụng cho scope Cart/Order được thử. Những rủi ro cũ còn lại ở mục 14.

## 10. Functional Tests

| Test | Expected | Actual | Kết quả |
|---|---|---|---|
| Login → detail → add 0→1→2 | Cart tăng đúng | 1 rồi 2 | PASS |
| Inc/dec/manual/zero/âm/vượt kho/remove/clear | Không âm/vượt kho; zero remove | Đúng; CSRF hợp lệ | PASS |
| Partial A×2, B×1, C×3; chọn A,C kèm ID A trùng | Checkout A,C một lần; sau mua còn B | 2 dòng, tổng 1100.00, còn B | PASS |
| COD, snapshot, DB price, stock | PENDING/COD; 2 items; A 10→8, C 10→7 | Đúng theo truy vấn SQL | PASS |
| History/detail, receiver/note XSS | Order hiện và text escaped | Đúng | PASS |
| IDOR | User khác bị chặn | 403 | PASS |
| User cancel hai lần | Chỉ một lần restore | A,C về 10; status CANCELLED | PASS |
| Admin CONFIRMED→SHIPPING→DELIVERED | Hợp lệ; invalid reject | Đúng | PASS |
| Sách inactive sau khi đã vào cart | Checkout thất bại; không trừ kho | Đúng | PASS |
| JSP home, admin book list/edit, admin order list/detail | HTTP 200, title escaped | Đúng | PASS |
| Migration có dữ liệu cũ | Dữ liệu giữ nguyên, CHECK hoạt động | Đúng trên DB tạm | PASS |

HTTP kiểm tra bằng `requests` trên Tomcat 10.1.44 và database tạm `bookstore_web_it_282660`. Trong lần đầu đã phát hiện cart.jsp trả 500 do EL `.empty`; sửa xong chạy lại toàn bộ và đạt `WEB_SMOKE_SUCCESS`. Đây là smoke test tự động ở môi trường tạm, chưa phải kiểm thử trình duyệt thủ công.

## 11. Concurrency Tests

JUnit SQL Server dùng hai thread tranh cuốn cuối: đúng một order thành công, quantity = 0, không oversell. Hai thread hủy cùng một đơn: đúng một lần thành công, tồn kho chỉ được cộng một lần. Test còn xác nhận rollback sau lỗi insert order item: order không tồn tại và quantity không đổi. HTTP cùng-session checkout đã được đồng bộ trên session để ngăn hai POST lặp đồng thời tạo hai order từ cùng selection.

## 12. Build Results

| Command | Kết quả |
|---|---|
| `$env:BOOKSTORE_SQL_INTEGRATION='true'; mvn clean test` | BUILD SUCCESS; 21 tests, 0 fail/error/skip |
| `$env:BOOKSTORE_SQL_INTEGRATION='true'; mvn clean package` | BUILD SUCCESS; 21 tests; tạo `target/bookstore.war` |
| HTTP Tomcat + SQL Server tạm | `WEB_SMOKE_SUCCESS`; JSP bổ sung đã mở đạt 200 |

Maven có cảnh báo compiler `-source/-target 17` thay vì `--release 17`; không làm fail build.

## 13. Bugs Found and Fixed

| Severity | Location | Cause → Fix | Verification |
|---|---|---|---|
| High | `views/cart.jsp` | EL `.empty` không parse, cart HTTP 500 → dùng `itemCount == 0` | Tomcat cart 200, toàn bộ HTTP smoke pass |
| High | `views/cart.jsp` và view sách | Title trong inline JS và EL text/attribute chưa escape → bỏ JS title, dùng `c:out` | Payload script trong HTTP chỉ hiện escaped |
| Medium | `CartServlet` | Add âm thành 1, vượt kho bị clamp, update âm xóa → reject âm/vượt kho; zero remove | HTTP cart cases pass |
| Medium | `CheckoutServlet` | ID chọn trùng làm tổng dự kiến sai; double POST cùng session có thể lặp order → deduplicate, đồng bộ session | HTTP duplicate selection; code review |
| Medium | `Cart_24162073` | Phép cộng int có thể overflow → tính qua long rồi cap | JUnit overflow pass |
| Medium | `AdminOrderServlet`, `CheckoutServlet` | Có thể lộ exception message SQL/internal → message chung, log server | Code review và HTTP lỗi nghiệp vụ |
| Medium | Migration/AdminBook | Quantity âm chưa có CHECK/validation → ràng buộc DB và validate admin | JUnit migration + SQL CHECK |
| Low | `EncodingFilter` | Mapping annotation và web.xml trùng → chỉ dùng web.xml | Runtime filter pipeline hoạt động |

## 14. Remaining Issues

| Loại | Vị trí | Tác động | Khuyến nghị |
|---|---|---|---|
| **BLOCKER trước khi chạy trên DB hiện tại** | `bookstore_db`, `sql/20261001_order_backend.sql` | DB hiện không có `orders`/`is_active`; feature Order sẽ lỗi | Backup DB, chạy migration, kiểm tra dữ liệu và smoke lại trên môi trường đích |
| **SHOULD FIX** | `DBConnection_24162073.java`, `UserDAO_24162073.java` | Mật khẩu SQL hardcoded; mật khẩu user đối chiếu plaintext | Đưa secret ra cấu hình an toàn, hash password và migration tài khoản |
| **SHOULD FIX** | `AdminBookServlet_24162073.java` | Upload chỉ giới hạn size, chưa xác thực nội dung/MIME và tên tệp chặt chẽ | Giới hạn loại ảnh, tạo tên server-side, lưu ngoài web root |
| **SHOULD FIX** | `LoginServlet_24162073.java` | Login giữ session ID cũ | Đổi session ID sau xác thực |
| **SHOULD FIX** | Các DAO cũ như `BookDAO`, `UserDAO` | Nhiều method cũ log và trả null/0 khi SQL lỗi, khó phân biệt lỗi DB | Refactor theo từng luồng để propagate lỗi |
| **NICE TO HAVE** | `HomeServlet`, `ProductServlet`, `AdminBookServlet` | Query author/rating theo từng sách (N+1) | Gom truy vấn khi dữ liệu lớn |
| **OUT OF SCOPE** | `target/` được Git track | Build làm dirty nhiều artifact | Bỏ tracking build output ở thay đổi Git riêng; không đưa artifact vào commit này |

Không phát hiện `DELETE FROM books`, duplicate OrderService/CartServlet/CheckoutServlet/CsrfFilter, hoặc query dùng `users(userid)` trong feature mới.

## 15. Manual Tests Still Required

1. Backup `bookstore_db`, chạy migration trên bản sao chứa dữ liệu thực; kiểm tra 9 sách hiện có, users, order schema, CHECK/FK và khả năng chạy lại migration.
2. Triển khai WAR cuối lên môi trường đích với cấu hình DB phù hợp, thao tác đầy đủ bằng trình duyệt trên desktop/mobile, gồm thông báo lỗi và điều hướng SiteMesh.
3. Kiểm tra hardening upload, session login và lưu giữ session nếu Tomcat được cấu hình persistence/replication.

## 16. Final Readiness

**Chưa nên merge tiếp vào `main` để triển khai ngay trên `bookstore_db` hiện tại.** Blocker cụ thể là migration chưa được áp dụng cho database này; cần backup và xác nhận migration trên bản sao trước. Sau đó chạy lại smoke test với WAR cuối. Mã Cart/Order trên nhánh integration đã build và vượt qua các bài test ở môi trường tạm.
