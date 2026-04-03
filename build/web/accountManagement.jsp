<%-- 
    Document   : accountManagement
    Created on : Mar 10, 2025, 9:38:37 AM
    Author     : thien
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>account management</title>
        
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
        <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;500;600&display=swap" rel="stylesheet">
        <link rel="stylesheet" href="./css/styleBookManegement.css"/>
    </head>
    <body>
        <header>
            <%@include file="template/headerAdmin.jsp" %>
        </header>
        <div class="container">
            <main class="main-content">
                <h1>Account Management</h1>
                <c:if test="${not empty requestScope.success}">
                    <div class="message success">${requestScope.success}</div>
                </c:if>
                <c:if test="${not empty requestScope.error}">
                    <div class="message error">${requestScope.error}</div>
                </c:if>

                <c:if test="${not empty requestScope.selectedUser}">
                    <div class="form-container">
                        <h3>Edit User</h3>
                        <form action="account" method="post" enctype="multipart/form-data">
                            <input type="hidden" name="action" value="updateUser">
                            <input type="hidden" name="userId" value="${requestScope.selectedUser.id}">
                            <label for="username">Username:</label>
                            <input type="text" name="username" value="${requestScope.selectedUser.username}" required>
                            <label for="password">Password:</label>
                            <input type="text" name="password" value="${requestScope.selectedUser.password}" required>
                            <label for="fullName">Full Name:</label>
                            <input type="text" name="fullName" value="${requestScope.selectedUser.fullname}" required>
                            <label for="email">Email:</label>
                            <input type="email" name="email" value="${requestScope.selectedUser.email}" required>
                            <label for="role">Role:</label>
                            <select name="role" required>
                                <option value="admin" ${requestScope.selectedUser.role == 'admin' ? 'selected' : ''}>Admin</option>
                                <option value="user" ${requestScope.selectedUser.role == 'user' ? 'selected' : ''}>User</option>
                            </select>
                            <button type="submit">Update User</button>
                            <a href="account?action=accountmanagement" style="margin-left: 10px;">Cancel</a>
                        </form>
                    </div>
                </c:if>

                <h3>User List</h3>
                <table>
                    <tr>
                        <th>ID</th>
                        <th>Username</th>
                        <th>Full Name</th>
                        <th>Email</th>
                        <th>Role</th>
                        <th>Image</th>
                        <th>Action</th>
                    </tr>
                    <c:if test="${empty requestScope.users}">
                        <tr><td colspan="7" style="text-align: center;">No users available.</td></tr>
                    </c:if>
                    <c:forEach var="user" items="${requestScope.users}">
                        <tr>
                            <td>${user.id}</td>
                            <td>${user.username}</td>
                            <td>${user.fullname}</td>
                            <td>${user.email}</td>
                            <td>${user.role}</td>
                            <td><img src="${user.imgUrl}" alt="User Image" style="max-width: 50px; max-height: 50px;"></td>
                            <td>
                                <a href="account?action=editUser&userId=${user.id}">Edit</a>
                                <a href="account?action=deleteUser&userId=${user.id}" onclick="return confirm('Do you want to delete?')">Delete</a>
                            </td>
                        </tr>
                    </c:forEach>
                </table>
            </main>
        </div>
        <footer>
            <%@include file="template/footer.jsp" %>
        </footer>
    </body>
</html>
