/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import dal.BookDAO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Book;
import RBAC.BaseRBACAccessControl;
import dal.BorrowDAO;
import dal.ReviewDAO;
import jakarta.servlet.http.HttpSession;
import java.util.List;
import model.Review;
import model.ReviewDTO;
import model.User;
/**
 *
 * @author thien
 */
@WebServlet(name = "ViewDetailServlet", urlPatterns = {"/viewdetail"})
public class ViewDetailServlet extends BaseRBACAccessControl {

    private BookDAO bookDAO = new BookDAO();
    private ReviewDAO reviewDAO = new ReviewDAO();
    private BorrowDAO borrowDAO = new BorrowDAO();
   

    @Override
    protected void doGetAccess(HttpServletRequest request, HttpServletResponse response, User acc) throws ServletException, IOException {
        String id_raw = request.getParameter("bookId");
        try{
        int bookId = Integer.parseInt(id_raw);
        Book book = bookDAO.getBookById(bookId);
        request.setAttribute("book", book);
        
        List<ReviewDTO > reviews = reviewDAO.getReviewBookById(bookId);
        request.setAttribute("reviews", reviews);
        request.getRequestDispatcher("viewdetail.jsp").forward(request, response);
        }catch(NumberFormatException e){
            response.sendRedirect("home");
        }
    }

    @Override
    protected void doPostAccess(HttpServletRequest request, HttpServletResponse response, User acc) throws ServletException, IOException {
        HttpSession session = request.getSession();
        
    User user = (User) session.getAttribute("user");

    if (user == null) {
        response.sendRedirect("login");
        return;
    }

    int bookId = Integer.parseInt(request.getParameter("bookId"));

    // 🔥 GỌI DAO Ở ĐÂY
    boolean result = borrowDAO.borrowBook(user.getId(), bookId);

    if (result) {
        session.setAttribute("success", "Borrow successful!");
    } else {
        session.setAttribute("error", "Borrow failed (maybe out of stock)");
    }

    // 🔥 QUAN TRỌNG: redirect sang mybooks
    response.sendRedirect("mybooks");
    }

}
