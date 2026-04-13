package controller;

import dal.StatisticDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import model.User;
import model.BookStatistic;
import RBAC.BaseRBACAccessControl;

@WebServlet(name = "StatisticServlet", urlPatterns = {"/statistic"})
public class StatisticServlet extends BaseRBACAccessControl {

    private StatisticDAO statisticDAO = new StatisticDAO();

    @Override
    protected void doGetAccess(HttpServletRequest request, HttpServletResponse response, User acc)
            throws ServletException, IOException {

        try {
            // 🔹 Tổng quan
            int totalUsers = statisticDAO.getTotalUsers();
            int totalBooks = statisticDAO.getTotalBooks();
            int totalBorrows = statisticDAO.getTotalBorrows();
            int totalBorrowing = statisticDAO.getTotalBorrowing();
            int totalOverdue = statisticDAO.getTotalOverdue();

            // 🔹 Top sách
            List<BookStatistic> topBooks = statisticDAO.getTopBorrowedBooks();

            // 🔹 Set attribute
            request.setAttribute("totalUsers", totalUsers);
            request.setAttribute("totalBooks", totalBooks);
            request.setAttribute("totalBorrows", totalBorrows);
            request.setAttribute("totalBorrowing", totalBorrowing);
            request.setAttribute("totalOverdue", totalOverdue);
            request.setAttribute("topBooks", topBooks);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Cannot load statistics!");
        }

        request.getRequestDispatcher("Statistic.jsp").forward(request, response);
    }

    @Override
    protected void doPostAccess(HttpServletRequest request, HttpServletResponse response, User acc)
            throws ServletException, IOException {
        response.sendRedirect("statistic");
    }
}