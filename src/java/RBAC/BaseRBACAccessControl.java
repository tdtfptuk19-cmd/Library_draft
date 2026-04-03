/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package RBAC;

import dal.UserDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import model.User;

/**
 *
 * @author thien
 */
public abstract class BaseRBACAccessControl extends HttpServlet {
    
    private static final List<String > PUBLIC_PAGE = List.of("/home");

    protected boolean isAuthorized(User acc, List<String> requiredPermissions) {
        UserDAO adao = new UserDAO();
        List<String> listPermissions = adao.getPermissions(acc);

        return listPermissions.stream().anyMatch(permission -> requiredPermissions.stream().anyMatch(path -> permission.startsWith(path))
        );
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession ses = request.getSession();
        User acc = (User) ses.getAttribute("user");
        String path = request.getServletPath();
        if (acc == null && !PUBLIC_PAGE.contains(path)) {
            response.sendRedirect("login");
            return;
        }
        if (acc != null && !isAuthorized(acc, List.of(request.getServletPath()))) {
            if (acc.getRole().equals("user")) {
                response.sendRedirect("home");
                return;
            }
            if (acc.getRole().equals("admin")) {
                response.sendRedirect("adminLogin");
                return;
            }
        }
        doGetAccess(request, response, acc);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession ses = request.getSession();
        User acc = (User) ses.getAttribute("user");
        String path = request.getServletPath();
        if (acc == null && !PUBLIC_PAGE.contains(path)) {
            response.sendRedirect("login");
            return;
        }
        if (acc != null && !isAuthorized(acc, List.of(request.getServletPath()))) {
            if (acc.getRole().equals("user")) {
                response.sendRedirect("home");
                return;
            }
            if (acc.getRole().equals("admin")) {
                response.sendRedirect("adminLogin");
                return;
            }
        }
        doPostAccess(request, response, acc);
    }

    protected abstract void doGetAccess(HttpServletRequest request, HttpServletResponse response, User acc) throws ServletException, IOException;

    protected abstract void doPostAccess(HttpServletRequest request, HttpServletResponse response, User acc) throws ServletException, IOException;
}
