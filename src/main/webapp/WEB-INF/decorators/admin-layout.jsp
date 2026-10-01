<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><sitemesh:write property='title'>BookStore Admin</sitemesh:write></title>
    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/style.css?v=4" rel="stylesheet">
    <sitemesh:write property='head'/>
</head>
<body>
    <header>
        <nav class="navbar navbar-expand-lg navbar-dark navbar-maroon">
            <div class="container">
                <a class="navbar-brand" href="${pageContext.request.contextPath}/home">🔧 BookStore Admin</a>
                <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
                    <span class="navbar-toggler-icon"></span>
                </button>
                <div class="collapse navbar-collapse" id="navbarNav">
                    <ul class="navbar-nav me-auto">
                        <li class="nav-item">
                            <a class="nav-link" href="${pageContext.request.contextPath}/home">Trang Chủ</a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link" href="${pageContext.request.contextPath}/admin/books">Quản lý sách</a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link" href="${pageContext.request.contextPath}/admin/orders">Quản lý đơn hàng</a>
                        </li>
                    </ul>
                    <ul class="navbar-nav">
                        <c:if test="${not empty sessionScope.user}">
                            <li class="nav-item">
                                <span class="nav-link">Xin chào, <c:out value="${sessionScope.user.fullname}"/></span>
                            </li>
                            <li class="nav-item">
                                <form action="${pageContext.request.contextPath}/logout" method="post" class="m-0">
                                    <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                                    <button type="submit" class="nav-link border-0 bg-transparent">Đăng xuất</button>
                                </form>
                            </li>
                        </c:if>
                    </ul>
                </div>
            </div>
        </nav>
    </header>

    <main class="container mt-4">
        <sitemesh:write property='body'/>
    </main>

    <footer class="text-white text-center py-3 mt-5">
        <div class="container">
            <p class="mb-1">Họ tên: Nguyễn Anh Minh | MSSV: 24162073 | Mã đề: 02</p>
            <p class="mb-0">&copy; 2026 BookStore</p>
        </div>
    </footer>

    <!-- Bootstrap 5 JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
