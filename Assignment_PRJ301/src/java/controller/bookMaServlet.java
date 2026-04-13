/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import dal.BookDAO;
import dal.CategoryDAO;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import java.io.File;
import java.util.List;
import model.Book;
import model.Category;
import model.User;
import RBAC.BaseRBACAccessControl;

/**
 *
 * @author thien
 */
@WebServlet(name = "bookMaServlet", urlPatterns = {"/admin"})
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 2, // 2MB
        maxFileSize = 1024 * 1024 * 10, // 10MB
        maxRequestSize = 1024 * 1024 * 50 // 50MB
)
public class bookMaServlet extends BaseRBACAccessControl {

    private BookDAO bookDAO = new BookDAO();
    private CategoryDAO categoryDAO = new CategoryDAO();
    private static final String UPLOAD_DIR = "img";

  
    private String extractFileName(Part part) {
        String contentDisp = part.getHeader("content-disposition");
        String[] items = contentDisp.split(";");
        for (String s : items) {
            if (s.trim().startsWith("filename")) {
                return s.substring(s.indexOf("=") + 2, s.length() - 1);
            }
        }
        return "";
    }

   
    @Override
    protected void doGetAccess(HttpServletRequest request, HttpServletResponse response, User acc) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null || (!"admin".equals(user.getRole()) && !"librarian".equals(user.getRole()))) {
            response.sendRedirect("login.jsp");
        }

        String action = request.getParameter("action");
        if ("bookmanagement".equals(action)) {
            List<Book> books = bookDAO.getAllBook();
            List<Category> categories = categoryDAO.getAllCategories();
            request.setAttribute("books", books);
            request.setAttribute("categories", categories);
            request.getRequestDispatcher("bookManegement.jsp").forward(request, response);
        } else if ("editBook".equals(action)) {
            int bookId = Integer.parseInt(request.getParameter("bookId"));
            Book book = bookDAO.getBookById(bookId);
            request.setAttribute("selectedBook", book);
            request.setAttribute("books", bookDAO.getAllBook());
            request.setAttribute("categories", categoryDAO.getAllCategories());
            request.getRequestDispatcher("bookManegement.jsp").forward(request, response);
        } else if ("deleteBook".equals(action)) {
            int bookId = Integer.parseInt(request.getParameter("bookId"));
            bookDAO.deleteBook(bookId);
            request.setAttribute("success", "Book seleted successfully");
            response.sendRedirect("admin?action=bookmanagement");
        } else {
            response.sendRedirect("admin?action=dashboard");
        }
    }

    @Override
    protected void doPostAccess(HttpServletRequest request, HttpServletResponse response, User acc) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null || (!"admin".equals(user.getRole()) && !"librarian".equals(user.getRole()))) {
            response.sendRedirect("login.jsp");
        }

        String action = request.getParameter("action");
        try {
            if ("addBook".equals(action)) {
                String title = request.getParameter("title");
                String author = request.getParameter("author");
                String publisher = request.getParameter("publisher");
                int categoryId = Integer.parseInt(request.getParameter("categoryId"));
                int quantity = Integer.parseInt(request.getParameter("quantity"));
                int available = Integer.parseInt(request.getParameter("available"));
                Part filePart = request.getPart("image");
                if (filePart == null || filePart.getSize() == 0) {
                    throw new Exception("Image file is required!");
                }

                String fileName = extractFileName(filePart);
                String applicationPath = request.getServletContext().getRealPath("");
                String uploadPath = applicationPath + File.separator + UPLOAD_DIR;
                File uploadDir = new File(uploadPath);
                if (!uploadDir.exists()) {
                    uploadDir.mkdir();
                }
                String filePath = uploadPath + File.separator + fileName;
                filePart.write(filePath);
                String imgUrl = UPLOAD_DIR + "/" + fileName;
                Book book = new Book(0, title, author, publisher, categoryId, quantity, available, imgUrl);
                bookDAO.addBook(book);
                request.setAttribute("success", "Book added successfully!");
                response.sendRedirect("admin?action=bookmanagement");
            } else if ("editBook".equals(action)) {
                int bookId = Integer.parseInt(request.getParameter("bookId"));
                String title = request.getParameter("title");
                String author = request.getParameter("author");
                String publisher = request.getParameter("publisher");
                int categoryId = Integer.parseInt(request.getParameter("categoryId"));
                int quantity = Integer.parseInt(request.getParameter("quantity"));
                int available = Integer.parseInt(request.getParameter("available"));
                Part filePart = request.getPart("image");

                Book existingBook = bookDAO.getBookById(bookId);
                String imgUrl = existingBook.getImgUrl();
                if (filePart != null && filePart.getSize() > 0) {
                    String fileName = extractFileName(filePart);
                    String applicationPath = request.getServletContext().getRealPath("");
                    String uploadPath = applicationPath + File.separator + UPLOAD_DIR;
                    File uploadDir = new File(uploadPath);
                    if (!uploadDir.exists()) {
                        uploadDir.mkdir();
                    }
                    String filePath = uploadPath + File.separator + fileName;
                    filePart.write(filePath);
                    imgUrl = UPLOAD_DIR + "/" + fileName;
                }

                Book book = new Book(bookId, title, author, publisher, categoryId, quantity, available, imgUrl);
                bookDAO.updateBook(book);
                request.setAttribute("success", "Book updated successfully!");
                response.sendRedirect("admin?action=bookmanagement");
            }
        } catch (Exception e) {
            request.setAttribute("error", "Error: " + e.getMessage());
            request.setAttribute("books", bookDAO.getAllBook());
            request.setAttribute("categories", categoryDAO.getAllCategories());
            request.getRequestDispatcher("bookManegement.jsp").forward(request, response);

        }
    }

}
