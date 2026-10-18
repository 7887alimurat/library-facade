package library.subsystem;

import java.util.HashMap;
import java.util.Map;

/** Stores book titles and how many copies are on the shelf. */
public class BookCatalog {
    private final Map<String, Book> books = new HashMap<>();
    private final Map<String, Integer> copiesOnShelf = new HashMap<>();

    public void addBook(Book book, int copies) {
        books.put(book.isbn(), book);
        copiesOnShelf.merge(book.isbn(), copies, Integer::sum);
    }

    public Book findByIsbn(String isbn) {
        Book book = books.get(isbn);
        if (book == null) {
            throw new IllegalArgumentException("Book not found: " + isbn);
        }
        return book;
    }

    public boolean hasFreeCopy(String isbn) {
        return copiesOnShelf.getOrDefault(isbn, 0) > 0;
    }

    public void takeCopy(String isbn) {
        if (!hasFreeCopy(isbn)) {
            throw new IllegalStateException("No free copies of " + isbn);
        }
        copiesOnShelf.merge(isbn, -1, Integer::sum);
    }

    public void putCopyBack(String isbn) {
        copiesOnShelf.merge(isbn, 1, Integer::sum);
    }
}
