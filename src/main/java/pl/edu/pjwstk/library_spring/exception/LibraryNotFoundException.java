package pl.edu.pjwstk.library_spring.exception;

public class LibraryNotFoundException extends RuntimeException {

    public LibraryNotFoundException(Long id) {
        super("Library with id " + id + " not found");
    }

    public LibraryNotFoundException(String message) {
        super(message);
    }
}
