package com.aica.aivoca.sentence.service;

import com.aica.aivoca.domain.Sentence;
import com.aica.aivoca.global.exception.NotFoundException;
import com.aica.aivoca.global.exception.dto.SuccessStatusResponse;
import com.aica.aivoca.global.exception.message.ErrorMessage;
import com.aica.aivoca.global.exception.message.SuccessMessage;
import com.aica.aivoca.global.jwt.CustomUserDetails;
import com.aica.aivoca.sentence.dto.SentenceGetResponseDto;
import com.aica.aivoca.sentence.repository.SentenceRepository;

import com.aica.aivoca.word.repository.SentenceWordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetSentenceService {

    private final SentenceRepository sentenceRepository;
    private final SentenceWordRepository sentenceWordRepository;

    @Transactional(readOnly = true)
    public SuccessStatusResponse<List<SentenceGetResponseDto>> getSentences(Long ignoredUserId, String search) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.userId();

        List<Sentence> sentences = (search != null && !search.isEmpty())
                ? sentenceRepository.findByUser_IdAndSentenceContaining(userId, search)
                : sentenceRepository.findByUser_Id(userId);

        if (sentences.isEmpty()) {
            throw new NotFoundException(ErrorMessage.SENTENCE_NOT_FOUND_BY_USER);
        }

        List<SentenceGetResponseDto> result = sentences.stream()
                .map(sentence -> {
                    List<Long> wordIds = sentenceWordRepository.findBySentence_Id(sentence.getId()).stream()
                            .map(sw -> sw.getWord().getId())
                            .toList();

                    return new SentenceGetResponseDto(
                            sentence.getId(),
                            wordIds,
                            sentence.getUser() != null ? sentence.getUser().getId() : null,
                            sentence.getSentence()
                    );
                })
                .toList();

        return SuccessStatusResponse.of(SuccessMessage.SENTENCE_GET_SUCCESS, result);
    }
}
