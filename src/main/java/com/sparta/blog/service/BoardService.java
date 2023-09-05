package com.sparta.blog.service;

import com.sparta.blog.dto.BoardRequestDto;
import com.sparta.blog.dto.BoardResponseDto;
import com.sparta.blog.entity.Board;
import com.sparta.blog.entity.User;
import com.sparta.blog.jwt.JwtUtil;
import com.sparta.blog.repository.BoardRepository;
import com.sparta.blog.repository.UserRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CookieValue;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    // 게시글 작성
    public BoardResponseDto createBoard(BoardRequestDto requestDto, @CookieValue(JwtUtil.AUTHORIZATION_HEADER) String tokenValue) {

        String token = jwtUtil.substringToken(tokenValue);

        if (!jwtUtil.validateToken(token)) {throw new IllegalArgumentException("Token Error");}

        Claims claims = jwtUtil.getUserInfoFromToken(token);

        String user = claims.getSubject();

        // RequestDto -> Entity
        Board board = new Board(requestDto, user);

        // DB 저장
        Board saveBoard = boardRepository.save(board);

        // Entity -> ResponseDto
        BoardResponseDto boardResponseDto = new BoardResponseDto(saveBoard);

        return boardResponseDto;
    }

    // 전체 게시글 목록 조회
    public List<BoardResponseDto> getBoards() {
        // DB 조회
        return boardRepository.findAllByOrderByCreatedAtDesc().stream().map(BoardResponseDto::new).toList();
    }

    // 선택한 게시글 조회
    public List<BoardResponseDto> getBoard(Long id) {
        return boardRepository.findById(id).stream().map(BoardResponseDto::new).toList();
    }

    // 선택한 게시글 수정
    @Transactional
    public BoardResponseDto updateBoard(Long id, BoardRequestDto requestDto, @CookieValue(JwtUtil.AUTHORIZATION_HEADER) String tokenValue) {

        String token = jwtUtil.substringToken(tokenValue);

        if (!jwtUtil.validateToken(token)) {throw new IllegalArgumentException("Token Error");}

        Claims claims = jwtUtil.getUserInfoFromToken(token);

        Board board = findBoard(id);
        board.update(requestDto.getTitle(), requestDto.getContents());
        return new BoardResponseDto(board);
    }

    // 선택한 게시글 삭제
    public BoardResponseDto deleteBoard(Long id, @CookieValue(JwtUtil.AUTHORIZATION_HEADER) String tokenValue) {

        String token = jwtUtil.substringToken(tokenValue);

        if (!jwtUtil.validateToken(token)) {throw new IllegalArgumentException("Token Error");}

        Claims claims = jwtUtil.getUserInfoFromToken(token);

        Board board = findBoard(id);
        boardRepository.delete(board);
        return new BoardResponseDto(board);
    }
    private Board findBoard(Long id) {
        return boardRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("선택한 메모는 존재하지 않습니다.")
        );
    }

}
