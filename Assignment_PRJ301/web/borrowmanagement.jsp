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
    <title>Borrow / Return Management</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;500;600&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="./css/styleBookManegement.css"/>
    <style>
        /* ── Tabs ── */
        .tab-nav{display:flex;gap:8px;margin-bottom:20px}
        .tab-nav button{padding:8px 20px;border:none;border-radius:6px;cursor:pointer;
            font-family:'Poppins',sans-serif;font-size:14px;background:#ddd;color:#333}
        .tab-nav button.active{background:#3498db;color:#fff}
        .tab-panel{display:none}.tab-panel.active{display:block}

        /* ── Toolbar ── */
        .toolbar{display:flex;gap:10px;margin-bottom:14px;flex-wrap:wrap;align-items:center}
        .toolbar input,.toolbar select{padding:7px 12px;border:1px solid #ccc;
            border-radius:5px;font-family:'Poppins',sans-serif;font-size:13px}

        /* ── Table ── */
        table{width:100%;border-collapse:collapse;font-size:13px}
        th{background:#2c3e50;color:#fff;padding:10px 12px;text-align:left;white-space:nowrap}
        td{padding:9px 12px;border-bottom:1px solid #eee;vertical-align:middle}
        tr:hover td{background:#f5f9ff}
        tr.row-overdue td{background:#fff5f5}

        /* ── Badges ── */
        .badge{display:inline-block;padding:3px 10px;border-radius:12px;font-size:12px;font-weight:600}
        .badge-borrowed{background:#d4edff;color:#1a6fa8}
        .badge-overdue{background:#fde8e8;color:#c0392b}
        .badge-returned{background:#d4f5e4;color:#1a7a46}
        .badge-unpaid{background:#fde8e8;color:#c0392b}
        .badge-paid{background:#d4f5e4;color:#1a7a46}
        .badge-overdue-type{background:#fff3cd;color:#856404}
        .badge-damaged-type{background:#fde8e8;color:#c0392b}
        .badge-overdue\+damaged-type{background:#f0e6ff;color:#6a1b9a}

        /* ── Buttons ── */
        .btn-return{background:#e67e22;color:#fff;border:none;padding:5px 12px;
            border-radius:5px;cursor:pointer;font-size:12px;font-family:'Poppins',sans-serif}
        .btn-return:hover{background:#ca6f1e}

        /* ── Fine summary row ── */
        .fine-total-row td{background:#fff8f0;font-weight:600;color:#c0392b}
        .fine-detail{font-size:11px;color:#888;display:block;margin-top:2px}
        .amount-big{font-size:15px;font-weight:700;color:#c0392b}

        /* ── Summary cards ── */
        .summary-cards{display:flex;gap:14px;margin-bottom:20px;flex-wrap:wrap}
        .scard{background:#fff;border-radius:8px;padding:14px 22px;
            box-shadow:0 2px 8px rgba(0,0,0,.08);min-width:150px;text-align:center}
        .scard .num{font-size:26px;font-weight:700;color:#2c3e50}
        .scard .lbl{font-size:11px;color:#888;margin-top:4px}
        .scard.unpaid .num{color:#c0392b}
        .scard.paid   .num{color:#27ae60}

        /* ── Modal ── */
        .modal-overlay{display:none;position:fixed;inset:0;
            background:rgba(0,0,0,.5);z-index:1000;align-items:center;justify-content:center}
        .modal-overlay.open{display:flex}
        .modal-box{background:#fff;border-radius:12px;padding:28px 32px;
            width:480px;max-width:95vw;box-shadow:0 8px 32px rgba(0,0,0,.2)}
        .modal-box h3{margin:0 0 16px;color:#2c3e50;font-size:17px}
        .info-block{background:#f4f6f8;padding:12px 14px;border-radius:8px;
            margin-bottom:14px;font-size:13px}
        .info-block p{margin:4px 0}
        .fine-preview{background:#fff8e1;border:1px solid #ffc107;
            border-left:4px solid #e67e22;border-radius:6px;
            padding:12px 14px;margin-bottom:14px;font-size:13px}
        .fine-preview .fine-line{display:flex;justify-content:space-between;
            padding:3px 0;border-bottom:1px dashed #fce9a0}
        .fine-preview .fine-line:last-child{border:none;font-weight:700;
            font-size:14px;color:#c0392b;padding-top:6px}
        .fine-preview .no-fine{color:#27ae60;font-weight:600;font-size:13px}
        .form-group{margin-bottom:12px}
        .form-group label{display:block;font-size:13px;font-weight:600;margin-bottom:5px}
        .form-group input[type=number]{width:100%;padding:8px 10px;border:1px solid #ccc;
            border-radius:5px;font-family:'Poppins',sans-serif;font-size:13px}
        .damage-section{display:none}
        .check-label{display:flex;align-items:center;gap:8px;font-size:13px;cursor:pointer}
        .modal-footer{display:flex;gap:10px;justify-content:flex-end;margin-top:18px}
        .btn-cancel{padding:8px 18px;background:#bdc3c7;color:#fff;border:none;
            border-radius:5px;cursor:pointer}
        .btn-confirm{padding:8px 20px;background:#27ae60;color:#fff;border:none;
            border-radius:5px;cursor:pointer;font-weight:600;font-size:13px}
        .btn-confirm:disabled{background:#95a5a6;cursor:not-allowed}
        .loading-msg{text-align:center;color:#888;font-size:13px;padding:12px}

        /* ── Flash messages ── */
        .msg{padding:10px 16px;border-radius:6px;margin-bottom:16px;font-size:13px}
        .msg.success{background:#d4edda;color:#155724;border:1px solid #c3e6cb}
        .msg.error{background:#f8d7da;color:#721c24;border:1px solid #f5c6cb}
    </style>
</head>
<body>
<header><%@include file="template/headerAdmin.jsp" %></header>

<div class="container">
<main class="main-content">
    <h1><i class="fas fa-exchange-alt"></i> Borrow / Return Management</h1>

    <%-- Flash messages --%>
    <c:if test="${not empty requestScope.success}">
        <div class="msg success"><i class="fas fa-check-circle"></i> ${requestScope.success}</div>
    </c:if>
    <c:if test="${not empty requestScope.error}">
        <div class="msg error"><i class="fas fa-exclamation-circle"></i> ${requestScope.error}</div>
    </c:if>

    <%-- Tabs --%>
    <div class="tab-nav">
        <button class="active" onclick="showTab('tab-borrows',this)">
            <i class="fas fa-book"></i> Borrow Records
        </button>
        <button onclick="showTab('tab-fines',this)">
            <i class="fas fa-money-bill-wave"></i> Fine History
        </button>
    </div>

    <%-- ══════════ TAB 1 — BORROW RECORDS ══════════ --%>
    <div id="tab-borrows" class="tab-panel active">
        <div class="toolbar">
            <input type="text" id="searchBorrow"
                   placeholder="Search Record ID or Borrower name..."
                   style="min-width:260px" oninput="filterBorrows()">
            <select id="statusFilter" onchange="filterBorrows()">
                <option value="">All Status</option>
                <option value="borrowed">Borrowed</option>
                <option value="overdue">Overdue</option>
                <option value="returned">Returned</option>
            </select>
        </div>

        <table id="borrowTable">
            <thead>
                <tr>
                    <th>ID</th><th>Borrower</th><th>Book Title</th>
                    <th>Borrow Date</th><th>Due Date</th><th>Return Date</th>
                    <th>Status</th><th>Action</th>
                </tr>
            </thead>
            <tbody>
                <c:if test="${empty borrows}">
                    <tr><td colspan="8" style="text-align:center;color:#888">No records.</td></tr>
                </c:if>
                <c:forEach var="b" items="${borrows}">
                    <tr class="${b.status=='overdue'?'row-overdue':''}"
                        data-id="${b.recordId}" data-borrower="${b.borrowerName}"
                        data-status="${b.status}">
                        <td>${b.recordId}</td>
                        <td>${b.borrowerName}</td>
                        <td>${b.bookTitle}</td>
                        <td>${b.borrowDate}</td>
                        <td>${b.dueDate}</td>
                        <td>${empty b.returnDate?'-':b.returnDate}</td>
                        <td><span class="badge badge-${b.status}">${b.status}</span></td>
                        <td>
                            <c:if test="${b.status=='borrowed'||b.status=='overdue'}">
                                <button class="btn-return"
                                    onclick="openReturnModal(${b.recordId},'${b.borrowerName}','${b.bookTitle}','${b.dueDate}')">
                                    <i class="fas fa-undo"></i> Return
                                </button>
                            </c:if>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </div>

    <%-- ══════════ TAB 2 — FINE HISTORY (grouped) ══════════ --%>
    <div id="tab-fines" class="tab-panel">

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
            <div class="scard">
                <div class="num">${cntUnpaid+cntPaid}</div>
                <div class="lbl">Total records with fines</div>
            </div>
            <div class="scard unpaid">
                <div class="num">${cntUnpaid}</div>
                <div class="lbl">Unpaid  (${totalUnpaid} VND)</div>
            </div>
            <div class="scard paid">
                <div class="num">${cntPaid}</div>
                <div class="lbl">Paid  (${totalPaid} VND)</div>
            </div>
        </div>

        <div class="toolbar">
            <input type="text" id="searchFine"
                   placeholder="Search borrower or book..."
                   style="min-width:220px" oninput="filterFines()">
            <select id="fineStatusF" onchange="filterFines()">
                <option value="">All Status</option>
                <option value="unpaid">Unpaid</option>
                <option value="paid">Paid</option>
            </select>
            <select id="fineTypeF" onchange="filterFines()">
                <option value="">All Types</option>
                <option value="overdue">Overdue only</option>
                <option value="damaged">Damaged only</option>
                <option value="overdue+damaged">Both</option>
            </select>
        </div>

        <table id="fineTable">
            <thead>
                <tr>
                    <th>Record ID</th>
                    <th>Borrower</th>
                    <th>Book</th>
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
                    <tr><td colspan="9" style="text-align:center;color:#888">No fine records.</td></tr>
                </c:if>
                <c:forEach var="f" items="${fines}">
                    <tr data-borrower="${f.borrowerName}" data-book="${f.bookTitle}"
                        data-fine-status="${f.status}" data-fine-type="${f.fineType}">
                        <td>${f.recordId}</td>
                        <td>${f.borrowerName}</td>
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
                            <%-- Show individual reasons as sub-lines --%>
                            <c:forTokens var="line" items="${f.reason}" delims="|">
                                <span class="fine-detail">${line}</span>
                            </c:forTokens>
                        </td>
                        <td class="amount-big">${f.fineAmount}</td>
                        <td><span class="badge badge-${f.status}">${f.status}</span></td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
        <p style="margin-top:10px;font-size:12px;color:#888">
            <i class="fas fa-info-circle"></i>
            Fines are collected at the counter during book return. Each row shows the combined total for one borrow record.
        </p>
    </div>
</main>
</div>

<%-- ══════════ RETURN MODAL ══════════ --%>
<div class="modal-overlay" id="returnModal">
    <div class="modal-box">
        <h3><i class="fas fa-undo"></i> Process Book Return</h3>

        <div class="info-block">
            <p><strong>Record ID:</strong> <span id="mRecordId"></span></p>
            <p><strong>Borrower:</strong>  <span id="mBorrower"></span></p>
            <p><strong>Book:</strong>      <span id="mBook"></span></p>
            <p><strong>Due Date:</strong>  <span id="mDueDate"></span></p>
        </div>

        <%-- Fine preview — populated by JS after fetch --%>
        <div class="fine-preview" id="finePreview">
            <div class="loading-msg"><i class="fas fa-spinner fa-spin"></i> Calculating fines...</div>
        </div>

        <form action="borrowmanagement" method="post" id="returnForm">
            <input type="hidden" name="action"   value="return">
            <input type="hidden" name="recordId" id="fRecordId">

            <div class="form-group">
                <label class="check-label">
                    <input type="checkbox" name="isDamaged" id="isDamaged"
                           onchange="onDamageToggle(this)">
                    Book is damaged / returned in poor condition
                </label>
            </div>

            <div class="form-group damage-section" id="damageSection">
                <label>Damage Fine Amount (VND)</label>
                <input type="number" name="damageAmount" id="damageAmount"
                       min="0" step="1000" placeholder="e.g. 50000"
                       oninput="updateTotal()">
            </div>

            <div class="modal-footer">
                <button type="button" class="btn-cancel" onclick="closeModal()">Cancel</button>
                <button type="submit" class="btn-confirm" id="btnConfirm"
                        onclick="return confirmReturn()">
                    <i class="fas fa-hand-holding-usd"></i> Collect & Return
                </button>
            </div>
        </form>
    </div>
</div>

<footer><%@include file="template/footer.jsp" %></footer>

<script>
// ── Tab ───────────────────────────────────────────────────────────────────────
function showTab(id, btn) {
    document.querySelectorAll('.tab-panel').forEach(p => p.classList.remove('active'));
    document.querySelectorAll('.tab-nav button').forEach(b => b.classList.remove('active'));
    document.getElementById(id).classList.add('active');
    btn.classList.add('active');
}

// ── Borrow table filter ───────────────────────────────────────────────────────
function filterBorrows() {
    const q  = document.getElementById('searchBorrow').value.toLowerCase();
    const st = document.getElementById('statusFilter').value;
    document.querySelectorAll('#borrowTable tbody tr[data-id]').forEach(row => {
        const matchQ  = !q  || row.dataset.id.includes(q) || (row.dataset.borrower||'').toLowerCase().includes(q);
        const matchSt = !st || row.dataset.status === st;
        row.style.display = (matchQ && matchSt) ? '' : 'none';
    });
}

// ── Fine table filter ─────────────────────────────────────────────────────────
function filterFines() {
    const q  = document.getElementById('searchFine').value.toLowerCase();
    const st = document.getElementById('fineStatusF').value;
    const tp = document.getElementById('fineTypeF').value;
    document.querySelectorAll('#fineTable tbody tr[data-borrower]').forEach(row => {
        const matchQ  = !q  || (row.dataset.borrower||'').toLowerCase().includes(q)
                             || (row.dataset.book||'').toLowerCase().includes(q);
        const matchSt = !st || row.dataset.fineStatus === st;
        const matchTp = !tp || row.dataset.fineType   === tp;
        row.style.display = (matchQ && matchSt && matchTp) ? '' : 'none';
    });
}

// ── Return modal ──────────────────────────────────────────────────────────────
let _overdueFine = 0;

function openReturnModal(recordId, borrower, book, dueDate) {
    _overdueFine = 0;
    document.getElementById('mRecordId').textContent = recordId;
    document.getElementById('mBorrower').textContent = borrower;
    document.getElementById('mBook').textContent     = book;
    document.getElementById('mDueDate').textContent  = dueDate;
    document.getElementById('fRecordId').value       = recordId;
    document.getElementById('isDamaged').checked     = false;
    document.getElementById('damageAmount').value    = '';
    document.getElementById('damageSection').style.display = 'none';
    document.getElementById('btnConfirm').disabled   = true;

    // Show loading then fetch fine preview
    document.getElementById('finePreview').innerHTML =
        '<div class="loading-msg"><i class="fas fa-spinner fa-spin"></i> Calculating fines...</div>';
    document.getElementById('returnModal').classList.add('open');

    fetch('borrowmanagement?action=previewReturn&recordId=' + recordId)
        .then(r => r.json())
        .then(data => {
            _overdueFine = data.overdueFine || 0;
            renderFinePreview(data.overdueDays, data.overdueFine, 0);
            document.getElementById('btnConfirm').disabled = false;
        })
        .catch(() => {
            document.getElementById('finePreview').innerHTML =
                '<span style="color:red">Failed to calculate fine. Please try again.</span>';
        });
}

function renderFinePreview(overdueDays, overdueFine, damageFine) {
    const total = overdueFine + damageFine;
    let html = '';

    if (overdueDays === 0 && damageFine === 0) {
        html = '<span class="no-fine"><i class="fas fa-check-circle"></i> No fine — book returned on time.</span>';
    } else {
        html = '<strong style="display:block;margin-bottom:6px;color:#856404">'
             + '<i class="fas fa-receipt"></i> Fine Summary (collected at counter)</strong>';

        if (overdueDays > 0) {
            html += '<div class="fine-line">'
                  + '<span>Overdue fine (' + overdueDays + ' day(s) × 5,000 VND)</span>'
                  + '<span>' + overdueFine.toLocaleString() + ' VND</span></div>';
        }
        if (damageFine > 0) {
            html += '<div class="fine-line">'
                  + '<span>Damage fine</span>'
                  + '<span>' + damageFine.toLocaleString() + ' VND</span></div>';
        }
        html += '<div class="fine-line">'
              + '<span>TOTAL TO COLLECT</span>'
              + '<span>' + total.toLocaleString() + ' VND</span></div>';
    }
    document.getElementById('finePreview').innerHTML = html;
}

function onDamageToggle(cb) {
    document.getElementById('damageSection').style.display = cb.checked ? 'block' : 'none';
    if (!cb.checked) {
        document.getElementById('damageAmount').value = '';
    }
    updateTotal();
}

function updateTotal() {
    const dmg = parseFloat(document.getElementById('damageAmount').value) || 0;
    const overdueDays = Math.round(_overdueFine / 5000);
    renderFinePreview(overdueDays, _overdueFine, dmg);
}

function confirmReturn() {
    const isDamaged  = document.getElementById('isDamaged').checked;
    const damageAmt  = parseFloat(document.getElementById('damageAmount').value) || 0;

    if (isDamaged && damageAmt <= 0) {
        alert('Please enter a valid damage fine amount (> 0 VND).');
        return false;
    }

    const overdueDays = Math.round(_overdueFine / 5000);
    const total = _overdueFine + (isDamaged ? damageAmt : 0);

    let msg = 'Confirm return and collect';
    if (total > 0) {
        msg = 'Confirm collecting ' + total.toLocaleString() + ' VND fine and returning book?';
    } else {
        msg = 'No fine due. Confirm book return?';
    }
    return confirm(msg);
}

function closeModal() {
    document.getElementById('returnModal').classList.remove('open');
}

// Close on overlay click
document.getElementById('returnModal').addEventListener('click', function(e) {
    if (e.target === this) closeModal();
});
</script>
</body>
</html>
