<%-- 
    Document   : login
    Created on : Mar 4, 2025, 12:23:48 AM
    Author     : thien
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>login</title>
        <link rel="preconnect" href="https://fonts.gstatic.com"/>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css">
        <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;500;600&display=swap" rel="stylesheet">
        <link rel="stylesheet" href="./css/stylesLogin.css"/>
    </head>
    <body>
        <form action="login" method="post">
            <h3>Login here</h3>

            <label for="username">User name</label>
            <input type="text" placeholder="Email or phone" id="username" name="username" required>

            <label for="password">Password</label>
            <input type="password" placeholder="Password" id="password" name="password" required>
            <c:if test="${not empty error}">
                <p style="color: red; font-size: 10px">${error}</p>
            </c:if>
            <c:if test="${not empty success}">
                <p style="color: green; text-align: center; font-size: 10px">${success}</p>
            </c:if> 
            <button>Login</button>
            <h4><a href="forgot-password">Forgot password?</a></h4>
            <h4>You don't have an account ? <a href="register">Register</a></h4>
        </form>

    </body>
</html>
