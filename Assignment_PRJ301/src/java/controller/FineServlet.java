package controller;

import RBAC.BaseRBACAccessControl;
import dal.FineDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import model.Fine;
import model.User;

@WebServlet(name = "FineServlet", urlPatterns = {"/fine"})
public class FineServlet extends BaseRBACAccessControl {

    private final FineDAO fineDAO = new FineDAO();

    private boolean isStaff(User acc) {
        return acc != null &&
               ("admin".equals(acc.getRole()) || "librarian".equals(acc.getRole()));
    }

    @Override
    protected void doGetAccess(HttpServletRequest request,
                               HttpServletResponse response, User acc)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        String action = request.getParameter("action");
        if (action == null) action = "myFines";

        if (session.getAttribute("success") != null) {
            request.setAttribute("success", session.getAttribute("success"));
            session.removeAttribute("success");
        }
        if (session.getAttribute("error") != null) {
            request.setAttribute("error", session.getAttribute("error"));
            session.removeAttribute("error");
        }

        switch (action) {
            // Staff view: grouped fines
            case "manage":
                if (!isStaff(acc)) { response.sendRedirect("home"); return; }
                List<Fine> grouped = fineDAO.getGroupedFines();
                request.setAttribute("fines", grouped);
                request.getRequestDispatcher("fineManagement.jsp")
                       .forward(request, response);
                break;

            // Borrower own fines
            case "myFines":
            default:
                List<Fine> myFines = fineDAO.getFinesByUser(acc.getId());
                request.setAttribute("fines", myFines);
                request.getRequestDispatcher("myFines.jsp")
                       .forward(request, response);
                break;
        }
    }

    @Override
    protected void doPostAccess(HttpServletRequest request,
                                HttpServletResponse response, User acc)
            throws ServletException, IOException {
        // No POST actions needed — fines are now collected during return
        response.sendRedirect("home");
    }
}
