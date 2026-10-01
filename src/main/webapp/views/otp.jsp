<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<div class="row justify-content-center">
    <div class="col-md-6 col-lg-4">
        <div class="card shadow-sm mt-5">
            <div class="card-body p-4">
                <h3 class="card-title text-center mb-2">Xác thực OTP</h3>
                <p class="text-center text-muted mb-4">Mã OTP đã được gửi đến email của bạn</p>
                
                <c:if test="${not empty error}">
                    <div class="alert alert-danger" role="alert">
                        ${error}
                    </div>
                </c:if>

                <form class="auth-form" action="${pageContext.request.contextPath}/verify-otp" method="POST">
                    <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                    <div class="mb-4">
                        <label for="otp" class="form-label">Nhập mã OTP</label>
                        <input type="text" class="form-control otp-input text-center fs-4 letter-spacing-2" id="otp" name="otp" maxlength="6" required>
                    </div>
                    <div class="d-grid gap-2">
                        <button type="submit" class="btn btn-primary">Xác thực</button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>
