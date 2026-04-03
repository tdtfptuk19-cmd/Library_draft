<%-- 
    Document   : profile
    Created on : Mar 6, 2025, 10:34:48 PM
    Author     : thien
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>profile</title>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
        <link rel="preconnect" href="https://fonts.gstatic.com"/>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css">
        <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;500;600&display=swap" rel="stylesheet">
        <link rel="stylesheet" href="./css/style.css">
        <link href="./css/stylesProfile.css" rel="stylesheet"/>
    </head>
    <body>

        <header>
            <c:choose>
                <c:when test="${sessionScope.user.role == 'admin'}">
                    <%@include file="template/headerAdmin.jsp" %>
                </c:when>
                <c:otherwise>
                    <%@include file="template/header.jsp" %>
                </c:otherwise>
            </c:choose>
        </header>


        <main class="content">
            <!-- account information -->
            <section class="profile-info">
                <h2>Account Information</h2>
                <div class="profile-details">
                    <img src="${sessionScope.user.imgUrl}" alt="Avatar" class="profile-img"/>

                    <div class="profile-text">
                        <p><strong>Full Name:</strong> ${sessionScope.user.fullname}</p>
                        <p><strong>Username:</strong> ${sessionScope.user.username}</p>
                        <p><strong>Email:</strong> ${sessionScope.user.email}</p>
                        <p><strong>Role:</strong> ${sessionScope.user.role}</p>
                        <p><strong>Status:</strong> ${sessionScope.user.status}</p>
                    </div>
                </div>

                <!-- update image -->
                <form action="profile" method="post" enctype="multipart/form-data" class="update-form">
                    <input type="hidden" name="action" value="updateImage">
                    <label for="image">Update Profile Image:</label>
                    <input type="file" name="image" id="image" accept="image/*" required>
                    <button type="submit">Upload</button>
                </form>

                <!-- change password -->
                <form action="profile" method="post" class="update-form">
                    <input type="hidden" name="action" value="changePassword">
                    <label for="oldPassword">Old Password:</label>
                    <input type="password" name="oldPassword" id="oldPassword" required><br>
                    <label for="newPassword">New Password:</label>
                    <input type="password" name="newPassword" id="newPassword" required><br>
                    <label for="confirmPassword">Confirm New Password:</label>
                    <input type="password" name="confirmPassword" id="confirmPassword" required><br>
                    <c:if test="${not empty error}">
                        <p style="color: red; font-size: 10px">${error}</p>
                    </c:if>
                    <c:if test="${not empty success}">
                        <p style="color: green; font-size: 10px">${success}</p>
                    </c:if>
                    <button type="submit">Change Password</button>
                </form>
            </section>
        </main>
        <footer>
            <%@include file="template/footer.jsp" %>
        </footer>
    </body>
</html>
