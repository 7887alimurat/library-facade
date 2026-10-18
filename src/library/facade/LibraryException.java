package library.facade;

/** The only exception type the client has to know about. */
public class LibraryException extends RuntimeException {
    public LibraryException(String message, Throwable cause) {
        super(message, cause);
    }

    public LibraryException(String message) {
        super(message);
    }
}
