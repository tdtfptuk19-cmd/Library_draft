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

public abstract class BaseRBACAccessControl extends HttpServlet {

    private static final List<String> PUBLIC_PAGE = List.of("/home");

    protected boolean isAuthorized(User acc, List<String> requiredPermissions) {
        UserDAO dao = new UserDAO();
        List<String> permissions = dao.getPermissions(acc);

        // Check: does any stored permission prefix-match the requested path?
        return permissions.stream().anyMatch(permission ->
            requiredPermissions.stream().anyMatch(path ->
                path.startsWith(permission) || permission.startsWith(path)
            )
        );
    }

    // ── Redirect each role to their own home when they hit an unauthorized path ──
    private void redirectToHome(User acc, HttpServletResponse response)
            throws IOException {
        if (acc == null) {
            response.sendRedirect("login");
            return;
        }
        switch (acc.getRole()) {
            case "admin":
                response.sendRedirect("adminLogin");
                break;
            case "librarian":
                response.sendRedirect("borrowmanagement?action=borrowmanagement");
                break;
            default: // "user"
                response.sendRedirect("home");
                break;
        }
    }

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        User acc  = (User) session.getAttribute("user");
        String path = request.getServletPath();

        // Not logged in → go to login (except public pages)
        if (acc == null) {
            if (!PUBLIC_PAGE.contains(path)) {
                response.sendRedirect("login");
            } else {
                doGetAccess(request, response, null);
            }
            return;
        }

        // Logged in but not authorized for this path → redirect to role home
        if (!isAuthorized(acc, List.of(path))) {
            redirectToHome(acc, response);
            return;
        }

        doGetAccess(request, response, acc);
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        User acc  = (User) session.getAttribute("user");
        String path = request.getServletPath();

        // Not logged in → go to login
        if (acc == null) {
            if (!PUBLIC_PAGE.contains(path)) {
                response.sendRedirect("login");
            } else {
                doPostAccess(request, response, null);
            }
            return;
        }

        // Logged in but not authorized → redirect to role home
        if (!isAuthorized(acc, List.of(path))) {
            redirectToHome(acc, response);
            return;
        }

        doPostAccess(request, response, acc);
    }

    protected abstract void doGetAccess(HttpServletRequest request,
                                         HttpServletResponse response,
                                         User acc)
            throws ServletException, IOException;

    protected abstract void doPostAccess(HttpServletRequest request,
                                          HttpServletResponse response,
                                          User acc)
            throws ServletException, IOException;
}
