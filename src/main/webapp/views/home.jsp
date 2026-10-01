<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Trang Chủ - BookStore</title>
    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        .book-card {
            border: 1px solid #ddd;
            border-radius: 5px;
            padding: 15px;
            height: 100%;
            display: flex;
            flex-direction: column;
        }
        .book-card img {
            max-width: 100%;
            height: auto;
            margin-bottom: 15px;
            object-fit: cover;
            max-height: 250px;
        }
        .card-body {
            flex-grow: 1;
        }
        .author-section {
            margin-bottom: 40px;
        }
    </style>
</head>
<body>
    <div class="container mt-5">
        <h1 class="mb-4">Hệ Thống Nhà Sách</h1>

        <c:choose>
            <c:when test="${not empty currentAuthorId}">
                <h2>Tác giả: <c:out value="${selectedAuthor.authorName}"/></h2>
                <div class="row">
                    <c:forEach var="book" items="${books}">
                        <div class="col-md-4 mb-4">
                            <div class="book-card">
                                <c:choose>
                                    <c:when test="${not empty book.coverImage}">
                                        <img src="${pageContext.request.contextPath}/uploads/<c:out value='${book.coverImage}'/>" alt="cover">
                                    </c:when>
                                    <c:otherwise>
                                        <img src="https://via.placeholder.com/150x200" alt="cover placeholder">
                                    </c:otherwise>
                                </c:choose>
                                <div class="card-body">
                                    <h5 class="card-title"><a href="${pageContext.request.contextPath}/book-detail?id=${book.bookid}"><c:out value="${book.title}"/></a></h5>
                                    <p class="card-text">Mã isbn: ${book.isbn}</p>
                                    <p class="card-text">Tác giả: 
                                        <c:forEach var="author" items="${book.authors}" varStatus="loop">
                                            <c:out value="${author.authorName}"/><c:if test="${!loop.last}">, </c:if>
                                        </c:forEach>
                                    </p>
                                    <p class="card-text">Publisher: <c:out value="${book.publisher}"/></p>
                                    <p class="card-text">Publish_date: ${book.publishDate}</p>
                                    <p class="card-text">Quantity: ${book.quantity}</p>
                                    <p class="card-text"><span class="badge bg-info">Review (${book.ratingCount})</span></p>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </div>

                <!-- Pagination -->
                <nav aria-label="Page navigation">
                    <ul class="pagination justify-content-center">
                        <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                            <a class="page-link" href="${pageContext.request.contextPath}/home?authorId=${currentAuthorId}&page=${currentPage - 1}" tabindex="-1">Trang trước</a>
                        </li>
                        <c:forEach begin="1" end="${totalPages}" var="p">
                            <li class="page-item ${p == currentPage ? 'active' : ''}">
                                <a class="page-link" href="${pageContext.request.contextPath}/home?authorId=${currentAuthorId}&page=${p}">${p}</a>
                            </li>
                        </c:forEach>
                        <li class="page-item ${currentPage == totalPages || totalPages == 0 ? 'disabled' : ''}">
                            <a class="page-link" href="${pageContext.request.contextPath}/home?authorId=${currentAuthorId}&page=${currentPage + 1}">Trang sau</a>
                        </li>
                    </ul>
                </nav>
            </c:when>

            <c:otherwise>
                <c:forEach var="entry" items="${authorBooksMap}">
                    <c:if test="${not empty entry.value}">
                        <div class="author-section">
                            <div class="d-flex justify-content-between align-items-center mb-3">
                                <h2>Tác giả: <c:out value="${entry.key.authorName}"/></h2>
                                <a href="${pageContext.request.contextPath}/home?authorId=${entry.key.authorId}" class="btn btn-outline-primary btn-sm">Xem tất cả >></a>
                            </div>
                            <div class="row">
                                <c:forEach var="book" items="${entry.value}">
                                    <div class="col-md-4 mb-4">
                                        <div class="book-card">
                                            <c:choose>
                                                <c:when test="${not empty book.coverImage}">
                                                    <img src="${pageContext.request.contextPath}/uploads/<c:out value='${book.coverImage}'/>" alt="cover">
                                                </c:when>
                                                <c:otherwise>
                                                    <img src="https://via.placeholder.com/150x200" alt="cover placeholder">
                                                </c:otherwise>
                                            </c:choose>
                                            <div class="card-body">
                                                <h5 class="card-title"><a href="${pageContext.request.contextPath}/book-detail?id=${book.bookid}"><c:out value="${book.title}"/></a></h5>
                                                <p class="card-text">Mã isbn: ${book.isbn}</p>
                                                <p class="card-text">Tác giả: 
                                                    <c:forEach var="author" items="${book.authors}" varStatus="loop">
                                                        <c:out value="${author.authorName}"/><c:if test="${!loop.last}">, </c:if>
                                                    </c:forEach>
                                                </p>
                                                <p class="card-text">Publisher: <c:out value="${book.publisher}"/></p>
                                                <p class="card-text">Publish_date: ${book.publishDate}</p>
                                                <p class="card-text">Quantity: ${book.quantity}</p>
                                                <p class="card-text"><span class="badge bg-info">Review (${book.ratingCount})</span></p>
                                            </div>
                                        </div>
                                    </div>
                                </c:forEach>
                            </div>
                        </div>
                    </c:if>
                </c:forEach>
            </c:otherwise>
        </c:choose>
    </div>

    <!-- Bootstrap 5 JS Bundle -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
