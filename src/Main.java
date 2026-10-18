import library.facade.LibraryException;
import library.facade.LibraryFacade;

import java.time.LocalDate;

/** Client: knows only LibraryFacade (and its exception), never the subsystem classes. */
public class Main {
    public static void main(String[] args) {
        LibraryFacade library = new LibraryFacade();

        library.registerMember("M1", "Alimurat");
        library.registerMember("M2", "Alan");
        library.addBook("111", "Clean Code", "Robert Martin", 1);
        library.addBook("222", "Head First Design Patterns", "E. Freeman", 2);

        LocalDate today = LocalDate.of(2026, 10, 18);

        System.out.println("1) Normal borrow and on-time return");
        library.borrowBook("M1", "111", today);
        double fine = library.returnBook("M1", "111", today.plusDays(10));
        System.out.println("  fine = " + fine);

        System.out.println("2) Late return");
        library.borrowBook("M1", "222", today);
        fine = library.returnBook("M1", "222", today.plusDays(20));
        System.out.println("  fine = " + fine);

        System.out.println("3) Error: no free copies");
        library.borrowBook("M1", "111", today);
        try {
            library.borrowBook("M2", "111", today);
        } catch (LibraryException e) {
            System.out.println("  error: " + e.getMessage());
        }

        System.out.println("4) Error: unknown member");
        try {
            library.borrowBook("M99", "222", today);
        } catch (LibraryException e) {
            System.out.println("  error: " + e.getMessage());
        }

        System.out.println("5) Error: return a book that was not borrowed");
        try {
            library.returnBook("M2", "222", today);
        } catch (LibraryException e) {
            System.out.println("  error: " + e.getMessage());
        }
    }
}
