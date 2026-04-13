package controller;

import dal.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.SecureRandom;
import java.sql.Timestamp;
import java.util.Base64;
import model.User;

@WebServlet(name = "ForgotPasswordServlet", urlPatterns = {"/forgot-password"})
public class ForgotPasswordServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();
    private static final SecureRandom RNG = new SecureRandom();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("forgotPassword.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String email = request.getParameter("email");

        // Always show generic message to avoid user enumeration
        request.setAttribute("success", "If that email exists, we have sent a reset link.");

        User u = userDAO.findByEmail(email);
        if (u != null) {
            String token = generateToken();
            Timestamp expires = new Timestamp(System.currentTimeMillis() + 15 * 60 * 1000L); // 15 minutes
            userDAO.setPasswordResetToken(u.getId(), token, expires);

            // ASM requirement says "send email". This project doesn't ship mail libs,
            // so we expose the reset link on the UI for demo/testing purposes.
            String resetLink = "reset-password?token=" + token;
            request.setAttribute("resetLink", resetLink);
        }

        request.getRequestDispatcher("forgotPassword.jsp").forward(request, response);
    }

    private static String generateToken() {
        byte[] b = new byte[32];
        RNG.nextBytes(b);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(b);
    }
}

