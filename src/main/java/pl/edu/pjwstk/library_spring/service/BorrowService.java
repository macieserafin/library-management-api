package pl.edu.pjwstk.library_spring.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.edu.pjwstk.library_spring.exception.BookNotFoundException;
import pl.edu.pjwstk.library_spring.exception.BorrowNotFoundException;
import pl.edu.pjwstk.library_spring.exception.UserNotFoundException;
import pl.edu.pjwstk.library_spring.model.Book;
import pl.edu.pjwstk.library_spring.model.Borrow;
import pl.edu.pjwstk.library_spring.model.User;
import pl.edu.pjwstk.library_spring.repository.BookRepository;
import pl.edu.pjwstk.library_spring.repository.BorrowRepository;
import pl.edu.pjwstk.library_spring.repository.UserRepository;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BorrowService {

    private final BorrowRepository borrowRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    public Borrow getBorrowById(Long id) {
        return borrowRepository.findById(id)
                .orElseThrow(() -> new BorrowNotFoundException(id));
    }

    public List<Borrow> getAllBorrows() {
        return borrowRepository.findAll();
    }

    public Borrow borrowBook(Long userId, Long bookId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException(bookId));

        boolean isAlreadyBorrowed = borrowRepository.findAll().stream()
                .anyMatch(borrow -> borrow.getBook().getId().equals(bookId) && borrow.getReturnDate() == null);

        if (isAlreadyBorrowed) {
            throw new RuntimeException("Book is already borrowed");
        }

        Borrow borrow = Borrow.builder()
                .user(user)
                .book(book)
                .borrowDate(LocalDate.now())
                .build();

        return borrowRepository.save(borrow);
    }

    public Borrow returnBook(Long id) {
        Borrow borrow = borrowRepository.findById(id)
                .orElseThrow(() -> new BorrowNotFoundException(id));

        if (borrow.getReturnDate() != null) {
            throw new RuntimeException("Borrow is already returned");
        }

        borrow.setReturnDate(LocalDate.now());
        return borrowRepository.save(borrow);
    }

    public List<Borrow> getBorrowsByUser(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        return borrowRepository.findAll().stream()
                .filter(borrow -> borrow.getUser().getId().equals(userId))
                .toList();
    }

    public List<Borrow> getActiveBorrows() {
        return borrowRepository.findAll().stream()
                .filter(borrow -> borrow.getReturnDate() == null)
                .toList();
    }
}