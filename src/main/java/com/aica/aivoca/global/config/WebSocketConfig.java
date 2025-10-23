package com.aica.aivoca.global.config;

import com.aica.aivoca.pronunciation.handler.PronunciationWebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * WebSocket 관련 설정을 위한 클래스입니다.
 */
@Configuration
@EnableWebSocket // Spring에서 WebSocket 기능을 활성화
public class WebSocketConfig implements WebSocketConfigurer {

    private final PronunciationWebSocketHandler pronunciationWebSocketHandler;

    // PronunciationWebSocketHandler를 주입받습니다.
    public WebSocketConfig(PronunciationWebSocketHandler pronunciationWebSocketHandler) {
        this.pronunciationWebSocketHandler = pronunciationWebSocketHandler;
    }

    /**
     * WebSocket 핸들러를 등록하는 메서드입니다.
     * @param registry 핸들러를 등록할 수 있는 레지스트리
     */
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // 클라이언트가 "/ws/pronunciation" 주소로 WebSocket 연결을 시도하면,
        // pronunciationWebSocketHandler가 그 통신을 처리하도록 설정합니다.
        registry.addHandler(pronunciationWebSocketHandler, "/ws/pronunciation")
                .setAllowedOrigins("*"); // 개발 중 편의를 위해 모든 도메인에서의 접속을 허용합니다.
    }
}