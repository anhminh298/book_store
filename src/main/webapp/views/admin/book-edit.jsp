<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Chỉnh sửa sách</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<div class="container mt-4">
    <h2>Chỉnh sửa sách</h2>

    <form action="${pageContext.request.contextPath}/admin/books?action=edit&id=${book.bookid}" method="POST" enctype="multipart/form-data">
        <div class="mb-3 form-group">
            <label for="isbn" class="form-label">ISBN</label>
            <input type="number" class="form-control" id="isbn" name="isbn" value="${book.isbn}" required>
        </div>
        <div class="mb-3 form-group">
            <label for="title" class="form-label">Tiêu đề</label>
            <input type="text" class="form-control" id="title" name="title" value="${book.title}" required>
        </div>
        <div class="mb-3 form-group">
            <label for="publisher" class="form-label">Nhà xuất bản</label>
            <input type="text" class="form-control" id="publisher" name="publisher" value="${book.publisher}">
        </div>
        <div class="mb-3 form-group">
            <label for="price" class="form-label">Giá</label>
            <input type="number" step="0.01" class="form-control" id="price" name="price" value="${book.price}" required>
        </div>
        <div class="mb-3 form-group">
            <label for="description" class="form-label">Mô tả</label>
            <textarea class="form-control" id="description" name="description" rows="4">${book.description}</textarea>
        </div>
        <div class="mb-3 form-group">
            <label for="publish_date" class="form-label">Ngày xuất bản</label>
            <input type="date" class="form-control" id="publish_date" name="publish_date" value="${book.publishDate}">
        </div>
        
        <div class="mb-3 form-group">
            <label class="form-label">Ảnh bìa hiện tại</label>
            <div>
                <c:if test="${not empty book.coverImage}">
                    <img src="${pageContext.request.contextPath}/uploads/${book.coverImage}" alt="Ảnh bìa" width="100" class="mb-2">
                </c:if>
                <c:if test="${empty book.coverImage}">
                    <p class="text-muted">Chưa có ảnh</p>
                </c:if>
            </div>
            <label for="coverImage" class="form-label">Chọn ảnh mới (để trống nếu giữ ảnh cũ)</label>
            <input type="file" class="form-control" id="coverImage" name="coverImage" accept="image/*">
            <input type="hidden" name="oldCoverImage" value="${book.coverImage}">
        </div>

        <div class="mb-3 form-group">
            <label for="quantity" class="form-label">Số lượng</label>
            <input type="number" class="form-control" id="quantity" name="quantity" value="${book.quantity}" required>
        </div>
        
        <div class="mb-3 form-group">
            <label for="author_id" class="form-label">Tác giả</label>
            <select class="form-control" id="author_id" name="author_id" required>
                <c:forEach var="author" items="${authors}">
                    <c:set var="isSelected" value="false" />
                    <c:forEach var="bookAuthor" items="${bookAuthors}">
                        <c:if test="${author.authorId == bookAuthor.authorId}">
                            <c:set var="isSelected" value="true" />
                        </c:if>
                    </c:forEach>
                    <option value="${author.authorId}" ${isSelected ? 'selected' : ''}>${author.authorName}</option>
                </c:forEach>
            </select>
        </div>

        <button type="submit" class="btn btn-primary">Cập nhật</button>
        <a href="${pageContext.request.contextPath}/admin/books" class="btn btn-secondary">Hủy</a>
    </form>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
