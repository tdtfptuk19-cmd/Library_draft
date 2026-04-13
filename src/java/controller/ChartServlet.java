package controller;

import com.google.gson.Gson;
import dal.ChartData;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Chart;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

@WebServlet(name = "ChartServlet", urlPatterns = {"/chartServlet"})
public class ChartServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            ChartData dao = new ChartData();

            // 🔹 Tổng quan
            int totalBooks = dao.getTotalBooks();
            int totalUsers = dao.getTotalUsers();
            int totalBorrows = dao.getCurrentLoans();
            int totalOverdue = dao.getOverdueBook();

            // 🔹 Dữ liệu theo tháng
            Map<String, Map<String, Integer>> monthlyStats = dao.getMonthlyStats();

            List<String> months = new ArrayList<>();
            Calendar cal = Calendar.getInstance();
            for (int i = 5; i >= 0; i--) {
                Calendar temp = (Calendar) cal.clone();
                temp.add(Calendar.MONTH, -i);
                String month = new SimpleDateFormat("yyyy-MM").format(temp.getTime());
                months.add(month);
            }

            int[] bookStats = new int[months.size()];
            int[] overdueStats = new int[months.size()];

            for (int i = 0; i < months.size(); i++) {
                Map<String, Integer> counts = monthlyStats.getOrDefault(months.get(i), new HashMap<>());
                bookStats[i] = counts.getOrDefault("borrowed", 0);
                overdueStats[i] = counts.getOrDefault("overdue", 0);
            }

            // 🔹 Thống kê theo danh mục
            Map<String, Integer> categoryStats = dao.getCategoryStats();
            int size = categoryStats.size();
            int[] categoryData = new int[size];
            String[] categoryLabels = new String[size];
            int index = 0;
            for (Map.Entry<String, Integer> entry : categoryStats.entrySet()) {
                categoryLabels[index] = entry.getKey();
                categoryData[index] = entry.getValue();
                index++;
            }

            // 🔹 Tạo object Chart
            Chart chart = new Chart(
                    totalBooks,
                    totalUsers,
                    totalBorrows,
                    totalOverdue,
                    bookStats,
                    overdueStats,
                    categoryData,
                    categoryLabels
            );
            chart.setMonths(months.toArray(new String[0]));
            System.out.println("DB totalBooks = " + dao.getTotalBooks());
            System.out.println("DB totalUsers = " + dao.getTotalUsers());
            System.out.println("DB totalBorrows = " + dao.getCurrentLoans());
            System.out.println("DB totalOverdue = " + dao.getOverdueBook());
            // 🔹 Convert sang JSON và gửi về client
            Gson gson = new Gson();
            String json = gson.toJson(chart);
            response.getWriter().write(json);

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\": \"Cannot load chart data\"}");
        }
    }
}
