package library.subsystem;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Keeps all loans and enforces borrowing rules (loan period, loan limit). */
public class LoanRegistry {
    public static final int LOAN_DAYS = 14;
    public static final int MAX_ACTIVE_LOANS_PER_MEMBER = 3;

    private final List<Loan> loans = new ArrayList<>();

    public boolean canBorrow(String memberId) {
        return countActiveLoans(memberId) < MAX_ACTIVE_LOANS_PER_MEMBER;
    }

    public Loan open(String memberId, String isbn, LocalDate today) {
        Loan loan = new Loan(memberId, isbn, today, today.plusDays(LOAN_DAYS));
        loans.add(loan);
        return loan;
    }

    public Loan findActive(String memberId, String isbn) {
        return loans.stream()
                .filter(l -> !l.isClosed())
                .filter(l -> l.getMemberId().equals(memberId) && l.getIsbn().equals(isbn))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "No active loan of " + isbn + " for member " + memberId));
    }

    public void close(Loan loan) {
        loan.close();
    }

    public void cancel(Loan loan) {
        loans.remove(loan);
    }

    private long countActiveLoans(String memberId) {
        return loans.stream()
                .filter(l -> !l.isClosed() && l.getMemberId().equals(memberId))
                .count();
    }
}
