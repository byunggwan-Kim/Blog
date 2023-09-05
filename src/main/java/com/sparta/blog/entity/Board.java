package com.sparta.blog.entity;

import com.sparta.blog.dto.BoardRequestDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "board")
@NoArgsConstructor
public class Board extends Timestamped{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "title", nullable = false)
    private String title;
    @Column(name = "username", nullable = false)
    private String username;
    @Column(name = "contents", nullable = false)
    private String contents;

    public Board(BoardRequestDto requestDto, String user) {
        this.title = requestDto.getTitle();
        this.contents = requestDto.getContents();
    }

    public void update(String title, String contents) {
        this.title = title;
        this.contents = contents;
    }
}
