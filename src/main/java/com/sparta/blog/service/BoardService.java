package com.sparta.blog.service;

import com.sparta.blog.dto.BoardRequestDto;
import com.sparta.blog.dto.BoardResponseDto;
import com.sparta.blog.entity.Board;
import com.sparta.blog.entity.LikeBoard;
import com.sparta.blog.entity.User;
import com.sparta.blog.entity.UserRoleEnum;
import com.sparta.blog.repository.BoardRepository;
import com.sparta.blog.repository.LikeBoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;
    private final LikeBoardRepository likeBoardRepository;


    // 게시글 작성
    public BoardResponseDto createBoard(BoardRequestDto requestDto, User user) {
        Board board = boardRepository.save(new Board(requestDto, user));
        return new BoardResponseDto(board);
    }

    // 전체 게시글 목록 조회
    public List<BoardResponseDto> getBoards() {
        return boardRepository.findAllByOrderByCreatedAtDesc().stream().map(BoardResponseDto::new).toList();
    }

    // 선택한 게시글 조회
    public List<BoardResponseDto> getBoard(Long id) {
        return boardRepository.findById(id).stream().map(BoardResponseDto::new).toList();
    }

    // 선택한 게시글 수정
    @Transactional
    public ResponseEntity<BoardResponseDto> updateBoard(Long id, BoardRequestDto requestDto, User user) {
        Board board = findBoard(id);

        if (user.getRole().equals(UserRoleEnum.ADMIN)) {
            board.update(requestDto.getTitle(), requestDto.getContents());
            return ResponseEntity.status(HttpStatus.OK).body(new BoardResponseDto(board));
        }

        if (!board.getUser().getUsername().equals(user.getUsername())) {
            throw new IllegalArgumentException("잘못된 접근입니다.");
        }

        board.update(requestDto.getTitle(), requestDto.getContents());
        return ResponseEntity.status(HttpStatus.OK).body(new BoardResponseDto(board));
    }

    // 선택한 게시글 삭제
    @Transactional
    public ResponseEntity<String> deleteBoard(Long id, User user) {
        Board board = findBoard(id);

        if (user.getRole().equals(UserRoleEnum.ADMIN)) {
            boardRepository.delete(board);
            return ResponseEntity.status(200).body("상태코드 : " + HttpStatus.OK.value() + " 메세지 : 관리자 권한 게시물 삭제 성공");
        }

        if (!board.getUser().getUsername().equals(user.getUsername())) {
            return ResponseEntity.status(404).body("상태코드 : " + HttpStatus.BAD_REQUEST.value() + " 메세지 : 게시물 삭제 불가");
        }

        boardRepository.delete(board);
        return ResponseEntity.status(200).body("상태코드 : " + HttpStatus.OK.value() + " 메세지 : 게시물 삭제 성공");
    }


    private Board findBoard(Long id) {
        return boardRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("선택한 메모는 존재하지 않습니다.")
        );
    }

    @Transactional
    // 게시글 좋아요 기능
    public ResponseEntity<String> likeBoard(Long id, User user) {
        Board board = findBoard(id);
        Optional<LikeBoard> likeBoardList = likeBoardRepository.findByBoardIdAndUserId(id, user.getId());

        if(likeBoardList.isEmpty()) {
            likeBoardRepository.save(new LikeBoard(user, board));
            return ResponseEntity.status(200).body("상태코드 : " + HttpStatus.OK.value() + " 메세지 : 게시물 좋아요 성공");
        }

        likeBoardRepository.delete(likeBoardList.get());
        return ResponseEntity.status(200).body("상태코드 : " + HttpStatus.OK.value() + " 메세지 : 게시물 좋아요 취소 성공");}
    }
