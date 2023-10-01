package com.sparta.blog.controller;

import com.sparta.blog.dto.*;
import com.sparta.blog.security.UserDetailsImpl;
import com.sparta.blog.service.BoardService;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api")
public class BoardController {

    private final BoardService boardService;

    public BoardController(BoardService boardService) { this.boardService = boardService; }

    @PostMapping(value = "/board", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ApiResponse<BoardResponseDto> createBoard(@RequestParam(value = "image") MultipartFile image,
                                                    @ModelAttribute BoardRequestModel boardRequestModel,
                                                    @AuthenticationPrincipal UserDetailsImpl userDetails) throws IOException {
        return boardService.createBoard(boardRequestModel, userDetails.getUser(), image);
    }

    // 전체 게시글 조회
    @GetMapping("/boards")
    public ApiResponse<List<BoardResponseDto>> getBoards() {
        return boardService.getBoards();
    }

    // 선택한 게시글 조회
    @GetMapping("/board/{id}")
    public ApiResponse<BoardResponseDto> getBoard(@PathVariable Long id) {
        return boardService.getBoard(id);
    }

    // 게시글 수정
    @PutMapping("/board/{id}")
    public ApiResponse<BoardResponseDto> updateBoard(@PathVariable Long id, @RequestBody BoardRequestDto requestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return boardService.updateBoard(id, requestDto, userDetails.getUser());
    }

    // 게시글 삭제
    @DeleteMapping("/board/{id}")
    public ApiResponse<String> deleteBoard(@PathVariable Long id, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return boardService.deleteBoard(id, userDetails.getUser());
    }

    // 게시글 좋아요 기능
    @PutMapping("/board/{id}/like")
    public ApiResponse<String> likeBoard(@PathVariable Long id, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return boardService.likeBoard(id, userDetails.getUser());
    }
}
