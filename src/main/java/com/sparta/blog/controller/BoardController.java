package com.sparta.blog.controller;

import com.sparta.blog.dto.BoardRequestDto;
import com.sparta.blog.dto.BoardResponseDto;
import com.sparta.blog.jwt.JwtUtil;
import com.sparta.blog.service.BoardService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class BoardController {

    private final BoardService boardService;

    public BoardController(BoardService boardService) { this.boardService = boardService; }

    // 게시글 작성
    @PostMapping("/board")
    public BoardResponseDto createBoard(@RequestBody BoardRequestDto requestDto,
                                        @CookieValue(JwtUtil.AUTHORIZATION_HEADER) String tokenValue) {
        return boardService.createBoard(requestDto, tokenValue);
    }

    // 전체 게시글 조회
    @GetMapping("/boards")
    public List<BoardResponseDto> getBoards() {
        return boardService.getBoards();
    }

    // 선택한 게시글 조회
    @GetMapping("/board/{id}")
    public List<BoardResponseDto> getBoard(@PathVariable Long id) {
        return boardService.getBoard(id);
    }

    // 게시글 수정
    @PutMapping("/board/{id}")
    public BoardResponseDto updateBoard(@PathVariable Long id,
                                        @RequestBody BoardRequestDto requestDto,
                                        @CookieValue(JwtUtil.AUTHORIZATION_HEADER) String tokenValue) {
        return boardService.updateBoard(id, requestDto, tokenValue);
    }

    // 게시글 삭제
    @DeleteMapping("/board/{id}")
    public BoardResponseDto deleteBoard(@PathVariable Long id,
                                        @CookieValue(JwtUtil.AUTHORIZATION_HEADER) String tokenValue) {
        return boardService.deleteBoard(id, tokenValue);
    }
}
