package com.aica.aivoca.word.dto;

import java.util.List;

public record WordGetResponseDto(
        Long wordId,
        Long sentenceId,
        String word,
        List<MeaningDto> meanings
) {}