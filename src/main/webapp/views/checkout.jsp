<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Thanh toán đơn hàng - BookStore</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/style.css?v=5" rel="stylesheet">
    <style>
        .checkout-box {
            background: #fff;
            border-radius: 12px;
            border: 1px solid var(--border, #E8C3CB);
            padding: 25px;
            box-shadow: 0 4px 15px rgba(139, 26, 43, 0.08);
        }
        .checkout-item-thumb {
            width: 50px;
            height: 70px;
            object-fit: cover;
            border-radius: 4px;
        }
    </style>
</head>
<body>
<div class="container my-4">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h2 class="page-title mb-0">📦 Xác Nhận Đặt Hàng</h2>
        <a href="${pageContext.request.contextPath}/cart" class="btn btn-outline-maroon btn-sm">
            &laquo; Quay lại giỏ hàng
        </a>
    </div>

    <!-- Error Alert -->
    <c:if test="${not empty error}">
        <div class="alert alert-danger alert-dismissible fade show" role="alert">
            <strong>Lỗi đặt hàng:</strong> <c:out value="${error}"/>
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    </c:if>

    <form action="${pageContext.request.contextPath}/checkout" method="POST" id="orderSubmitForm">
        <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">

        <div class="row g-4">
            <!-- Left Column: Shipping Information -->
            <div class="col-lg-7">
                <div class="checkout-box mb-4">
                    <h5 class="fw-bold text-maroon mb-3 border-bottom pb-2">
                        📍 1. Thông Tin Nhận Hàng
                    </h5>

                    <div class="mb-3">
                        <label for="receiverName" class="form-label fw-semibold">Họ tên người nhận <span class="text-danger">*</span></label>
                        <input type="text"
                               class="form-control"
                               id="receiverName"
                               name="receiverName"
                               maxlength="100"
                               required
                               placeholder="Ví dụ: Nguyễn Văn A"
                               value="<c:out value='${not empty receiverName ? receiverName : sessionScope.user.fullname}'/>">
                    </div>

                    <div class="row">
                        <div class="col-md-6 mb-3">
                            <label for="receiverPhone" class="form-label fw-semibold">Số điện thoại <span class="text-danger">*</span></label>
                            <input type="tel"
                                   class="form-control"
                                   id="receiverPhone"
                                   name="receiverPhone"
                                   maxlength="20"
                                   required
                                   placeholder="Ví dụ: 0912345678"
                                   value="<c:out value='${not empty receiverPhone ? receiverPhone : sessionScope.user.phone}'/>">
                        </div>
                        <div class="col-md-6 mb-3">
                            <label for="receiverEmail" class="form-label fw-semibold">Email nhận thông báo <span class="text-danger">*</span></label>
                            <input type="email"
                                   class="form-control"
                                   id="receiverEmail"
                                   name="receiverEmail"
                                   maxlength="255"
                                   required
                                   placeholder="name@example.com"
                                   value="<c:out value='${not empty receiverEmail ? receiverEmail : sessionScope.user.email}'/>">
                        </div>
                    </div>

                    <div class="mb-3">
                        <label for="shippingAddress" class="form-label fw-semibold">Địa chỉ giao hàng chi tiết <span class="text-danger">*</span></label>
                        <textarea class="form-control"
                                  id="shippingAddress"
                                  name="shippingAddress"
                                  rows="3"
                                  maxlength="500"
                                  required
                                  placeholder="Số nhà, tên đường, phường/xã, quận/huyện, tỉnh/thành phố..."><c:out value="${shippingAddress}"/></textarea>
                    </div>

                    <div class="mb-0">
                        <label for="note" class="form-label fw-semibold">Ghi chú đơn hàng (tùy chọn)</label>
                        <textarea class="form-control"
                                  id="note"
                                  name="note"
                                  rows="2"
                                  maxlength="500"
                                  placeholder="Ghi chú thêm về thời gian giao hàng, chỉ dẫn địa điểm..."><c:out value="${note}"/></textarea>
                    </div>
                </div>

                <!-- Payment Method Section -->
                <div class="checkout-box">
                    <h5 class="fw-bold text-maroon mb-3 border-bottom pb-2">
                        💳 2. Phương Thức Thanh Toán
                    </h5>

                    <div class="form-check p-3 border rounded bg-light">
                        <input class="form-check-input" type="radio" name="paymentMethod" id="paymentCOD" value="COD" checked>
                        <label class="form-check-label fw-bold d-block" for="paymentCOD">
                            💵 Thanh toán khi nhận hàng (COD)
                        </label>
                        <small class="text-muted">
                            Bạn sẽ kiểm tra hàng và thanh toán trực tiếp bằng tiền mặt cho nhân viên giao hàng khi nhận sách.
                        </small>
                    </div>
                </div>
            </div>

            <!-- Right Column: Order Items & Summary -->
            <div class="col-lg-5">
                <div class="checkout-box">
                    <h5 class="fw-bold text-maroon mb-3 border-bottom pb-2">
                        📚 3. Đơn Hàng Của Bạn (${checkoutItems.size()} sản phẩm)
                    </h5>

                    <div class="table-responsive mb-3" style="max-height: 320px; overflow-y: auto;">
                        <table class="table table-borderless align-middle mb-0">
                            <tbody>
                            <c:forEach var="item" items="${checkoutItems}">
                                <tr class="border-bottom">
                                    <td style="width: 55px;" class="ps-0">
                                        <c:choose>
                                            <c:when test="${not empty item.book.coverImage}">
                                                <img src="${pageContext.request.contextPath}/uploads/${item.book.coverImage}"
                                                     alt="<c:out value='${item.book.title}'/>"
                                                     class="checkout-item-thumb">
                                            </c:when>
                                            <c:otherwise>
                                                <div class="bg-light d-flex align-items-center justify-content-center rounded"
                                                     style="width: 50px; height: 70px;">📖</div>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <h6 class="mb-1 fs-6">
                                            <c:out value="${item.book.title}"/>
                                        </h6>
                                        <small class="text-muted">
                                            Số lượng: <span class="fw-bold">${item.quantity}</span> × 
                                            <fmt:formatNumber value="${item.book.price}" type="currency" currencySymbol="₫" maxFractionDigits="0"/>
                                        </small>
                                    </td>
                                    <td class="text-end pe-0 fw-bold text-danger">
                                        <fmt:formatNumber value="${item.subtotal}" type="currency" currencySymbol="₫" maxFractionDigits="0"/>
                                    </td>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                    </div>

                    <div class="border-top pt-3">
                        <div class="d-flex justify-content-between mb-2">
                            <span class="text-muted">Tạm tính:</span>
                            <span class="fw-semibold">
                                <fmt:formatNumber value="${estimatedTotal}" type="currency" currencySymbol="₫" maxFractionDigits="0"/>
                            </span>
                        </div>
                        <div class="d-flex justify-content-between mb-3">
                            <span class="text-muted">Phí vận chuyển:</span>
                            <span class="text-success fw-bold">Miễn phí</span>
                        </div>
                        <div class="d-flex justify-content-between align-items-baseline mb-4 pt-2 border-top">
                            <span class="fs-5 fw-bold">Tổng thanh toán:</span>
                            <span class="fs-3 fw-bold text-danger">
                                <fmt:formatNumber value="${estimatedTotal}" type="currency" currencySymbol="₫" maxFractionDigits="0"/>
                            </span>
                        </div>

                        <button type="submit" class="btn btn-maroon btn-lg w-100 fw-bold py-3 shadow-sm" id="btnPlaceOrder">
                            ✅ Xác Nhận Đặt Hàng (COD)
                        </button>

                        <p class="text-muted small text-center mt-3 mb-0">
                            Bằng việc nhấn đặt hàng, bạn đồng ý với điều khoản mua sắm tại BookStore.
                        </p>
                    </div>
                </div>
            </div>
        </div>
    </form>
</div>

<script>
    document.getElementById('orderSubmitForm').addEventListener('submit', function() {
        const btn = document.getElementById('btnPlaceOrder');
        btn.disabled = true;
        btn.innerHTML = '<span class="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>Đang xử lý đơn hàng...';
    });
</script>
</body>
</html>
