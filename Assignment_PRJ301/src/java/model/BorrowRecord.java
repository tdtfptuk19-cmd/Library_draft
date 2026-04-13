package model;

public class BorrowRecord {
    private int    recordId;
    private int    bookId;
    private String bookTitle;
    private String borrowDate;
    private String dueDate;
    private String returnDate;
    private String status;
    private String borrowerName; // for admin/librarian display

    public BorrowRecord() {}

    public BorrowRecord(int recordId, int bookId, String bookTitle,
                        String borrowDate, String dueDate,
                        String returnDate, String status) {
        this.recordId   = recordId;
        this.bookId     = bookId;
        this.bookTitle  = bookTitle;
        this.borrowDate = borrowDate;
        this.dueDate    = dueDate;
        this.returnDate = returnDate;
        this.status     = status;
    }

    public int    getRecordId()              { return recordId; }
    public void   setRecordId(int v)         { this.recordId = v; }
    public int    getBookId()                { return bookId; }
    public void   setBookId(int v)           { this.bookId = v; }
    public String getBookTitle()             { return bookTitle; }
    public void   setBookTitle(String v)     { this.bookTitle = v; }
    public String getBorrowDate()            { return borrowDate; }
    public void   setBorrowDate(String v)    { this.borrowDate = v; }
    public String getDueDate()               { return dueDate; }
    public void   setDueDate(String v)       { this.dueDate = v; }
    public String getReturnDate()            { return returnDate; }
    public void   setReturnDate(String v)    { this.returnDate = v; }
    public String getStatus()                { return status; }
    public void   setStatus(String v)        { this.status = v; }
    public String getBorrowerName()          { return borrowerName; }
    public void   setBorrowerName(String v)  { this.borrowerName = v; }
}
