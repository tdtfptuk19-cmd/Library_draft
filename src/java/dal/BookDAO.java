package dal;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Book;

public class BookDAO {

    public List<Book> getAllBook() {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM Book";

        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Book book = new Book();
                book.setBookId(rs.getInt("book_id"));
                book.setTitle(rs.getString("title"));
                book.setAuthor(rs.getString("author"));
                book.setPublisher(rs.getString("publisher"));
                book.setCategoryId(rs.getInt("category_id"));
                book.setQuantity(rs.getInt("quantity"));
                book.setAvailable(rs.getInt("available"));
                book.setImgUrl(rs.getString("img_url"));
                books.add(book);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return books;
    }

    public List<Book> getBooksByCategory(String categoryName) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT b.* FROM Book b JOIN Category c ON b.category_id = c.category_id WHERE c.category_name = ?";

        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, categoryName);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Book book = new Book();
                book.setBookId(rs.getInt("book_id"));
                book.setTitle(rs.getString("title"));
                book.setAuthor(rs.getString("author"));
                book.setPublisher(rs.getString("publisher"));
                book.setCategoryId(rs.getInt("category_id"));
                book.setQuantity(rs.getInt("quantity"));
                book.setAvailable(rs.getInt("available"));
                book.setImgUrl(rs.getString("img_url"));
                books.add(book);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return books;
    }

    public Book getBookById(int bookId) {
        String sql = "SELECT * FROM Book WHERE book_id = ?";

        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, bookId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Book book = new Book();
                book.setBookId(rs.getInt("book_id"));
                book.setTitle(rs.getString("title"));
                book.setAuthor(rs.getString("author"));
                book.setPublisher(rs.getString("publisher"));
                book.setCategoryId(rs.getInt("category_id"));
                book.setQuantity(rs.getInt("quantity"));
                book.setAvailable(rs.getInt("available"));
                book.setImgUrl(rs.getString("img_url"));
                return book;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<Book> searchBook(String keyword) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT b.* FROM Book b JOIN Category c ON b.category_id = c.category_id "
                + "WHERE LOWER(c.category_name) LIKE LOWER(?) OR LOWER(b.title) LIKE LOWER(?) "
                + "OR LOWER(b.author) LIKE LOWER(?) OR LOWER(b.publisher) LIKE LOWER(?)";

        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            for (int i = 1; i <= 4; i++) {
                ps.setString(i, "%" + keyword + "%");
            }

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Book book = new Book();
                book.setBookId(rs.getInt("book_id"));
                book.setTitle(rs.getString("title"));
                book.setAuthor(rs.getString("author"));
                book.setPublisher(rs.getString("publisher"));
                book.setCategoryId(rs.getInt("category_id"));
                book.setQuantity(rs.getInt("quantity"));
                book.setAvailable(rs.getInt("available"));
                book.setImgUrl(rs.getString("img_url"));
                books.add(book);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return books;
    }

    public void deleteBook(int bookId) {
        String sql = "DELETE FROM Book WHERE book_id = ?";

        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, bookId);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addBook(Book book) {
        String sql = "INSERT INTO Book (title, author, publisher, category_id, quantity, available, img_url) VALUES (?,?,?,?,?,?,?)";

        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, book.getTitle());
            ps.setString(2, book.getAuthor());
            ps.setString(3, book.getPublisher());
            ps.setInt(4, book.getCategoryId());
            ps.setInt(5, book.getQuantity());
            ps.setInt(6, book.getAvailable());
            ps.setString(7, book.getImgUrl());

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateBook(Book book) {
        String sql = "UPDATE Book SET title=?, author=?, publisher=?, category_id=?, quantity=?, available=?, img_url=? WHERE book_id=?";

        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, book.getTitle());
            ps.setString(2, book.getAuthor());
            ps.setString(3, book.getPublisher());
            ps.setInt(4, book.getCategoryId());
            ps.setInt(5, book.getQuantity());
            ps.setInt(6, book.getAvailable());
            ps.setString(7, book.getImgUrl());
            ps.setInt(8, book.getBookId());

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}