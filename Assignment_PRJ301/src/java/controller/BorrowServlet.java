package controller;

import dal.BorrowDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import model.User;
import RBAC.BaseRBACAccessControl;

@WebServlet(name = "BorrowServlet", urlPatterns = {"/borrow"})
public class BorrowServlet extends BaseRBACAccessControl {

    private BorrowDAO borrowDAO = new BorrowDAO();

    @Override
    protected void doGetAccess(HttpServletRequest request, HttpServletResponse response, User acc)
            throws ServletException, IOException {

        // 👉 Không xử lý GET → luôn redirect về mybooks
        response.sendRedirect("mybooks");
    }

    @Override
    protected void doPostAccess(HttpServletRequest request, HttpServletResponse response, User acc)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        // 🔒 Check login
        if (user == null) {
            response.sendRedirect("login");
            return;
        }

        String action = request.getParameter("action");

        try {
            switch (action) {

                case "borrow":
                    handleBorrow(request, session, user);
                    break;

                case "return":
                    handleReturn(request, session);
                    break;

                default:
                    session.setAttribute("error", "Invalid action!");
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("error", "Something went wrong!");
        }

        // 🔥 PRG pattern (Post → Redirect → Get)
        response.sendRedirect("mybooks");
    }

    // ================================
    // 🔹 HANDLE BORROW
    // ================================
    private void handleBorrow(HttpServletRequest request, HttpSession session, User user) {

        int userId = user.getId();
        int bookId = Integer.parseInt(request.getParameter("bookId"));

        if (!borrowDAO.canBorrow(userId)) {
            session.setAttribute("error", "You can only borrow up to 5 books.");
            return;
        }

        boolean success = borrowDAO.borrowBook(userId, bookId);

        if (success) {
            session.setAttribute("success", "Borrow successful!");
        } else {
            session.setAttribute("error", "Book is not available.");
        }
    }

    // ================================
    // 🔹 HANDLE RETURN
    // ================================
    private void handleReturn(HttpServletRequest request, HttpSession session) {

        int recordId = Integer.parseInt(request.getParameter("recordId"));

        boolean success = borrowDAO.returnBook(recordId, false, 0);

        if (success) {
            session.setAttribute("success", "Return successful!");
        } else {
            session.setAttribute("error", "Return failed!");
        }
    }
}
