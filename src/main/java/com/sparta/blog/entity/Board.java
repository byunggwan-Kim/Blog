package com.sparta.blog.entity;

import com.sparta.blog.dto.BoardRequestDto;
import com.sparta.blog.dto.BoardRequestModel;
import com.sparta.blog.dto.CommentResponseDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "board")
@NoArgsConstructor
public class Board extends Timestamped{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "contents", nullable = false)
    private String contents;

    @Column
    private String imgUrl;

    @OneToMany(mappedBy = "board", cascade = CascadeType.REMOVE)
    private List<Comment> commentsList = new ArrayList<>();

    @OneToMany(mappedBy = "board", cascade = CascadeType.REMOVE)
    private List<LikeBoard> likeBoardList = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="users_id", nullable = false)
    private User user;

    public Board(BoardRequestModel boardRequestModel, User user, String imgUrl) {
        this.title = boardRequestModel.getTitle();
        this.contents = boardRequestModel.getContents();
        this.imgUrl = imgUrl;
        this.user = user;
    }

    public void update(String title, String contents) {
        this.title = title;
        this.contents = contents;
    }
}
