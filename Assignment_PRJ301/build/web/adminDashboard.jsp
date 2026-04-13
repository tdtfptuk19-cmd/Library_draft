<%-- 
    Document   : adminDashboard
    Created on : Mar 5, 2025, 11:51:13 PM
    Author     : thien
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Admin dashboard</title>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
        <link rel="preconnect" href="https://fonts.gstatic.com"/>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css">
        <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;500;600&display=swap" rel="stylesheet">
        <link rel="stylesheet" href="./css/styleadmin.css">
    </head>
    <body>
        <header>
            <%@include file="template/headerAdmin.jsp" %>
        </header>
        <main>
            <section class="hero">
                <div class="hero-content">
                    <h1>Library Management</h1>
                    <p>Start with your manager</p>
                    <a href="statistic" class="cta-button">Get Started</a>
                </div>
            </section>
            <section class="features">
                <h2>Your Library Tools</h2>
                <div class="feature-grid">
                    <div class="feature-card">
                        <i class="fas fa-users"></i>
                        <h3>Account management</h3>
                        <p>View list of users, edit or delete accounts if needed</p>

                        <a href="account?action=accountmanagement" class="feature-link">Account management</a>

                    </div>
                    <div class="feature-card">
                        <i class="fas fa-book"></i>
                        <h3>Books management</h3>
                        <p>Add, edit, delete book information in the library</p>
                        <a href="admin?action=bookmanagement" class="feature-link">Book management</a>
                    </div>
                    <div class="feature-card">
                        <i class="fas fa-book-reader"></i>
                        <h3>Borrow/return</h3>
                        <p>Track borrowed books and confirm returns.</p>
                        <a href="borrowmanagement?action=borrowmanagement" class="feature-link">Borrow/return management</a>
                    </div>
                    <div class="feature-card">
                        <i class="fas fa-signal"></i>
                        <h3>Statistic</h3>
                        <p>View list of books being borrowed and overdue.</p>
                        <a href="statistic" class="feature-link">Statistic</a>
                    </div>
                </div>
            </section>
        </main>
        <footer>
            <%@include file="template/footer.jsp" %>
        </footer>
    </body>
</html>
