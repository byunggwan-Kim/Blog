package com.sparta.blog.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BoardRequestModel {
    private String title;
    private String contents;
    private String imgUrl;
}
