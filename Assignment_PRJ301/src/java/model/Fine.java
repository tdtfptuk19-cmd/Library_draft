package model;

public class Fine {
    private int    fineId;
    private int    recordId;
    private double fineAmount;
    private String reason;
    private String status;    // "unpaid" | "paid"
    private String fineType;  // "overdue" | "damaged"
    private String createdAt;

    // join fields for display
    private String borrowerName;
    private String bookTitle;
    private String dueDate;
    private String returnDate;

    public Fine() {}

    public Fine(int fineId, int recordId, double fineAmount,
                String reason, String status, String fineType, String createdAt) {
        this.fineId     = fineId;
        this.recordId   = recordId;
        this.fineAmount = fineAmount;
        this.reason     = reason;
        this.status     = status;
        this.fineType   = fineType;
        this.createdAt  = createdAt;
    }

    public int    getFineId()              { return fineId; }
    public void   setFineId(int v)         { this.fineId = v; }

    public int    getRecordId()            { return recordId; }
    public void   setRecordId(int v)       { this.recordId = v; }

    public double getFineAmount()          { return fineAmount; }
    public void   setFineAmount(double v)  { this.fineAmount = v; }

    public String getReason()              { return reason; }
    public void   setReason(String v)      { this.reason = v; }

    public String getStatus()              { return status; }
    public void   setStatus(String v)      { this.status = v; }

    public String getFineType()            { return fineType; }
    public void   setFineType(String v)    { this.fineType = v; }

    public String getCreatedAt()           { return createdAt; }
    public void   setCreatedAt(String v)   { this.createdAt = v; }

    public String getBorrowerName()        { return borrowerName; }
    public void   setBorrowerName(String v){ this.borrowerName = v; }

    public String getBookTitle()           { return bookTitle; }
    public void   setBookTitle(String v)   { this.bookTitle = v; }

    public String getDueDate()             { return dueDate; }
    public void   setDueDate(String v)     { this.dueDate = v; }

    public String getReturnDate()          { return returnDate; }
    public void   setReturnDate(String v)  { this.returnDate = v; }
}
