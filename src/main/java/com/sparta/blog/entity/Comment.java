package com.sparta.blog.entity;

import com.sparta.blog.dto.CommentRequestDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "comment")
@NoArgsConstructor
public class Comment extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String comments;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Comment(CommentRequestDto requestDto, Board board, User user) {
        this.comments = requestDto.getComments();
        this.board = board;
        this.user = user;
    }

    public void update(CommentRequestDto requestDto, User user) {
        this.comments = requestDto.getComments();
        this.user = user;
    }
}
