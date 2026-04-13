package controller;

import dal.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import model.User;
import util.PasswordPolicy;
import util.PasswordUtil;

@WebServlet(name = "ResetPasswordServlet", urlPatterns = {"/reset-password"})
public class ResetPasswordServlet extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String token = request.getParameter("token");
        if (token == null || token.isBlank()) {
            response.sendRedirect("login");
            return;
        }
        request.setAttribute("token", token);
        request.getRequestDispatcher("resetPassword.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String token = request.getParameter("token");
        String password = request.getParameter("password");
        String confirm = request.getParameter("confirmPassword");

        request.setAttribute("token", token);

        if (token == null || token.isBlank()) {
            request.setAttribute("error", "Invalid reset token.");
            request.getRequestDispatcher("resetPassword.jsp").forward(request, response);
            return;
        }
        if (password == null || !password.equals(confirm)) {
            request.setAttribute("error", "Password and confirm password not match.");
            request.getRequestDispatcher("resetPassword.jsp").forward(request, response);
            return;
        }
        String policyError = PasswordPolicy.validate(password);
        if (policyError != null) {
            request.setAttribute("error", policyError);
            request.getRequestDispatcher("resetPassword.jsp").forward(request, response);
            return;
        }

        User u = userDAO.findByResetToken(token);
        if (u == null) {
            request.setAttribute("error", "Reset token expired or invalid.");
            request.getRequestDispatcher("resetPassword.jsp").forward(request, response);
            return;
        }

        String hashed = PasswordUtil.hashPassword(password.toCharArray());
        userDAO.updateUserPass(u.getId(), hashed);
        userDAO.clearPasswordResetToken(u.getId());

        request.setAttribute("success", "Password reset successfully. Please login.");
        request.getRequestDispatcher("login.jsp").forward(request, response);
    }
}

