package com.sparta.blog.dto;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class SignupRequestDto {
    @Size(min = 4,max = 10)
    @Pattern(regexp = "^[a-z0-9]*$", message = "올바른 양식이 아닙니다.")
    private String username;

    @Size(min = 8,max = 15)
    @Pattern(regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]*$", message = "비밀번호 형식이 일치하지 않습니다.")
    private String password;

    private boolean admin = false;
    private String adminToken = "";
}