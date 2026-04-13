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
import java.util.List;
import model.Book;
import RBAC.BaseRBACAccessControl;
import jakarta.servlet.http.HttpSession;
import model.User;

/**
 *
 * @author thien
 */
@WebServlet(name = "CatalogServlet", urlPatterns = {"/catalog"})
public class CatalogServlet extends BaseRBACAccessControl {

    private BookDAO bookDAO = new BookDAO();


    @Override
    protected void doGetAccess(HttpServletRequest request, HttpServletResponse response, User acc) throws ServletException, IOException {
        String keyword = request.getParameter("keyword");
        if (keyword == null || keyword.trim().isEmpty()) {
            keyword = (String) request.getAttribute("keyword");
        }
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null) {
            response.sendRedirect("login");
            return;
        }
        doPostAccess(request, response, user);
    }

    @Override
    protected void doPostAccess(HttpServletRequest request, HttpServletResponse response, User acc) throws ServletException, IOException {
       String keyword = request.getParameter("keyword");
        if (keyword != null && !keyword.trim().isEmpty()) {
            request.setAttribute("keyword", keyword);
        }
        String category = request.getParameter("category");

        List<Book> books;
        if (keyword != null && !keyword.trim().isEmpty()) {
            books = bookDAO.searchBook(keyword);
            request.setAttribute("keyword", keyword);
        } else if (category != null && !category.trim().isEmpty() && !category.trim().equals("All")) {
            books = bookDAO.getBooksByCategory(category);
        } else {
            books = bookDAO.getAllBook();
        }
        request.setAttribute("books", books);
        request.getRequestDispatcher("Catalog.jsp").forward(request, response);
    }

}
