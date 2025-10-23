package com.aica.aivoca.sentence.service;

import com.aica.aivoca.domain.Sentence;
import com.aica.aivoca.domain.SentenceId;
import com.aica.aivoca.global.exception.CustomException;
import com.aica.aivoca.global.exception.dto.SuccessStatusResponse;
import com.aica.aivoca.global.exception.message.ErrorMessage;
import com.aica.aivoca.global.exception.message.SuccessMessage;
import com.aica.aivoca.global.jwt.CustomUserDetails;
import com.aica.aivoca.sentence.repository.SentenceRepository;
import com.aica.aivoca.word.repository.SentenceWordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeleteSentenceService {

    private final SentenceRepository sentenceRepository;
    private final SentenceWordRepository sentenceWordRepository;

    @Transactional
    public SuccessStatusResponse<Void> deleteSentence(Long sentenceSpecificId) {
        // 1. 현재 사용자 ID 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // 인증 확인
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long currentUserId = userDetails.userId();

        // 2. 복합 키 객체 생성
        SentenceId sentenceId = new SentenceId(sentenceSpecificId, currentUserId);

        // 3. 문장 조회 (복합 키 사용)
        Sentence sentence = sentenceRepository.findById(sentenceId) // findById에 SentenceId 객체 전달
                .orElseThrow(() -> {
                    log.warn("Sentence not found with composite id: {}", sentenceId);
                    return new CustomException(ErrorMessage.SENTENCE_NOT_FOUND);
                });

        // 4. 사용자 권한 검증
        if (sentence.getUser() == null || !sentence.getUser().getId().equals(currentUserId)) {
            //오류 처리
            throw new CustomException(ErrorMessage.FORBIDDEN_ACCESS);
        }

        // 문장-단어 테이블 삭제
        sentenceWordRepository.deleteBySentence(sentence);

        // 5. 문장 삭제
        sentenceRepository.delete(sentence); // 또는 sentenceRepository.deleteById(sentenceId);
        log.info("Sentence with composite id {} deleted successfully by user {}", sentenceId, currentUserId);

        // 6. 성공 응답 반환
        return SuccessStatusResponse.of(SuccessMessage.SENTENCE_DELETE_SUCCESS);
    }
}
