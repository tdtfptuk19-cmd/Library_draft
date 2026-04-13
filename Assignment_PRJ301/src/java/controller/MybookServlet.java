package controller;

import RBAC.BaseRBACAccessControl;
import dal.BorrowDAO;
import dal.FineDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import model.BorrowRecord;
import model.Fine;
import model.User;

@WebServlet(name = "MybookServlet", urlPatterns = {"/mybooks"})
public class MybookServlet extends BaseRBACAccessControl {

    private final BorrowDAO borrowDAO = new BorrowDAO();
    private final FineDAO   fineDAO   = new FineDAO();

    @Override
    protected void doGetAccess(HttpServletRequest request,
                               HttpServletResponse response,
                               User acc)
            throws ServletException, IOException {

        HttpSession session = request.getSession();

        // Flash messages (set by BorrowServlet via PRG)
        if (session.getAttribute("success") != null) {
            request.setAttribute("success", session.getAttribute("success"));
            session.removeAttribute("success");
        }
        if (session.getAttribute("error") != null) {
            request.setAttribute("error", session.getAttribute("error"));
            session.removeAttribute("error");
        }

        List<BorrowRecord> history = borrowDAO.getBorrowHistory(acc.getId());
        List<Fine>         fines   = fineDAO.getFinesByUser(acc.getId());

        request.setAttribute("borrowHistory", history);
        request.setAttribute("myFines",       fines);

        request.getRequestDispatcher("mybooks.jsp").forward(request, response);
    }

    @Override
    protected void doPostAccess(HttpServletRequest request,
                                HttpServletResponse response,
                                User acc)
            throws ServletException, IOException {
        // POST not used — BorrowServlet handles borrows/returns
        doGetAccess(request, response, acc);
    }
}
