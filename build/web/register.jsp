<%-- 
    Document   : register
    Created on : Mar 5, 2025, 10:00:09 PM
    Author     : thien
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>register</title>
        <link rel="preconnect" href="https://fonts.gstatic.com"/>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css">
        <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;500;600&display=swap" rel="stylesheet">
        <link rel="stylesheet" href="./css/stylesRegister.css"/>
    </head>
    <body>
        <form action="register" method="post">
            <h3>Register here</h3>

            <label for="username">Username</label>
            <input type="text" placeholder="Enter your username" id="username" name="username" required>

            <label for="password">Password</label>
            <input type="password" placeholder="Enter your password" id="password" name="password" required>

            <label for="confirmPassword">Confirm Password</label>
            <input type="password" placeholder="Confirm your password" id="confirmPassword" name="confirmPassword" required>

            <label for="email">Email</label>
            <input type="email" placeholder="Enter your email" id="email" name="email" required>

            <label for="fullname">Full Name</label>
            <input type="text" placeholder="Enter your full name" id="fullname" name="fullname" required>
            <c:if test="${not empty error}">
                <p style="color: red; text-align: center; font-size: 10px">${error}</p>
            </c:if>
            <c:if test="${not empty success}">
                <p style="color: green; text-align: center; font-size: 10px">${success}</p>
            </c:if>

            <button type="submit">Register</button>
            <h4>Already have an account? <a href="login">Login</a></h4>

        </form>
    </body>
</html>
