package library.facade;

import library.subsystem.Book;
import library.subsystem.BookCatalog;
import library.subsystem.FineCalculator;
import library.subsystem.Loan;
import library.subsystem.LoanRegistry;
import library.subsystem.Member;
import library.subsystem.MemberRegistry;
import library.subsystem.NotificationService;

import java.time.LocalDate;

/**
 * Facade: a simple API (borrow / return) over five collaborating subsystem classes.
 * It only coordinates them in the right order and does not contain business rules.
 */
public class LibraryFacade {
    private final MemberRegistry members;
    private final BookCatalog catalog;
    private final LoanRegistry loans;
    private final FineCalculator fines;
    private final NotificationService notifications;

    public LibraryFacade() {
        this(new MemberRegistry(), new BookCatalog(), new LoanRegistry(),
                new FineCalculator(), new NotificationService());
    }

    /** Subsystem can be injected, so advanced clients and tests can use their own parts. */
    public LibraryFacade(MemberRegistry members, BookCatalog catalog, LoanRegistry loans,
                         FineCalculator fines, NotificationService notifications) {
        this.members = members;
        this.catalog = catalog;
        this.loans = loans;
        this.fines = fines;
        this.notifications = notifications;
    }

    public void registerMember(String memberId, String name) {
        try {
            members.register(memberId, name);
        } catch (IllegalArgumentException e) {
            throw new LibraryException("Cannot register member: " + e.getMessage(), e);
        }
    }

    public void addBook(String isbn, String title, String author, int copies) {
        catalog.addBook(new Book(isbn, title, author), copies);
    }

    public void borrowBook(String memberId, String isbn) {
        borrowBook(memberId, isbn, LocalDate.now());
    }

    public void borrowBook(String memberId, String isbn, LocalDate today) {
        Member member;
        Book book;
        try {
            member = members.findById(memberId);
            book = catalog.findByIsbn(isbn);
        } catch (IllegalArgumentException e) {
            throw new LibraryException("Cannot borrow: " + e.getMessage(), e);
        }
        if (!loans.canBorrow(memberId)) {
            throw new LibraryException("Cannot borrow: " + member.name() + " reached the loan limit ("
                    + LoanRegistry.MAX_ACTIVE_LOANS_PER_MEMBER + ")");
        }
        if (!catalog.hasFreeCopy(isbn)) {
            throw new LibraryException("Cannot borrow: no free copies of \"" + book.title() + "\"");
        }

        catalog.takeCopy(isbn);
        Loan loan = null;
        try {
            loan = loans.open(memberId, isbn, today);
            notifications.send(member, "You borrowed \"" + book.title()
                    + "\". Please return it by " + loan.getDueOn() + ".");
        } catch (RuntimeException e) {
            // roll back so the subsystem stays consistent
            if (loan != null) {
                loans.cancel(loan);
            }
            catalog.putCopyBack(isbn);
            throw new LibraryException("Borrowing failed, changes were rolled back", e);
        }
    }

    /** @return fine amount (0.0 if returned on time) */
    public double returnBook(String memberId, String isbn) {
        return returnBook(memberId, isbn, LocalDate.now());
    }

    public double returnBook(String memberId, String isbn, LocalDate today) {
        try {
            Member member = members.findById(memberId);
            Book book = catalog.findByIsbn(isbn);
            Loan loan = loans.findActive(memberId, isbn);

            double fine = fines.calculate(loan, today);
            loans.close(loan);
            catalog.putCopyBack(isbn);

            String message = "You returned \"" + book.title() + "\".";
            if (fine > 0) {
                message += " Late fine: " + String.format("%.2f", fine) + " USD.";
            }
            notifications.send(member, message);
            return fine;
        } catch (IllegalArgumentException | IllegalStateException e) {
            throw new LibraryException("Cannot return: " + e.getMessage(), e);
        }
    }
}
