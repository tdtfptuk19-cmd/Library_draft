<%-- 
    Document   : bookmanagement
    Created on : Mar 10, 2025
    Author     : thien
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%

if (session.getAttribute("user") == null || !"admin".equals(((model.User) session.getAttribute("user")).getRole())) {
    response.sendRedirect("login.jsp");
    return;
}
%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Book Management</title>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
        <link rel="preconnect" href="https://fonts.gstatic.com"/>
        <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;500;600&display=swap" rel="stylesheet">
        <link rel="stylesheet" href="./css/styleBookManegement.css"/>

    </head>
    <body>
        <header>
            <%@include file="template/headerAdmin.jsp" %>
        </header>
        <div class="container">
            <main class="main-content">
                <h1>Book Management</h1>

                
                <c:if test="${not empty requestScope.success}">
                    <div class="message success">${requestScope.success}</div>
                </c:if>
                <c:if test="${not empty requestScope.error}">
                    <div class="message error">${requestScope.error}</div>
                </c:if>

                <div class="form-container">
                    <h3>Add New Book</h3>
                    <form action="admin" method="post" enctype="multipart/form-data">
                        <input type="hidden" name="action" value="addBook">
                        <label for="title">Title:</label>
                        <input type="text" name="title" id="title" required>
                        <label for="author">Author:</label>
                        <input type="text" name="author" id="author" required>
                        <label for="publisher">Publisher:</label>
                        <input type="text" name="publisher" id="publisher" required>
                        <label for="categoryId">Category:</label>
                        <select name="categoryId" id="categoryId" required>
                            <option value="">Select Category</option>
                            <c:if test="${empty requestScope.categories}">
                                <option value="" disabled>No categories available</option>
                            </c:if>
                            <c:forEach var="category" items="${requestScope.categories}">
                                <option value="${category.categoryId}">${category.categoryName}</option>
                            </c:forEach>
                        </select>
                        <label for="quantity">Quantity:</label>
                        <input type="number" name="quantity" id="quantity" min="0" required>
                        <label for="available">Available:</label>
                        <input type="number" name="available" id="available" min="0" required>
                        <label for="image">Image:</label>
                        <input type="file" name="image" id="image" accept="image/*" onchange="previewImage(event)" required>
                        <img id="preview" class="preview-image" style="display: none;">
                        <button type="submit">Add Book</button>
                    </form>
                </div>

                <c:if test="${not empty requestScope.selectedBook}">
                    <div class="form-container">
                        <h3>Edit Book</h3>
                        <form action="admin" method="post" enctype="multipart/form-data">
                            <input type="hidden" name="action" value="editBook">
                            <input type="hidden" name="bookId" value="${requestScope.selectedBook.bookId}">
                            <label for="editTitle">Title:</label>
                            <input type="text" name="title" id="editTitle" value="${requestScope.selectedBook.title}" required>
                            <label for="editAuthor">Author:</label>
                            <input type="text" name="author" id="editAuthor" value="${requestScope.selectedBook.author}" required>
                            <label for="editPublisher">Publisher:</label>
                            <input type="text" name="publisher" id="editPublisher" value="${requestScope.selectedBook.publisher}" required>
                            <label for="editCategoryId">Category:</label>
                            <select name="categoryId" id="editCategoryId" required>
                                <c:if test="${empty requestScope.categories}">
                                    <option value="" disabled>No categories available</option>
                                </c:if>
                                <c:forEach var="category" items="${requestScope.categories}">
                                    <option value="${category.categoryId}" ${category.categoryId == selectedBook.categoryId ? 'selected' : ''}>${category.categoryName}</option>
                                </c:forEach>
                            </select>
                            <label for="editQuantity">Quantity:</label>
                            <input type="number" name="quantity" id="editQuantity" value="${requestScope.selectedBook.quantity}" min="0" required>
                            <label for="editAvailable">Available:</label>
                            <input type="number" name="available" id="editAvailable" value="${requestScope.selectedBook.available}" min="0" required>
                            <label for="editImage">Image (leave blank to keep current):</label>
                            <input type="file" name="image" id="editImage" accept="image/*" onchange="previewImage(event)">
                            <img src="${requestScope.selectedBook.imgUrl}" alt="Current Image" class="preview-image">
                            <button type="submit">Update Book</button>
                            <a href="admin?action=bookmanagement" style="margin-left: 10px;">Cancel</a>
                        </form>
                    </div>
                </c:if>

                <h3>Book List</h3>
                <table>
                    <tr>
                        <th>ID</th>
                        <th>Title</th>
                        <th>Author</th>
                        <th>Category</th>
                        <th>Quantity</th>
                        <th>Available</th>
                        <th>Image</th>
                        <th>Action</th>
                    </tr>
                    <c:if test="${empty requestScope.books}">
                        <tr>
                            <td colspan="8" style="text-align: center;">No books available.</td>
                        </tr>
                    </c:if>
                    <c:forEach var="book" items="${requestScope.books}">
                        <tr>
                            <td>${book.bookId}</td>
                            <td>${book.title}</td>
                            <td>${book.author}</td>
                            <td>${book.categoryId}</td>
                            <td>${book.quantity}</td>
                            <td>${book.available}</td>
                            <td><img src="${book.imgUrl}" alt="Book Image" style="max-width: 50px; max-height: 50px;"></td>
                            <td>
                                <a href="admin?action=editBook&bookId=${book.bookId}">Edit</a>
                                <a href="admin?action=deleteBook&bookId=${book.bookId}" onclick="return confirm('Do you want delete?')">Delete</a>
                            </td>
                        </tr>
                    </c:forEach>
                </table>
            </main>
        </div>
        <footer>
            <%@include file="template/footer.jsp" %>
        </footer>
        <script>
            function previewImage(event) {
                const file = event.target.files[0];
                const preview = document.getElementById('preview');
                if (file) {
                    const reader = new FileReader();
                    reader.onload = function (e) {
                        preview.src = e.target.result;
                        preview.style.display = 'block';
                    }
                    reader.readAsDataURL(file);
                }
            }
        </script>
    </body>
</html>