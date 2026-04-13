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

@WebServlet(name = "BorrowManagementServlet", urlPatterns = {"/borrowmanagement"})
public class borrowMagServlet extends BaseRBACAccessControl {

    private final BorrowDAO borrowDAO = new BorrowDAO();
    private final FineDAO   fineDAO   = new FineDAO();

    private boolean hasAccess(User acc) {
        return acc != null &&
               ("admin".equals(acc.getRole()) || "librarian".equals(acc.getRole()));
    }

    @Override
    protected void doGetAccess(HttpServletRequest request,
                               HttpServletResponse response, User acc)
            throws ServletException, IOException {

        if (!hasAccess(acc)) { response.sendRedirect("login"); return; }

        String action = request.getParameter("action");
        if (action == null) action = "borrowmanagement";

        switch (action) {

            // AJAX-style: calculate fine preview before modal confirm
            case "previewReturn":
                previewReturn(request, response);
                break;

            case "borrowmanagement":
            default:
                loadBorrowList(request, response, request.getSession());
                break;
        }
    }

    @Override
    protected void doPostAccess(HttpServletRequest request,
                                HttpServletResponse response, User acc)
            throws ServletException, IOException {

        if (!hasAccess(acc)) { response.sendRedirect("login"); return; }

        HttpSession session = request.getSession();
        String action = request.getParameter("action");
        if (action == null) action = "";

        switch (action) {
            case "return":
                handleReturn(request, session);
                break;
            default:
                session.setAttribute("error", "Invalid action.");
                break;
        }
        response.sendRedirect("borrowmanagement?action=borrowmanagement");
    }

    // ── Preview: calculate fine before showing modal ─────────────────────────
    // Returns JSON: { overdueDays, overdueFine, isFineExist }
    private void previewReturn(HttpServletRequest request,
                               HttpServletResponse response)
            throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        try {
            int  recordId    = Integer.parseInt(request.getParameter("recordId"));
            long overdueDays = borrowDAO.calcOverdueDays(recordId);
            long overdueFine = overdueDays * 5000L;
            boolean fineExists = fineDAO.hasFine(recordId, "overdue");

            response.getWriter().write(
                "{\"overdueDays\":" + overdueDays
              + ",\"overdueFine\":" + overdueFine
              + ",\"fineExists\":"  + fineExists + "}"
            );
        } catch (Exception e) {
            response.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    // ── Process return: fine collected + book returned in one transaction ─────
    private void handleReturn(HttpServletRequest request, HttpSession session) {
        try {
            int recordId = Integer.parseInt(request.getParameter("recordId").trim());

            boolean isDamaged = "on".equals(request.getParameter("isDamaged"));
            double  damageAmt = 0;
            if (isDamaged) {
                String dmg = request.getParameter("damageAmount");
                if (dmg != null && !dmg.isBlank())
                    damageAmt = Double.parseDouble(dmg.trim());
            }

            boolean ok = borrowDAO.returnBook(recordId, isDamaged, damageAmt);

            if (ok) {
                session.setAttribute("success",
                    "Book returned successfully! If there is a fine, please pay it in 'My Fines' (QR).");
            } else {
                session.setAttribute("error",
                    "Return failed. Record not found or already returned.");
            }
        } catch (NumberFormatException e) {
            session.setAttribute("error", "Invalid Record ID or damage amount.");
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("error", "Unexpected error during return.");
        }
    }

    // ── Load main page data ───────────────────────────────────────────────────
    private void loadBorrowList(HttpServletRequest request,
                                HttpServletResponse response, HttpSession session)
            throws ServletException, IOException {

        if (session.getAttribute("success") != null) {
            request.setAttribute("success", session.getAttribute("success"));
            session.removeAttribute("success");
        }
        if (session.getAttribute("error") != null) {
            request.setAttribute("error", session.getAttribute("error"));
            session.removeAttribute("error");
        }

        List<BorrowRecord> borrows = borrowDAO.getAllBorrows();
        // Use grouped fines for the Fine Management tab
        List<Fine> fines = fineDAO.getGroupedFines();

        request.setAttribute("borrows", borrows);
        request.setAttribute("fines",   fines);

        request.getRequestDispatcher("borrowmanagement.jsp")
               .forward(request, response);
    }
}
