<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Lịch sử đơn hàng - BookStore</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/style.css?v=5" rel="stylesheet">
</head>
<body>
<div class="container my-4">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h2 class="page-title mb-0">📜 Lịch Sử Đơn Hàng</h2>
        <a href="${pageContext.request.contextPath}/products" class="btn btn-outline-maroon btn-sm">
            &laquo; Tiếp tục mua sắm
        </a>
    </div>

    <!-- Alert Messages -->
    <c:if test="${not empty sessionScope.orderSuccess}">
        <div class="alert alert-success alert-dismissible fade show" role="alert">
            <strong>Thành công!</strong> <c:out value="${sessionScope.orderSuccess}"/>
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
        <c:remove var="orderSuccess" scope="session"/>
    </c:if>

    <c:if test="${not empty sessionScope.orderError}">
        <div class="alert alert-danger alert-dismissible fade show" role="alert">
            <strong>Lưu ý:</strong> <c:out value="${sessionScope.orderError}"/>
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
        <c:remove var="orderError" scope="session"/>
    </c:if>

    <c:if test="${not empty error}">
        <div class="alert alert-danger alert-dismissible fade show" role="alert">
            <strong>Lỗi:</strong> <c:out value="${error}"/>
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    </c:if>

    <c:choose>
        <c:when test="${empty orders}">
            <div class="card p-5 text-center my-4 border-0 shadow-sm">
                <div class="mb-3" style="font-size: 4rem;">📦</div>
                <h4 class="text-muted">Bạn chưa có đơn hàng nào!</h4>
                <p class="text-secondary">Hãy lựa chọn những cuốn sách yêu thích và trải nghiệm dịch vụ của BookStore nhé.</p>
                <div class="mt-3">
                    <a href="${pageContext.request.contextPath}/products" class="btn btn-maroon px-4 py-2">
                        Mua sắm ngay
                    </a>
                </div>
            </div>
        </c:when>

        <c:otherwise>
            <div class="card border-0 shadow-sm">
                <div class="table-responsive">
                    <table class="table table-hover align-middle mb-0">
                        <thead class="table-light">
                        <tr>
                            <th>Mã đơn</th>
                            <th>Ngày đặt</th>
                            <th>Người nhận</th>
                            <th>Thanh toán</th>
                            <th>Tổng tiền</th>
                            <th>Trạng thái</th>
                            <th class="text-center">Thao tác</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach var="order" items="${orders}">
                            <tr>
                                <td>
                                    <a href="${pageContext.request.contextPath}/order-detail?id=${order.orderId}"
                                       class="fw-bold text-maroon text-decoration-none">
                                        #${order.orderId}
                                    </a>
                                </td>
                                <td>
                                    <fmt:formatDate value="${order.createdAt}" pattern="dd/MM/yyyy HH:mm"/>
                                </td>
                                <td>
                                    <div><c:out value="${order.receiverName}"/></div>
                                    <small class="text-muted"><c:out value="${order.receiverPhone}"/></small>
                                </td>
                                <td>
                                    <span class="badge bg-light text-dark border">
                                        ${order.paymentMethod}
                                    </span>
                                </td>
                                <td>
                                    <span class="fw-bold text-danger">
                                        <fmt:formatNumber value="${order.totalAmount}" type="currency" currencySymbol="₫" maxFractionDigits="0"/>
                                    </span>
                                </td>
                                <td>
                                    <c:choose>
                                        <c:when test="${order.status == 'PENDING'}">
                                            <span class="badge bg-warning text-dark">Chờ xác nhận</span>
                                        </c:when>
                                        <c:when test="${order.status == 'CONFIRMED'}">
                                            <span class="badge bg-primary">Đã xác nhận</span>
                                        </c:when>
                                        <c:when test="${order.status == 'SHIPPING'}">
                                            <span class="badge bg-info text-dark">Đang giao hàng</span>
                                        </c:when>
                                        <c:when test="${order.status == 'DELIVERED'}">
                                            <span class="badge bg-success">Đã giao hàng</span>
                                        </c:when>
                                        <c:when test="${order.status == 'CANCELLED'}">
                                            <span class="badge bg-danger">Đã hủy</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="badge bg-secondary">${order.status}</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td class="text-center">
                                    <div class="d-flex justify-content-center gap-2">
                                        <a href="${pageContext.request.contextPath}/order-detail?id=${order.orderId}"
                                           class="btn btn-outline-primary btn-sm">
                                            Chi tiết
                                        </a>

                                        <c:if test="${order.status == 'PENDING'}">
                                            <form action="${pageContext.request.contextPath}/order/cancel"
                                                  method="POST"
                                                  class="d-inline"
                                                  onsubmit="return confirm('Bạn có chắc chắn muốn hủy đơn hàng #${order.orderId}? Tồn kho sách sẽ được hoàn trả.');">
                                                <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                                                <input type="hidden" name="orderId" value="${order.orderId}">
                                                <button type="submit" class="btn btn-outline-danger btn-sm">
                                                    Hủy đơn
                                                </button>
                                            </form>
                                        </c:if>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>
        </c:otherwise>
    </c:choose>
</div>
</body>
</html>
