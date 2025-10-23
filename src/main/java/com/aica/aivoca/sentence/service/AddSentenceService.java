package com.aica.aivoca.sentence.service;

import com.aica.aivoca.domain.Sentence;
import com.aica.aivoca.domain.Users; // Users import 주석 해제
import com.aica.aivoca.global.exception.CustomException;
import com.aica.aivoca.global.exception.dto.SuccessStatusResponse;
import com.aica.aivoca.global.exception.message.ErrorMessage;
import com.aica.aivoca.global.exception.message.SuccessMessage;
import com.aica.aivoca.sentence.dto.SentenceRequestDto;
import com.aica.aivoca.sentence.dto.SentenceResponseDto;
import com.aica.aivoca.sentence.repository.SentenceRepository;
import com.aica.aivoca.user.repository.UsersRepository; // UsersRepository import 주석 해제
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.aica.aivoca.global.jwt.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Slf4j
@Service
@RequiredArgsConstructor
public class AddSentenceService {

    private final SentenceRepository sentenceRepository;
    private final UsersRepository usersRepository;

    @Transactional
    public SuccessStatusResponse<SentenceResponseDto> addSentence(SentenceRequestDto requestDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || !(authentication.getPrincipal() instanceof CustomUserDetails)) {
            throw new CustomException(ErrorMessage.USER_NOT_FOUND);
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long currentUserId = userDetails.userId();

        Users user = usersRepository.findById(currentUserId)
                .orElseThrow(() -> new CustomException(ErrorMessage.USER_NOT_FOUND));

        // 요청 DTO 유효성 검사 (필요시)
        if (requestDto.sentenceId() == null) {
            throw new CustomException(ErrorMessage.SENTENCE_ID_REQUIRED);
        }
        if (requestDto.sentence() == null || requestDto.sentence().isBlank()) {
            throw new CustomException(ErrorMessage.SENTENCE_TEXT_REQUIRED);
        }

        Long sentenceSpecificId = requestDto.sentenceId();

        // 복합 키 중복 검사 (sentence_id + user_id)
        if (sentenceRepository.existsByIdAndUserId(sentenceSpecificId, currentUserId)) {
            throw new CustomException(ErrorMessage.SENTENCE_ID_ALREADY_EXISTS);
        }


        if (sentenceRepository.existsByUserAndSentence(user, requestDto.sentence())) {
            // 이 사용자는 이 문장 내용을 이미 다른 ID로 저장했음을 나타냄
            throw new CustomException(ErrorMessage.SENTENCE_ALREADY_EXISTS);
        }

        // Sentence 객체 생성
        Sentence sentence = Sentence.builder()
                .id(sentenceSpecificId)
                .userId(currentUserId)
                .user(user)
                .sentence(requestDto.sentence())
                .build();

        sentenceRepository.save(sentence);

        // 응답 DTO 생성
        SentenceResponseDto responseDto = new SentenceResponseDto(sentence.getId(), sentence.getUserId(), sentence.getSentence());

        return SuccessStatusResponse.of(SuccessMessage.SENTENCE_ADD_SUCCESS, responseDto);
    }
}