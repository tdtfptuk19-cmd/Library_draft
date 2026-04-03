/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import dal.UserDAO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import model.User;
import RBAC.BaseRBACAccessControl;

/**
 *
 * @author thien
 */
@WebServlet(name = "AccountServlet", urlPatterns = {"/account"})
public class AccountServlet extends BaseRBACAccessControl {

    private UserDAO userDAO = new UserDAO();

  
    @Override
    protected void doGetAccess(HttpServletRequest request, HttpServletResponse response, User acc) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null || !"admin".equals(user.getRole())) {
            response.sendRedirect("login");
            return;
        }

        String action = request.getParameter("action");
        if ("accountmanagement".equals(action)) {
            List<User> users = userDAO.getAllUsers();
            request.setAttribute("users", users);
            request.getRequestDispatcher("accountManagement.jsp").forward(request, response);
        } else if ("editUser".equals(action)) {
            int userId = Integer.parseInt(request.getParameter("userId"));
            User selectedUser = userDAO.getUserId(userId);
            if (selectedUser == null) {
                request.setAttribute("error", "User not found");
            } else {
                request.setAttribute("selectedUser", selectedUser);
            }
            request.setAttribute("users", userDAO.getAllUsers());
            request.getRequestDispatcher("accountManagement.jsp").forward(request, response);
        } else if ("deleteUser".equals(action)) {
            int userId = Integer.parseInt(request.getParameter("userId"));
            User userToDelete = userDAO.getUserId(userId);
            if (userToDelete != null) {
                userDAO.deleteUser(userId);
                request.setAttribute("success", "User deleted successfully!");
            } else {
                request.setAttribute("error", "User not found!");
            }
            List<User> users = userDAO.getAllUsers();
            request.setAttribute("users", users);
            request.getRequestDispatcher("accountManagement.jsp").forward(request, response);
        } else {
            response.sendRedirect("adminLogin");
        }
    }

    @Override
    protected void doPostAccess(HttpServletRequest request, HttpServletResponse response, User acc) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null || !"admin".equals(user.getRole())) {
            response.sendRedirect("login");
            return;
        }

        String action = request.getParameter("action");
        try {
            if ("updateUser".equals(action)) {
                int userId = Integer.parseInt(request.getParameter("userId"));
                String username = request.getParameter("username");
                String password = request.getParameter("password");
                String fullName = request.getParameter("fullName");
                String email = request.getParameter("email");
                String role = request.getParameter("role");
                User updatedUser = new User(userId, username, password, email, fullName, role, null, null);
                userDAO.updateUser(updatedUser);
                request.setAttribute("success", "User updated successfully!");
                List<User> users = userDAO.getAllUsers();
                request.setAttribute("users", users);
                request.getRequestDispatcher("accountManagement.jsp").forward(request, response);
            }
        } catch (Exception e) {
            request.setAttribute("error", "Error: " + e.getMessage());
            request.setAttribute("users", userDAO.getAllUsers());
            request.getRequestDispatcher("accountManagement.jsp").forward(request, response);
        }
    }

}
