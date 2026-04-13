package dal;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class ChartData {

    public int getTotalBooks() {
        String sql = "SELECT COALESCE(SUM(quantity), 0) AS total_books FROM Book";

        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("total_books");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public int getTotalUsers() {
        String sql = "SELECT COUNT(*) AS total_users FROM Users WHERE status = 'active'";

        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("total_users");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public int getCurrentLoans() {
        String sql = "SELECT COUNT(*) AS current_loans FROM Borrow_Record WHERE status = 'borrowed'";

        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("current_loans");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public int getOverdueBook() {
        String sql = "SELECT COUNT(*) AS overdue_books FROM Borrow_Record WHERE status = 'overdue'";

        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("overdue_books");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public Map<String, Map<String, Integer>> getMonthlyStats() {
        Map<String, Map<String, Integer>> stats = new HashMap<>();

        String sql = "SELECT FORMAT(borrow_date, 'yyyy-MM') AS month, " +
                     "COUNT(*) AS borrowed_count, " +
                     "SUM(CASE WHEN status = 'overdue' THEN 1 ELSE 0 END) AS overdue_count " +
                     "FROM Borrow_Record " +
                     "WHERE borrow_date >= DATEADD(MONTH, -5, DATEADD(MONTH, DATEDIFF(MONTH, 0, GETDATE()), 0)) " +
                     "AND borrow_date <= GETDATE() " +
                     "GROUP BY FORMAT(borrow_date, 'yyyy-MM') " +
                     "ORDER BY month";

        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String month = rs.getString("month");

                Map<String, Integer> counts = new HashMap<>();
                counts.put("borrowed", rs.getInt("borrowed_count"));
                counts.put("overdue", rs.getInt("overdue_count"));

                stats.put(month, counts);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return stats;
    }

    public Map<String, Integer> getCategoryStats() {
        Map<String, Integer> stats = new HashMap<>();

        String sql = "SELECT c.category_name, COALESCE(SUM(b.quantity), 0) AS total " +
                     "FROM Category c " +
                     "LEFT JOIN Book b ON b.category_id = c.category_id " +
                     "GROUP BY c.category_name";

        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                stats.put(rs.getString("category_name"), rs.getInt("total"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return stats;
    }
}