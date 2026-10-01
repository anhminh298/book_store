<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Quản lý sách</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<div class="container mt-4">
    <h2>Quản lý sách</h2>

    <c:if test="${param.success != null}">
        <div class="alert alert-success">Thao tác thành công!</div>
    </c:if>
    <c:if test="${param.error != null}">
        <div class="alert alert-danger">Có lỗi xảy ra. Vui lòng thử lại!</div>
    </c:if>

    <div class="mb-3">
        <a href="${pageContext.request.contextPath}/admin/books?action=create" class="btn btn-primary">Thêm sách mới</a>
    </div>

    <table class="table table-striped table-hover admin-table">
        <thead class="table-dark">
        <tr>
            <th>STT</th>
            <th>Ảnh bìa</th>
            <th>Tiêu đề</th>
            <th>ISBN</th>
            <th>NXB</th>
            <th>Giá</th>
            <th>Số lượng</th>
            <th>Thao tác</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="book" items="${books}" varStatus="status">
            <tr>
                <td>${(currentPage - 1) * 5 + status.index + 1}</td>
                <td>
                    <c:if test="${not empty book.coverImage}">
                        <img src="${pageContext.request.contextPath}/uploads/${book.coverImage}" alt="${book.title}" width="60" height="80" style="object-fit: cover;">
                    </c:if>
                    <c:if test="${empty book.coverImage}">
                        <span class="text-muted">No Image</span>
                    </c:if>
                </td>
                <td>${book.title}</td>
                <td>${book.isbn}</td>
                <td>${book.publisher}</td>
                <td>${book.price}</td>
                <td>${book.quantity}</td>
                <td>
                    <a href="${pageContext.request.contextPath}/admin/books?action=edit&id=${book.bookid}" class="btn btn-warning btn-sm">Sửa</a>
                    <a href="${pageContext.request.contextPath}/admin/books?action=delete&id=${book.bookid}" class="btn btn-danger btn-sm" onclick="return confirm('Bạn có chắc muốn xóa?');">Xóa</a>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>

    <nav aria-label="Page navigation">
        <ul class="pagination justify-content-center">
            <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                <a class="page-link" href="${pageContext.request.contextPath}/admin/books?page=${currentPage - 1}">Trang trước</a>
            </li>
            <c:forEach begin="1" end="${totalPages}" var="i">
                <li class="page-item ${currentPage == i ? 'active' : ''}">
                    <a class="page-link" href="${pageContext.request.contextPath}/admin/books?page=${i}">${i}</a>
                </li>
            </c:forEach>
            <li class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                <a class="page-link" href="${pageContext.request.contextPath}/admin/books?page=${currentPage + 1}">Trang sau</a>
            </li>
        </ul>
    </nav>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
