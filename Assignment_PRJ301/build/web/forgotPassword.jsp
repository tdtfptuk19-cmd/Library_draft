<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Forgot Password</title>
        <link rel="preconnect" href="https://fonts.gstatic.com"/>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css">
        <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;500;600&display=swap" rel="stylesheet">
        <link rel="stylesheet" href="./css/stylesLogin.css"/>
    </head>
    <body>
        <form action="forgot-password" method="post">
            <h3>Forgot Password</h3>

            <label for="email">Email</label>
            <input type="email" placeholder="Enter your email" id="email" name="email" required>

            <c:if test="${not empty error}">
                <p style="color: red; font-size: 10px">${error}</p>
            </c:if>
            <c:if test="${not empty success}">
                <p style="color: green; text-align: center; font-size: 10px">${success}</p>
            </c:if>

            <c:if test="${not empty resetLink}">
                <p style="font-size: 12px; word-break: break-all;">
                    Demo reset link: <a href="${resetLink}">${resetLink}</a>
                </p>
            </c:if>

            <button type="submit">Send reset link</button>
            <h4><a href="login">Back to Login</a></h4>
        </form>
    </body>
</html>

