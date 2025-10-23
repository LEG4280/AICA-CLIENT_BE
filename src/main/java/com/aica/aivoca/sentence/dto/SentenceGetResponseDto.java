package com.aica.aivoca.sentence.dto;

import java.util.List;

public record SentenceGetResponseDto(
        Long sentenceId,
        List<Long> wordIds,
        Long userId,
        String sentence
) {}