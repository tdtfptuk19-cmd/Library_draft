<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Reset Password</title>
        <link rel="preconnect" href="https://fonts.gstatic.com"/>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css">
        <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;500;600&display=swap" rel="stylesheet">
        <link rel="stylesheet" href="./css/stylesLogin.css"/>
    </head>
    <body>
        <form action="reset-password" method="post">
            <h3>Reset Password</h3>
            <input type="hidden" name="token" value="${token}">

            <label for="password">New Password</label>
            <input type="password" placeholder="New password" id="password" name="password" required>

            <label for="confirmPassword">Confirm New Password</label>
            <input type="password" placeholder="Confirm new password" id="confirmPassword" name="confirmPassword" required>

            <c:if test="${not empty error}">
                <p style="color: red; font-size: 10px">${error}</p>
            </c:if>
            <c:if test="${not empty success}">
                <p style="color: green; text-align: center; font-size: 10px">${success}</p>
            </c:if>

            <button type="submit">Reset</button>
            <h4><a href="login">Back to Login</a></h4>
        </form>
    </body>
</html>

