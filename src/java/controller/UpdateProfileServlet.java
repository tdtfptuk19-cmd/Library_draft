/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import dal.UserDAO;
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
import model.User;

/**
 *
 * @author thien
 */
@WebServlet(name = "UpdateProfileServlet", urlPatterns = {"/profile"})
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 2, // 2MB
        maxFileSize = 1024 * 1024 * 10, // 10MB
        maxRequestSize = 1024 * 1024 * 50)   // 50MBF
public class UpdateProfileServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet UpdateProfileServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet UpdateProfileServlet at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    private UserDAO userDAO = new UserDAO();
    private static final String UPLOAD_DIR = "img";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
//        processRequest(request, response);
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        if (user == null) {
            response.sendRedirect("login");
            return;
        }
        request.getRequestDispatcher("profile.jsp").forward(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
//        processRequest(request, response);
        HttpSession session = request.getSession();// get current session
        User user = (User) session.getAttribute("user"); //get user object has save in session when user login 
        if (user == null) {
            response.sendRedirect("login");
            return;
        } // check user
        
        String action = request.getParameter("action");// get parameter action from jsp
        if("updateImage".equals(action)){ // check action 
            Part filePart = request.getPart("image"); // get object Part from <input type = "file" name = "image">
            String fileName = extractFileName(filePart); // get  orginial file 
            String applicationPath = request.getServletContext().getRealPath("");// lay duong dan thuc te cua thu muc goc ung dung web tren server
            String uploadPath = applicationPath + File.separator/*/or\*/ + UPLOAD_DIR;
            File uploadDir = new File(uploadPath);
            if(!uploadDir.exists()){
                uploadDir.mkdir();// chi tao mot cap , khong tao thu muc cha
            }
            String filePath = uploadPath + File.separator + fileName;// duong dan day du cua anh
            filePart.write(filePath); // ghi du lieu anh vao filePath
            String imgUrl = UPLOAD_DIR+"/" + fileName;
            user.setImgUrl(imgUrl);
            userDAO.updateUserImg(user.getId(), imgUrl);
            session.setAttribute("user", user);
            response.sendRedirect("profile");
        }else if("changePassword".equals(action)){
            String oldPassword = request.getParameter("oldPassword");
            String newPassword = request.getParameter("newPassword");
            String confirmPassword = request.getParameter("confirmPassword");
            
            if(!user.getPassword().equals(oldPassword)){
                request.setAttribute("error", "Old password is incorrect.");
            } else if(!newPassword.equals(confirmPassword)){
                request.setAttribute("error", "new and olf password not match.");
            } else{
                userDAO.updateUserPass(user.getId(), newPassword);
                user.setPassword(newPassword);
                session.setAttribute("user", user);
                request.setAttribute("success", "Password changed successfully.");
            }
            request.getRequestDispatcher("profile.jsp").forward(request, response);
        }
    }
    private String extractFileName(Part part){
        String contentDisp = part.getHeader("content-disposition");
        String[] items = contentDisp.split(";");
        for(String s: items){
            if(s.trim().startsWith("filename")){
                return s.substring(s.indexOf("=") + 2, s.length() -1);
            }
        }
        return "";
    }
    /*
    example:  khi lay part.getHeader("content-disposition") thi no se tra ve la from-data; name=\"image\"; filename=\"avartar.jsp\"
                      
    
    */

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
