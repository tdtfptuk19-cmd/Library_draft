package dal;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Book;
import model.FavoriteBook;

public class FavoriteDAO {

    public boolean addFavorite(int userId, int bookId) {
        String checkSql = "SELECT 1 FROM Favorite_Book WHERE user_id = ? AND book_id = ?";
        String insertSql = "INSERT INTO Favorite_Book (user_id, book_id) VALUES (?, ?)";

        try (Connection conn = new DBContext().getConnection()) {

            // check tồn tại
            try (PreparedStatement checkPs = conn.prepareStatement(checkSql)) {
                checkPs.setInt(1, userId);
                checkPs.setInt(2, bookId);
                ResultSet rs = checkPs.executeQuery();

                if (rs.next()) {
                    return false; // đã tồn tại
                }
            }

            // insert
            try (PreparedStatement insertPs = conn.prepareStatement(insertSql)) {
                insertPs.setInt(1, userId);
                insertPs.setInt(2, bookId);
                insertPs.executeUpdate();
                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean removeFavorite(int userId, int bookId) {
        String sql = "DELETE FROM Favorite_Book WHERE user_id = ? AND book_id = ?";

        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, bookId);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public List<FavoriteBook> getFavorites(int userId) {
        List<FavoriteBook> favorites = new ArrayList<>();

        String sql = "SELECT b.* FROM Favorite_Book f "
                   + "JOIN Book b ON f.book_id = b.book_id "
                   + "WHERE f.user_id = ?";

        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                FavoriteBook fb = new FavoriteBook();
                Book book = new Book();

                book.setBookId(rs.getInt("book_id"));
                book.setTitle(rs.getString("title"));
                book.setAuthor(rs.getString("author"));
                book.setPublisher(rs.getString("publisher"));
                book.setCategoryId(rs.getInt("category_id"));
                book.setQuantity(rs.getInt("quantity"));
                book.setAvailable(rs.getInt("available"));
                book.setImgUrl(rs.getString("img_url"));

                fb.setBook(book);
                favorites.add(fb);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return favorites;
    }
}