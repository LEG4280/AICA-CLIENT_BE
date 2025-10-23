package com.aica.aivoca.pronunciation.controller;

import com.aica.aivoca.pronunciation.dto.PronunciationResponseDto;
import com.aica.aivoca.pronunciation.service.PronunciationAssessmentService;
import com.microsoft.cognitiveservices.speech.PronunciationAssessmentResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/pronunciation")
public class PronunciationController {

    private final PronunciationAssessmentService pronunciationService;

    public PronunciationController(PronunciationAssessmentService pronunciationService) {
        this.pronunciationService = pronunciationService;
    }

    /*
     * ⭐️ 아래 메서드는 이제 WebSocket 방식으로 대체되었으므로 주석 처리합니다.
     * 이 코드를 남겨두면 assessPronunciation 메서드가 없다는 컴파일 오류가 발생합니다.
     */
    /*
    @PostMapping("/assess")
    public ResponseEntity<?> assessPronunciation(
            @RequestParam("audio") MultipartFile audioFile,
            @RequestParam("text") String referenceText) {

        try {
            byte[] audioData = audioFile.getBytes();

            PronunciationAssessmentResult result = pronunciationService.assessPronunciation(audioData, referenceText);

            if (result != null) {
                PronunciationResponseDto responseDto = new PronunciationResponseDto(
                        result.getAccuracyScore(),
                        result.getFluencyScore(),
                        result.getPronunciationScore()
                );
                return ResponseEntity.ok(responseDto);
            } else {
                return ResponseEntity.internalServerError().body("발음 평가에 실패했습니다.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("요청 처리 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
    */
}