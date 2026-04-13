package controller;

import RBAC.BaseRBACAccessControl;
import dal.FineDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import model.Fine;
import model.User;

@WebServlet(name = "QRPaymentServlet", urlPatterns = {"/qrpay"})
public class QRPaymentServlet extends BaseRBACAccessControl {

    private final FineDAO fineDAO = new FineDAO();

    // Demo VietQR params (replace with your real bank info)
    // Provided QR: Viettel Money (MB Bank)
    private static final String BANK_ID = "MB"; // MB Bank
    private static final String ACCOUNT_NO = "9704229206640828808";
    private static final String ACCOUNT_NAME = "TRAN DAI THANH";

    @Override
    protected void doGetAccess(HttpServletRequest request, HttpServletResponse response, User acc)
            throws ServletException, IOException {
        String fineIdRaw = request.getParameter("fineId");
        if (fineIdRaw == null) {
            response.sendRedirect("fine?action=myFines");
            return;
        }

        int fineId;
        try {
            fineId = Integer.parseInt(fineIdRaw);
        } catch (NumberFormatException e) {
            response.sendRedirect("fine?action=myFines");
            return;
        }

        Integer ownerId = fineDAO.getFineOwnerUserId(fineId);
        if (ownerId == null || ownerId != acc.getId()) {
            response.sendRedirect("fine?action=myFines");
            return;
        }

        Fine fine = fineDAO.getFineById(fineId);
        if (fine == null) {
            response.sendRedirect("fine?action=myFines");
            return;
        }

        String addInfo = "FINE" + fineId + "_R" + fine.getRecordId();
        long amount = (long) Math.round(fine.getFineAmount());

        // VietQR hosted image (no extra jar/libs needed)
        String qrUrl = "https://img.vietqr.io/image/"
                + BANK_ID + "-" + ACCOUNT_NO + "-compact2.png"
                + "?amount=" + amount
                + "&addInfo=" + urlEncode(addInfo)
                + "&accountName=" + urlEncode(ACCOUNT_NAME);

        request.setAttribute("fine", fine);
        request.setAttribute("qrUrl", qrUrl);
        request.setAttribute("addInfo", addInfo);
        request.getRequestDispatcher("qrpay.jsp").forward(request, response);
    }

    @Override
    protected void doPostAccess(HttpServletRequest request, HttpServletResponse response, User acc)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        String fineIdRaw = request.getParameter("fineId");
        int fineId;
        try {
            fineId = Integer.parseInt(fineIdRaw);
        } catch (Exception e) {
            session.setAttribute("error", "Invalid fine.");
            response.sendRedirect("fine?action=myFines");
            return;
        }

        Integer ownerId = fineDAO.getFineOwnerUserId(fineId);
        if (ownerId == null || ownerId != acc.getId()) {
            session.setAttribute("error", "Not allowed.");
            response.sendRedirect("fine?action=myFines");
            return;
        }

        boolean ok = fineDAO.collectFine(fineId);
        session.setAttribute(ok ? "success" : "error",
                ok ? "Payment recorded successfully!" : "Payment failed. Please try again.");
        response.sendRedirect("fine?action=myFines");
    }

    private static String urlEncode(String s) {
        if (s == null) return "";
        return URLEncoder.encode(s, StandardCharsets.UTF_8);
    }
}

