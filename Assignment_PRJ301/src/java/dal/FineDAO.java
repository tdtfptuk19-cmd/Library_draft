package dal;

import model.Fine;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FineDAO {

    // Insert fine as PAID immediately — used during return transaction
    // Fine is collected at counter before book is accepted back
    public void insertFinePaid(Connection conn, int recordId,
                               double amount, String reason, String fineType)
            throws SQLException {
        String sql = "INSERT INTO Fine (record_id, fine_amount, reason, fine_type, status) "
                   + "VALUES (?, ?, ?, ?, 'paid')";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, recordId);
            ps.setDouble(2, amount);
            ps.setString(3, reason);
            ps.setString(4, fineType);
            ps.executeUpdate();
        }
    }

    // Insert fine as UNPAID — kept for legacy/standalone fine creation
    public void insertFine(Connection conn, int recordId,
                           double amount, String reason, String fineType)
            throws SQLException {
        String sql = "INSERT INTO Fine (record_id, fine_amount, reason, fine_type, status) "
                   + "VALUES (?, ?, ?, ?, 'unpaid')";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, recordId);
            ps.setDouble(2, amount);
            ps.setString(3, reason);
            ps.setString(4, fineType);
            ps.executeUpdate();
        }
    }

    // Check if a fine of given type already exists for this record
    public boolean hasFine(int recordId, String fineType) {
        String sql = "SELECT COUNT(*) FROM Fine WHERE record_id = ? AND fine_type = ?";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, recordId);
            ps.setString(2, fineType);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (Exception e) { e.printStackTrace(); }
        return false;
    }

    // Collect (mark paid) a standalone unpaid fine
    public boolean collectFine(int fineId) {
        String sql = "UPDATE Fine SET status = 'paid' WHERE fine_id = ?";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, fineId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) { e.printStackTrace(); return false; }
    }

    public Fine getFineById(int fineId) {
        String sql = "SELECT f.fine_id, f.record_id, f.fine_amount, f.reason, "
             + "f.status, f.fine_type, CONVERT(VARCHAR, f.created_at, 120) AS created_at, "
             + "u.fullname AS borrower_name, b.title AS book_title, "
             + "CONVERT(VARCHAR, br.due_date, 23) AS due_date, "
             + "CONVERT(VARCHAR, br.return_date, 23) AS return_date "
             + "FROM Fine f "
             + "JOIN Borrow_Record br ON f.record_id = br.record_id "
             + "JOIN Users u ON br.user_id = u.user_id "
             + "JOIN Book b  ON br.book_id = b.book_id "
             + "WHERE f.fine_id = ?";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, fineId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    public Integer getFineOwnerUserId(int fineId) {
        String sql = "SELECT br.user_id "
                   + "FROM Fine f "
                   + "JOIN Borrow_Record br ON f.record_id = br.record_id "
                   + "WHERE f.fine_id = ?";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, fineId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return null;
    }

    // ── All fines — flat list for admin/librarian ────────────────────────────
    public List<Fine> getAllFines() {
        String sql = buildJoinSQL(false);
        return queryFines(sql, null);
    }

    // ── Borrower own fines ───────────────────────────────────────────────────
    public List<Fine> getFinesByUser(int userId) {
        String sql = buildJoinSQL(true);
        return queryFines(sql, userId);
    }

    // ── Grouped fines by record — for Fine Management screen ────────────────
    // Returns one Fine per RECORD (not per fine row), with:
    //   fineAmount = total across all fines for that record
    //   reason     = summary of fine types
    //   fineType   = "overdue" | "damaged" | "overdue+damaged"
    //   status     = "paid" if all paid, "unpaid" if any unpaid
    public List<Fine> getGroupedFines() {
        String sql = "SELECT "
            + "  MAX(f.fine_id)                          AS fine_id, "
            + "  f.record_id                             AS record_id, "
            + "  SUM(f.fine_amount)                      AS fine_amount, "
            + "  STRING_AGG(f.reason, ' | ')             AS reason, "
            + "  CASE "
            + "    WHEN COUNT(DISTINCT f.fine_type) > 1  THEN 'overdue+damaged' "
            + "    ELSE MAX(f.fine_type) "
            + "  END                                     AS fine_type, "
            + "  CASE "
            + "    WHEN SUM(CASE WHEN f.status='unpaid' THEN 1 ELSE 0 END) > 0 "
            + "         THEN 'unpaid' ELSE 'paid' "
            + "  END                                     AS status, "
            + "  MAX(CONVERT(VARCHAR, f.created_at, 120)) AS created_at, "
            + "  u.fullname                              AS borrower_name, "
            + "  b.title                                 AS book_title, "
            + "  CONVERT(VARCHAR, br.due_date,    23)   AS due_date, "
            + "  CONVERT(VARCHAR, br.return_date, 23)   AS return_date "
            + "FROM Fine f "
            + "JOIN Borrow_Record br ON f.record_id = br.record_id "
            + "JOIN Users u          ON br.user_id  = u.user_id "
            + "JOIN Book  b          ON br.book_id  = b.book_id "
            + "GROUP BY f.record_id, u.fullname, b.title, "
            + "         br.due_date, br.return_date "
            + "ORDER BY MAX(f.created_at) DESC";

        List<Fine> list = new ArrayList<>();
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    // ── private helpers ──────────────────────────────────────────────────────
    private String buildJoinSQL(boolean filterByUser) {
        return "SELECT f.fine_id, f.record_id, f.fine_amount, f.reason, "
             + "f.status, f.fine_type, CONVERT(VARCHAR, f.created_at, 120) AS created_at, "
             + "u.fullname AS borrower_name, b.title AS book_title, "
             + "CONVERT(VARCHAR, br.due_date, 23) AS due_date, "
             + "CONVERT(VARCHAR, br.return_date, 23) AS return_date "
             + "FROM Fine f "
             + "JOIN Borrow_Record br ON f.record_id = br.record_id "
             + "JOIN Users u ON br.user_id = u.user_id "
             + "JOIN Book b  ON br.book_id = b.book_id "
             + (filterByUser ? "WHERE br.user_id = ? " : "")
             + "ORDER BY f.created_at DESC";
    }

    private List<Fine> queryFines(String sql, Integer userId) {
        List<Fine> list = new ArrayList<>();
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            if (userId != null) ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) list.add(mapRow(rs));
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }

    private Fine mapRow(ResultSet rs) throws SQLException {
        Fine f = new Fine();
        f.setFineId(rs.getInt("fine_id"));
        f.setRecordId(rs.getInt("record_id"));
        f.setFineAmount(rs.getDouble("fine_amount"));
        f.setReason(rs.getString("reason"));
        f.setStatus(rs.getString("status"));
        f.setFineType(rs.getString("fine_type"));
        f.setCreatedAt(rs.getString("created_at"));
        f.setBorrowerName(rs.getString("borrower_name"));
        f.setBookTitle(rs.getString("book_title"));
        f.setDueDate(rs.getString("due_date"));
        f.setReturnDate(rs.getString("return_date"));
        return f;
    }
}
