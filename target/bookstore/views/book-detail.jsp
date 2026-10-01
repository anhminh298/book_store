<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Chi Tiết Sách - BookStore</title>
    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        .book-detail img {
            max-width: 300px;
            width: 100%;
            height: auto;
            border-radius: 8px;
            box-shadow: 0 4px 8px rgba(0,0,0,0.1);
        }
        .review-item {
            border-bottom: 1px solid #eee;
            padding: 15px 0;
        }
        .review-item:last-child {
            border-bottom: none;
        }
        .star-rating {
            color: #ffc107;
        }
    </style>
</head>
<body>
    <div class="container mt-5">
        <a href="${pageContext.request.contextPath}/home" class="btn btn-secondary mb-4">&laquo; Quay lại trang chủ</a>
        
        <!-- Book Detail Section -->
        <div class="row book-detail mb-5">
            <div class="col-md-4 text-center text-md-start">
                <c:choose>
                    <c:when test="${not empty book.coverImage}">
                        <img src="${pageContext.request.contextPath}/uploads/${book.coverImage}" alt="cover">
                    </c:when>
                    <c:otherwise>
                        <img src="https://via.placeholder.com/300x400" alt="cover placeholder">
                    </c:otherwise>
                </c:choose>
            </div>
            <div class="col-md-8 mt-4 mt-md-0">
                <h1 class="mb-3">${book.title}</h1>
                <p><strong>Mã ISBN:</strong> ${book.isbn}</p>
                <p><strong>Tác giả:</strong>
                    <c:forEach var="author" items="${authors}" varStatus="loop">
                        ${author.authorName}<c:if test="${!loop.last}">, </c:if>
                    </c:forEach>
                </p>
                <p><strong>Nhà xuất bản:</strong> ${book.publisher}</p>
                <p><strong>Ngày xuất bản:</strong> ${book.publishDate}</p>
                <p><strong>Số lượng:</strong> ${book.quantity}</p>
                <p><strong>Giá:</strong> ${book.price} VNĐ</p>
                <div class="mt-4">
                    <strong>Mô tả:</strong>
                    <p class="mt-2">${book.description}</p>
                </div>
                <div class="mt-4">
                    <span class="badge bg-primary fs-6">Reviews (${ratingCount})</span>
                </div>
            </div>
        </div>

        <hr>

        <!-- Reviews Section -->
        <div class="reviews-section mt-5">
            <h3 class="mb-4">Reviews</h3>
            
            <c:if test="${empty ratings}">
                <p class="text-muted">Chưa có đánh giá nào cho sách này.</p>
            </c:if>

            <c:forEach var="rating" items="${ratings}">
                <div class="review-item">
                    <div class="d-flex justify-content-between align-items-center mb-2">
                        <strong><c:out value="${rating.userFullname != null ? rating.userFullname : 'Người dùng ẩn danh'}"/></strong>
                        <div class="star-rating fs-5">
                            <c:forEach begin="1" end="5" var="i">
                                <c:choose>
                                    <c:when test="${i <= rating.rating}">&#9733;</c:when>
                                    <c:otherwise>&#9734;</c:otherwise>
                                </c:choose>
                            </c:forEach>
                        </div>
                    </div>
                    <p class="mb-0"><c:out value="${rating.reviewText}"/></p>
                </div>
            </c:forEach>

            <div class="mt-5">
                <h4>Viết đánh giá của bạn</h4>
                <c:choose>
                    <c:when test="${sessionScope.user != null}">
                        <form action="${pageContext.request.contextPath}/review" method="POST" class="mt-3">
                            <input type="hidden" name="bookid" value="${book.bookid}">
                            
                            <div class="mb-3">
                                <label class="form-label">Đánh giá sao:</label>
                                <div>
                                    <div class="form-check form-check-inline">
                                        <input class="form-check-input" type="radio" name="rating" id="rating1" value="1" required>
                                        <label class="form-check-label" for="rating1">1 &#9733;</label>
                                    </div>
                                    <div class="form-check form-check-inline">
                                        <input class="form-check-input" type="radio" name="rating" id="rating2" value="2">
                                        <label class="form-check-label" for="rating2">2 &#9733;</label>
                                    </div>
                                    <div class="form-check form-check-inline">
                                        <input class="form-check-input" type="radio" name="rating" id="rating3" value="3">
                                        <label class="form-check-label" for="rating3">3 &#9733;</label>
                                    </div>
                                    <div class="form-check form-check-inline">
                                        <input class="form-check-input" type="radio" name="rating" id="rating4" value="4">
                                        <label class="form-check-label" for="rating4">4 &#9733;</label>
                                    </div>
                                    <div class="form-check form-check-inline">
                                        <input class="form-check-input" type="radio" name="rating" id="rating5" value="5" checked>
                                        <label class="form-check-label" for="rating5">5 &#9733;</label>
                                    </div>
                                </div>
                            </div>
                            
                            <div class="mb-3">
                                <label for="reviewText" class="form-label">Nội dung đánh giá:</label>
                                <textarea class="form-control" id="reviewText" name="reviewText" rows="3" required></textarea>
                            </div>
                            
                            <button type="submit" class="btn btn-primary">Gửi đánh giá</button>
                        </form>
                    </c:when>
                    <c:otherwise>
                        <div class="alert alert-warning mt-3" role="alert">
                            Vui lòng <a href="${pageContext.request.contextPath}/login" class="alert-link">đăng nhập</a> để viết đánh giá.
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>

    <!-- Bootstrap 5 JS Bundle -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
