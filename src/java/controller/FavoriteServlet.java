/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import dal.FavoriteDAO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.User;
import RBAC.BaseRBACAccessControl;

/**
 *
 * @author thien
 */
@WebServlet(name = "FavoriteServlet", urlPatterns = {"/favorite"})
public class FavoriteServlet extends BaseRBACAccessControl {

    private FavoriteDAO favoriteDAO = new FavoriteDAO();


    @Override
    protected void doGetAccess(HttpServletRequest request, HttpServletResponse response, User acc) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        int userId = user.getId();
        session.setAttribute("favorites", favoriteDAO.getFavorites(userId));
        request.getRequestDispatcher("favorites.jsp").forward(request, response);
    }

    @Override
    protected void doPostAccess(HttpServletRequest request, HttpServletResponse response, User acc) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        String action = request.getParameter("action");
        int userId = user.getId();
        int bookId = Integer.parseInt(request.getParameter("bookId"));

        if ("add".equals(action)) {
            if (favoriteDAO.addFavorite(userId, bookId)) {
                request.setAttribute("success", "Book added to favorites.");
            } else {
                request.setAttribute("error", "Book already in favorites.");
            }
        } else if ("remove".equals(action)) {
            if (favoriteDAO.removeFavorite(userId, bookId)) {
                request.setAttribute("success", "Book removed from favorites.");
            } else {
                request.setAttribute("error", "Failed to remove book.");
            }
        }
        session.setAttribute("favorites", favoriteDAO.getFavorites(userId));
        response.sendRedirect("favorite");
    }

}
