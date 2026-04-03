package dal;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.ReviewDTO;

public class ReviewDAO {

    public boolean addReview(int userId, int bookId, int rating, String comment) {
        String sql = "INSERT INTO Review (user_id, book_id, rating, comment) VALUES (?, ?, ?, ?)";

        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, bookId);
            ps.setInt(3, rating);
            ps.setString(4, comment);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public List<ReviewDTO> getReviewBookById(int bookId) {
        List<ReviewDTO> reviews = new ArrayList<>();

        String sql = "SELECT r.review_id, r.user_id, u.username, r.rating, r.comment "
                   + "FROM Review r JOIN Users u ON r.user_id = u.user_id "
                   + "WHERE r.book_id = ?";

        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, bookId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                ReviewDTO review = new ReviewDTO(
                        rs.getInt("review_id"),
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getInt("rating"),
                        rs.getString("comment")
                );
                reviews.add(review);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return reviews;
    }

    public boolean updateReview(int reviewId, int rating, String comment) {
        String sql = "UPDATE Review SET rating = ?, comment = ? WHERE review_id = ?";

        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, rating);
            ps.setString(2, comment);
            ps.setInt(3, reviewId);

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean deleteReview(int reviewId) {
        String sql = "DELETE FROM Review WHERE review_id = ?";

        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, reviewId);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}