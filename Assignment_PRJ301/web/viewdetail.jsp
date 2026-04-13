<%-- 
    Document   : bookdetail
    Created on : Mar 8, 2025, 8:30:06 PM
    Author     : thien
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Book detail</title>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
        <link rel="stylesheet" href="./css/styleViewDetail.css"/>
    </head>
    <body>
        <header>
            <%@include file="template/header.jsp" %>
        </header>
        <main class="content">
            <section class="book-detail">
                <div class="book-detail-container">
                    <img src="${book.imgUrl}" alt="${book.title}" class="book-detail-img"/>
                    <div class="book-detail-info">
                        <h1>${book.title}</h1>
                        <p><strong>Author:</strong> ${book.author}</p>
                        <p><strong>Publisher:</strong> ${book.publisher}</p>
                        <p><strong>Available:</strong> ${book.available}</p>
                        <c:if test="${not empty sessionScope.user}">
                            <div class="book-actions">
                                <c:if test="${book.available > 0}">
                                    <form action="viewdetail" method="post" style="display: inline;">
                                        <input type="hidden" name="action" value="borrow">
                                        <input type="hidden" name="userId" value="${sessionScope.user.id}">
                                        <input type="hidden" name="bookId" value="${book.bookId}">
                                        <button type="submit" class="action-btn"><i class="fas fa-book-reader"></i> Borrow</button>
                                    </form>
                                </c:if>
                                <form action="favorite" method="post" style="display: inline;">
                                    <input type="hidden" name="action" value="add">
                                    <input type="hidden" name="userId" value="${sessionScope.user.id}">
                                    <input type="hidden" name="bookId" value="${book.bookId}">
                                    <button type="submit" class="action-btn"><i class="fas fa-heart"></i> Add to Favorites</button>
                                </form>
                            </div>
                            <div class="review-section">
                                <h3>Leave a Review</h3>
                                <form action="review" method="post">
                                    <input type="hidden" name="bookId" value="${book.bookId}">
                                    <label>Rating (1-5):</label>
                                    <input type="number" name="rating" min="1" max="5" required>
                                    <label>Comment:</label>
                                    <textarea name="comment" required></textarea>
                                    <button type="submit" class="action-btn">Submit Review</button>
                                </form>
                            </div>
                            <div class="review-list">
                                <h3>Reviews</h3>
                                <c:if test="${empty requestScope.reviews}">
                                    <p>No reviews yet. Be the first to review this book!</p>
                                </c:if>

                                <c:if test="${not empty requestScope.reviews}">
                                    <table border="1" class="review-table">
                                        <thead>
                                            <tr>
                                                <th>Username</th>
                                                <th>Rating</th>
                                                <th>Comment</th>
                                                <th>Actions</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <c:forEach var="review" items="${requestScope.reviews}">
                                                <tr>
                                                    <td><strong>${review.username}</strong></td>
                                                    <td>${review.rating} <i class="fas fa-star"></i></td>
                                                    <td>${review.comment}</td>
                                                    <td>
                                                        <c:if test="${sessionScope.user != null && sessionScope.user.id == review.userId}">


                                                            <input type="hidden" name="reviewId" value="${review.reviewId}">
                                                            <input type="hidden" name="bookId" value="${book.bookId}">
                                                            <button type="submit" class="edit-btn"><i class="fas fa-edit"></i> Edit</button>


                                                            <form action="deleteReview" method="post" onsubmit="return confirm('Are you sure you want to delete this review?');" style="display:inline;">
                                                                <input type="hidden" name="reviewId" value="${review.reviewId}">
                                                                <input type="hidden" name="bookId" value="${book.bookId}">
                                                                <button type="submit" class="delete-btn"><i class="fas fa-trash"></i> Delete</button>
                                                            </form>
                                                        </c:if>
                                                    </td>
                                                </tr>
                                            </c:forEach>
                                        </tbody>
                                    </table>
                                </c:if>
                            </div>
                        </c:if>
                    </div>
                </div>
            </section>
            <!-- Modal chinh sua review -->
            <div id="editReviewModal" class="modal">
                <div class="modal-content">
                    <span class="close-btn">&times;</span>
                    <h2>Edit Review</h2>
                    <form id="editReviewForm" action="editReview" method="post">
                        <input type="hidden" id="editReviewId" name="reviewId">
                        <input type="hidden" id="editBookId" name="bookId">

                        <label>Rating (1-5):</label>
                        <input type="number" id="editRating" name="rating" min="1" max="5" required>

                        <label>Comment:</label>
                        <textarea id="editComment" name="comment" required></textarea>

                        <button type="submit" class="action-btn"><i class="fas fa-save"></i> Save Changes</button>
                    </form>
                </div>
            </div>

        </main>
        <footer>
            <%@include file="template/footer.jsp" %>
        </footer>
        <script>
            document.addEventListener("DOMContentLoaded", function () {
                let modal = document.getElementById("editReviewModal");
                let closeBtn = document.querySelector(".close-btn");

                document.querySelectorAll(".edit-btn").forEach(button => {
                    button.addEventListener("click", function (event) {
                        event.preventDefault(); // Ngăn chặn hành động mặc định của form

                        // Tìm phần tử chứa review
                        let row = this.closest("tr");

                        // Lấy dữ liệu từ cột tương ứng trong bảng
                        let reviewId = row.querySelector("input[name='reviewId']").value;
                        let bookId = row.querySelector("input[name='bookId']").value;
                        let rating = row.cells[1].textContent.trim().split(" ")[0]; // Chỉ lấy số rating
                        let comment = row.cells[2].textContent.trim();

                        // Gán dữ liệu vào modal
                        document.getElementById("editReviewId").value = reviewId;
                        document.getElementById("editBookId").value = bookId;
                        document.getElementById("editRating").value = rating;
                        document.getElementById("editComment").value = comment;

                        // Hiển thị modal
                        modal.style.display = "flex";
                    });
                });

                // Ẩn modal khi nhấn nút X
                closeBtn.addEventListener("click", function () {
                    modal.style.display = "none";
                });

                // Đóng modal khi click bên ngoài
                window.addEventListener("click", function (event) {
                    if (event.target == modal) {
                        modal.style.display = "none";
                    }
                });
            });
        </script>

    </body>
</html>
