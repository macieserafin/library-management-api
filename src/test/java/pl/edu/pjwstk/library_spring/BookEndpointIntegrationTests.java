package pl.edu.pjwstk.library_spring;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import pl.edu.pjwstk.library_spring.repository.BookRepository;
import pl.edu.pjwstk.library_spring.repository.BorrowRepository;
import pl.edu.pjwstk.library_spring.repository.LibraryRepository;
import pl.edu.pjwstk.library_spring.repository.UserRepository;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class BookEndpointIntegrationTests {

    private static final Pattern ID_PATTERN = Pattern.compile("\"id\":(\\d+)");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BorrowRepository borrowRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private LibraryRepository libraryRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void cleanDatabase() {
        borrowRepository.deleteAll();
        bookRepository.deleteAll();
        libraryRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void shouldCreateReadFilterUpdateAndDeleteBook() throws Exception {
        String libraryLocation = mockMvc.perform(post("/libraries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Main Library"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Main Library")))
                .andReturn()
                .getResponse()
                .getContentAsString();

        String libraryId = firstId(libraryLocation);

        String bookResponse = mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Clean Code",
                                  "author": "Robert C. Martin",
                                  "year": 2008,
                                  "library": {
                                    "id": %s
                                  }
                                }
                                """.formatted(libraryId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Clean Code")))
                .andExpect(jsonPath("$.author", is("Robert C. Martin")))
                .andExpect(jsonPath("$.year", is(2008)))
                .andExpect(jsonPath("$.library.id", is(Integer.parseInt(libraryId))))
                .andReturn()
                .getResponse()
                .getContentAsString();

        String bookId = firstId(bookResponse);

        mockMvc.perform(get("/books/" + bookId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Clean Code")));

        mockMvc.perform(get("/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        mockMvc.perform(post("/books/filter")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "author": "Robert C. Martin"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(Integer.parseInt(bookId))));

        mockMvc.perform(put("/books/" + bookId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Clean Architecture",
                                  "author": "Robert C. Martin",
                                  "year": 2017,
                                  "library": {
                                    "id": %s
                                  }
                                }
                                """.formatted(libraryId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Clean Architecture")))
                .andExpect(jsonPath("$.year", is(2017)));

        mockMvc.perform(delete("/books/" + bookId))
                .andExpect(status().isOk());

        mockMvc.perform(get("/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", empty()));
    }

    @Test
    void shouldCreateReadUpdateAndDeleteLibrary() throws Exception {
        String libraryResponse = mockMvc.perform(post("/libraries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Branch Library"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Branch Library")))
                .andReturn()
                .getResponse()
                .getContentAsString();

        String libraryId = firstId(libraryResponse);

        mockMvc.perform(get("/libraries/" + libraryId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Branch Library")));

        mockMvc.perform(get("/libraries"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        mockMvc.perform(put("/libraries/" + libraryId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Updated Branch"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Updated Branch")));

        mockMvc.perform(delete("/libraries/" + libraryId))
                .andExpect(status().isOk());

        mockMvc.perform(get("/libraries"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", empty()));
    }

    @Test
    void shouldCreateReadUpdateAndDeleteUser() throws Exception {
        String userResponse = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Anna Kowalska",
                                  "email": "anna.kowalska@example.com",
                                  "userType": "STUDENT"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Anna Kowalska")))
                .andExpect(jsonPath("$.email", is("anna.kowalska@example.com")))
                .andReturn()
                .getResponse()
                .getContentAsString();

        String userId = firstId(userResponse);

        mockMvc.perform(get("/users/" + userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email", is("anna.kowalska@example.com")));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        mockMvc.perform(put("/users/" + userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Anna Nowak",
                                  "email": "anna.nowak@example.com",
                                  "userType": "TEACHER"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Anna Nowak")))
                .andExpect(jsonPath("$.userType", is("TEACHER")));

        mockMvc.perform(delete("/users/" + userId))
                .andExpect(status().isOk());

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", empty()));
    }

    @Test
    void shouldBorrowAndReturnBook() throws Exception {
        String libraryResponse = mockMvc.perform(post("/libraries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Borrow Library"
                                }
                                """))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        String libraryId = firstId(libraryResponse);

        String bookResponse = mockMvc.perform(post("/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "Domain-Driven Design",
                                  "author": "Eric Evans",
                                  "year": 2003,
                                  "library": {
                                    "id": %s
                                  }
                                }
                                """.formatted(libraryId)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        String bookId = firstId(bookResponse);

        String userResponse = mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Jan Nowak",
                                  "email": "jan.nowak@example.com",
                                  "userType": "STUDENT"
                                }
                                """))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        String userId = firstId(userResponse);

        String borrowResponse = mockMvc.perform(post("/borrows")
                        .param("userId", userId)
                        .param("bookId", bookId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.id", is(Integer.parseInt(userId))))
                .andExpect(jsonPath("$.book.id", is(Integer.parseInt(bookId))))
                .andExpect(jsonPath("$.returnDate").doesNotExist())
                .andReturn()
                .getResponse()
                .getContentAsString();
        String borrowId = firstId(borrowResponse);

        mockMvc.perform(get("/borrows/" + borrowId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(Integer.parseInt(borrowId))));

        mockMvc.perform(get("/borrows"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        mockMvc.perform(put("/borrows/" + borrowId + "/return"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.returnDate").exists());
    }

    private static String firstId(String json) {
        Matcher matcher = ID_PATTERN.matcher(json);
        if (!matcher.find()) {
            throw new IllegalArgumentException("Response does not contain id: " + json);
        }
        return matcher.group(1);
    }
}
