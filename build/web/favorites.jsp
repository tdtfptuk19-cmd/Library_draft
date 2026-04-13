<%-- 
    Document   : favorites
    Created on : Mar 8, 2025, 8:43:16 PM
    Author     : thien
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>favorites</title>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
        <link rel="stylesheet" href="./css/styleFavorites.css"/>
    </head>
    <body>
        <header>
        <%@include file="template/header.jsp" %>
    </header>

    <main class="content">
        <h1>My Favorites</h1>

        <c:if test="${not empty sessionScope.error}">
            <p style="color: red">${sessionScope.error}</p>
            <c:remove var="error" scope="session"/>
        </c:if>
        <c:if test="${not empty sessionScope.success}">
            <p style="color: green">${sessionScope.success}</p>
            <c:remove var="success" scope="session"/>
        </c:if>

        <section class="favorites-section">
            <div class="book-grid">
                <c:if test="${empty sessionScope.favorites}">
                    <p style="text-align: center; color: #c41e3a; font-size: 20px">No favorites found.</p>
                </c:if>
                <c:forEach var="favorite" items="${sessionScope.favorites}">
                    <div class="book-card">
                        <img src="${favorite.book.imgUrl}" alt="${favorite.book.title}" class="book-img"/>
                        <h3>${favorite.book.title}</h3>
                        <p>${favorite.book.author}</p>
                        <form action="favorite" method="post" style="display: inline;">
                            <input type="hidden" name="action" value="remove">
                            <input type="hidden" name="userId" value="${sessionScope.user.id}">
                            <input type="hidden" name="bookId" value="${favorite.book.bookId}">
                            <button type="submit" class="action-btn"><i class="fas fa-trash"></i> Remove</button>
                        </form>
                    </div>
                </c:forEach>
            </div>
        </section>
    </main>

    <footer>
        <%@include file="template/footer.jsp" %>
    </footer>
    </body>
</html>
