<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Đặt hàng thành công - BookStore</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/style.css?v=5" rel="stylesheet">
    <style>
        .success-card {
            max-width: 650px;
            margin: 40px auto;
            background: #fff;
            border-radius: 16px;
            border: 1px solid var(--border, #E8C3CB);
            box-shadow: 0 8px 30px rgba(139, 26, 43, 0.1);
            padding: 40px;
            text-align: center;
        }
        .success-icon-badge {
            width: 80px;
            height: 80px;
            margin: 0 auto 20px;
            border-radius: 50%;
            background: #e8f5e9;
            color: #2e7d32;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 2.5rem;
        }
    </style>
</head>
<body>
<div class="container my-5">
    <div class="success-card">
        <div class="success-icon-badge">
            ✓
        </div>
        <h2 class="fw-bold text-success mb-2">Đặt Hàng Thành Công!</h2>
        <p class="text-muted mb-4">
            Cảm ơn bạn đã tin tưởng mua sắm tại BookStore. Đơn hàng của bạn đã được ghi nhận vào hệ thống.
        </p>

        <div class="card bg-light border-0 p-3 text-start mb-4">
            <div class="d-flex justify-content-between mb-2">
                <span class="text-muted">Mã đơn hàng:</span>
                <span class="fw-bold fs-5 text-maroon">#${orderId}</span>
            </div>
            <div class="d-flex justify-content-between mb-2">
                <span class="text-muted">Phương thức thanh toán:</span>
                <span class="fw-semibold">Thanh toán khi nhận hàng (COD)</span>
            </div>
            <div class="d-flex justify-content-between mb-2">
                <span class="text-muted">Trạng thái:</span>
                <span class="badge bg-warning text-dark">Chờ xác nhận (PENDING)</span>
            </div>
            <c:if test="${not empty order}">
                <div class="d-flex justify-content-between mb-2">
                    <span class="text-muted">Người nhận:</span>
                    <span class="fw-semibold"><c:out value="${order.receiverName}"/> (${order.receiverPhone})</span>
                </div>
                <div class="d-flex justify-content-between mb-2">
                    <span class="text-muted">Địa chỉ nhận hàng:</span>
                    <span class="fw-semibold text-end" style="max-width: 60%;"><c:out value="${order.shippingAddress}"/></span>
                </div>
                <div class="d-flex justify-content-between pt-2 border-top">
                    <span class="fw-bold">Tổng thanh toán:</span>
                    <span class="fw-bold fs-5 text-danger">
                        <fmt:formatNumber value="${order.totalAmount}" type="currency" currencySymbol="₫" maxFractionDigits="0"/>
                    </span>
                </div>
            </c:if>
        </div>

        <div class="d-flex flex-column flex-sm-row justify-content-center gap-3">
            <a href="${pageContext.request.contextPath}/order-detail?id=${orderId}" class="btn btn-maroon px-4 py-2 fw-semibold">
                📄 Xem Chi Tiết Đơn Hàng
            </a>
            <a href="${pageContext.request.contextPath}/order-history" class="btn btn-outline-maroon px-4 py-2 fw-semibold">
                📋 Lịch Sử Đơn Hàng
            </a>
            <a href="${pageContext.request.contextPath}/products" class="btn btn-light px-4 py-2 border">
                📚 Tiếp Tục Mua Sắm
            </a>
        </div>
    </div>
</div>
</body>
</html>
