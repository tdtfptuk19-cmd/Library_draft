package controller;

import dal.BorrowDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import model.BorrowRecord;
import model.User;
import RBAC.BaseRBACAccessControl;

@WebServlet(name = "BorrowManagementServlet", urlPatterns = {"/borrowmanagement"})
public class borrowMagServlet extends BaseRBACAccessControl {

    private BorrowDAO borrowDAO = new BorrowDAO();

    @Override
    protected void doGetAccess(HttpServletRequest request, HttpServletResponse response, User acc)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        // 🔒 Check admin
        if (user == null || !"admin".equals(user.getRole())) {
            response.sendRedirect("login");
            return;
        }

        String action = request.getParameter("action");

        // 🎯 DEFAULT: show list
        if (action == null || action.equals("list")) {
            loadBorrowList(request, response);
            return;
        }

        // 🎯 RETURN BOOK
        if (action.equals("return")) {
            try {
                int borrowId = Integer.parseInt(request.getParameter("borrowId"));

                boolean success = borrowDAO.returnBook(borrowId);

                if (success) {
                    session.setAttribute("success", "Return book successfully!");
                } else {
                    session.setAttribute("error", "Return failed!");
                }

            } catch (Exception e) {
                session.setAttribute("error", "Invalid borrow ID!");
            }

            // 🔥 redirect để tránh duplicate submit
            response.sendRedirect("borrowmanagement");
            return;
        }

        // ❌ fallback
        response.sendRedirect("borrowmanagement");
    }

    @Override
    protected void doPostAccess(HttpServletRequest request, HttpServletResponse response, User acc)
            throws ServletException, IOException {

        // 👉 POST dùng cho action như return
        doGetAccess(request, response, acc);
    }

    // ✅ Load list riêng (clean code)
    private void loadBorrowList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<BorrowRecord> borrows = borrowDAO.getAllBorrows();

        request.setAttribute("borrows", borrows);
        request.getRequestDispatcher("borrowmanagement.jsp").forward(request, response);
    }
}