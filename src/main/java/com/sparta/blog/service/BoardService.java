package com.sparta.blog.service;

import com.sparta.blog.dto.BoardRequestDto;
import com.sparta.blog.dto.BoardResponseDto;
import com.sparta.blog.entity.Board;
import com.sparta.blog.repository.BoardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class BoardService {
    private final BoardRepository boardRepository;

    @Autowired
    public BoardService(BoardRepository boardRepository) {this.boardRepository = boardRepository;}


    // 게시글 작성
    public BoardResponseDto createBoard(BoardRequestDto requestDto) {
        // RequestDto -> Entity
        Board board = new Board(requestDto);

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
    public BoardResponseDto updateBoard(Long id, BoardRequestDto requestDto) {
        // 해당 메모가 DB에 존재하는지 확인
        Board board = findBoard(id);

        // 패스워드가 맞는지 확인 후 맞으면 내용 수정
        if(board.getPassword().equals(requestDto.getPassword())) {
            board.update(requestDto);

        } else {
            throw new IllegalArgumentException("패스워드가 일치하지 않습니다.");
        }
        return new BoardResponseDto(board);
    }

    // 선택한 게시글 삭제
    public BoardResponseDto deleteBoard(Long id, BoardRequestDto requestDto) {
        // 해당 메모가 DB에 존재하는지 확인
        Board board = findBoard(id);

        // 패스워드가 맞는지 확인 후 맞으면 삭제
        if(board.getPassword().equals(requestDto.getPassword())) {
            boardRepository.delete(board);

        } else {
            throw new IllegalArgumentException("패스워드가 일치하지 않습니다.");
        }
        return new BoardResponseDto(board);
    }
    private Board findBoard(Long id) {
        return boardRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("선택한 메모는 존재하지 않습니다.")
        );
    }

}
