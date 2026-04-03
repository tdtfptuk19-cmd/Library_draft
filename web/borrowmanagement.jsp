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
        <title>Borrow/Return Management</title>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
        <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;500;600&display=swap" rel="stylesheet">
        <link rel="stylesheet" href="./css/styleBookManegement.css"/>
    </head>
    <body>
        <header><%@include file="template/headerAdmin.jsp" %></header>
        <div class="container">
            <main class="main-content">
                <h1>Borrow/Return Management</h1>
                <c:if test="${not empty requestScope.success}">
                    <div class="message success">${requestScope.success}</div>
                </c:if>
                <c:if test="${not empty requestScope.error}">
                    <div class="message error">${requestScope.error}</div>
                </c:if>

                <h3>Borrow List</h3>
                <table>
                    <tr>
                        <th>ID</th>
                        <th>Book ID</th>
                        <th>Book Title</th>
                        <th>Borrow Date</th>
                        <th>Due Date</th>
                        <th>Return Date</th>
                        <th>Status</th>
                    </tr>
                    <c:if test="${empty requestScope.borrows}">
                        <tr><td colspan="9" style="text-align: center;">No borrows available.</td></tr>
                    </c:if>
                    <c:forEach var="borrow" items="${requestScope.borrows}">
                        <tr>
                            <td>${borrow.recordId}</td>
                            <td>${borrow.bookId}</td>
                            <td>${borrow.bookTitle}</td>
                            <td>${borrow.borrowDate}</td>
                            <td>${borrow.dueDate}</td>
                            <td>${borrow.returnDate}</td>
                            <td>${borrow.status}</td>
                            <td>
                            <td>
                                <c:choose>
                                    <c:when test="${borrow.status == 'borrowed'}">
                                        <a href="borrowmanagement?action=returnBook&recordId=${borrow.recordId}" 
                                           onclick="return confirm('Confirm return?')">Return</a>
                                    </c:when>
                                    <c:otherwise>N/A</c:otherwise>
                                </c:choose>
                            </td>
                        </tr>
                    </c:forEach>
                </table>
            </main>
        </div>
        <footer><%@include file="template/footer.jsp" %></footer>
    </body>
</html>
