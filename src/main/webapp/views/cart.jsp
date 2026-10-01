<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Giỏ hàng - BookStore</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/style.css?v=5" rel="stylesheet">
    <style>
        .cart-table img {
            width: 70px;
            height: 95px;
            object-fit: cover;
            border-radius: 6px;
        }
        .qty-input-group {
            max-width: 140px;
        }
        .qty-input-group .form-control {
            text-align: center;
            font-weight: 600;
        }
        .cart-summary-card {
            background: #fff;
            border-radius: 12px;
            border: 1px solid var(--border, #E8C3CB);
            box-shadow: 0 4px 15px rgba(139, 26, 43, 0.08);
            position: sticky;
            top: 20px;
        }
    </style>
</head>
<body>
<div class="container my-4">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h2 class="page-title mb-0">🛒 Giỏ Hàng Của Bạn</h2>
        <a href="${pageContext.request.contextPath}/products" class="btn btn-outline-maroon btn-sm">
            &laquo; Tiếp tục mua sắm
        </a>
    </div>

    <!-- Alert Messages -->
    <c:if test="${not empty sessionScope.cartSuccess}">
        <div class="alert alert-success alert-dismissible fade show" role="alert">
            <strong>Thành công!</strong> <c:out value="${sessionScope.cartSuccess}"/>
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
        <c:remove var="cartSuccess" scope="session"/>
    </c:if>

    <c:if test="${not empty sessionScope.cartError}">
        <div class="alert alert-danger alert-dismissible fade show" role="alert">
            <strong>Lưu ý:</strong> <c:out value="${sessionScope.cartError}"/>
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
        <c:remove var="cartError" scope="session"/>
    </c:if>

    <c:choose>
        <c:when test="${empty sessionScope.cart or sessionScope.cart.itemCount == 0}">
            <div class="card p-5 text-center my-4 border-0 shadow-sm">
                <div class="mb-3" style="font-size: 4rem;">🛍️</div>
                <h4 class="text-muted">Giỏ hàng của bạn đang trống!</h4>
                <p class="text-secondary">Hãy khám phá kho sách đa dạng và chọn những tựa sách yêu thích nhé.</p>
                <div class="mt-3">
                    <a href="${pageContext.request.contextPath}/products" class="btn btn-maroon px-4 py-2">
                        Khám phá sách ngay
                    </a>
                </div>
            </div>
        </c:when>

        <c:otherwise>
            <!-- Form Partial Checkout: sends selectedBookIds[] via POST to /checkout/prepare -->
            <form id="checkoutForm" action="${pageContext.request.contextPath}/checkout/prepare" method="POST">
                <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">

                <div class="row g-4">
                    <!-- Left Column: Items Table -->
                    <div class="col-lg-8">
                        <div class="card border-0 shadow-sm">
                            <div class="card-header bg-white py-3 border-bottom d-flex justify-content-between align-items-center">
                                <div class="form-check mb-0">
                                    <input class="form-check-input" type="checkbox" id="selectAllCheckbox" checked>
                                    <label class="form-check-label fw-bold" for="selectAllCheckbox">
                                        Chọn tất cả (${sessionScope.cart.itemCount} sản phẩm)
                                    </label>
                                </div>
                                <button type="button" class="btn btn-outline-danger btn-sm" onclick="submitClearCart()">
                                    🗑️ Xóa toàn bộ
                                </button>
                            </div>

                            <div class="card-body p-0">
                                <div class="table-responsive">
                                    <table class="table align-middle mb-0 cart-table">
                                        <thead class="table-light">
                                        <tr>
                                            <th style="width: 40px;"></th>
                                            <th style="width: 80px;">Ảnh</th>
                                            <th>Sách</th>
                                            <th>Đơn giá</th>
                                            <th style="width: 150px;">Số lượng</th>
                                            <th>Thành tiền</th>
                                            <th style="width: 50px;"></th>
                                        </tr>
                                        </thead>
                                        <tbody>
                                        <c:forEach var="item" items="${sessionScope.cart.items}">
                                            <tr id="row-${item.book.bookid}">
                                                <td>
                                                    <input class="form-check-input item-checkbox"
                                                           type="checkbox"
                                                           name="selectedBookIds"
                                                           value="${item.book.bookid}"
                                                           data-price="${item.book.price}"
                                                           data-qty="${item.quantity}"
                                                           checked
                                                           onchange="updateSelectionSummary()">
                                                </td>
                                                <td>
                                                    <c:choose>
                                                        <c:when test="${not empty item.book.coverImage}">
                                                            <img src="${pageContext.request.contextPath}/uploads/<c:out value='${item.book.coverImage}'/>"
                                                                 alt="<c:out value='${item.book.title}'/>">
                                                        </c:when>
                                                        <c:otherwise>
                                                            <div class="bg-light d-flex align-items-center justify-content-center rounded"
                                                                 style="width: 70px; height: 95px; font-size: 1.5rem;">📖</div>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </td>
                                                <td>
                                                    <h6 class="mb-1">
                                                        <a href="${pageContext.request.contextPath}/book-detail?id=${item.book.bookid}"
                                                           class="text-decoration-none text-dark fw-bold">
                                                            <c:out value="${item.book.title}"/>
                                                        </a>
                                                    </h6>
                                                    <small class="text-muted d-block">
                                                        ISBN: <c:out value="${item.book.isbn}"/>
                                                    </small>
                                                    <small class="badge bg-light text-dark border mt-1">
                                                        Kho: ${item.book.quantity}
                                                    </small>
                                                </td>
                                                <td>
                                                    <span class="fw-semibold text-danger">
                                                        <fmt:formatNumber value="${item.book.price}" type="currency" currencySymbol="₫" maxFractionDigits="0"/>
                                                    </span>
                                                </td>
                                                <td>
                                                    <div class="input-group input-group-sm qty-input-group">
                                                        <button type="button" class="btn btn-outline-secondary"
                                                                onclick="updateItemQty(${item.book.bookid}, 'dec', ${item.quantity - 1})"
                                                                ${item.quantity <= 1 ? 'disabled' : ''}>-</button>
                                                        <input type="number"
                                                               class="form-control item-qty-input"
                                                               value="${item.quantity}"
                                                               min="1"
                                                               max="${item.book.quantity}"
                                                               id="qty-${item.book.bookid}"
                                                               onchange="changeItemQtyInput(${item.book.bookid}, this.value, ${item.book.quantity})">
                                                        <button type="button" class="btn btn-outline-secondary"
                                                                onclick="updateItemQty(${item.book.bookid}, 'inc', ${item.quantity + 1})"
                                                                ${item.quantity >= item.book.quantity ? 'disabled' : ''}>+</button>
                                                    </div>
                                                </td>
                                                <td>
                                                    <span class="fw-bold text-danger subtotal-text" id="subtotal-${item.book.bookid}">
                                                        <fmt:formatNumber value="${item.subtotal}" type="currency" currencySymbol="₫" maxFractionDigits="0"/>
                                                    </span>
                                                </td>
                                                <td>
                                                    <button type="button" class="btn btn-link text-danger p-0"
                                                            title="Xóa sản phẩm"
                                                            onclick="submitRemoveItem(${item.book.bookid})">
                                                        ✖
                                                    </button>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                        </tbody>
                                    </table>
                                </div>
                            </div>
                        </div>
                    </div>

                    <!-- Right Column: Checkout Summary -->
                    <div class="col-lg-4">
                        <div class="card cart-summary-card p-4">
                            <h5 class="fw-bold mb-3 border-bottom pb-2">Tóm tắt đơn hàng</h5>
                            
                            <div class="d-flex justify-content-between mb-2">
                                <span class="text-muted">Tổng số lượng trong giỏ:</span>
                                <span class="fw-semibold">${sessionScope.cart.totalQuantity} cuốn</span>
                            </div>

                            <div class="d-flex justify-content-between mb-2">
                                <span class="text-muted">Đã chọn thanh toán:</span>
                                <span class="fw-bold text-primary" id="selectedCountText">0 sản phẩm</span>
                            </div>

                            <hr>

                            <div class="d-flex justify-content-between align-items-baseline mb-4">
                                <span class="fs-6 fw-bold">Tổng tiền thanh toán:</span>
                                <span class="fs-4 fw-bold text-danger" id="selectedTotalText">0 ₫</span>
                            </div>

                            <button type="submit" class="btn btn-maroon btn-lg w-100 fw-bold py-2 shadow-sm" id="btnCheckoutSubmit">
                                Tiến Hành Đặt Hàng &raquo;
                            </button>

                            <p class="text-muted small text-center mt-3 mb-0">
                                🔒 Thanh toán khi nhận hàng (COD). Kiểm tra tồn kho trước khi đặt.
                            </p>
                        </div>
                    </div>
                </div>
            </form>

            <!-- Hidden Helper Forms for Mutations -->
            <form id="updateQtyForm" action="${pageContext.request.contextPath}/cart/update" method="POST" style="display: none;">
                <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                <input type="hidden" name="bookId" id="updateBookId">
                <input type="hidden" name="action" id="updateAction">
                <input type="hidden" name="quantity" id="updateQuantity">
            </form>

            <form id="removeItemForm" action="${pageContext.request.contextPath}/cart/remove" method="POST" style="display: none;">
                <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                <input type="hidden" name="bookId" id="removeBookId">
            </form>

            <form id="clearCartForm" action="${pageContext.request.contextPath}/cart/clear" method="POST" style="display: none;">
                <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
            </form>
        </c:otherwise>
    </c:choose>
</div>

<script>
    function formatCurrency(amount) {
        return new Intl.NumberFormat('vi-VN', { style: 'currency', currency: 'VND' }).format(amount);
    }

    function updateSelectionSummary() {
        const checkboxes = document.querySelectorAll('.item-checkbox');
        let selectedCount = 0;
        let selectedTotal = 0;
        let allChecked = checkboxes.length > 0;

        checkboxes.forEach(cb => {
            if (cb.checked) {
                selectedCount++;
                const price = parseFloat(cb.dataset.price) || 0;
                const qty = parseInt(cb.dataset.qty) || 0;
                selectedTotal += price * qty;
            } else {
                allChecked = false;
            }
        });

        const selectAllCb = document.getElementById('selectAllCheckbox');
        if (selectAllCb) {
            selectAllCb.checked = allChecked && checkboxes.length > 0;
        }

        const countText = document.getElementById('selectedCountText');
        const totalText = document.getElementById('selectedTotalText');
        const checkoutBtn = document.getElementById('btnCheckoutSubmit');

        if (countText) countText.textContent = selectedCount + ' sản phẩm';
        if (totalText) totalText.textContent = formatCurrency(selectedTotal);

        if (checkoutBtn) {
            if (selectedCount === 0) {
                checkoutBtn.disabled = true;
                checkoutBtn.classList.add('disabled');
            } else {
                checkoutBtn.disabled = false;
                checkoutBtn.classList.remove('disabled');
            }
        }
    }

    document.addEventListener('DOMContentLoaded', function() {
        const selectAllCb = document.getElementById('selectAllCheckbox');
        if (selectAllCb) {
            selectAllCb.addEventListener('change', function() {
                const checkboxes = document.querySelectorAll('.item-checkbox');
                checkboxes.forEach(cb => {
                    cb.checked = selectAllCb.checked;
                });
                updateSelectionSummary();
            });
        }

        const checkoutForm = document.getElementById('checkoutForm');
        if (checkoutForm) {
            checkoutForm.addEventListener('submit', function(e) {
                const checked = document.querySelectorAll('.item-checkbox:checked');
                if (checked.length === 0) {
                    e.preventDefault();
                    alert('Vui lòng chọn ít nhất 1 sản phẩm để tiến hành thanh toán.');
                }
            });
        }

        updateSelectionSummary();
    });

    function updateItemQty(bookId, action, targetQty) {
        const form = document.getElementById('updateQtyForm');
        document.getElementById('updateBookId').value = bookId;
        document.getElementById('updateAction').value = action;
        document.getElementById('updateQuantity').value = targetQty;
        form.submit();
    }

    function changeItemQtyInput(bookId, value, maxStock) {
        let qty = parseInt(value);
        if (isNaN(qty) || qty <= 0) {
            qty = 1;
        } else if (qty > maxStock) {
            alert('Số lượng tối đa còn trong kho là ' + maxStock);
            qty = maxStock;
        }
        const form = document.getElementById('updateQtyForm');
        document.getElementById('updateBookId').value = bookId;
        document.getElementById('updateAction').value = 'set';
        document.getElementById('updateQuantity').value = qty;
        form.submit();
    }

    function submitRemoveItem(bookId) {
        if (confirm('Bạn có chắc chắn muốn xóa sách này khỏi giỏ hàng?')) {
            const form = document.getElementById('removeItemForm');
            document.getElementById('removeBookId').value = bookId;
            form.submit();
        }
    }

    function submitClearCart() {
        if (confirm('Bạn có chắc chắn muốn xóa toàn bộ giỏ hàng?')) {
            document.getElementById('clearCartForm').submit();
        }
    }
</script>
</body>
</html>
