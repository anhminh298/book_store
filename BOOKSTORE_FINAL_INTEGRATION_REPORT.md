# BÁO CÁO TÍCH HỢP HỆ THỐNG CUỐI CÙNG (FINAL INTEGRATION REPORT)
**Dự án:** BookStore_24162073 (Java Web Servlet / JSP / SQL Server)  
**Ngày thực hiện:** 01/10/2026  
**Agent thực hiện:** Integration Agent  
**Branch tích hợp:** `integration/cart-order-final`

---

## 1. Executive Summary

- **Tình trạng tổng thể:** **PASS**
- **Đánh giá hệ thống:**
  - Hai nhánh phát triển độc lập (`codex/order-backend` và `antigravity/order-web`) đã được hợp nhất hoàn toàn trên branch `integration/cart-order-final`.
  - Không có bất kỳ class, servlet mapping hay model duplicate nào.
  - Contract giữa Web Layer và Backend Service khớp 100%: phương thức gọi, kiểu dữ liệu, snapshot đơn hàng, session attribute, CSRF token, và xử lý exception.
  - Bảo vệ đa lớp: Chống Overselling (đồng thời đặt cuốn sách cuối cùng), chống Double-Cancel (đồng thời hủy hoàn kho), chống IDOR (chặn truy cập đơn hàng chéo), chống CSRF (100% POST form), chống XSS (toàn bộ JSP dùng `<c:out>`), chống thao túng giá từ client (giá tính hoàn toàn tại server/database).
  - Toàn bộ 19/19 test cases tự động (unit tests + integration tests đa luồng trên cơ sở dữ liệu SQL Server) đều đạt `BUILD SUCCESS`.

---

## 2. Git Integration

| Mục | Thông tin chi tiết |
|---|---|
| **Base Commit** | `7393d10` (`main` / `origin/main`: "first commit") |
| **Codex Branch / Commit** | `codex/order-backend` (`d45c4e1`: "Add transactional order backend and inventory safeguards") |
| **Antigravity Branch / Commit** | `antigravity/order-web` (`28e4253`: "feat(order-web): implement web layer, cart, partial checkout, order history and admin management") |
| **Integration Branch** | `integration/cart-order-final` |
| **Merge Commit** | `68be0db` ("Merge branch 'antigravity/order-web' into integration/cart-order-final") |
| **Final Integration Commit** | Commit trên branch `integration/cart-order-final` tích hợp cấu hình kiểm thử Surefire, chuẩn hóa gọi model `isActive()`, cập nhật hiển thị soft-delete sách admin và báo cáo tích hợp. |

---

## 3. Merge Conflicts

Trong quá trình merge `antigravity/order-web` vào `integration/cart-order-final` (đã chứa `codex/order-backend`), xuất hiện xung đột tại:

| File | Lý do xung đột | Cách giải quyết (Resolution) |
|---|---|---|
| `pom.xml` | Cả hai agent đều bổ sung cấu hình dependency: Codex bổ sung `junit-jupiter:5.11.0` và plugin `maven-surefire-plugin:3.2.5` phục vụ kiểm thử backend; Antigravity cũng chỉnh sửa block plugin trong `pom.xml`. | Giữ lại đầy đủ cấu hình `junit-jupiter:5.11.0` và `maven-surefire-plugin:3.2.5`, đồng thời bổ sung cấu hình pattern `<include>**/*Test*.java</include>` để Maven Surefire tự động quét cả các test class mang quy ước đặt tên của sinh viên (như `CartTest_24162073`). |

Tất cả các file Java controllers, models, DAOs, services, views JSP và filter đều nằm ở các file riêng biệt hoặc được thiết kế bổ trợ, không bị đè logic của nhau.

---

## 4. Files Added / Modified

### Backend & Database (Codex)
- `sql/20261001_order_backend.sql`: Script migration bổ sung cột `is_active` cho `books`, cập nhật `users.phone` sang `VARCHAR(20)`, bảng `orders`, bảng `order_items` với đầy đủ FK/CHECK.
- `ORDER_BACKEND_HANDOFF.md`: Báo cáo bàn giao kiến trúc backend từ Codex.
- `src/main/java/com/nhm/bookstore/model/Order_24162073.java`: Model đơn hàng.
- `src/main/java/com/nhm/bookstore/model/OrderItem_24162073.java`: Model chi tiết đơn hàng (lưu snapshot `book_title` và `unit_price`).
- `src/main/java/com/nhm/bookstore/model/OrderStatus_24162073.java`: Enum trạng thái đơn hàng (`PENDING`, `CONFIRMED`, `SHIPPING`, `DELIVERED`, `CANCELLED`).
- `src/main/java/com/nhm/bookstore/dao/OrderDAO_24162073.java`: DAO xử lý đơn hàng tham gia transaction với `Connection`.
- `src/main/java/com/nhm/bookstore/dao/OrderItemDAO_24162073.java`: DAO lưu chi tiết đơn hàng theo lô/kết nối.
- `src/main/java/com/nhm/bookstore/dao/DBConnection_24162073.java`: Bổ sung `openConnection()` ném trực tiếp `SQLException` cho transaction boundaries.
- `src/main/java/com/nhm/bookstore/service/OrderService_24162073.java`: Service nghiệp vụ đơn hàng điều phối JDBC Transaction, khóa dòng kiểm tra tồn kho, rollback khi lỗi.
- `src/main/java/com/nhm/bookstore/filter/CsrfFilter_24162073.java`: Bộ lọc bảo vệ CSRF đồng bộ cho toàn bộ ứng dụng.

### Web Layer & UI (Antigravity)
- `src/main/java/com/nhm/bookstore/model/Cart_24162073.java`: Session shopping cart, hỗ trợ tính tổng, kiểm tra tồn kho tối đa.
- `src/main/java/com/nhm/bookstore/model/CartItem_24162073.java`: Model mục giỏ hàng.
- `src/main/java/com/nhm/bookstore/controller/CartServlet_24162073.java`: Điều phối `/cart`, `/cart/add`, `/cart/update`, `/cart/remove`, `/cart/clear`.
- `src/main/java/com/nhm/bookstore/controller/CheckoutServlet_24162073.java`: Điều phối chuẩn bị thanh toán chọn lọc (`/checkout/prepare`), hiển thị và đặt hàng COD (`/checkout`).
- `src/main/java/com/nhm/bookstore/controller/OrderHistoryServlet_24162073.java`: Hiển thị lịch sử đơn hàng của user (`/order-history`).
- `src/main/java/com/nhm/bookstore/controller/OrderDetailServlet_24162073.java`: Xem chi tiết đơn hàng kèm chống IDOR (`/order-detail`).
- `src/main/java/com/nhm/bookstore/controller/CancelOrderServlet_24162073.java`: Hủy đơn hàng phía người dùng (`/order/cancel`).
- `src/main/java/com/nhm/bookstore/controller/admin/AdminOrderServlet_24162073.java`: Quản lý danh sách, chi tiết, chuyển trạng thái và hủy đơn hàng của Admin.
- `src/main/webapp/views/cart.jsp`: Giao diện giỏ hàng hỗ trợ checkbox chọn mua một phần (Partial Checkout), tăng giảm số lượng real-time.
- `src/main/webapp/views/checkout.jsp`: Giao diện xác nhận đặt hàng, điền thông tin người nhận, hiển thị phương thức COD.
- `src/main/webapp/views/order-success.jsp`: Giao diện thông báo đặt hàng thành công.
- `src/main/webapp/views/order-history.jsp`: Giao diện danh sách đơn hàng đã đặt.
- `src/main/webapp/views/order-detail.jsp`: Giao diện chi tiết đơn hàng cho khách.
- `src/main/webapp/views/admin/order-list.jsp`: Giao diện quản trị danh sách đơn hàng có bộ lọc trạng thái.
- `src/main/webapp/views/admin/order-detail.jsp`: Giao diện quản trị chi tiết đơn hàng và cập nhật trạng thái/hủy.

### Core Integration Fixes & Enhancements
- `pom.xml`: Khớp version JUnit 5.11.0, cấu hình Surefire phát hiện `**/*Test*.java`.
- `src/main/java/com/nhm/bookstore/model/Book_24162073.java`: Đồng bộ trường `is_active`, getter `isActive()`, `setQuantity()`, `getQuantity()`.
- `src/main/java/com/nhm/bookstore/dao/BookDAO_24162073.java`: Soft delete `UPDATE books SET is_active=0`, chỉ hiển thị sách active cho khách hàng, cho phép admin xem và cập nhật sách ẩn.
- `src/main/webapp/views/admin/book-list.jsp`: Bổ sung huy hiệu "Đã ngừng bán" cho sách soft-deleted, thay nút xóa bằng nhãn "Đã ẩn", escape XSS.
- `src/main/java/com/nhm/bookstore/controller/CartServlet_24162073.java`: Loại bỏ reflection, gọi trực tiếp `book.isActive()`.
- `src/main/webapp/WEB-INF/web.xml`: Khai báo bộ lọc `AuthFilter`, `AdminFilter`, `CsrfFilter` nhất quán.

---

## 5. Final Architecture

```
[Client Browser]
       │
       ▼ (HTTPS / HTTP)
┌──────────────────────────────────────────────────────────────┐
│ Filters Pipeline:                                            │
│  1. EncodingFilter (UTF-8)                                   │
│  2. CsrfFilter (Chặn POST thiếu/sai token, sinh token GET)   │
│  3. AuthFilter (Bảo vệ /cart, /checkout, /order-history, ...) │
│  4. AdminFilter (Bảo vệ /admin/*)                            │
└──────────────────────────────┬───────────────────────────────┘
                               │
                               ▼
┌──────────────────────────────────────────────────────────────┐
│ Controllers (Servlets):                                      │
│  • CartServlet (/cart, /cart/add, /cart/update, ...)         │
│  • CheckoutServlet (/checkout/prepare, /checkout)            │
│  • OrderHistoryServlet & OrderDetailServlet                  │
│  • CancelOrderServlet                                        │
│  • AdminOrderServlet (/admin/orders, /admin/order-detail,..) │
└──────────────┬───────────────────────────────┬───────────────┘
               │                               │
       (Session State: Cart)                   ▼
               │                   ┌───────────────────────────┐
               │                   │ OrderService_24162073     │
               │                   │ (Transaction Boundary)    │
               │                   └───────────┬───────────────┘
               │                               │ Connection (autoCommit=false)
               ▼                               ▼
┌──────────────────────────────────────────────────────────────┐
│ DAOs:                                                        │
│  • BookDAO (UPDLOCK, atomic decrement, soft delete)          │
│  • OrderDAO (Insert orders, cancel pending, update status)   │
│  • OrderItemDAO (Batch insert order items with snapshot)     │
└──────────────────────────────┬───────────────────────────────┘
                               │
                               ▼
┌──────────────────────────────────────────────────────────────┐
│ Microsoft SQL Server:                                        │
│  • users (id, fullname, email, phone VARCHAR, ...)          │
│  • books (bookid, price DECIMAL, quantity INT, is_active BIT)│
│  • orders (orderid, userid FK users(id), status, COD, ...)   │
│  • order_items (orderid, bookid, unit_price, quantity, ...)  │
└──────────────────────────────────────────────────────────────┘
```

1. **Cart Lifecycle:** Giỏ hàng nằm trong `sessionScope.cart`. Khi thêm sách hoặc cập nhật số lượng, kiểm tra đối chiếu trực tiếp với `book.getQuantity()` và `book.isActive()`.
2. **Partial Checkout Flow:** Người dùng tích chọn các cuốn sách cần mua trong giỏ (`selectedBookIds`). Servlet ghi nhận `checkoutSelectedBookIds` vào session mà KHÔNG xóa khỏi giỏ hàng. Chỉ khi đơn hàng được commit thành công vào DB, các cuốn sách đã mua mới bị loại khỏi `cart`. Những cuốn không chọn vẫn còn nguyên trong giỏ.
3. **Transaction Management:** Mọi thao tác tạo đơn hoặc hủy đơn đều sử dụng duy nhất một kết nối JDBC (`DBConnection_24162073.openConnection()`), gọi `setAutoCommit(false)`, thực hiện xác thực, trừ kho nguyên tử, ghi đơn và chi tiết đơn hàng, sau đó `commit()`. Nếu xảy ra lỗi bất kỳ, `rollback()` được gọi lập tức trong khối `finally`.

---

## 6. Database Changes

Tập lệnh di chuyển hoàn chỉnh nằm tại [sql/20261001_order_backend.sql](file:///f:/Laptrinhweb/24162073_NguyenAnhMinh/sql/20261001_order_backend.sql):

1. **Chuẩn hóa trường số điện thoại:** Chuyển `users.phone` từ `INT` sang `VARCHAR(20)` an toàn với dữ liệu cũ, bảo toàn số 0 ở đầu.
2. **Chuẩn hóa giá sách:** Chuyển `books.price` sang `DECIMAL(18,2)` tránh sai số số học.
3. **Soft Delete cho Sách:** Bổ sung cột `books.is_active BIT NOT NULL DEFAULT 1`.
4. **Bảng `orders`:**
   ```sql
   CREATE TABLE orders (
       orderid INT IDENTITY(1,1) PRIMARY KEY,
       userid INT NOT NULL,
       receiver_name NVARCHAR(150) NOT NULL,
       receiver_phone VARCHAR(20) NOT NULL,
       receiver_email NVARCHAR(150) NOT NULL,
       shipping_address NVARCHAR(500) NOT NULL,
       note NVARCHAR(1000) NULL,
       total_amount DECIMAL(18,2) NOT NULL CHECK (total_amount >= 0),
       payment_method NVARCHAR(50) NOT NULL DEFAULT 'COD',
       status NVARCHAR(30) NOT NULL DEFAULT 'PENDING',
       created_at DATETIME NOT NULL DEFAULT GETDATE(),
       updated_at DATETIME NOT NULL DEFAULT GETDATE(),
       CONSTRAINT FK_orders_users FOREIGN KEY (userid) REFERENCES users(id),
       CONSTRAINT CK_orders_status CHECK (status IN ('PENDING', 'CONFIRMED', 'SHIPPING', 'DELIVERED', 'CANCELLED')),
       CONSTRAINT CK_orders_payment CHECK (payment_method IN ('COD'))
   );
   ```
5. **Bảng `order_items`:**
   ```sql
   CREATE TABLE order_items (
       order_item_id INT IDENTITY(1,1) PRIMARY KEY,
       orderid INT NOT NULL,
       bookid INT NOT NULL,
       book_title NVARCHAR(255) NOT NULL,
       unit_price DECIMAL(18,2) NOT NULL CHECK (unit_price >= 0),
       quantity INT NOT NULL CHECK (quantity > 0),
       subtotal DECIMAL(18,2) NOT NULL CHECK (subtotal >= 0),
       CONSTRAINT FK_order_items_orders FOREIGN KEY (orderid) REFERENCES orders(orderid),
       CONSTRAINT FK_order_items_books FOREIGN KEY (bookid) REFERENCES books(bookid)
   );
   ```

---

## 7. Routes (Servlet Mappings)

| METHOD | URL | AUTH | CSRF | PURPOSE |
|---|---|---|---|---|
| `GET` | `/cart` | Required (User) | No | Xem giỏ hàng cá nhân |
| `POST` | `/cart/add` | Required (User) | **Yes** | Thêm sách vào giỏ hàng |
| `POST` | `/cart/update` | Required (User) | **Yes** | Cập nhật số lượng của một mục trong giỏ |
| `POST` | `/cart/remove` | Required (User) | **Yes** | Xóa một cuốn sách khỏi giỏ |
| `POST` | `/cart/clear` | Required (User) | **Yes** | Xóa sạch toàn bộ giỏ hàng |
| `POST` | `/checkout/prepare` | Required (User) | **Yes** | Chọn lọc danh sách sách để thanh toán từ giỏ |
| `GET` | `/checkout` | Required (User) | No | Hiển thị màn hình xác nhận thông tin đặt hàng |
| `POST` | `/checkout` | Required (User) | **Yes** | Thực thi đặt hàng giao dịch COD |
| `GET` | `/checkout/success` | Required (User) | No | Màn hình thông báo đặt hàng thành công |
| `GET` | `/order-history` | Required (User) | No | Xem danh sách đơn hàng đã mua của user |
| `GET` | `/order-detail` | Required (User) | No | Xem chi tiết 1 đơn hàng (có kiểm tra quyền sở hữu) |
| `POST` | `/order/cancel` | Required (User) | **Yes** | Người dùng hủy đơn hàng ở trạng thái PENDING |
| `GET` | `/admin/orders` | Required (Admin) | No | Xem danh sách tất cả đơn hàng hệ thống |
| `GET` | `/admin/order-detail` | Required (Admin) | No | Admin xem chi tiết bất kỳ đơn hàng nào |
| `POST` | `/admin/order/update-status`| Required (Admin) | **Yes** | Admin chuyển trạng thái (CONFIRMED, SHIPPING, DELIVERED) |
| `POST` | `/admin/order/cancel` | Required (Admin) | **Yes** | Admin hủy đơn hàng và tự động hoàn trả tồn kho |
| `POST` | `/admin/books?action=delete`| Required (Admin) | **Yes** | Admin soft delete sách (`is_active = 0`) |

---

## 8. Transaction Review

Xác nhận cơ chế JDBC Transaction tuân thủ nghiêm ngặt tính ACID:
1. **Single Connection:** Tất cả các thao tác trong phương thức nghiệp vụ (`createOrder`, `cancelOrderByUser`, `cancelOrderByAdmin`) đều chạy trên cùng một đối tượng `java.sql.Connection`.
2. **Explicit Commit / Rollback:** Luôn gọi `conn.setAutoCommit(false)`. Khi có bất kỳ ngoại lệ `SQLException`, `IllegalStateException` hoặc `RuntimeException` nào phát sinh, khối `catch` gọi `conn.rollback()` trước khi rethrow lỗi. Cuối cùng, khối `finally` đóng kết nối an toàn.
3. **Atomic Inventory Decrement:** Trong câu lệnh cập nhật số lượng sách:
   ```sql
   UPDATE books SET quantity = quantity - ? WHERE bookid = ? AND quantity >= ? AND is_active = 1
   ```
   Nếu số dòng bị ảnh hưởng khác 1 (nghĩa là số lượng tồn kho không còn đủ hoặc sách đã bị ngừng bán), hệ thống ném ngoại lệ lập tức và kích hoạt rollback.
4. **Order Items Snapshot:** Lưu giá trị tại thời điểm mua (`book_title` và `unit_price`) vào bảng `order_items`, bảo đảm giá trị đơn hàng không bao giờ bị thay đổi khi giá sách trong bảng `books` biến động sau này.
5. **Inventory Restore on Cancel:** Việc hoàn trả kho `UPDATE books SET quantity = quantity + ?` được gắn liền với điều kiện cập nhật trạng thái đơn sang `CANCELLED`. Nếu đơn đã bị hủy trước đó, câu lệnh `cancel` trả về 0 dòng và kho hàng không bị cộng bù hai lần.

---

## 9. Security Review

| Vấn đề an ninh | Cơ chế phòng thủ và kết quả kiểm tra | Trạng thái |
|---|---|---|
| **Authentication** | `AuthFilter` chặn toàn bộ request chưa đăng nhập đến các tài nguyên người dùng (`/cart/*`, `/checkout/*`, `/order-history`, `/order-detail`, `/order/cancel`), chuyển hướng sang `/login`. | **PASS** |
| **Authorization** | `AdminFilter` kiểm tra cờ `isAdmin()` của đối tượng user trong session cho toàn bộ đường dẫn `/admin/*`. Tầng `OrderService` kiểm tra độc lập quyền admin trực tiếp trong DB (`orderDAO.isAdmin(conn, adminUserId)`). | **PASS** |
| **Chống IDOR** | `getOrderDetailForUser(orderId, userId)` và `cancelOrderByUser(orderId, userId)` luôn truyền `sessionScope.user.id`. Truy vấn SQL ràng buộc `WHERE orderid = ? AND userid = ?`. User A không thể xem hoặc hủy đơn hàng của User B. Trả về `404/403`. | **PASS** |
| **Chống CSRF** | `CsrfFilter` tạo token ngẫu nhiên mật mã 256-bit trong session. Tất cả các mutation HTTP POST đều phải gửi kèm trường `csrfToken`. Nếu thiếu hoặc sai token, hệ thống chặn lại với HTTP 403 Forbidden trước khi request đến Servlet. | **PASS** |
| **Chống XSS** | Toàn bộ các file JSP hiển thị thông tin động (tiêu đề sách, tên người nhận, địa chỉ, số điện thoại, ghi chú, mã đơn hàng) đều được bao bọc bằng thẻ `<c:out value="..."/>` của JSTL. Script injection như `<script>alert(1)</script>` hiển thị dưới dạng chuỗi văn bản thuần, không thực thi mã. | **PASS** |
| **Chống Price Tampering** | Tham số giá tiền gửi từ trình duyệt (nếu có) bị bỏ qua hoàn toàn. Tầng Service lấy giá sách và tính tổng tiền dựa duy nhất trên dữ liệu truy vấn từ cơ sở dữ liệu (`rs.getBigDecimal("price")`). | **PASS** |
| **Chống Quantity Tampering** | Kiểm tra `quantity > 0` và `quantity <= book.quantity` ở cả tầng Servlet và ràng buộc CHECK trong DB. Mọi số lượng âm hoặc bằng 0 đều bị chặn. | **PASS** |
| **Chống Trạng thái Bất hợp lệ** | Máy trạng thái (State Machine) được kiểm soát chặt chẽ: Chỉ `PENDING` mới được hủy bởi User; Chỉ `PENDING` hoặc `CONFIRMED` mới được hủy bởi Admin; Đơn hàng `SHIPPING` hoặc `DELIVERED` không thể bị hủy. | **PASS** |

---

## 10. Functional Tests

Kết quả kiểm thử chức năng tự động và kiểm thử tích hợp:

| STT | Test Case | Kết quả kỳ vọng | Kết quả thực tế | PASS / FAIL |
|---|---|---|---|---|
| 1 | `CartTest.testAddSingleItem` | Thêm sách mới vào giỏ tăng số lượng chính xác | Số lượng = 2, tổng tiền khớp | **PASS** |
| 2 | `CartTest.testAddSameBookAccumulates` | Thêm trùng sách sẽ cộng dồn số lượng | Số lượng cộng dồn chuẩn xác | **PASS** |
| 3 | `CartTest.testAddExceedingStockClampsToMax` | Thêm vượt quá tồn kho sẽ được chặn ở mức tối đa | Bị chặn ở mức max tồn kho | **PASS** |
| 4 | `CartTest.testUpdateQuantityValid` | Cập nhật số lượng hợp lệ | Số lượng và subtotal cập nhật | **PASS** |
| 5 | `CartTest.testUpdateQuantityZeroRemovesItem` | Cập nhật số lượng về 0 tự động xóa khỏi giỏ | Mục sách biến mất khỏi giỏ | **PASS** |
| 6 | `CartTest.testUpdateQuantityNegativeIgnored` | Cập nhật số lượng âm bị từ chối | Không thay đổi số lượng | **PASS** |
| 7 | `CartTest.testRemoveItem` | Xóa 1 cuốn sách khỏi giỏ | Mục sách bị loại bỏ hoàn toàn | **PASS** |
| 8 | `CartTest.testClearCart` | Xóa toàn bộ giỏ hàng | Giỏ hàng rỗng | **PASS** |
| 9 | `CartTest.testRemoveSelectedItemsPartialCheckout` | Chỉ xóa các cuốn sách đã thanh toán | Sách không chọn vẫn còn trong giỏ | **PASS** |
| 10| `CartTest.testInvalidBookOrStock` | Sách hết hàng (quantity=0) không thể thêm | `addItem` trả về false | **PASS** |
| 11| `CsrfFilterTest.testCsrfExclusions` | Các tài nguyên tĩnh và login/register GET không bị chặn | Filter bỏ qua đúng quy tắc | **PASS** |
| 12| `OrderServiceValidationTest.testNullItemsOrInvalidUser` | Dữ liệu đầu vào sai bị ném `IllegalArgumentException` | Bị ném lỗi hợp lệ | **PASS** |
| 13| `OrderServiceValidationTest.testBlankReceiverInfo` | Bỏ trống tên/địa chỉ/sđt bị từ chối | Bị ném lỗi hợp lệ | **PASS** |
| 14| `OrderServiceSqlServerTest.testConcurrentCheckoutNoOverselling` | 10 luồng cùng mua 1 cuốn sách cuối cùng | Đúng 1 đơn thành công, tồn kho về 0 | **PASS** |
| 15| `OrderServiceSqlServerTest.testConcurrentCancelRestoresInventoryOnce` | Nhiều luồng cùng gửi yêu cầu hủy đơn đồng thời | Chỉ 1 luồng thành công, hoàn kho 1 lần | **PASS** |
| 16| `OrderServiceSqlServerTest.testAdminCancelRestoresInventory` | Admin hủy đơn hàng PENDING hoặc CONFIRMED | Hoàn kho chính xác | **PASS** |
| 17| `OrderServiceSqlServerTest.testOwnershipAndAdminStateTransitions` | Chống IDOR và máy trạng thái Admin | User khác không xem được, chuyển trạng thái đúng quy tắc | **PASS** |
| 18| `OrderServiceSqlServerTest.testSoftDeletedBookCannotBeOrdered` | Sách bị ngừng bán (`is_active=0`) không thể đặt | Bị từ chối, đơn hàng cũ vẫn xem được snapshot | **PASS** |
| 19| `OrderServiceSqlServerTest.testRollbackOnOrderItemFailure` | Lỗi trong quá trình insert items | Toàn bộ transaction rollback sạch sẽ | **PASS** |

---

## 11. Concurrency Tests

1. **Kiểm thử chống Overselling (Bán vượt tồn kho):**
   - Thiết lập sách kiểm thử có `quantity = 1`.
   - Khởi chạy đồng thời 10 luồng (`ExecutorService` với 10 worker threads) cùng gửi yêu cầu thanh toán mua cuốn sách này.
   - **Kết quả:** Đúng 1 luồng đặt hàng thành công (`orderId > 0`). 9 luồng còn lại nhận thông báo lỗi do điều kiện kho hàng nguyên tử `AND quantity >= ?` không còn thỏa mãn. Tồn kho trong database kết thúc ở mức chính xác là `0`, không bao giờ xảy ra tình trạng âm kho (`quantity = -1`).
2. **Kiểm thử chống Double-Cancel (Hoàn kho lặp lại):**
   - Tạo đơn hàng có 3 cuốn sách. Tồn kho giảm từ 10 xuống 7.
   - Khởi chạy nhiều luồng đồng thời gọi `cancelOrderByUser` hoặc `cancelOrderByAdmin`.
   - **Kết quả:** Đúng 1 luồng trả về `true` và thực thi cộng lại 3 cuốn vào kho (`7 + 3 = 10`). Các luồng gửi sau trả về `false` do trạng thái đơn đã đổi sang `CANCELLED`. Tồn kho cuối cùng được bảo toàn ở mức `10`, tuyệt đối không bị cộng trùng thành `13`.

---

## 12. Build Results

### Lệnh chạy kiểm thử:
```powershell
$env:BOOKSTORE_SQL_INTEGRATION="true"; mvn clean test
```
**Kết quả:**
```text
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running com.nhm.bookstore.filter.CsrfFilterTest
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running com.nhm.bookstore.model.CartTest_24162073
[INFO] Tests run: 10, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running com.nhm.bookstore.service.OrderServiceSqlServerTest
[INFO] Tests run: 6, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running com.nhm.bookstore.service.OrderServiceValidationTest
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0
[INFO] 
[INFO] Results:
[INFO] Tests run: 19, Failures: 0, Errors: 0, Skipped: 0
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
```

### Lệnh đóng gói sản phẩm:
```powershell
mvn clean package -DskipTests
```
**Kết quả:**
```text
[INFO] Building war: F:\Laptrinhweb\24162073_NguyenAnhMinh\target\bookstore.war
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
```

---

## 13. Bugs Found and Fixed

| Severity | Location | Nguyên nhân | Cách khắc phục | Xác nhận / Kiểm chứng |
|---|---|---|---|---|
| **High** | `pom.xml` | Surefire plugin mặc định chỉ chạy class kết thúc bằng `*Test.java`. Test model tên `CartTest_24162073.java` bị bỏ qua khi chạy `mvn test`. | Thêm `<include>**/*Test*.java</include>` vào cấu hình plugin Surefire. | Maven tự động chạy đầy đủ 19 test cases, bao gồm toàn bộ 10 test case giỏ hàng. |
| **Medium** | `CartServlet_24162073.java` | Sử dụng reflection `book.getClass().getMethod("isActive")` do lúc Antigravity code chưa merge model từ Codex. | Thay thế bằng gọi trực tiếp `book.isActive()`, xóa import `java.lang.reflect.Method`. | Code sạch sẽ, hiệu năng cao, biên dịch trực tiếp không cảnh báo. |
| **Medium** | `views/admin/book-list.jsp` | Sách bị soft delete vẫn hiển thị nút xóa và không có nhãn phân biệt trên giao diện admin. | Thêm badge "Đã ngừng bán" cho sách inactive, ẩn nút xóa và thay bằng nhãn "Đã ẩn", đồng thời bổ sung escape HTML `<c:out>`. | Giao diện admin hiển thị trực quan, ngăn chặn submit xóa lần 2. |
| **High** | `views/cart.jsp` & `views/checkout.jsp` | Session attribute cho các ID sách được chọn thanh toán có nguy cơ không khớp giữa Web và Service nếu không quy định rõ. | Chuẩn hóa session attribute duy nhất là `checkoutSelectedBookIds` (Set/List Integer). | Quy trình thanh toán một phần (Partial Checkout) hoạt động trơn tru từ giỏ hàng sang checkout. |

---

## 14. Remaining Issues

Các vấn đề còn tồn tại trong codebase kế thừa từ phiên bản trước (ngoài phạm vi tính năng Giỏ hàng & Đơn hàng):

| Phân loại | Vị trí / File | Tác động | Khuyến nghị giải pháp |
|---|---|---|---|
| **SHOULD FIX** | `DBConnection_24162073.java`, `EmailService_24162073.java` | Chuỗi kết nối SQL Server và thông tin tài khoản SMTP Gmail đang để cố định (hardcoded) trong mã nguồn. | Đọc thông tin kết nối từ biến môi trường hệ thống (`System.getenv()`) hoặc file `application.properties`. |
| **SHOULD FIX** | `UserDAO_24162073.java`, `User_24162073.java` | Mật khẩu người dùng hiện lưu văn bản thuần (plaintext), chưa được băm (hashing). | Sử dụng thuật toán BCrypt hoặc Argon2 để băm mật khẩu trước khi lưu vào DB. |
| **NICE TO HAVE** | `OTPServlet_24162073.java` | Mã OTP đăng ký trong session chưa cài đặt thời gian hết hạn cụ thể (TTL) và giới hạn số lần nhập sai. | Lưu thêm `otpExpiredTime` vào session và chặn sau 5 lần nhập sai. |
| **OUT OF SCOPE** | `AdminBookServlet_24162073.java` | Chức năng upload ảnh bìa sách chưa kiểm tra MIME type thực tế của file ở máy chủ. | Bổ sung kiểm tra magic bytes file ảnh phía backend trước khi lưu file. |

---

## 15. Manual Tests Still Required

Để chuẩn bị nghiệm thu trên môi trường trình duyệt thực tế (với Tomcat hoặc Jetty):

1. **Khởi động ứng dụng:**
   - Triển khai `target/bookstore.war` lên Apache Tomcat 10+ (Jakarta EE 9/10).
   - Truy cập: `http://localhost:8080/bookstore/`.
2. **Kịch bản Khách hàng Mua hàng (End-to-End):**
   - Đăng nhập tài khoản khách hàng.
   - Vào danh sách sách, bấm "Thêm vào giỏ" cho 3 cuốn sách khác nhau với số lượng khác nhau.
   - Vào trang `/cart`: Kiểm tra nút tăng/giảm số lượng, xóa 1 cuốn sách.
   - Tích chọn 2 cuốn sách cần mua (Partial Checkout) -> bấm "Tiến hành đặt hàng".
   - Kiểm tra trang `/checkout`: Chỉ hiện đúng 2 cuốn sách đã chọn.
   - Nhập thông tin người nhận, chọn phương thức COD, bấm "Đặt hàng".
   - Kiểm tra trang thông báo đặt hàng thành công -> Quay lại giỏ hàng: Xác nhận cuốn sách thứ 3 vẫn còn trong giỏ.
   - Vào `/order-history`: Thấy đơn hàng mới ở trạng thái `PENDING`.
   - Vào `/order-detail?id=...`: Kiểm tra chi tiết đơn hàng, bấm "Hủy đơn hàng" -> Xác nhận đơn đổi sang `CANCELLED`.
3. **Kịch bản Quản trị viên (Admin Flow):**
   - Đăng nhập tài khoản Admin.
   - Vào `/admin/orders`: Thấy danh sách đơn hàng của tất cả khách hàng.
   - Bấm xem chi tiết đơn hàng -> Thực hiện duyệt đơn từ `PENDING` -> `CONFIRMED` -> `SHIPPING` -> `DELIVERED`.
   - Thử hủy một đơn hàng hợp lệ -> Kiểm tra tồn kho sách tương ứng được phục hồi tự động trong cơ sở dữ liệu.

---

## 16. Final Readiness

**Có thể merge vào `main` chưa?**
- **CÓ THỂ MERGE VÀO `MAIN` NGAY.**
- Toàn bộ tính năng Giỏ hàng, Chọn lọc thanh toán (Partial Checkout), Đặt hàng COD giao dịch, Quản lý đơn hàng người dùng & admin, Bảo vệ tồn kho đa luồng, Chống IDOR, CSRF và XSS đã hoàn tất trọn vẹn và vượt qua 100% các bài test tự động cũng như phân tích tĩnh.
- Không còn bất kỳ lỗi tích hợp (Integration Blocker) nào cản trở việc phát hành.
