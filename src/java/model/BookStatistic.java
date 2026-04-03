package model;

public class BookStatistic {
    
    private String title;
    private int total;

    // 🔹 Constructor rỗng
    public BookStatistic() {
    }

    // 🔹 Constructor đầy đủ
    public BookStatistic(String title, int total) {
        this.title = title;
        this.total = total;
    }

    // 🔹 Getter & Setter
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    // 🔹 toString (debug)
    @Override
    public String toString() {
        return "BookStatistic{" +
                "title='" + title + '\'' +
                ", total=" + total +
                '}';
    }
}