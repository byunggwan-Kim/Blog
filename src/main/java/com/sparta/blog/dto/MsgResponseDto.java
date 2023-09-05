package com.sparta.blog.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public class MsgResponseDto {
    private String msg;
    private int StatusCode;

    public MsgResponseDto(String msg, int statusCode) {
        this.msg = msg;
        this.StatusCode = statusCode;
    }
}
