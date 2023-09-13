package com.sparta.blog.dto;

import com.sparta.blog.entity.Comment;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Getter
//@Service
@RequiredArgsConstructor
public class CommentResponseDto {

    private Long id;
    private String comments;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private String username;
    private Integer likeCount;

    public CommentResponseDto(Comment comment){//commentResponseDto에 comment를 넣어줌
        this.id = comment.getId();//댓글의 id
        this.username = comment.getUser().getUsername();//댓글을 쓴사람의 이름
        this.comments = comment.getComments();//댓글 내용
        this.createdAt = comment.getCreatedAt();
        this.modifiedAt = comment.getModifiedAt();
        this.likeCount = comment.getLikeCommentList().size();
    }
}
