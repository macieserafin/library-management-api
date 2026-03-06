package pl.edu.pjwstk.library_spring.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.edu.pjwstk.library_spring.exception.LibraryNotFoundException;
import pl.edu.pjwstk.library_spring.model.Library;
import pl.edu.pjwstk.library_spring.repository.LibraryRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LibraryService {

    private final LibraryRepository libraryRepository;

    public Library getLibraryById(Long id) {
        return libraryRepository.findById(id)
                .orElseThrow(() -> new LibraryNotFoundException(id));
    }

    public List<Library> getAllLibraries() {
        return libraryRepository.findAll();
    }

    public Library createLibrary(Library library) {
        return libraryRepository.save(library);
    }

    public Library updateLibrary(Long id, Library library) {
        Library existingLibrary = libraryRepository.findById(id)
                .orElseThrow(() -> new LibraryNotFoundException(id));

        existingLibrary.setName(library.getName());

        return libraryRepository.save(existingLibrary);
    }

    public void deleteLibrary(Long id) {
        Library existingLibrary = libraryRepository.findById(id)
                .orElseThrow(() -> new LibraryNotFoundException(id));

        libraryRepository.delete(existingLibrary);
    }
}