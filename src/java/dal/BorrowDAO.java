package dal;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.BorrowRecord;

public class BorrowDAO {

    public boolean canBorrow(int userId) {
        String sql = "SELECT COUNT(*) AS count FROM Borrow_Record "
                + "WHERE user_id = ? AND (LOWER(status) = 'borrowed' OR LOWER(status) = 'overdue')";

        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            if (rs.next() && rs.getInt("count") < 5) {
                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean borrowBook(int userId, int bookId) {
        Connection conn = null;

        try {
            conn = new DBContext().getConnection();
            conn.setAutoCommit(false);

            String checkSQL = "SELECT available FROM Book WHERE book_id = ?";
            PreparedStatement checkPs = conn.prepareStatement(checkSQL);
            checkPs.setInt(1, bookId);
            ResultSet rs = checkPs.executeQuery();

            if (rs.next() && rs.getInt("available") > 0) {

                String updateBookSQL = "UPDATE Book SET available = available - 1 WHERE book_id = ? AND available > 0";
                PreparedStatement updateBookPs = conn.prepareStatement(updateBookSQL);
                updateBookPs.setInt(1, bookId);
                int updated = updateBookPs.executeUpdate();

                if (updated > 0) {
                    String insertSQL = "INSERT INTO Borrow_Record (user_id, book_id, borrow_date, due_date, status) "
                            + "VALUES (?, ?, GETDATE(), DATEADD(DAY, 14, GETDATE()), 'borrowed')";
                    PreparedStatement insertPs = conn.prepareStatement(insertSQL);
                    insertPs.setInt(1, userId);
                    insertPs.setInt(2, bookId);
                    insertPs.executeUpdate();

                    conn.commit();
                    return true;
                }
            }

            conn.rollback();

        } catch (Exception e) {
            e.printStackTrace();
            try {
                if (conn != null) conn.rollback();
            } catch (Exception ex) {}
        } finally {
            try {
                if (conn != null) conn.setAutoCommit(true);
                if (conn != null) conn.close();
            } catch (Exception e) {}
        }

        return false;
    }

    public boolean returnBook(int recordId) {
        Connection conn = null;

        try {
            conn = new DBContext().getConnection();
            conn.setAutoCommit(false);

            String checkSql = "SELECT book_id FROM Borrow_Record WHERE record_id = ? AND (status = 'borrowed' OR status = 'overdue')";
            PreparedStatement checkPs = conn.prepareStatement(checkSql);
            checkPs.setInt(1, recordId);
            ResultSet rs = checkPs.executeQuery();

            if (rs.next()) {
                int bookId = rs.getInt("book_id");

                String updateBookSql = "UPDATE Book SET available = available + 1 WHERE book_id = ?";
                PreparedStatement updateBookPs = conn.prepareStatement(updateBookSql);
                updateBookPs.setInt(1, bookId);
                updateBookPs.executeUpdate();

                String updateBorrowSql = "UPDATE Borrow_Record SET return_date = GETDATE(), status = 'returned' WHERE record_id = ?";
                PreparedStatement updateBorrowPs = conn.prepareStatement(updateBorrowSql);
                updateBorrowPs.setInt(1, recordId);
                updateBorrowPs.executeUpdate();

                conn.commit();
                return true;
            }

            conn.rollback();

        } catch (Exception e) {
            e.printStackTrace();
            try {
                if (conn != null) conn.rollback();
            } catch (Exception ex) {}
        } finally {
            try {
                if (conn != null) conn.setAutoCommit(true);
                if (conn != null) conn.close();
            } catch (Exception e) {}
        }

        return false;
    }

    public List<BorrowRecord> getBorrowHistory(int userId) {
        List<BorrowRecord> list = new ArrayList<>();

        String sql = "SELECT b.record_id, b.book_id, bk.title AS book_title, b.borrow_date, b.due_date, b.return_date, b.status "
                + "FROM Borrow_Record b JOIN Book bk ON b.book_id = bk.book_id WHERE b.user_id = ? ORDER BY b.borrow_date DESC";

        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                BorrowRecord r = new BorrowRecord();
                r.setRecordId(rs.getInt("record_id"));
                r.setBookId(rs.getInt("book_id"));
                r.setBookTitle(rs.getString("book_title"));
                r.setBorrowDate(rs.getString("borrow_date"));
                r.setDueDate(rs.getString("due_date"));
                r.setReturnDate(rs.getString("return_date"));
                r.setStatus(rs.getString("status"));
                list.add(r);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public List<BorrowRecord> getAllBorrows() {
        List<BorrowRecord> list = new ArrayList<>();

        String sql = "SELECT b.record_id, b.book_id, bk.title AS book_title, b.borrow_date, b.due_date, b.return_date, b.status "
                + "FROM Borrow_Record b JOIN Book bk ON b.book_id = bk.book_id";

        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                BorrowRecord b = new BorrowRecord(
                        rs.getInt("record_id"),
                        rs.getInt("book_id"),
                        rs.getString("book_title"),
                        rs.getString("borrow_date"),
                        rs.getString("due_date"),
                        rs.getString("return_date"),
                        rs.getString("status")
                );
                list.add(b);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}