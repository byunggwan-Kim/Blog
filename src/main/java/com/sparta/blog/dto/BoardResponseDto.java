package com.sparta.blog.dto;

import com.sparta.blog.entity.Board;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public class BoardResponseDto {
    private Long id;
    private String title;
    private String username;
    private String contents;
    private String imgUrl;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private Integer likeCount;

    private List<CommentResponseDto> commentList = new ArrayList<>();
//    private String password;


    public BoardResponseDto(Board board) {
        this.id = board.getId();
        this.title = board.getTitle();
        this.username = board.getUser().getUsername();
        this.contents = board.getContents();
        this.imgUrl = board.getImgUrl();
        this.createdAt = board.getCreatedAt();
        this.modifiedAt = board.getModifiedAt();
        board.getCommentsList().forEach(comment -> commentList.add(new CommentResponseDto(comment)));
        Collections.reverse(commentList);
        this.likeCount = board.getLikeBoardList().size();
    }

    public BoardResponseDto(Long id, BoardRequestDto requestDto) {
        this.id = id;
        this.title = requestDto.getTitle();
        this.contents = requestDto.getContents();
    }
}
