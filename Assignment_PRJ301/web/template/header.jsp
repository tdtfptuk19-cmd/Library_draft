<%-- 
    Document   : header
    Created on : Mar 6, 2025, 10:32:07 PM
    Author     : thien
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>header</title>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
        <link rel="preconnect" href="https://fonts.gstatic.com"/>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css">
        <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;500;600&display=swap" rel="stylesheet">
        <link rel="stylesheet" href="./css/style.css">
    </head>
    <body>
        <header>
            <nav class="navbar">
                <a href="home" style="cursor: pointer; text-decoration: none">
                    <div class="logo" >
                        <i class="fas fa-book-open"></i>
                        <span>Library.Thien</span>
                    </div>
                </a>
                <ul class="nav-links">
                    <li><a href="home">Home</a></li>
                    <li><a href="catalog">Catalog</a></li>
                    <li><a href="#">Contact</a></li>
                    <li class="auth-links">
                    <c:if test="${empty sessionScope.user}">
                        <a href="login" class="login-btn">Login</a>
                        <a href="register" class="register-btn">Register</a>
                    </c:if>
                    <c:if test="${ not empty sessionScope.user}">
                        <div class="profile-menu">
                            <div class="profile-icon">
                                <img src="${sessionScope.user.imgUrl}" alt="Avatar" style="width: 30px; border-radius: 50%;"/>
                                <span style="color: black">${sessionScope.user.fullname}</span>
                            </div>
                            <div class="profile-dropdown">
                                <a href="profile">My Profile</a>
                                <a href="mybooks">My Books</a>
                                <a href="fine?action=myFines"><i class="fas fa-money-bill-wave"></i> My Fines</a>
                    </c:if>
                    <c:if test="${sessionScope.user.role == 'librarian'}">
                                <a href="borrowmanagement?action=borrowmanagement">Borrow Management</a>
                                <a href="admin?action=bookmanagement">Book Management</a>
                                <a href="fine?action=manage">Fine Management</a>
                                <a href="favorite">My Favorites</a>
                                <a href="fine?action=myFines"><i class="fas fa-money-bill-wave"></i> My Fines</a>
                                <a href="logout" class="logout-btn">Logout</a>
                            </div>
                        </div>
                    </c:if>
                    </li>
                </ul>
            </nav>
        </header>
    </body>
</html>
