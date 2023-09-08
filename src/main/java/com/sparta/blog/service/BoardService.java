package com.sparta.blog.service;

import com.sparta.blog.dto.BoardRequestDto;
import com.sparta.blog.dto.BoardResponseDto;
import com.sparta.blog.entity.Board;
import com.sparta.blog.entity.User;
import com.sparta.blog.entity.UserRoleEnum;
import com.sparta.blog.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;

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
    public ResponseEntity<String> updateBoard(Long id, BoardRequestDto requestDto, User user) {
        Board board = findBoard(id);

        if(user.getRole().equals(UserRoleEnum.ADMIN)) {
            board.update(requestDto.getTitle(), requestDto.getContents());
            return ResponseEntity.status(200).body("상태코드 : " + HttpStatus.OK.value() + " 메세지 : 관리자 권한 게시물 수정 성공");}

        if(!board.getUser().getUsername().equals(user.getUsername())) {
            return ResponseEntity.status(404).body("상태코드 : " + HttpStatus.BAD_REQUEST.value() + " 메세지 : 게시물 수정 불가");}

        board.update(requestDto.getTitle(), requestDto.getContents());
        return ResponseEntity.status(200).body("상태코드 : " + HttpStatus.OK.value() + " 메세지 : 게시물 수정 성공");}


    // 선택한 게시글 삭제
    @Transactional
    public ResponseEntity<String> deleteBoard(Long id, User user) {
        Board board = findBoard(id);

        if(user.getRole().equals(UserRoleEnum.ADMIN)) {
            boardRepository.delete(board);
            return ResponseEntity.status(200).body("상태코드 : " + HttpStatus.OK.value() + " 메세지 : 관리자 권한 게시물 삭제 성공");}

        if(!board.getUser().getUsername().equals(user.getUsername())) {
            return ResponseEntity.status(404).body("상태코드 : " + HttpStatus.BAD_REQUEST.value() + " 메세지 : 게시물 삭제 불가");}

        boardRepository.delete(board);
        return ResponseEntity.status(200).body("상태코드 : " + HttpStatus.OK.value() + " 메세지 : 게시물 삭제 성공");}

    private Board findBoard(Long id) {
        return boardRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("선택한 메모는 존재하지 않습니다.")
        );
    }

}
