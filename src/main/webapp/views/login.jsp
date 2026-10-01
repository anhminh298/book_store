<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<div class="row justify-content-center">
    <div class="col-md-6 col-lg-5">
        <div class="card shadow-sm mt-5">
            <div class="card-body p-4">
                <h3 class="card-title text-center mb-4">Đăng nhập</h3>
                
                <c:if test="${not empty error}">
                    <div class="alert alert-danger" role="alert">
                        ${error}
                    </div>
                </c:if>
                <c:if test="${not empty param.success}">
                    <div class="alert alert-success" role="alert">
                        Đăng ký thành công! Vui lòng đăng nhập.
                    </div>
                </c:if>

                <form class="auth-form" action="${pageContext.request.contextPath}/login" method="POST">
                    <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                    <div class="mb-3">
                        <label for="email" class="form-label">Email</label>
                        <input type="email" class="form-control" id="email" name="email" required>
                    </div>
                    <div class="mb-4">
                        <label for="passwd" class="form-label">Mật khẩu</label>
                        <input type="password" class="form-control" id="passwd" name="passwd" required>
                    </div>
                    <div class="d-grid gap-2">
                        <button type="submit" class="btn btn-primary">Đăng nhập</button>
                    </div>
                </form>
                
                <div class="text-center mt-3">
                    <a href="${pageContext.request.contextPath}/register" class="text-decoration-none">Chưa có tài khoản? Đăng ký</a>
                </div>
            </div>
        </div>
    </div>
</div>
