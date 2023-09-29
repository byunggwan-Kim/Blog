package com.sparta.blog.security;

import com.sparta.blog.entity.RefreshToken;
import com.sparta.blog.entity.UserRoleEnum;
import com.sparta.blog.jwt.JwtUtil;
import com.sparta.blog.repository.RefreshTokenRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j(topic = "JWT 검증 및 인가")
public class JwtAuthorizationFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService;
    private final RefreshTokenRepository refreshTokenRepository;

    public JwtAuthorizationFilter(JwtUtil jwtUtil, UserDetailsServiceImpl userDetailsService, RefreshTokenRepository refreshTokenRepository) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    // 필터 검증
    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain filterChain) throws ServletException, IOException {
        String accessToken = jwtUtil.getTokenFromRequest(req);
        String refreshToken = jwtUtil.getJwtFromHeader(req);

        // 토큰이 null인지, 길이가 0인지, 공백이 포함 되어 있는지 확인
        if (StringUtils.hasText(accessToken)) {

            accessToken = jwtUtil.substringToken(accessToken);
            if (!jwtUtil.validateToken(accessToken)) {
                if (!jwtUtil.validateToken(refreshToken)) {
                throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
            }
            Claims info = jwtUtil.getUserInfoFromToken(refreshToken);
            String username = info.getSubject();

            RefreshToken existRefreshToken = refreshTokenRepository.findByUsername(username).orElseThrow(() ->
                    new IllegalArgumentException("유효하지 않는 토큰입니다."));
            String existRefreshTokenCode = jwtUtil.substringToken(existRefreshToken.getToken());
            if (!existRefreshTokenCode.equals(refreshToken)) throw new IllegalArgumentException("유효하지 않는 토큰입니다.");
        }
        Claims info = jwtUtil.getUserInfoFromToken(refreshToken);
        String username = info.getSubject();
        UserRoleEnum role = UserRoleEnum.valueOf(String.valueOf(info.get("auth")));

        accessToken = jwtUtil.createAccessToken(username, role);
        jwtUtil.addJwtToCookie(accessToken, res);
        accessToken = jwtUtil.substringToken(accessToken);

        info = jwtUtil.getUserInfoFromToken(accessToken);
        try {
            setAuthentication(info.getSubject());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
        filterChain.doFilter(req,res);
}

    // 권한 인증 처리
    public void setAuthentication(String username) {
        // 인증 컨텍스트 생성
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        // 인증 객체 생성
        Authentication authentication = createAuthentication(username);
        // 인증 컨텍스트에 인증 정보를 담는다.
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
    }

    // 인증 객체 생성
    private Authentication createAuthentication(String username) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }
}