package com.sparta.blog.dto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BoardRequestDto {
    private String title;
    private String contents;

    public BoardRequestDto(String title, String contents) {
        this.title = title;
        this.contents = contents;
    }
}
