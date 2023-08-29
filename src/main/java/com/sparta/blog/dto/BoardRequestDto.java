package com.sparta.blog.dto;
import lombok.Getter;

@Getter
public class BoardRequestDto {
    private String title;
    private String writer;
    private String contents;
    private String password;

    public BoardRequestDto(String title, String writer, String contents, String password) {
        this.title = title;
        this.writer = writer;
        this.contents = contents;
        this.password = password;
    }
}
