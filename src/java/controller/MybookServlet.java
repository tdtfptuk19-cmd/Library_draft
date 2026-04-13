/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import dal.BorrowDAO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import model.BorrowRecord;
import model.User;
import RBAC.BaseRBACAccessControl;

/**
 *
 * @author thien
 */
@WebServlet(name = "MybookServlet", urlPatterns = {"/mybooks"})
public class MybookServlet extends BaseRBACAccessControl {

    private BorrowDAO borrowDAO = new BorrowDAO();

    @Override
    protected void doGetAccess(HttpServletRequest request, HttpServletResponse response, User acc) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null) {
            response.sendRedirect("login");
            return;
        }

        // 🔥 LOAD MỚI TỪ DB
        List<BorrowRecord> borrowHistory = borrowDAO.getBorrowHistory(user.getId());

        // 🔥 QUAN TRỌNG: dùng request
        request.setAttribute("borrowHistory", borrowHistory);

        request.getRequestDispatcher("mybooks.jsp").forward(request, response);
    }

    @Override
    protected void doPostAccess(HttpServletRequest request, HttpServletResponse response, User acc) throws ServletException, IOException {
        String recordIdRaw = request.getParameter("recordId");

        if (recordIdRaw != null) {
            int recordId = Integer.parseInt(recordIdRaw);

            boolean success = borrowDAO.returnBook(recordId);

            System.out.println("Return result: " + success);
        }

        // 🔥 QUAN TRỌNG
        doGetAccess(request, response, acc);
    }

}
