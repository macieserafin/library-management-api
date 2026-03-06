package pl.edu.pjwstk.library_spring.exception;

public class BorrowNotFoundException extends RuntimeException {
    public BorrowNotFoundException(Long id) {
        super("Borrow with id " + id + " not found");
    }
}
