<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
if (session.getAttribute("user") == null ||
    (!"admin".equals(((model.User)session.getAttribute("user")).getRole()) &&
     !"librarian".equals(((model.User)session.getAttribute("user")).getRole()))) {
    response.sendRedirect("login.jsp"); return;
}
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Fine Management</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;500;600&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="./css/styleBookManegement.css"/>
    <style>
        .toolbar{display:flex;gap:10px;margin-bottom:16px;flex-wrap:wrap;align-items:center}
        .toolbar input,.toolbar select{padding:7px 12px;border:1px solid #ccc;
            border-radius:5px;font-family:'Poppins',sans-serif;font-size:13px}
        .summary-cards{display:flex;gap:16px;margin-bottom:22px;flex-wrap:wrap}
        .scard{background:#fff;border-radius:8px;padding:16px 24px;
            box-shadow:0 2px 8px rgba(0,0,0,.08);min-width:160px;text-align:center}
        .scard .num{font-size:28px;font-weight:700;color:#2c3e50}
        .scard .lbl{font-size:12px;color:#888;margin-top:4px}
        .scard.unpaid .num{color:#c0392b}
        .scard.paid   .num{color:#27ae60}
        .scard.total  .num{color:#2980b9}

        table{width:100%;border-collapse:collapse;font-size:13px}
        th{background:#2c3e50;color:#fff;padding:10px 12px;text-align:left;white-space:nowrap}
        td{padding:9px 12px;border-bottom:1px solid #eee;vertical-align:middle}
        tr:hover td{background:#f5f9ff}

        .badge{display:inline-block;padding:3px 10px;border-radius:12px;font-size:12px;font-weight:600}
        .badge-unpaid{background:#fde8e8;color:#c0392b}
        .badge-paid{background:#d4f5e4;color:#1a7a46}
        .badge-overdue-type{background:#fff3cd;color:#856404}
        .badge-damaged-type{background:#fde8e8;color:#c0392b}
        .badge-overdue\+damaged-type{background:#f0e6ff;color:#6a1b9a}

        .amount-big{font-size:15px;font-weight:700;color:#c0392b}
        .amount-paid{font-size:14px;color:#888;text-decoration:line-through}
        .fine-detail{font-size:11px;color:#888;display:block;margin-top:2px;white-space:pre-wrap}

        .msg{padding:10px 16px;border-radius:6px;margin-bottom:16px;font-size:13px}
        .msg.success{background:#d4edda;color:#155724;border:1px solid #c3e6cb}
        .msg.error  {background:#f8d7da;color:#721c24;border:1px solid #f5c6cb}
        .info-note{font-size:12px;color:#888;margin-top:10px}
    </style>
</head>
<body>
<header><%@include file="template/headerAdmin.jsp" %></header>

<div class="container">
<main class="main-content">
    <h1><i class="fas fa-money-bill-wave"></i> Fine Management</h1>

    <c:if test="${not empty requestScope.success}">
        <div class="msg success"><i class="fas fa-check-circle"></i> ${requestScope.success}</div>
    </c:if>
    <c:if test="${not empty requestScope.error}">
        <div class="msg error"><i class="fas fa-exclamation-circle"></i> ${requestScope.error}</div>
    </c:if>

    <%-- Summary cards --%>
    <c:set var="totalUnpaid" value="0"/>
    <c:set var="totalPaid"   value="0"/>
    <c:set var="cntUnpaid"   value="0"/>
    <c:set var="cntPaid"     value="0"/>
    <c:forEach var="f" items="${fines}">
        <c:choose>
            <c:when test="${f.status=='unpaid'}">
                <c:set var="totalUnpaid" value="${totalUnpaid+f.fineAmount}"/>
                <c:set var="cntUnpaid"   value="${cntUnpaid+1}"/>
            </c:when>
            <c:otherwise>
                <c:set var="totalPaid" value="${totalPaid+f.fineAmount}"/>
                <c:set var="cntPaid"   value="${cntPaid+1}"/>
            </c:otherwise>
        </c:choose>
    </c:forEach>

    <div class="summary-cards">
        <div class="scard total">
            <div class="num">${cntUnpaid+cntPaid}</div>
            <div class="lbl">Total fine records</div>
        </div>
        <div class="scard unpaid">
            <div class="num">${cntUnpaid}</div>
            <div class="lbl">Unpaid<br/><small>${totalUnpaid} VND</small></div>
        </div>
        <div class="scard paid">
            <div class="num">${cntPaid}</div>
            <div class="lbl">Paid<br/><small>${totalPaid} VND</small></div>
        </div>
    </div>

    <%-- Filters --%>
    <div class="toolbar">
        <input type="text" id="searchInput"
               placeholder="Search borrower or book..." style="min-width:220px"
               oninput="filterTable()">
        <select id="statusFilter" onchange="filterTable()">
            <option value="">All Status</option>
            <option value="unpaid">Unpaid</option>
            <option value="paid">Paid</option>
        </select>
        <select id="typeFilter" onchange="filterTable()">
            <option value="">All Types</option>
            <option value="overdue">Overdue only</option>
            <option value="damaged">Damaged only</option>
            <option value="overdue+damaged">Both</option>
        </select>
    </div>

    <%-- Grouped fine table — one row per borrow record --%>
    <table id="fineTable">
        <thead>
            <tr>
                <th>Record ID</th>
                <th>Borrower</th>
                <th>Book Title</th>
                <th>Fine Type</th>
                <th>Due Date</th>
                <th>Return Date</th>
                <th>Fine Details</th>
                <th>Total (VND)</th>
                <th>Status</th>
            </tr>
        </thead>
        <tbody>
            <c:if test="${empty fines}">
                <tr>
                    <td colspan="9" style="text-align:center;color:#888;padding:24px">
                        <i class="fas fa-check-circle" style="color:#27ae60"></i> No fine records.
                    </td>
                </tr>
            </c:if>
            <c:forEach var="f" items="${fines}">
                <tr data-borrower="${f.borrowerName}" data-book="${f.bookTitle}"
                    data-fine-status="${f.status}" data-fine-type="${f.fineType}">
                    <td>${f.recordId}</td>
                    <td><strong>${f.borrowerName}</strong></td>
                    <td>${f.bookTitle}</td>
                    <td>
                        <span class="badge badge-${f.fineType}-type">
                            <c:choose>
                                <c:when test="${f.fineType=='overdue'}">
                                    <i class="fas fa-clock"></i> Overdue
                                </c:when>
                                <c:when test="${f.fineType=='damaged'}">
                                    <i class="fas fa-exclamation-triangle"></i> Damaged
                                </c:when>
                                <c:otherwise>
                                    <i class="fas fa-layer-group"></i> Overdue + Damaged
                                </c:otherwise>
                            </c:choose>
                        </span>
                    </td>
                    <td>${f.dueDate}</td>
                    <td>${empty f.returnDate?'-':f.returnDate}</td>
                    <td>
                        <%-- Split pipe-delimited reasons into individual lines --%>
                        <c:forTokens var="line" items="${f.reason}" delims="|">
                            <span class="fine-detail">${line}</span>
                        </c:forTokens>
                    </td>
                    <td class="${f.status=='paid'?'amount-paid':'amount-big'}">
                        ${f.fineAmount} VND
                    </td>
                    <td>
                        <span class="badge badge-${f.status}">
                            <c:choose>
                                <c:when test="${f.status=='paid'}">
                                    <i class="fas fa-check"></i> Paid
                                </c:when>
                                <c:otherwise>
                                    <i class="fas fa-times"></i> Unpaid
                                </c:otherwise>
                            </c:choose>
                        </span>
                    </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>

    <p class="info-note">
        <i class="fas fa-info-circle"></i>
        Each row shows the <strong>combined total</strong> for one borrow record.
        Fines are collected at the library counter during book return — no separate collection step is needed.
    </p>
</main>
</div>

<footer><%@include file="template/footer.jsp" %></footer>

<script>
function filterTable() {
    const q  = document.getElementById('searchInput').value.toLowerCase();
    const st = document.getElementById('statusFilter').value;
    const tp = document.getElementById('typeFilter').value;
    document.querySelectorAll('#fineTable tbody tr[data-borrower]').forEach(row => {
        const matchQ  = !q  || (row.dataset.borrower||'').toLowerCase().includes(q)
                             || (row.dataset.book||'').toLowerCase().includes(q);
        const matchSt = !st || row.dataset.fineStatus === st;
        const matchTp = !tp || row.dataset.fineType   === tp;
        row.style.display = (matchQ && matchSt && matchTp) ? '' : 'none';
    });
}
</script>
</body>
</html>
