<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Thêm sách mới</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<div class="container mt-4">
    <h2>Thêm sách mới</h2>

    <form action="${pageContext.request.contextPath}/admin/books?action=create" method="POST" enctype="multipart/form-data">
        <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
        <div class="mb-3 form-group">
            <label for="isbn" class="form-label">ISBN</label>
            <input type="number" class="form-control" id="isbn" name="isbn" required>
        </div>
        <div class="mb-3 form-group">
            <label for="title" class="form-label">Tiêu đề</label>
            <input type="text" class="form-control" id="title" name="title" required>
        </div>
        <div class="mb-3 form-group">
            <label for="publisher" class="form-label">Nhà xuất bản</label>
            <input type="text" class="form-control" id="publisher" name="publisher">
        </div>
        <div class="mb-3 form-group">
            <label for="price" class="form-label">Giá</label>
            <input type="number" step="0.01" class="form-control" id="price" name="price" required>
        </div>
        <div class="mb-3 form-group">
            <label for="description" class="form-label">Mô tả</label>
            <textarea class="form-control" id="description" name="description" rows="4"></textarea>
        </div>
        <div class="mb-3 form-group">
            <label for="publish_date" class="form-label">Ngày xuất bản</label>
            <input type="date" class="form-control" id="publish_date" name="publish_date">
        </div>
        <div class="mb-3 form-group">
            <label for="coverImage" class="form-label">Ảnh bìa</label>
            <input type="file" class="form-control" id="coverImage" name="coverImage" accept="image/*">
        </div>
        <div class="mb-3 form-group">
            <label for="quantity" class="form-label">Số lượng</label>
            <input type="number" class="form-control" id="quantity" name="quantity" required>
        </div>
        <div class="mb-3 form-group">
            <label for="author_id" class="form-label">Tác giả</label>
            <select class="form-control" id="author_id" name="author_id" required>
                <c:forEach var="author" items="${authors}">
                    <option value="${author.authorId}">${author.authorName}</option>
                </c:forEach>
            </select>
        </div>

        <button type="submit" class="btn btn-primary">Thêm sách</button>
        <a href="${pageContext.request.contextPath}/admin/books" class="btn btn-secondary">Hủy</a>
    </form>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
