<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>My Books</title>
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
        <link rel="stylesheet" href="./css/styleMyBook.css"/>
    </head>

    <body>
        <header>
            <%@include file="template/header.jsp" %>
        </header>

        <main class="content">
            <h1>My Books</h1>

            <!-- ✅ MESSAGE -->
            <c:if test="${not empty sessionScope.error}">
                <p style="color: red">${sessionScope.error}</p>
                <c:remove var="error" scope="session"/>
            </c:if>

            <c:if test="${not empty sessionScope.success}">
                <p style="color: green">${sessionScope.success}</p>
                <c:remove var="success" scope="session"/>
            </c:if>

            <!-- ========================= -->
            <!-- 📚 CURRENTLY BORROWING -->
            <!-- ========================= -->
            <section class="borrow-history">
                <h2>Currently Borrowed Books</h2>

                <table border="1">
                    <tr>
                        <th>Book Title</th>
                        <th>Borrow Date</th>
                        <th>Due Date</th>
                        <th>Status</th>
                        <th>Action</th>
                    </tr>

                    <c:if test="${empty borrowHistory}">
                        <tr>
                            <td colspan="5" style="text-align: center;">
                                No borrowed books found.
                            </td>
                        </tr>
                    </c:if>

                    <c:forEach var="record" items="${borrowHistory}">
                        <c:if test="${record.status == 'borrowed' || record.status == 'overdue'}">
                            <tr>
                                <td>${record.bookTitle}</td>
                                <td>${record.borrowDate}</td>
                                <td>${record.dueDate}</td>

                                <!-- 🎨 STATUS -->
                                <td>
                                    <c:choose>
                                        <c:when test="${record.status == 'borrowed'}">
                                            <span style="color: green;">Borrowed</span>
                                        </c:when>
                                        <c:when test="${record.status == 'overdue'}">
                                            <span style="color: red;">Overdue</span>
                                        </c:when>
                                    </c:choose>
                                </td>

                                <!-- 🔁 RETURN BUTTON -->
                                <td>
                                    <form action="borrow" method="post"
                                          onsubmit="return confirm('Return this book?');">

                                        <input type="hidden" name="action" value="return">
                                        <input type="hidden" name="recordId" value="${record.recordId}">

                                        <button type="submit">
                                            <i class="fas fa-undo"></i> Return
                                        </button>
                                    </form>
                                </td>
                            </tr>
                        </c:if>
                    </c:forEach>
                </table>

                <!-- ========================= -->
                <!-- 📜 HISTORY -->
                <!-- ========================= -->
                <h2>Borrow History</h2>

                <table border="1">
                    <tr>
                        <th>Book Title</th>
                        <th>Borrow Date</th>
                        <th>Due Date</th>
                        <th>Return Date</th>
                        <th>Status</th>
                    </tr>

                    <c:if test="${empty borrowHistory}">
                        <tr>
                            <td colspan="5" style="text-align: center;">
                                No borrow history found.
                            </td>
                        </tr>
                    </c:if>

                    <c:forEach var="record" items="${borrowHistory}">
                        <tr>
                            <td>${record.bookTitle}</td>
                            <td>${record.borrowDate}</td>
                            <td>${record.dueDate}</td>
                            <td>
                                <c:choose>
                                    <c:when test="${empty record.returnDate}">
                                        -
                                    </c:when>
                                    <c:otherwise>
                                        ${record.returnDate}
                                    </c:otherwise>
                                </c:choose>
                            </td>

                            <!-- 🎨 STATUS -->
                            <td>
                                <c:choose>
                                    <c:when test="${record.status == 'borrowed'}">
                                        <span style="color: green;">Borrowed</span>
                                    </c:when>
                                    <c:when test="${record.status == 'overdue'}">
                                        <span style="color: red;">Overdue</span>
                                    </c:when>
                                    <c:when test="${record.status == 'returned'}">
                                        <span style="color: gray;">Returned</span>
                                    </c:when>
                                </c:choose>
                            </td>
                        </tr>
                    </c:forEach>
                </table>
            </section>
        </main>

        <footer>
            <%@include file="template/footer.jsp" %>
        </footer>

    </body>
</html>