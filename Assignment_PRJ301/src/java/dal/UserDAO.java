package dal;

import model.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import util.PasswordUtil;

public class UserDAO {

    public User login(String username, String password) {
        String sql = "SELECT * FROM Users WHERE username = ?";

        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String storedPassword = rs.getString("password");
                if (!PasswordUtil.verifyPassword(password.toCharArray(), storedPassword)) {
                    return null;
                }

                // Auto-upgrade legacy plain-text password to PBKDF2 after successful login
                if (!PasswordUtil.isHashedFormat(storedPassword)) {
                    String upgraded = PasswordUtil.hashPassword(password.toCharArray());
                    updateUserPass(rs.getInt("user_id"), upgraded);
                    storedPassword = upgraded;
                }

                return new User(
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        storedPassword,
                        rs.getString("email"),
                        rs.getString("fullname"),
                        trim(rs.getString("role")),
                        trim(rs.getString("status")),
                        rs.getString("img_url"),
                        safeGet(rs, "phone")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean addUser(User user) {
        String sql = "INSERT INTO Users (username, password, email, fullname, role, status, img_url, phone) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user.getUsername());
            ps.setString(2, PasswordUtil.hashPassword(user.getPassword().toCharArray()));
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getFullname());
            ps.setString(5, user.getRole());
            ps.setString(6, user.getStatus());
            ps.setString(7, user.getImgUrl());
            ps.setString(8, user.getPhone());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean isUsernameExists(String username) {
        String sql = "SELECT COUNT(*) FROM Users WHERE username = ?";

        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public boolean isEmailExists(String email) {
        String sql = "SELECT COUNT(*) FROM Users WHERE email = ?";

        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public void updateUserImg(int userId, String imgUrl) {
        String sql = "UPDATE Users SET img_url = ? WHERE user_id = ?";

        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, imgUrl);
            ps.setInt(2, userId);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateUserPass(int userId, String password) {
        String sql = "UPDATE Users SET password = ? WHERE user_id = ?";

        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, password);
            ps.setInt(2, userId);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM Users";

        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                User user = new User(
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("email"),
                        rs.getString("fullname"),
                        trim(rs.getString("role")),
                        trim(rs.getString("status")),
                        rs.getString("img_url"),
                        safeGet(rs, "phone")
                );
                users.add(user);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return users;
    }

    public void deleteUser(int userId) {
        String sql = "DELETE FROM Users WHERE user_id = ?";

        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateUser(User user) {
        String sql = "UPDATE Users SET username = ?, password = ?, email = ?, fullname = ?, role = ?, phone = ? WHERE user_id = ?";

        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, user.getUsername());
            String passToStore = user.getPassword();
            if (!PasswordUtil.isHashedFormat(passToStore)) {
                passToStore = PasswordUtil.hashPassword(passToStore.toCharArray());
            }
            ps.setString(2, passToStore);
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getFullname());
            ps.setString(5, user.getRole());
            ps.setString(6, user.getPhone());
            ps.setInt(7, user.getId());

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public User getUserId(int userId) {
        String sql = "SELECT * FROM Users WHERE user_id = ?";

        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new User(
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("email"),
                        rs.getString("fullname"),
                        trim(rs.getString("role")),
                        trim(rs.getString("status")),
                        rs.getString("img_url"),
                        safeGet(rs, "phone")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<String> getPermissions(User user) {
        List<String> permissions = new ArrayList<>();
        String sql = "SELECT canAccess FROM Permission WHERE role = ?";

        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String role = user == null ? null : trim(user.getRole());
            ps.setString(1, role);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String p = rs.getString("canAccess");
                if (p != null) permissions.add(p.trim());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        // Fallback defaults so the app still works even if Permission seed wasn't applied.
        // RBAC is prefix-based, so keep these as clean servlet paths (no query string).
        String role = user == null ? null : trim(user.getRole());
        if ("user".equalsIgnoreCase(role)) {
            addIfMissing(permissions, "/home");
            addIfMissing(permissions, "/catalog");
            addIfMissing(permissions, "/viewdetail");
            addIfMissing(permissions, "/mybooks");
            addIfMissing(permissions, "/favorite");
            addIfMissing(permissions, "/borrow");
            addIfMissing(permissions, "/fine");
            addIfMissing(permissions, "/qrpay");
            addIfMissing(permissions, "/profile");
            addIfMissing(permissions, "/logout");
        } else if ("admin".equalsIgnoreCase(role) || "librarian".equalsIgnoreCase(role)) {
            addIfMissing(permissions, "/home");
            addIfMissing(permissions, "/catalog");
            addIfMissing(permissions, "/viewdetail");
            addIfMissing(permissions, "/mybooks");
            addIfMissing(permissions, "/favorite");
            addIfMissing(permissions, "/fine");
            addIfMissing(permissions, "/qrpay");
            addIfMissing(permissions, "/profile");
            addIfMissing(permissions, "/logout");
        }

        return permissions;
    }

    public User findByEmail(String email) {
        String sql = "SELECT * FROM Users WHERE email = ?";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new User(
                            rs.getInt("user_id"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("email"),
                            rs.getString("fullname"),
                            trim(rs.getString("role")),
                            trim(rs.getString("status")),
                            rs.getString("img_url"),
                            safeGet(rs, "phone")
                    );
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void setPasswordResetToken(int userId, String token, Timestamp expiresAt) {
        String sql = "UPDATE Users SET reset_token = ?, reset_expires = ? WHERE user_id = ?";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, token);
            ps.setTimestamp(2, expiresAt);
            ps.setInt(3, userId);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public User findByResetToken(String token) {
        String sql = "SELECT * FROM Users WHERE reset_token = ? AND reset_expires IS NOT NULL AND reset_expires > GETDATE()";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, token);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new User(
                            rs.getInt("user_id"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("email"),
                            rs.getString("fullname"),
                            trim(rs.getString("role")),
                            trim(rs.getString("status")),
                            rs.getString("img_url"),
                            safeGet(rs, "phone")
                    );
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void clearPasswordResetToken(int userId) {
        String sql = "UPDATE Users SET reset_token = NULL, reset_expires = NULL WHERE user_id = ?";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateProfileInfo(int userId, String fullname, String email, String phone) {
        String sql = "UPDATE Users SET fullname = ?, email = ?, phone = ? WHERE user_id = ?";
        try (Connection conn = new DBContext().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, fullname);
            ps.setString(2, email);
            ps.setString(3, phone);
            ps.setInt(4, userId);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String safeGet(ResultSet rs, String column) {
        try {
            return rs.getString(column);
        } catch (SQLException e) {
            return null;
        }
    }

    private static String trim(String s) {
        return s == null ? null : s.trim();
    }

    private static void addIfMissing(List<String> list, String permission) {
        if (permission == null) return;
        for (String p : list) {
            if (permission.equals(p)) return;
        }
        list.add(permission);
    }
}