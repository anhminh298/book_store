# Order backend handoff

## A. Files created

- `.gitignore`: ignores generated Maven output under `target/`.
- `sql/20261001_order_backend.sql`: migration for phone, price, soft delete, orders and order items.
- `src/main/java/com/nhm/bookstore/model/Order_24162073.java`
- `src/main/java/com/nhm/bookstore/model/OrderItem_24162073.java`
- `src/main/java/com/nhm/bookstore/model/OrderStatus_24162073.java`
- `src/main/java/com/nhm/bookstore/dao/OrderDAO_24162073.java`
- `src/main/java/com/nhm/bookstore/dao/OrderItemDAO_24162073.java`
- `src/main/java/com/nhm/bookstore/service/OrderService_24162073.java`
- `src/main/java/com/nhm/bookstore/filter/CsrfFilter_24162073.java`
- `src/test/java/com/nhm/bookstore/filter/CsrfFilterTest.java`
- `src/test/java/com/nhm/bookstore/service/OrderServiceValidationTest.java`
- `src/test/java/com/nhm/bookstore/service/OrderServiceSqlServerTest.java`

## B. Files modified

- `pom.xml`: JUnit 5 and Surefire for backend tests.
- `DBConnection_24162073.java`: added throwing `openConnection()` for transaction boundaries.
- `BookDAO_24162073.java`, `BookService_24162073.java`, `Book_24162073.java`: active book filtering, admin access to inactive rows, soft delete and transaction-aware inventory methods.
- `AdminBookServlet_24162073.java`: admin list/edit can see inactive books; delete is POST only.
- `User_24162073.java`, `UserDAO_24162073.java`, `OTPServlet_24162073.java`: phone is a string.
- `AuthFilter_24162073.java`, `AdminFilter_24162073.java`, `web.xml`: explicit filter mappings without duplicate annotations.
- Existing JSPs `login.jsp`, `register.jsp`, `otp.jsp`, `book-detail.jsp`, `admin/book-form.jsp`, `admin/book-edit.jsp`, `admin/book-list.jsp`: added CSRF fields; admin book delete link became a POST form; the existing registration phone field now uses `type="tel"` so leading zeros are preserved.

## C. Database migration

- Existing `bookstore_db`: run `sql/20261001_order_backend.sql` in SSMS or `sqlcmd`.
- Fresh database: run `sql/init_database.sql` first, then the migration. Do not also run `insert_data.sql` because `init_database.sql` already seeds data.
- The migration was exercised on a temporary SQL Server database by integration tests. It was **not applied to the existing `bookstore_db`**.
- Existing `INT` phone values convert to text; any leading zeros previously lost cannot be reconstructed.

## D. Public integration contract

`OrderService_24162073` public methods (all throw `SQLException`):

```java
int createOrder(int userId, Map<Integer,Integer> items,
                String receiverName, String receiverPhone, String receiverEmail,
                String shippingAddress, String note)
List<Order_24162073> getOrdersByUser(int userId)
Order_24162073 getOrderDetailForUser(int orderId, int userId)
Order_24162073 getOrderDetailForAdmin(int adminUserId, int orderId)
List<Order_24162073> getAllOrders(int adminUserId, OrderStatus_24162073 status)
boolean cancelOrderByUser(int orderId, int userId)
boolean cancelOrderByAdmin(int adminUserId, int orderId)
boolean updateOrderStatus(int adminUserId, int orderId, OrderStatus_24162073 next)
```

`getAllOrders(..., null)` returns all statuses. Detail methods return `null` for missing or unauthorized user orders. Cancel and update methods return `false` for an invalid/currently unavailable transition. Admin methods throw `SecurityException` if the supplied user ID is not an admin. Invalid inputs throw `IllegalArgumentException`; an unavailable book or insufficient quantity throws `IllegalStateException`. Web handlers should map these to suitable HTTP responses and preserve the cart on failed checkout.

`Order_24162073` getters:

```text
getOrderId(): int              getUserId(): int
getUserEmail(): String         getUserFullname(): String
getReceiverName(): String      getReceiverPhone(): String
getReceiverEmail(): String     getShippingAddress(): String
getNote(): String              getTotalAmount(): BigDecimal
getPaymentMethod(): String     getStatus(): OrderStatus_24162073
getCreatedAt(): Timestamp      getUpdatedAt(): Timestamp
getItems(): List<OrderItem_24162073>
```

`OrderItem_24162073` getters:

```text
getOrderItemId(): int          getOrderId(): int
getBookId(): int               getBookTitle(): String
getQuantity(): int             getUnitPrice(): BigDecimal
getSubtotal(): BigDecimal
```

`OrderStatus_24162073` values: `PENDING`, `CONFIRMED`, `SHIPPING`, `DELIVERED`, `CANCELLED`.

`Book_24162073`: `getBookid()`, `getQuantity()`, `getPrice()`, `isActive()`. `User_24162073.getPhone()` now returns `String`.

CSRF session attribute: `csrfToken`. CSRF request parameter: `csrfToken`. Add this hidden field to **every new POST form**:

```jsp
<input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
```

## E. Security behavior

- `AuthFilter` in `web.xml` covers `/review`, `/profile`, `/cart`, `/cart/*`, `/checkout`, `/checkout/*`, `/order-history`, `/order-detail`, `/order/cancel`.
- `AdminFilter` covers `/admin/*`.
- `CsrfFilter` covers `/*`: dynamic GET creates a 256-bit session token; every POST requires matching `csrfToken`; missing/wrong tokens return HTTP 403. Multipart form tokens are supported.
- User order detail is queried by both `orderId` and `userId`. Web handlers must pass the user ID from the authenticated session, never from request parameters.
- Admin service methods query `users.is_admin` using the supplied authenticated admin ID.
- Admin book delete now rejects GET with HTTP 405 and accepts POST.

## F. Transaction behavior

- `createOrder` opens one JDBC connection, disables auto-commit, inserts order, reads active books with update locks, performs conditional stock decrement, saves title/price snapshots, updates total, then commits. Any SQL or runtime error rolls back. Book IDs are processed in sorted order to reduce deadlocks.
- Cancel uses a conditional status update first. Only the request changing the status restores inventory, within the same transaction.
- Admin status updates use a conditional previous-status check. Cancellation has a separate method because it restores inventory.
- DAO transaction methods receive a `Connection`, do not close it, and propagate `SQLException`.

## G. Build/test result

- `mvn clean test`: passed; three unit tests, SQL integration tests skipped by default.
- `$env:BOOKSTORE_SQL_INTEGRATION='true'; mvn -q clean test`: passed nine tests (three unit, six SQL Server integration) against a temporary database. The temporary database is dropped.
- `mvn -q package`: passed and produced the WAR.
- The SQL tests cover concurrent checkout of the last copy, concurrent cancel, admin cancellation, IDOR/admin transitions, soft-delete history, and rollback after an order-item insert failure.

## H. Remaining integration requests for Web Layer

1. Do not create duplicate backend models/DAOs/services. Use the public service signatures above.
2. Build the `Map<Integer,Integer>` for checkout solely from selected IDs stored in session and quantities already in the session cart; reject IDs absent from the cart. Do not pass browser-supplied prices or totals.
3. After successful `createOrder`, remove only purchased cart items and clear `checkoutSelectedBookIds`. On failure leave cart and selection intact.
4. Add `csrfToken` to every new POST form: cart mutations, checkout preparation/finalization, user cancel, admin status update/cancel. A missing token receives 403 before a controller runs.
5. For user history/detail/cancel, pass `session.user.id`; for admin list/detail/status/cancel, pass the authenticated admin ID. Handle `null`, `false` and exceptions as described above.
6. Admin book list currently includes inactive rows; display an inactive badge or hide its delete action in the Web Layer if desired.

## I. Git

Branch: `codex/order-backend`. Commit hash is reported in the final response after commit.
