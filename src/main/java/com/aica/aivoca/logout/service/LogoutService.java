package com.aica.aivoca.logout.service;

import com.aica.aivoca.login.repository.RefreshTokenRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogoutService {

    private final RefreshTokenRedisRepository refreshTokenRedisRepository;

    public void logout(Long userId) {
        // Redis에 저장된 RefreshToken이 있으면 삭제
        String savedRefreshToken = refreshTokenRedisRepository.findByUserId(userId);
        if (savedRefreshToken != null) {
            refreshTokenRedisRepository.delete(userId);
        }

        // RefreshToken이 없어도 무조건 성공으로 간주
        // 별도 예외를 발생시키지 않음

    }
}