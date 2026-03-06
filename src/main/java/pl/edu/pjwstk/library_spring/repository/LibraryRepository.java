package pl.edu.pjwstk.library_spring.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.edu.pjwstk.library_spring.model.Library;

public interface LibraryRepository extends JpaRepository<Library, Long> {
}
