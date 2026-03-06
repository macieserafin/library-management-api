package pl.edu.pjwstk.library_spring.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookFilterDto {
    private String title;
    private String author;
    private String year;
}
