package com.sparta.blog.service;

import com.sparta.blog.dto.BoardRequestDto;
import com.sparta.blog.dto.BoardResponseDto;
import com.sparta.blog.dto.CommentRequestDto;
import com.sparta.blog.dto.CommentResponseDto;
import com.sparta.blog.entity.*;
import com.sparta.blog.repository.BoardRepository;
import com.sparta.blog.repository.CommentRepository;
import com.sparta.blog.repository.LikeCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;
    private final LikeCommentRepository likeCommentRepository;


    // 댓글 작성
    public CommentResponseDto createComment(CommentRequestDto requestDto, User user) {
        Board board = findBoard(requestDto.getBoardId());
        Comment comment = commentRepository.save(new Comment(requestDto, board, user));
        CommentResponseDto commentResponseDto = new CommentResponseDto(comment);
        //commentResponseDto에 comment를 넣어줌
        return commentResponseDto;
    }


    // 댓글 수정
    @Transactional
    public ResponseEntity<CommentResponseDto> updateComment(Long id, CommentRequestDto requestDto, User user) {
        Comment comment = findComment(id);

        if (user.getRole().equals(UserRoleEnum.ADMIN)) {
            comment.update(requestDto, user);
            return ResponseEntity.status(200).body(new CommentResponseDto(comment));}

            if (!comment.getUser().getUsername().equals(user.getUsername())) {
                throw new IllegalArgumentException("잘못된 접근입니다.");}

                comment.update(requestDto, user);
                return ResponseEntity.status(200).body(new CommentResponseDto(comment));}

    // 댓글 삭제
    @Transactional
    public ResponseEntity<String> deleteComment(Long id, User user) {
        Comment comment = findComment(id);

        if (user.getRole().equals(UserRoleEnum.ADMIN)) {
            commentRepository.delete(comment);
            return ResponseEntity.status(200).body("상태코드 : " + HttpStatus.OK.value() + " 메세지 : 관리자 권한 댓글 삭제 성공");
        }

        if (!comment.getUser().getUsername().equals(user.getUsername())) {
            return ResponseEntity.status(404).body("상태코드 : " + HttpStatus.BAD_REQUEST.value() + " 메세지 : 댓글 삭제 불가");
        }

        commentRepository.delete(comment);
        return ResponseEntity.status(200).body("상태코드 : " + HttpStatus.OK.value() + " 메세지 : 댓글 삭제 성공");
    }

    // 댓글 좋아요 기능
    @Transactional
    public ResponseEntity<String> likeComment(Long id, User user) {
        Comment comment = findComment(id);
        Optional<LikeComment> likeCommentList = likeCommentRepository.findByCommentIdAndUserId(id, user.getId());

        if (likeCommentList.isEmpty()) {
            likeCommentRepository.save(new LikeComment(user, comment));
            return ResponseEntity.status(200).body("상태코드 : " + HttpStatus.OK.value() + " 메세지 : 댓글 좋아요 성공");
        }

        likeCommentRepository.delete(likeCommentList.get());
        return ResponseEntity.status(200).body("상태코드 : " + HttpStatus.OK.value() + " 메세지 : 댓글 좋아요 취소 성공");
    }

    private Board findBoard(Long id) {
        return boardRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("선택한 메모는 존재하지 않습니다.")
        );
    }

    private Comment findComment(Long id) {
        return commentRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("선택한 댓글은 존재하지 않습니다.")
        );
    }


}
