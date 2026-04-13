<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>My Fines</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;500;600&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="./css/styleMyBook.css"/>
    <style>
        .fine-section{background:white;padding:2rem;border-radius:10px;
            box-shadow:0 3px 10px rgba(0,0,0,.1);margin-bottom:2rem}
        .fine-section h2{font-size:1.6rem;margin-bottom:1.5rem;color:#333}
        .fine-section table{width:100%;border-collapse:collapse}
        .fine-section th,.fine-section td{padding:.9rem 1rem;text-align:left;border:1px solid #ddd}
        .fine-section th{background:#f9f9f9;font-weight:600;color:#333}
        .fine-section tr:hover td{background:#f5f5f5}
        .badge{display:inline-block;padding:3px 10px;border-radius:12px;font-size:12px;font-weight:600}
        .badge-paid{background:#d4f5e4;color:#1a7a46}
        .badge-unpaid{background:#fde8e8;color:#c0392b}
        .badge-overdue-type{background:#fff3cd;color:#856404}
        .badge-damaged-type{background:#fde8e8;color:#c0392b}
        .badge-overdue\+damaged-type{background:#f0e6ff;color:#6a1b9a}
        .alert-clear{background:#e8f5e9;border:1px solid #81c784;
            border-left:4px solid #27ae60;padding:14px 18px;border-radius:6px;margin-bottom:20px;font-size:14px}
        .amount-cell{font-weight:700;color:#c0392b}
        .fine-detail{font-size:11px;color:#888;display:block;margin-top:2px}
    </style>
</head>
<body>
<header><%@include file="template/header.jsp" %></header>

<main class="content">
    <h1>My Fine History</h1>

    <c:choose>
        <c:when test="${empty fines}">
            <div class="alert-clear">
                <i class="fas fa-check-circle"></i>
                <strong>No fines on record.</strong> Keep returning books on time!
            </div>
        </c:when>
        <c:otherwise>
            <div class="fine-section">
                <h2><i class="fas fa-receipt"></i> Fine Records</h2>
                <table>
                    <thead>
                        <tr>
                            <th>Book</th>
                            <th>Fine Type</th>
                            <th>Amount (VND)</th>
                            <th>Details</th>
                            <th>Due Date</th>
                            <th>Return Date</th>
                            <th>Status</th>
                            <th>Action</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="f" items="${fines}">
                            <tr>
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
                                <td class="amount-cell">${f.fineAmount}</td>
                                <td>${f.reason}</td>
                                <td>${f.dueDate}</td>
                                <td>${empty f.returnDate?'-':f.returnDate}</td>
                                <td><span class="badge badge-${f.status}">${f.status}</span></td>
                                <td>
                                    <c:if test="${f.status=='unpaid'}">
                                        <a href="qrpay?fineId=${f.fineId}" style="color:#27ae60;font-weight:600">
                                            <i class="fas fa-qrcode"></i> Pay by QR
                                        </a>
                                    </c:if>
                                    <c:if test="${f.status=='paid'}">
                                        -
                                    </c:if>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
                <p style="margin-top:12px;font-size:12px;color:#888">
                    <i class="fas fa-info-circle"></i>
                    You can pay unpaid fines online via QR.
                </p>
            </div>
        </c:otherwise>
    </c:choose>
</main>

<footer><%@include file="template/footer.jsp" %></footer>
</body>
</html>
