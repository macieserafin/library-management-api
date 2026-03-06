package pl.edu.pjwstk.library_spring.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.edu.pjwstk.library_spring.dto.BookFilterDto;
import pl.edu.pjwstk.library_spring.exception.BookNotFoundException;
import pl.edu.pjwstk.library_spring.exception.LibraryNotFoundException;
import pl.edu.pjwstk.library_spring.model.Book;
import pl.edu.pjwstk.library_spring.model.Library;
import pl.edu.pjwstk.library_spring.repository.BookRepository;
import pl.edu.pjwstk.library_spring.repository.LibraryRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final LibraryRepository libraryRepository;

    public Book getBookById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));
    }

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public Book createBook(Book book) {
        if (book.getLibrary() == null || book.getLibrary().getId() == null) {
            throw new LibraryNotFoundException("Library id is required");
        }

        Library library = libraryRepository.findById(book.getLibrary().getId())
                .orElseThrow(() -> new LibraryNotFoundException(book.getLibrary().getId()));

        book.setLibrary(library);

        return bookRepository.save(book);
    }

    public Book updateBook(Long id, Book book) {
        Book existingBook = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));

        existingBook.setTitle(book.getTitle());
        existingBook.setAuthor(book.getAuthor());
        existingBook.setYear(book.getYear());

        if (book.getLibrary() != null && book.getLibrary().getId() != null) {
            Library library = libraryRepository.findById(book.getLibrary().getId())
                    .orElseThrow(() -> new LibraryNotFoundException(book.getLibrary().getId()));
            existingBook.setLibrary(library);
        }

        return bookRepository.save(existingBook);
    }

    public void deleteBook(Long id) {
        Book existingBook = bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));

        bookRepository.delete(existingBook);
    }

    public List<Book> filterBooks(BookFilterDto filter) {
        return bookRepository.findAll().stream()
                .filter(book -> filter.getTitle() == null || book.getTitle().equals(filter.getTitle()))
                .filter(book -> filter.getAuthor() == null || book.getAuthor().equals(filter.getAuthor()))
                .filter(book -> filter.getYear() == null || book.getYear().equals(filter.getYear()))
                .toList();
    }
}