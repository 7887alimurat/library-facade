package library.subsystem;

import java.time.LocalDate;

/** One borrowing of one book by one member. */
public class Loan {
    private final String memberId;
    private final String isbn;
    private final LocalDate borrowedOn;
    private final LocalDate dueOn;
    private boolean closed;

    public Loan(String memberId, String isbn, LocalDate borrowedOn, LocalDate dueOn) {
        this.memberId = memberId;
        this.isbn = isbn;
        this.borrowedOn = borrowedOn;
        this.dueOn = dueOn;
    }

    public String getMemberId() { return memberId; }
    public String getIsbn() { return isbn; }
    public LocalDate getBorrowedOn() { return borrowedOn; }
    public LocalDate getDueOn() { return dueOn; }
    public boolean isClosed() { return closed; }
    void close() { closed = true; }
}
