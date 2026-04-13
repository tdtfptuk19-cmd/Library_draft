<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>QR Payment</title>
    <link rel="preconnect" href="https://fonts.gstatic.com"/>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;500;600&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="./css/styleMyBook.css"/>
    <style>
        .box{background:#fff;padding:24px;border-radius:12px;box-shadow:0 3px 10px rgba(0,0,0,.12);max-width:900px;margin:24px auto}
        .grid{display:grid;grid-template-columns:1.2fr .8fr;gap:18px}
        .qr{display:flex;align-items:center;justify-content:center;background:#fafafa;border:1px solid #eee;border-radius:12px;padding:18px}
        .qr img{max-width:100%;height:auto}
        .meta p{margin:8px 0}
        .amount{font-size:22px;font-weight:700;color:#c0392b}
        .tag{display:inline-block;padding:4px 10px;background:#f0e6ff;color:#6a1b9a;border-radius:10px;font-size:12px;font-weight:600}
        .btns{display:flex;gap:10px;margin-top:16px}
        .btn{border:none;border-radius:8px;padding:10px 14px;font-family:'Poppins',sans-serif;cursor:pointer}
        .btn-primary{background:#27ae60;color:#fff}
        .btn-secondary{background:#bdc3c7;color:#fff;text-decoration:none;display:inline-flex;align-items:center}
        .note{font-size:12px;color:#888;margin-top:10px}
        @media(max-width:860px){.grid{grid-template-columns:1fr}}
    </style>
</head>
<body>
<header><%@include file="template/header.jsp" %></header>

<div class="box">
    <h2><i class="fas fa-qrcode"></i> Pay by QR</h2>
    <div class="grid">
        <div class="meta">
            <p><strong>Book:</strong> ${fine.bookTitle}</p>
            <p><strong>Fine type:</strong> <span class="tag">${fine.fineType}</span></p>
            <p><strong>Reason:</strong> ${fine.reason}</p>
            <p><strong>Amount:</strong> <span class="amount">${fine.fineAmount} VND</span></p>
            <p><strong>Transfer content:</strong> <code>${addInfo}</code></p>
            <p><strong>Status:</strong> ${fine.status}</p>

            <div class="btns">
                <a class="btn btn-secondary" href="fine?action=myFines">Back</a>
                <form action="qrpay" method="post" style="margin:0">
                    <input type="hidden" name="fineId" value="${fine.fineId}">
                    <button class="btn btn-primary" type="submit"
                            onclick="return confirm('Confirm you have paid this fine?')">
                        I have paid
                    </button>
                </form>
            </div>
            <div class="note">
                This is a demo QR transfer flow. In real systems, payment confirmation should be validated by a payment gateway callback.
            </div>
        </div>

        <div class="qr">
            <img src="${qrUrl}" alt="QR Code">
        </div>
    </div>
</div>

<footer><%@include file="template/footer.jsp" %></footer>
</body>
</html>

