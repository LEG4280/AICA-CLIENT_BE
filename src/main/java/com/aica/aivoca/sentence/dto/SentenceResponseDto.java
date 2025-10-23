package com.aica.aivoca.sentence.dto;

public record SentenceResponseDto(
        Long sentenceId,
        Long userId,
        String sentence) {}