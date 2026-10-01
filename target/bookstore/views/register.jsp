<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<div class="row justify-content-center">
    <div class="col-md-6 col-lg-5">
        <div class="card shadow-sm mt-5">
            <div class="card-body p-4">
                <h3 class="card-title text-center mb-4">Đăng ký tài khoản</h3>
                
                <c:if test="${not empty error}">
                    <div class="alert alert-danger" role="alert">
                        ${error}
                    </div>
                </c:if>
                <c:if test="${not empty success}">
                    <div class="alert alert-success" role="alert">
                        ${success}
                    </div>
                </c:if>

                <form class="auth-form" action="${pageContext.request.contextPath}/register" method="POST">
                    <div class="mb-3">
                        <label for="email" class="form-label">Email</label>
                        <input type="email" class="form-control" id="email" name="email" required>
                    </div>
                    <div class="mb-3">
                        <label for="fullname" class="form-label">Họ và tên</label>
                        <input type="text" class="form-control" id="fullname" name="fullname" required>
                    </div>
                    <div class="mb-3">
                        <label for="phone" class="form-label">Số điện thoại</label>
                        <input type="number" class="form-control" id="phone" name="phone">
                    </div>
                    <div class="mb-3">
                        <label for="passwd" class="form-label">Mật khẩu</label>
                        <input type="password" class="form-control" id="passwd" name="passwd" required>
                    </div>
                    <div class="mb-4">
                        <label for="confirmPasswd" class="form-label">Xác nhận mật khẩu</label>
                        <input type="password" class="form-control" id="confirmPasswd" name="confirmPasswd" required>
                    </div>
                    <div class="d-grid gap-2">
                        <button type="submit" class="btn btn-primary">Đăng ký</button>
                    </div>
                </form>
                
                <div class="text-center mt-3">
                    <a href="${pageContext.request.contextPath}/login" class="text-decoration-none">Đã có tài khoản? Đăng nhập</a>
                </div>
            </div>
        </div>
    </div>
</div>
