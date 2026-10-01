<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Quản lý đơn hàng #${order.orderId} - Admin</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/style.css?v=5" rel="stylesheet">
    <style>
        .admin-card {
            background: #fff;
            border-radius: 12px;
            border: 1px solid var(--border, #E8C3CB);
            padding: 25px;
            box-shadow: 0 4px 15px rgba(139, 26, 43, 0.08);
            margin-bottom: 25px;
        }
    </style>
</head>
<body>
<div class="container my-4">
    <div class="d-flex justify-content-between align-items-center mb-3">
        <h2 class="page-title mb-0">🔧 Chi Tiết Đơn Hàng #${order.orderId}</h2>
        <a href="${pageContext.request.contextPath}/admin/orders" class="btn btn-outline-secondary btn-sm">
            &laquo; Quay lại danh sách đơn hàng
        </a>
    </div>

    <!-- Alert Messages -->
    <c:if test="${not empty sessionScope.adminOrderSuccess}">
        <div class="alert alert-success alert-dismissible fade show" role="alert">
            <strong>Thành công!</strong> <c:out value="${sessionScope.adminOrderSuccess}"/>
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
        <c:remove var="adminOrderSuccess" scope="session"/>
    </c:if>

    <c:if test="${not empty sessionScope.adminOrderError}">
        <div class="alert alert-danger alert-dismissible fade show" role="alert">
            <strong>Lưu ý:</strong> <c:out value="${sessionScope.adminOrderError}"/>
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
        <c:remove var="adminOrderError" scope="session"/>
    </c:if>

    <!-- Status & Info Banner -->
    <div class="card border-0 shadow-sm mb-4">
        <div class="card-body p-4 d-flex flex-wrap justify-content-between align-items-center">
            <div>
                <span class="text-muted d-block mb-1">Mã đơn hàng: <strong class="text-dark">#${order.orderId}</strong> | Khách hàng: <strong>User #${order.userId}</strong></span>
                <span class="text-muted">Ngày đặt: 
                    <strong><fmt:formatDate value="${order.createdAt}" pattern="dd/MM/yyyy HH:mm:ss"/></strong>
                </span>
                <c:if test="${not empty order.updatedAt}">
                    <span class="text-muted ms-3">Cập nhật lần cuối: 
                        <strong><fmt:formatDate value="${order.updatedAt}" pattern="dd/MM/yyyy HH:mm:ss"/></strong>
                    </span>
                </c:if>
            </div>
            <div class="mt-2 mt-md-0">
                <span class="text-muted me-2">Trạng thái hiện tại:</span>
                <c:choose>
                    <c:when test="${order.status == 'PENDING'}">
                        <span class="badge bg-warning text-dark fs-6 px-3 py-2">Chờ xác nhận (PENDING)</span>
                    </c:when>
                    <c:when test="${order.status == 'CONFIRMED'}">
                        <span class="badge bg-primary fs-6 px-3 py-2">Đã xác nhận (CONFIRMED)</span>
                    </c:when>
                    <c:when test="${order.status == 'SHIPPING'}">
                        <span class="badge bg-info text-dark fs-6 px-3 py-2">Đang giao hàng (SHIPPING)</span>
                    </c:when>
                    <c:when test="${order.status == 'DELIVERED'}">
                        <span class="badge bg-success fs-6 px-3 py-2">Đã giao hàng (DELIVERED)</span>
                    </c:when>
                    <c:when test="${order.status == 'CANCELLED'}">
                        <span class="badge bg-danger fs-6 px-3 py-2">Đã hủy (CANCELLED)</span>
                    </c:when>
                    <c:otherwise>
                        <span class="badge bg-secondary fs-6 px-3 py-2">${order.status}</span>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>

    <div class="row g-4">
        <!-- Left: Customer Information & State Machine Operations -->
        <div class="col-lg-5">
            <div class="admin-card">
                <h5 class="fw-bold text-maroon mb-3 border-bottom pb-2">
                    📍 Thông Tin Khách Hàng & Giao Nhận
                </h5>
                <p class="mb-2"><strong>Mã khách hàng:</strong> User #${order.userId}</p>
                <p class="mb-2"><strong>Người nhận:</strong> <c:out value="${order.receiverName}"/></p>
                <p class="mb-2"><strong>Số điện thoại:</strong> <c:out value="${order.receiverPhone}"/></p>
                <p class="mb-2"><strong>Email:</strong> <c:out value="${order.receiverEmail}"/></p>
                <p class="mb-2"><strong>Địa chỉ giao hàng:</strong> <c:out value="${order.shippingAddress}"/></p>
                <p class="mb-2">
                    <strong>Ghi chú:</strong>
                    <c:choose>
                        <c:when test="${not empty order.note}">
                            <c:out value="${order.note}"/>
                        </c:when>
                        <c:otherwise>
                            <span class="text-muted fst-italic">Không có ghi chú</span>
                        </c:otherwise>
                    </c:choose>
                </p>
                <p class="mb-0">
                    <strong>Phương thức thanh toán:</strong>
                    <span class="badge bg-light text-dark border">${order.paymentMethod}</span>
                </p>
            </div>

            <!-- State Machine Actions for Admin -->
            <div class="admin-card">
                <h5 class="fw-bold text-maroon mb-3 border-bottom pb-2">
                    ⚙️ Xử Lý Trạng Thái Đơn Hàng
                </h5>

                <c:choose>
                    <c:when test="${order.status == 'PENDING'}">
                        <p class="text-muted small">Đơn hàng mới tạo đang chờ duyệt. Chọn thao tác hợp lệ:</p>
                        
                        <div class="d-grid gap-2">
                            <!-- Advance to CONFIRMED -->
                            <form action="${pageContext.request.contextPath}/admin/order/update-status" method="POST">
                                <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                                <input type="hidden" name="orderId" value="${order.orderId}">
                                <input type="hidden" name="nextStatus" value="CONFIRMED">
                                <button type="submit" class="btn btn-primary w-100 py-2 fw-semibold">
                                    ✓ Xác Nhận Đơn Hàng (PENDING &rarr; CONFIRMED)
                                </button>
                            </form>

                            <!-- Cancel Order -->
                            <form action="${pageContext.request.contextPath}/admin/order/cancel"
                                  method="POST"
                                  onsubmit="return confirm('Bạn có chắc muốn hủy đơn hàng #${order.orderId}? Toàn bộ số lượng tồn kho sách sẽ được hoàn trả.');">
                                <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                                <input type="hidden" name="orderId" value="${order.orderId}">
                                <button type="submit" class="btn btn-outline-danger w-100 py-2 fw-semibold">
                                    ✕ Hủy Đơn Hàng (Hoàn Tồn Kho)
                                </button>
                            </form>
                        </div>
                    </c:when>

                    <c:when test="${order.status == 'CONFIRMED'}">
                        <p class="text-muted small">Đơn hàng đã được xác nhận. Chọn thao tác hợp lệ:</p>
                        
                        <div class="d-grid gap-2">
                            <!-- Advance to SHIPPING -->
                            <form action="${pageContext.request.contextPath}/admin/order/update-status" method="POST">
                                <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                                <input type="hidden" name="orderId" value="${order.orderId}">
                                <input type="hidden" name="nextStatus" value="SHIPPING">
                                <button type="submit" class="btn btn-info text-dark w-100 py-2 fw-semibold">
                                    🚚 Chuyển Sang Giao Hàng (CONFIRMED &rarr; SHIPPING)
                                </button>
                            </form>

                            <!-- Cancel Order -->
                            <form action="${pageContext.request.contextPath}/admin/order/cancel"
                                  method="POST"
                                  onsubmit="return confirm('Bạn có chắc muốn hủy đơn hàng #${order.orderId}? Toàn bộ số lượng tồn kho sách sẽ được hoàn trả.');">
                                <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                                <input type="hidden" name="orderId" value="${order.orderId}">
                                <button type="submit" class="btn btn-outline-danger w-100 py-2 fw-semibold">
                                    ✕ Hủy Đơn Hàng (Hoàn Tồn Kho)
                                </button>
                            </form>
                        </div>
                    </c:when>

                    <c:when test="${order.status == 'SHIPPING'}">
                        <p class="text-muted small">Đơn hàng đang trên đường giao tới khách hàng. Khi giao thành công, bấm xác nhận:</p>
                        
                        <!-- Advance to DELIVERED -->
                        <form action="${pageContext.request.contextPath}/admin/order/update-status" method="POST">
                            <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                            <input type="hidden" name="orderId" value="${order.orderId}">
                            <input type="hidden" name="nextStatus" value="DELIVERED">
                            <button type="submit" class="btn btn-success w-100 py-2 fw-semibold">
                                🎉 Đã Giao Thành Công (SHIPPING &rarr; DELIVERED)
                            </button>
                        </form>
                        <p class="text-muted small mt-2 mb-0">
                            * Lưu ý: Đơn hàng đang vận chuyển không được hủy trực tiếp từ giao diện.
                        </p>
                    </c:when>

                    <c:when test="${order.status == 'DELIVERED'}">
                        <div class="alert alert-success mb-0 py-3">
                            <h6 class="fw-bold mb-1">Đơn hàng đã hoàn thành!</h6>
                            <small>Đơn hàng đã giao thành công tới khách hàng. Trạng thái kết thúc, không thể thay đổi.</small>
                        </div>
                    </c:when>

                    <c:when test="${order.status == 'CANCELLED'}">
                        <div class="alert alert-secondary mb-0 py-3">
                            <h6 class="fw-bold mb-1">Đơn hàng đã bị hủy!</h6>
                            <small>Đơn hàng đã hủy và tồn kho đã được hoàn trả. Trạng thái kết thúc, không thể thay đổi.</small>
                        </div>
                    </c:when>
                </c:choose>
            </div>
        </div>

        <!-- Right: Ordered Items Details -->
        <div class="col-lg-7">
            <div class="admin-card">
                <h5 class="fw-bold text-maroon mb-3 border-bottom pb-2">
                    📚 Danh Sách Sản Phẩm Đặt Mua
                </h5>

                <div class="table-responsive mb-3">
                    <table class="table align-middle mb-0">
                        <thead class="table-light">
                        <tr>
                            <th>Tên sách (Snapshot lịch sử)</th>
                            <th class="text-center" style="width: 100px;">Đơn giá</th>
                            <th class="text-center" style="width: 80px;">SL</th>
                            <th class="text-end" style="width: 130px;">Thành tiền</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach var="item" items="${order.items}">
                            <tr>
                                <td>
                                    <h6 class="mb-0 fs-6">
                                        <c:out value="${item.bookTitle}"/>
                                    </h6>
                                    <small class="text-muted">Mã sách gốc: ${item.bookId}</small>
                                </td>
                                <td class="text-center">
                                    <fmt:formatNumber value="${item.unitPrice}" type="currency" currencySymbol="₫" maxFractionDigits="0"/>
                                </td>
                                <td class="text-center fw-bold">
                                    ${item.quantity}
                                </td>
                                <td class="text-end fw-bold text-danger">
                                    <fmt:formatNumber value="${item.subtotal}" type="currency" currencySymbol="₫" maxFractionDigits="0"/>
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>

                <div class="border-top pt-3">
                    <div class="d-flex justify-content-between mb-2">
                        <span class="text-muted">Tạm tính sản phẩm:</span>
                        <span class="fw-semibold">
                            <fmt:formatNumber value="${order.totalAmount}" type="currency" currencySymbol="₫" maxFractionDigits="0"/>
                        </span>
                    </div>
                    <div class="d-flex justify-content-between mb-3">
                        <span class="text-muted">Phí giao hàng:</span>
                        <span class="text-success fw-bold">0 ₫</span>
                    </div>
                    <div class="d-flex justify-content-between align-items-baseline pt-2 border-top">
                        <span class="fs-5 fw-bold">Tổng doanh thu đơn hàng:</span>
                        <span class="fs-3 fw-bold text-danger">
                            <fmt:formatNumber value="${order.totalAmount}" type="currency" currencySymbol="₫" maxFractionDigits="0"/>
                        </span>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
</body>
</html>
