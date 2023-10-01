package com.sparta.blog.service;

import com.sparta.blog.dto.ApiResponse;
import com.sparta.blog.dto.BoardRequestDto;
import com.sparta.blog.dto.BoardRequestModel;
import com.sparta.blog.dto.BoardResponseDto;
import com.sparta.blog.entity.Board;
import com.sparta.blog.entity.LikeBoard;
import com.sparta.blog.entity.User;
import com.sparta.blog.entity.UserRoleEnum;
import com.sparta.blog.exception.CustomException;
import com.sparta.blog.exception.ErrorCode;
import com.sparta.blog.repository.BoardRepository;
import com.sparta.blog.repository.LikeBoardRepository;
import com.sparta.blog.repository.UserRepository;
import com.sparta.blog.s3.S3Uploader;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;
    private final LikeBoardRepository likeBoardRepository;
    private final S3Uploader s3Uploader;

    public ApiResponse<BoardResponseDto> createBoard(BoardRequestModel boardRequestModel, User user, MultipartFile image) throws IOException {
        if (!image.isEmpty()) {
            String fileName = s3Uploader.upload(image, "board/" + user.getUsername());
            Board board = boardRepository.save(new Board(boardRequestModel, user, fileName));
            return ApiResponse.successData(new BoardResponseDto(board));
        } else {
            throw new IOException("사진을 추가하여 주세요");
        }
    }

    // 전체 게시글 목록 조회
//    public List<BoardResponseDto> getBoards() {
//        return boardRepository.findAllByOrderByCreatedAtDesc().stream().map(BoardResponseDto::new).toList();
//    }

    public ApiResponse<List<BoardResponseDto>> getBoards() {
        List<BoardResponseDto> responseDtos = boardRepository.findAllByOrderByCreatedAtDesc().stream().map(BoardResponseDto::new).collect(Collectors.toList());
        return ApiResponse.successDataList(responseDtos);
    }

    // 선택한 게시글 조회
    public ApiResponse<BoardResponseDto> getBoard(Long id) {
        Optional<Board> boardOptional = boardRepository.findById(id);
        if (boardOptional.isPresent()) {
            return ApiResponse.successData(new BoardResponseDto(boardOptional.get()));
        } else {
            return ApiResponse.error("게시물을 찾을 수 없습니다.");
        }
    }

    // 선택한 게시글 수정
    @Transactional
    public ApiResponse<BoardResponseDto> updateBoard(Long id, BoardRequestDto requestDto, User user) {
        Board board = findBoard(id);

        if (user.getRole().equals(UserRoleEnum.ADMIN)) {
            board.update(requestDto.getTitle(), requestDto.getContents());
            return ApiResponse.successData(new BoardResponseDto(board));
        }

        if (!board.getUser().getUsername().equals(user.getUsername())) {
            throw new CustomException(ErrorCode.USERNAME_NOT_FOUND);
        }

        board.update(requestDto.getTitle(), requestDto.getContents());
        return ApiResponse.successData(new BoardResponseDto(board));
    }

    // 선택한 게시글 삭제
    @Transactional
    public ApiResponse<String> deleteBoard(Long id, User user) {
        Board board = findBoard(id);

        if (user.getRole().equals(UserRoleEnum.ADMIN)) {
            boardRepository.delete(board);
            return ApiResponse.successMessage("관리자 권한 게시물 삭제 성공");
        }

        if (!board.getUser().getUsername().equals(user.getUsername())) {
            return ApiResponse.error("게시물 삭제 불가");
        }

        boardRepository.delete(board);
        return ApiResponse.successMessage("게시물 삭제 성공");
    }

    private Board findBoard(Long id) {
        return boardRepository.findById(id).orElseThrow(() ->
                new CustomException(ErrorCode.USERNAME_NOT_FOUND)
        );
    }

    @Transactional
    // 게시글 좋아요 기능
    public ApiResponse<String> likeBoard(Long id, User user) {
        Board board = findBoard(id);
        Optional<LikeBoard> likeBoardList = likeBoardRepository.findByBoardIdAndUserId(id, user.getId());

        if (likeBoardList.isEmpty()) {
            likeBoardRepository.save(new LikeBoard(user, board));
            return ApiResponse.successMessage("게시물 좋아요 성공");
        }

        likeBoardRepository.delete(likeBoardList.get());
        return ApiResponse.successMessage("게시물 삭제 성공");
    }
}
