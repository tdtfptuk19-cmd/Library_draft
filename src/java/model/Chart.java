package model;

public class Chart {

    // 🔹 Tổng quan
    private int totalBooks;
    private int totalUsers;
    private int totalBorrows;
    private int totalOverdue;

    // 🔹 Line chart
    private int[] bookStats;
    private int[] overdueStats;
    private String[] months;

    // 🔹 Pie chart
    private int[] categoryData;
    private String[] categoryLabels;

    // 🔹 Constructor đầy đủ
    public Chart(int totalBooks, int totalUsers, int totalBorrows, int totalOverdue,
                 int[] bookStats, int[] overdueStats,
                 int[] categoryData, String[] categoryLabels) {

        this.totalBooks = totalBooks;
        this.totalUsers = totalUsers;
        this.totalBorrows = totalBorrows;
        this.totalOverdue = totalOverdue;
        this.bookStats = bookStats;
        this.overdueStats = overdueStats;
        this.categoryData = categoryData;
        this.categoryLabels = categoryLabels;
    }

    public Chart() {
    }

    // 🔹 Getter & Setter

    public int getTotalBooks() {
        return totalBooks;
    }

    public void setTotalBooks(int totalBooks) {
        this.totalBooks = totalBooks;
    }

    public int getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(int totalUsers) {
        this.totalUsers = totalUsers;
    }

    public int getTotalBorrows() {
        return totalBorrows;
    }

    public void setTotalBorrows(int totalBorrows) {
        this.totalBorrows = totalBorrows;
    }

    public int getTotalOverdue() {
        return totalOverdue;
    }

    public void setTotalOverdue(int totalOverdue) {
        this.totalOverdue = totalOverdue;
    }

    public int[] getBookStats() {
        return bookStats;
    }

    public void setBookStats(int[] bookStats) {
        this.bookStats = bookStats;
    }

    public int[] getOverdueStats() {
        return overdueStats;
    }

    public void setOverdueStats(int[] overdueStats) {
        this.overdueStats = overdueStats;
    }

    public int[] getCategoryData() {
        return categoryData;
    }

    public void setCategoryData(int[] categoryData) {
        this.categoryData = categoryData;
    }

    public String[] getCategoryLabels() {
        return categoryLabels;
    }

    public void setCategoryLabels(String[] categoryLabels) {
        this.categoryLabels = categoryLabels;
    }

    public String[] getMonths() {
        return months;
    }

    public void setMonths(String[] months) {
        this.months = months;
    }
}