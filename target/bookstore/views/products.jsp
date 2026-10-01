<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<html>
<head>
    <title>Sản phẩm - BookStore</title>
</head>
<body>
    <div class="products-page">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h2 class="page-title"><i class="bi bi-book"></i> Tất cả sản phẩm</h2>
            <span class="badge bg-maroon fs-6">${totalCount} sách</span>
        </div>

        <c:if test="${empty books}">
            <div class="alert alert-info">Chưa có sản phẩm nào.</div>
        </c:if>

        <div class="row">
            <c:forEach var="book" items="${books}" varStatus="status">
                <div class="col-lg-4 col-md-6 mb-4">
                    <div class="book-card">
                        <div class="book-card-img-wrapper">
                            <c:choose>
                                <c:when test="${not empty book.coverImage}">
                                    <img src="${pageContext.request.contextPath}/uploads/${book.coverImage}" alt="${book.title}">
                                </c:when>
                                <c:otherwise>
                                    <div class="book-card-placeholder">
                                        <span>📚</span>
                                    </div>
                                </c:otherwise>
                            </c:choose>
                        </div>
                        <div class="card-body">
                            <h5 class="card-title">
                                <a href="${pageContext.request.contextPath}/book-detail?id=${book.bookid}">${book.title}</a>
                            </h5>
                            <p class="card-text"><strong>Mã ISBN:</strong> ${book.isbn}</p>
                            <p class="card-text"><strong>Tác giả:</strong>
                                <c:forEach var="author" items="${book.authors}" varStatus="authorStatus">
                                    ${author.authorName}<c:if test="${!authorStatus.last}">, </c:if>
                                </c:forEach>
                            </p>
                            <p class="card-text"><strong>NXB:</strong> ${book.publisher}</p>
                            <p class="card-text"><strong>Ngày XB:</strong> ${book.publishDate}</p>
                            <div class="d-flex justify-content-between align-items-center mt-2">
                                <span class="price-tag">${book.price} VNĐ</span>
                                <span class="badge bg-maroon">
                                    <c:choose>
                                        <c:when test="${book.quantity > 0}">Còn ${book.quantity}</c:when>
                                        <c:otherwise>Hết hàng</c:otherwise>
                                    </c:choose>
                                </span>
                            </div>
                            <div class="mt-2">
                                <span class="star-rating">
                                    ★ Review (${book.ratingCount})
                                </span>
                            </div>
                        </div>
                    </div>
                </div>
            </c:forEach>
        </div>

        <!-- Pagination -->
        <c:if test="${totalPages > 1}">
            <nav aria-label="Product pagination" class="mt-4">
                <ul class="pagination justify-content-center">
                    <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                        <a class="page-link" href="${pageContext.request.contextPath}/products?page=${currentPage - 1}">
                            &laquo; Trang trước
                        </a>
                    </li>
                    <c:forEach begin="1" end="${totalPages}" var="i">
                        <li class="page-item ${currentPage == i ? 'active' : ''}">
                            <a class="page-link" href="${pageContext.request.contextPath}/products?page=${i}">${i}</a>
                        </li>
                    </c:forEach>
                    <li class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                        <a class="page-link" href="${pageContext.request.contextPath}/products?page=${currentPage + 1}">
                            Trang sau &raquo;
                        </a>
                    </li>
                </ul>
            </nav>
        </c:if>
    </div>
</body>
</html>
