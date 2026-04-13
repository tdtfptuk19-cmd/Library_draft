package dal;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.BookStatistic;

public class StatisticDAO {

    // 🔹 Tổng user
    public int getTotalUsers() {
        String sql = "SELECT COUNT(*) FROM Users";

        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    // 🔹 Tổng sách
    public int getTotalBooks() {
        String sql = "SELECT COUNT(*) FROM Book";

        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    // 🔹 Tổng lượt mượn
    public int getTotalBorrows() {
        String sql = "SELECT COUNT(*) FROM Borrow_Record";

        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    // 🔹 Đang mượn
    public int getTotalBorrowing() {
        String sql = "SELECT COUNT(*) FROM Borrow_Record WHERE status = 'borrowed'";

        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    // 🔹 Quá hạn
    public int getTotalOverdue() {
        String sql = "SELECT COUNT(*) FROM Borrow_Record WHERE status = 'overdue'";

        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    // 🔹 Top sách mượn nhiều
    public List<BookStatistic> getTopBorrowedBooks() {
        List<BookStatistic> list = new ArrayList<>();

        String sql = """
            SELECT TOP 5 b.title, COUNT(*) AS total
            FROM Borrow_Record br
            JOIN Book b ON br.book_id = b.book_id
            GROUP BY b.title
            ORDER BY total DESC
        """;

        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new BookStatistic(
                        rs.getString("title"),
                        rs.getInt("total")
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
}