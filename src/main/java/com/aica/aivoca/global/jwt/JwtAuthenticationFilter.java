package com.aica.aivoca.global.jwt;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    // 필터를 건너뛸 URI 목록 (토큰 없이 접근 허용)
    private static final List<String> WHITELIST = List.of(
            "/api/auth",
            "/api/login",
            "/api/reissue",
            "/api/voca",
            "/api/wordinfo",
            "/hc",
            "/env",
            "/api/pronunciation",
            "/ws/pronunciation"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();

        // 화이트리스트 경로는 필터 로직 건너뜀
        if (WHITELIST.stream().anyMatch(path::startsWith)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = resolveToken(request);

        try {
            if (token == null) {
                throw new IllegalArgumentException("Authorization 헤더가 없습니다.");
            }

            if (!jwtTokenProvider.validateToken(token)) {
                throw new IllegalArgumentException("액세스토큰이 유효하지 않습니다.");
            }

            Claims claims = jwtTokenProvider.getClaims(token);
            Long userId = Long.parseLong(claims.getSubject());
            String userUid = (String) claims.get("user_uid");

            Authentication auth = new UsernamePasswordAuthenticationToken(
                    new CustomUserDetails(userId, userUid),
                    null,
                    List.of()
            );
            SecurityContextHolder.getContext().setAuthentication(auth);

            filterChain.doFilter(request, response);

        } catch (IllegalArgumentException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // or 401 if preferred
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\": 401, \"message\": \"" + e.getMessage() + "\"}");
        }
    }

    private String resolveToken(HttpServletRequest request) {
        String bearer = request.getHeader("Authorization");
        if (bearer != null && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }
}
