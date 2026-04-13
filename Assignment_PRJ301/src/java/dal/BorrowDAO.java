package dal;

import model.BorrowRecord;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BorrowDAO {

    // ── Borrow limit check ───────────────────────────────────────────────────
    public boolean canBorrow(int userId) {
        String sql = "SELECT COUNT(*) AS cnt FROM Borrow_Record "
                   + "WHERE user_id = ? "
                   + "AND (LOWER(status) = 'borrowed' OR LOWER(status) = 'overdue')";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("cnt") < 5;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    // ── Borrow a book ────────────────────────────────────────────────────────
    public boolean borrowBook(int userId, int bookId) {
        Connection conn = null;
        try {
            conn = new DBContext().getConnection();
            conn.setAutoCommit(false);

            PreparedStatement chk = conn.prepareStatement(
                "SELECT available FROM Book WHERE book_id = ?");
            chk.setInt(1, bookId);
            ResultSet rs = chk.executeQuery();

            if (rs.next() && rs.getInt("available") > 0) {
                PreparedStatement upBook = conn.prepareStatement(
                    "UPDATE Book SET available = available - 1 "
                  + "WHERE book_id = ? AND available > 0");
                upBook.setInt(1, bookId);
                if (upBook.executeUpdate() > 0) {
                    PreparedStatement ins = conn.prepareStatement(
                        "INSERT INTO Borrow_Record "
                      + "(user_id, book_id, borrow_date, due_date, status) "
                      + "VALUES (?, ?, GETDATE(), DATEADD(DAY,14,GETDATE()), 'borrowed')");
                    ins.setInt(1, userId);
                    ins.setInt(2, bookId);
                    ins.executeUpdate();
                    conn.commit();
                    return true;
                }
            }
            conn.rollback();
        } catch (Exception e) {
            e.printStackTrace();
            try { if (conn != null) conn.rollback(); } catch (Exception ex) {}
        } finally { closeConn(conn); }
        return false;
    }

    // ── Calculate overdue days for a record (preview before return) ──────────
    public long calcOverdueDays(int recordId) {
        String sql = "SELECT due_date FROM Borrow_Record "
                   + "WHERE record_id = ? "
                   + "AND (LTRIM(RTRIM(LOWER(status))) = 'borrowed' OR LTRIM(RTRIM(LOWER(status))) = 'overdue')";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, recordId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Date dueDate = rs.getDate("due_date");
                Date today   = new Date(System.currentTimeMillis());
                long diff    = today.getTime() - dueDate.getTime();
                long days    = diff / (1000L * 60 * 60 * 24);
                return Math.max(0, days);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }

    // ── Return book — FINE FIRST, then return ────────────────────────────────
    // New flow: fine is created AND collected (paid) in the same transaction
    // as the return. No unpaid fine is ever left after return.
    public boolean returnBook(int recordId, boolean isDamaged, double damageAmount) {
        Connection conn = null;
        try {
            conn = new DBContext().getConnection();
            conn.setAutoCommit(false);

            // 1. Fetch record by ID first (status can be inconsistent in DB seeds)
            PreparedStatement sel = conn.prepareStatement(
                "SELECT book_id, due_date, status FROM Borrow_Record WHERE record_id = ?");
            sel.setInt(1, recordId);
            ResultSet rs = sel.executeQuery();

            if (!rs.next()) { conn.rollback(); return false; }

            int  bookId  = rs.getInt("book_id");
            Date dueDate = rs.getDate("due_date");
            String status = rs.getString("status");
            String normStatus = status == null ? "" : status.trim().toLowerCase();
            if ("returned".equals(normStatus)) {
                conn.rollback();
                return false;
            }
            Date today   = new Date(System.currentTimeMillis());

            // 2. Calculate overdue days
            long diffMs      = today.getTime() - dueDate.getTime();
            long overdueDays = Math.max(0, diffMs / (1000L * 60 * 60 * 24));

            FineDAO fineDAO = new FineDAO();

            // 3. Create overdue fine as UNPAID (user can pay online via QR)
            if (overdueDays > 0 && !fineDAO.hasFine(recordId, "overdue")) {
                double amount = overdueDays * 5000.0;
                String reason = "Overdue " + overdueDays + " day(s) - "
                              + (long)(overdueDays * 5000) + " VND";
                fineDAO.insertFine(conn, recordId, amount, reason, "overdue");
            }

            // 4. Create damage fine as UNPAID
            if (isDamaged && damageAmount > 0) {
                fineDAO.insertFine(conn, recordId, damageAmount,
                                   "Book returned in damaged condition", "damaged");
            }

            // 5. Mark record as returned
            PreparedStatement upRec = conn.prepareStatement(
                "UPDATE Borrow_Record "
              + "SET status = 'returned', return_date = GETDATE() "
              + "WHERE record_id = ? AND LTRIM(RTRIM(LOWER(status))) <> 'returned'");
            upRec.setInt(1, recordId);
            int updated = upRec.executeUpdate();
            if (updated == 0) {
                conn.rollback();
                return false;
            }

            // 6. Restore book availability
            PreparedStatement upBook = conn.prepareStatement(
                "UPDATE Book SET available = available + 1 WHERE book_id = ?");
            upBook.setInt(1, bookId);
            upBook.executeUpdate();

            conn.commit();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            try { if (conn != null) conn.rollback(); } catch (Exception ex) {}
        } finally { closeConn(conn); }
        return false;
    }

    // ── Borrower: own borrow history ─────────────────────────────────────────
    public List<BorrowRecord> getBorrowHistory(int userId) {
        String sql = "SELECT br.record_id, br.book_id, b.title AS book_title, "
                   + "CONVERT(VARCHAR, br.borrow_date, 23) AS borrow_date, "
                   + "CONVERT(VARCHAR, br.due_date,    23) AS due_date, "
                   + "CONVERT(VARCHAR, br.return_date, 23) AS return_date, "
                   + "br.status "
                   + "FROM Borrow_Record br "
                   + "JOIN Book b ON br.book_id = b.book_id "
                   + "WHERE br.user_id = ? "
                   + "ORDER BY br.borrow_date DESC";
        List<BorrowRecord> list = new ArrayList<>();
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs, false));
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // ── Admin/Librarian: all borrow records with borrower name ───────────────
    public List<BorrowRecord> getAllBorrows() {
        String sql = "SELECT br.record_id, br.book_id, b.title AS book_title, "
                   + "CONVERT(VARCHAR, br.borrow_date, 23) AS borrow_date, "
                   + "CONVERT(VARCHAR, br.due_date,    23) AS due_date, "
                   + "CONVERT(VARCHAR, br.return_date, 23) AS return_date, "
                   + "br.status, u.fullname AS borrower_name "
                   + "FROM Borrow_Record br "
                   + "JOIN Book b ON br.book_id = b.book_id "
                   + "JOIN Users u ON br.user_id = u.user_id "
                   + "ORDER BY br.borrow_date DESC";
        List<BorrowRecord> list = new ArrayList<>();
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs, true));
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    private BorrowRecord mapRow(ResultSet rs, boolean withBorrower) throws SQLException {
        BorrowRecord r = new BorrowRecord();
        r.setRecordId(rs.getInt("record_id"));
        r.setBookId(rs.getInt("book_id"));
        r.setBookTitle(rs.getString("book_title"));
        r.setBorrowDate(rs.getString("borrow_date"));
        r.setDueDate(rs.getString("due_date"));
        r.setReturnDate(rs.getString("return_date"));
        r.setStatus(rs.getString("status"));
        if (withBorrower) r.setBorrowerName(rs.getString("borrower_name"));
        return r;
    }

    private void closeConn(Connection conn) {
        try { if (conn != null) { conn.setAutoCommit(true); conn.close(); } } catch (Exception e) {}
    }
}
