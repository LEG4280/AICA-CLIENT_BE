package com.aica.aivoca.pronunciation.service;

import com.aica.aivoca.pronunciation.dto.PronunciationResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.cognitiveservices.speech.*;
import com.microsoft.cognitiveservices.speech.audio.AudioConfig;
import com.microsoft.cognitiveservices.speech.audio.PushAudioInputStream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import java.io.IOException;

@Service
public class PronunciationAssessmentService {

    @Value("${azure.speech.subscription-key}")
    private String subscriptionKey;

    @Value("${azure.speech.service-region}")
    private String serviceRegion;

    // DTO 객체를 JSON 문자열로 변환하기 위해 ObjectMapper를 사용
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 실시간 스트리밍 발음 평가를 시작하고 SpeechRecognizer를 설정합니다.
     * @param session 클라이언트와 통신할 WebSocket 세션
     * @param referenceText 평가 기준이 될 텍스트
     * @param pushStream 오디오 데이터를 받을 스트림
     * @return 생성 및 시작된 SpeechRecognizer 인스턴스
     */
    public SpeechRecognizer startContinuousAssessment(WebSocketSession session, String referenceText, PushAudioInputStream pushStream) throws Exception {

        SpeechConfig speechConfig = SpeechConfig.fromSubscription(subscriptionKey, serviceRegion);
        speechConfig.setSpeechRecognitionLanguage("en-US");
        speechConfig.setProperty("SpeechServiceResponse_EndSilenceTimeoutMs", "3000");


        // 발음 평가에 대한 상세 설정
        PronunciationAssessmentConfig pronunciationConfig = new PronunciationAssessmentConfig(
                referenceText,
                PronunciationAssessmentGradingSystem.HundredMark,
                PronunciationAssessmentGranularity.Phoneme,
                true);

        // 오디오 입력을 파일이 아닌, 실시간 스트림으로 받도록 설정
        AudioConfig audioConfig = AudioConfig.fromStreamInput(pushStream);

        // 설정들을 종합하여 SpeechRecognizer 인스턴스 생성
        SpeechRecognizer recognizer = new SpeechRecognizer(speechConfig, audioConfig);
        pronunciationConfig.applyTo(recognizer);

        // 이벤트 리스너 등록: 음성 인식이 성공할 때마다 이 부분이 실행
        recognizer.recognized.addEventListener((s, e) -> {
            // 결과가 유효한 음성 인식인 경우에만 처리
            if (e.getResult().getReason() == ResultReason.RecognizedSpeech) {
                System.out.println("RECOGNIZED: Text=" + e.getResult().getText());
                PronunciationAssessmentResult assessmentResult = PronunciationAssessmentResult.fromResult(e.getResult());

                PronunciationResponseDto responseDto = new PronunciationResponseDto(
                        assessmentResult.getAccuracyScore()
                );
                try {
                    // DTO를 JSON 문자열로 변환하여 WebSocket 세션을 통해 클라이언트로 전송
                    String jsonResponse = objectMapper.writeValueAsString(responseDto);
                    session.sendMessage(new TextMessage(jsonResponse));
                } catch (IOException ex) {
                    System.err.println("결과 전송 중 오류 발생: " + ex.getMessage());
                }
            }
        });
        // Canceled 이벤트 처리 (오류 발생 시 로그 출력)
        recognizer.canceled.addEventListener((s, e) -> {
            System.out.println("CANCELED: Reason=" + e.getReason());
            if (e.getReason() == CancellationReason.Error) {
                System.out.println("CANCELED: ErrorDetails=" + e.getErrorDetails());
            }
        });

        // 비동기적으로 연속적인 음성 인식을 시작
        recognizer.startContinuousRecognitionAsync().get();
        return recognizer;
    }

    /**
     * 진행 중인 발음 평가를 중지하고 리소스를 정리합니다.
     * @param recognizer 중지할 SpeechRecognizer 인스턴스
     */
    public void stopContinuousAssessment(SpeechRecognizer recognizer) throws Exception {
        if (recognizer != null) {
            // 연속 인식 중지
            recognizer.stopContinuousRecognitionAsync().get();
            // 리소스 해제
            recognizer.close();
        }
    }
}