package dal;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.Book;

public class BookDAO {

    private static final int DEFAULT_PAGE_SIZE = 10;

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

    public List<Book> getHomeBooks(String q, Integer categoryId, String author, String publisher,
                                   String sort, int page, int pageSize) {
        List<Book> books = new ArrayList<>();
        if (pageSize <= 0) pageSize = DEFAULT_PAGE_SIZE;
        if (page <= 0) page = 1;

        String orderBy = buildOrderBy(sort);

        StringBuilder sql = new StringBuilder();
        sql.append("SELECT b.* ")
           .append("FROM Book b ")
           .append("WHERE 1=1 ");

        List<Object> params = new ArrayList<>();

        if (q != null && !q.isBlank()) {
            sql.append("AND (LOWER(b.title) LIKE ? OR LOWER(b.author) LIKE ? OR LOWER(b.publisher) LIKE ?) ");
            String like = "%" + q.toLowerCase() + "%";
            params.add(like);
            params.add(like);
            params.add(like);
        }
        if (categoryId != null) {
            sql.append("AND b.category_id = ? ");
            params.add(categoryId);
        }
        if (author != null && !author.isBlank()) {
            sql.append("AND LOWER(b.author) LIKE ? ");
            params.add("%" + author.toLowerCase() + "%");
        }
        if (publisher != null && !publisher.isBlank()) {
            sql.append("AND LOWER(b.publisher) LIKE ? ");
            params.add("%" + publisher.toLowerCase() + "%");
        }

        sql.append(orderBy)
           .append(" OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");

        int offset = (page - 1) * pageSize;
        params.add(offset);
        params.add(pageSize);

        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            bindParams(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) books.add(mapBook(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return books;
    }

    public int countHomeBooks(String q, Integer categoryId, String author, String publisher) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT COUNT(*) ")
           .append("FROM Book b ")
           .append("WHERE 1=1 ");

        List<Object> params = new ArrayList<>();

        if (q != null && !q.isBlank()) {
            sql.append("AND (LOWER(b.title) LIKE ? OR LOWER(b.author) LIKE ? OR LOWER(b.publisher) LIKE ?) ");
            String like = "%" + q.toLowerCase() + "%";
            params.add(like);
            params.add(like);
            params.add(like);
        }
        if (categoryId != null) {
            sql.append("AND b.category_id = ? ");
            params.add(categoryId);
        }
        if (author != null && !author.isBlank()) {
            sql.append("AND LOWER(b.author) LIKE ? ");
            params.add("%" + author.toLowerCase() + "%");
        }
        if (publisher != null && !publisher.isBlank()) {
            sql.append("AND LOWER(b.publisher) LIKE ? ");
            params.add("%" + publisher.toLowerCase() + "%");
        }

        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            bindParams(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<Book> getNewestBooks(int limit) {
        return queryTop("SELECT TOP (?) * FROM Book ORDER BY created_at DESC", limit);
    }

    public List<Book> getTopRatedBooks(int limit) {
        String sql = "SELECT TOP (?) b.* "
                   + "FROM Book b "
                   + "LEFT JOIN Review r ON b.book_id = r.book_id "
                   + "GROUP BY b.book_id, b.title, b.author, b.publisher, b.category_id, b.quantity, b.available, b.created_at, b.img_url "
                   + "ORDER BY AVG(CAST(r.rating AS FLOAT)) DESC, COUNT(r.review_id) DESC, b.created_at DESC";
        return queryTop(sql, limit);
    }

    public List<Book> getBestSellingBooks(int limit) {
        String sql = "SELECT TOP (?) b.* "
                   + "FROM Book b "
                   + "LEFT JOIN Borrow_Record br ON b.book_id = br.book_id "
                   + "GROUP BY b.book_id, b.title, b.author, b.publisher, b.category_id, b.quantity, b.available, b.created_at, b.img_url "
                   + "ORDER BY COUNT(br.record_id) DESC, b.created_at DESC";
        return queryTop(sql, limit);
    }

    public List<Book> getMostFavoritedBooks(int limit) {
        String sql = "SELECT TOP (?) b.* "
                   + "FROM Book b "
                   + "LEFT JOIN Favorite_Book fb ON b.book_id = fb.book_id "
                   + "GROUP BY b.book_id, b.title, b.author, b.publisher, b.category_id, b.quantity, b.available, b.created_at, b.img_url "
                   + "ORDER BY COUNT(fb.favorite_id) DESC, b.created_at DESC";
        return queryTop(sql, limit);
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

    private static String buildOrderBy(String sort) {
        // Default: newest
        if (sort == null) sort = "";
        switch (sort) {
            case "title_asc":
                return " ORDER BY b.title ASC ";
            case "title_desc":
                return " ORDER BY b.title DESC ";
            case "author_asc":
                return " ORDER BY b.author ASC ";
            case "author_desc":
                return " ORDER BY b.author DESC ";
            case "newest":
            default:
                return " ORDER BY b.created_at DESC ";
        }
    }

    private static void bindParams(PreparedStatement ps, List<Object> params) throws SQLException {
        for (int i = 0; i < params.size(); i++) {
            Object v = params.get(i);
            if (v instanceof Integer) ps.setInt(i + 1, (Integer) v);
            else ps.setObject(i + 1, v);
        }
    }

    private List<Book> queryTop(String sql, int limit) {
        List<Book> list = new ArrayList<>();
        if (limit <= 0) return list;
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapBook(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    private static Book mapBook(ResultSet rs) throws SQLException {
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
}