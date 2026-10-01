<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Quản lý đơn hàng - Admin</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/style.css?v=5" rel="stylesheet">
</head>
<body>
<div class="container my-4">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h2 class="page-title mb-0">📦 Quản Lý Đơn Hàng</h2>
        <span class="badge bg-maroon fs-6">${orders.size()} đơn hàng</span>
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

    <c:if test="${not empty error}">
        <div class="alert alert-danger alert-dismissible fade show" role="alert">
            <strong>Lỗi:</strong> <c:out value="${error}"/>
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    </c:if>

    <!-- Filter Bar (Real backend filtering) -->
    <div class="card border-0 shadow-sm mb-4">
        <div class="card-body p-3">
            <div class="d-flex flex-wrap align-items-center gap-2">
                <span class="fw-bold me-2 text-muted">Lọc theo trạng thái:</span>
                <a href="${pageContext.request.contextPath}/admin/orders?status=ALL"
                   class="btn btn-sm ${currentStatus == 'ALL' ? 'btn-maroon' : 'btn-outline-secondary'}">
                    Tất cả
                </a>
                <c:forEach var="st" items="${statuses}">
                    <a href="${pageContext.request.contextPath}/admin/orders?status=${st.name()}"
                       class="btn btn-sm ${currentStatus == st.name() ? 'btn-maroon' : 'btn-outline-secondary'}">
                        <c:choose>
                            <c:when test="${st == 'PENDING'}">Chờ xác nhận</c:when>
                            <c:when test="${st == 'CONFIRMED'}">Đã xác nhận</c:when>
                            <c:when test="${st == 'SHIPPING'}">Đang giao</c:when>
                            <c:when test="${st == 'DELIVERED'}">Đã giao</c:when>
                            <c:when test="${st == 'CANCELLED'}">Đã hủy</c:when>
                            <c:otherwise>${st}</c:otherwise>
                        </c:choose>
                    </a>
                </c:forEach>
            </div>
        </div>
    </div>

    <!-- Orders Table -->
    <div class="card border-0 shadow-sm admin-table">
        <c:choose>
            <c:when test="${empty orders}">
                <div class="text-center py-5 text-muted">
                    <p class="fs-5 mb-0">Không có đơn hàng nào thuộc trạng thái này.</p>
                </div>
            </c:when>
            <c:otherwise>
                <div class="table-responsive">
                    <table class="table table-hover align-middle mb-0">
                        <thead class="table-dark">
                        <tr>
                            <th>Mã đơn</th>
                            <th>User ID</th>
                            <th>Người nhận</th>
                            <th>SĐT</th>
                            <th>Tổng tiền</th>
                            <th>PTTT</th>
                            <th>Trạng thái</th>
                            <th>Ngày đặt</th>
                            <th class="text-center">Thao tác</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach var="order" items="${orders}">
                            <tr>
                                <td>
                                    <a href="${pageContext.request.contextPath}/admin/order-detail?id=${order.orderId}"
                                       class="fw-bold text-maroon text-decoration-none">
                                        #${order.orderId}
                                    </a>
                                </td>
                                <td>
                                    <span class="badge bg-light text-dark border">User #${order.userId}</span>
                                </td>
                                <td>
                                    <c:out value="${order.receiverName}"/>
                                </td>
                                <td>
                                    <c:out value="${order.receiverPhone}"/>
                                </td>
                                <td>
                                    <span class="fw-bold text-danger">
                                        <fmt:formatNumber value="${order.totalAmount}" type="currency" currencySymbol="₫" maxFractionDigits="0"/>
                                    </span>
                                </td>
                                <td>
                                    <span class="badge bg-light text-dark border">${order.paymentMethod}</span>
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
                                <td>
                                    <small class="text-muted">
                                        <fmt:formatDate value="${order.createdAt}" pattern="dd/MM/yyyy HH:mm"/>
                                    </small>
                                </td>
                                <td class="text-center">
                                    <a href="${pageContext.request.contextPath}/admin/order-detail?id=${order.orderId}"
                                       class="btn btn-outline-primary btn-sm">
                                        Chi tiết &raquo;
                                    </a>
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div>
</body>
</html>
