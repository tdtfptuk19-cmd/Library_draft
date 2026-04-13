<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>My Books</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;500;600&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="./css/styleMyBook.css"/>
    <style>
        .tab-nav{display:flex;gap:8px;margin-bottom:24px}
        .tab-nav button{padding:9px 22px;border:none;border-radius:6px;cursor:pointer;
            font-family:'Poppins',sans-serif;font-size:14px;background:#ddd;color:#333}
        .tab-nav button.active{background:#c41e3a;color:#fff}
        .tab-panel{display:none}.tab-panel.active{display:block}
        .badge{display:inline-block;padding:3px 10px;border-radius:12px;font-size:12px;font-weight:600}
        .badge-borrowed{background:#d4edff;color:#1a6fa8}
        .badge-overdue{background:#fde8e8;color:#c0392b}
        .badge-returned{background:#d4f5e4;color:#1a7a46}
        .badge-paid{background:#d4f5e4;color:#1a7a46}
        .badge-unpaid{background:#fde8e8;color:#c0392b}
        .badge-overdue-type{background:#fff3cd;color:#856404}
        .badge-damaged-type{background:#fde8e8;color:#c0392b}
        .badge-overdue\+damaged-type{background:#f0e6ff;color:#6a1b9a}
        .info-box{background:#e8f4fd;border-left:4px solid #3498db;
            padding:12px 16px;border-radius:4px;margin-top:10px;font-size:13px;color:#1a5276}
        .msg{padding:10px 16px;border-radius:6px;margin-bottom:16px;font-size:13px}
        .msg.success{background:#d4edda;color:#155724;border:1px solid #c3e6cb}
        .msg.error{background:#f8d7da;color:#721c24;border:1px solid #f5c6cb}
        .amount-cell{font-weight:700;color:#c0392b}
    </style>
</head>
<body>
<header><%@include file="template/header.jsp" %></header>

<main class="content">
    <h1>My Books</h1>

    <c:if test="${not empty requestScope.success}">
        <div class="msg success"><i class="fas fa-check-circle"></i> ${requestScope.success}</div>
    </c:if>
    <c:if test="${not empty requestScope.error}">
        <div class="msg error"><i class="fas fa-exclamation-circle"></i> ${requestScope.error}</div>
    </c:if>

    <div class="tab-nav">
        <button class="active" onclick="showTab('tab-active',this)">
            <i class="fas fa-book-open"></i> Active Borrows
        </button>
        <button onclick="showTab('tab-history',this)">
            <i class="fas fa-history"></i> Borrow History
        </button>
        <button onclick="showTab('tab-fines',this)">
            <i class="fas fa-receipt"></i> My Fine History
        </button>
    </div>

    <%-- ── ACTIVE BORROWS ── --%>
    <div id="tab-active" class="tab-panel active">
        <section class="borrow-history">
            <h2>Currently Borrowed</h2>
            <table border="1">
                <tr>
                    <th>Book Title</th><th>Borrow Date</th>
                    <th>Due Date</th><th>Status</th>
                </tr>
                <c:set var="hasActive" value="false"/>
                <c:forEach var="r" items="${borrowHistory}">
                    <c:if test="${r.status=='borrowed'||r.status=='overdue'}">
                        <c:set var="hasActive" value="true"/>
                        <tr>
                            <td>${r.bookTitle}</td>
                            <td>${r.borrowDate}</td>
                            <td>${r.dueDate}</td>
                            <td><span class="badge badge-${r.status}">${r.status}</span></td>
                        </tr>
                    </c:if>
                </c:forEach>
                <c:if test="${not hasActive}">
                    <tr><td colspan="4" style="text-align:center;color:#888">No active borrows.</td></tr>
                </c:if>
            </table>
            <div class="info-box">
                <i class="fas fa-info-circle"></i>
                <strong>How to return a book:</strong>
                Bring the physical book to the library counter.
                The librarian will process the return and collect any applicable fines at that time.
                No action is required on this page to return a book.
            </div>
        </section>
    </div>

    <%-- ── BORROW HISTORY ── --%>
    <div id="tab-history" class="tab-panel">
        <section class="borrow-history">
            <h2>Borrow History</h2>
            <table border="1">
                <tr>
                    <th>Book Title</th><th>Borrow Date</th>
                    <th>Due Date</th><th>Return Date</th><th>Status</th>
                </tr>
                <c:if test="${empty borrowHistory}">
                    <tr><td colspan="5" style="text-align:center;color:#888">No history yet.</td></tr>
                </c:if>
                <c:forEach var="r" items="${borrowHistory}">
                    <tr>
                        <td>${r.bookTitle}</td>
                        <td>${r.borrowDate}</td>
                        <td>${r.dueDate}</td>
                        <td>${empty r.returnDate?'-':r.returnDate}</td>
                        <td><span class="badge badge-${r.status}">${r.status}</span></td>
                    </tr>
                </c:forEach>
            </table>
        </section>
    </div>

    <%-- ── MY FINE HISTORY ── --%>
    <div id="tab-fines" class="tab-panel">
        <section class="borrow-history">
            <h2>My Fine History</h2>
            <c:choose>
                <c:when test="${empty myFines}">
                    <p style="color:#27ae60;font-weight:600">
                        <i class="fas fa-check-circle"></i>
                        No fines on record. Keep returning books on time!
                    </p>
                </c:when>
                <c:otherwise>
                    <table border="1">
                        <tr>
                            <th>Book</th><th>Fine Type</th>
                            <th>Amount (VND)</th><th>Reason</th>
                            <th>Due Date</th><th>Return Date</th><th>Status</th>
                        </tr>
                        <c:forEach var="f" items="${myFines}">
                            <tr>
                                <td>${f.bookTitle}</td>
                                <td>
                                    <span class="badge badge-${f.fineType}-type">
                                        <c:choose>
                                            <c:when test="${f.fineType=='overdue'}"><i class="fas fa-clock"></i> Overdue</c:when>
                                            <c:when test="${f.fineType=='damaged'}"><i class="fas fa-exclamation-triangle"></i> Damaged</c:when>
                                            <c:otherwise><i class="fas fa-layer-group"></i> Overdue + Damaged</c:otherwise>
                                        </c:choose>
                                    </span>
                                </td>
                                <td class="amount-cell">${f.fineAmount}</td>
                                <td>${f.reason}</td>
                                <td>${f.dueDate}</td>
                                <td>${empty f.returnDate?'-':f.returnDate}</td>
                                <td><span class="badge badge-${f.status}">${f.status}</span></td>
                            </tr>
                        </c:forEach>
                    </table>
                    <p style="margin-top:12px;font-size:12px;color:#888">
                        <i class="fas fa-info-circle"></i>
                        Fines are collected at the library counter when you return a book.
                    </p>
                </c:otherwise>
            </c:choose>
        </section>
    </div>
</main>

<footer><%@include file="template/footer.jsp" %></footer>
<script>
function showTab(id, btn) {
    document.querySelectorAll('.tab-panel').forEach(p => p.classList.remove('active'));
    document.querySelectorAll('.tab-nav button').forEach(b => b.classList.remove('active'));
    document.getElementById(id).classList.add('active');
    btn.classList.add('active');
}
</script>
</body>
</html>
