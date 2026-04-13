/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import RBAC.BaseRBACAccessControl;
import dal.BookDAO;
import dal.CategoryDAO;
import java.util.List;
import model.Book;
import model.Category;
import model.User;

/**
 *
 * @author thien
 */
@WebServlet(name = "HomeServlet", urlPatterns = {"/home"})
public class HomeServlet extends BaseRBACAccessControl{

    @Override
    protected void doGetAccess(HttpServletRequest request, HttpServletResponse response, User acc) throws ServletException, IOException {
        BookDAO bookDAO = new BookDAO();
        CategoryDAO categoryDAO = new CategoryDAO();

        String q = request.getParameter("q");
        String author = request.getParameter("author");
        String publisher = request.getParameter("publisher");
        String sort = request.getParameter("sort");
        String pageRaw = request.getParameter("page");
        String categoryIdRaw = request.getParameter("categoryId");

        Integer categoryId = null;
        try {
            if (categoryIdRaw != null && !categoryIdRaw.isBlank()) {
                categoryId = Integer.parseInt(categoryIdRaw);
            }
        } catch (NumberFormatException ignored) {}

        int page = 1;
        try {
            if (pageRaw != null && !pageRaw.isBlank()) {
                page = Integer.parseInt(pageRaw);
            }
        } catch (NumberFormatException ignored) {}

        int pageSize = 10;
        List<Book> books = bookDAO.getHomeBooks(q, categoryId, author, publisher, sort, page, pageSize);
        int total = bookDAO.countHomeBooks(q, categoryId, author, publisher);
        int totalPages = (int) Math.ceil(total / (double) pageSize);
        if (totalPages <= 0) totalPages = 1;
        if (page > totalPages) page = totalPages;

        // Home sections
        request.setAttribute("featuredBooks", bookDAO.getTopRatedBooks(8));
        request.setAttribute("newBooks", bookDAO.getNewestBooks(8));
        request.setAttribute("bestSellingBooks", bookDAO.getBestSellingBooks(8));
        request.setAttribute("mostFavoritedBooks", bookDAO.getMostFavoritedBooks(8));

        List<Category> categories = categoryDAO.getAllCategories();
        request.setAttribute("categories", categories);

        // Search results list
        request.setAttribute("books", books);
        request.setAttribute("page", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("total", total);

        // Keep filters in UI
        request.setAttribute("q", q);
        request.setAttribute("author", author);
        request.setAttribute("publisher", publisher);
        request.setAttribute("sort", sort == null ? "newest" : sort);
        request.setAttribute("categoryId", categoryId);

        request.getRequestDispatcher("homepage.jsp").forward(request, response);
    }

    @Override
    protected void doPostAccess(HttpServletRequest request, HttpServletResponse response, User acc) throws ServletException, IOException {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}
