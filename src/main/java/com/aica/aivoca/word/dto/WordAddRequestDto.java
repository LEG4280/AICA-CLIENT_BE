package com.aica.aivoca.word.dto;

public record WordAddRequestDto(
        Long wordId,
        Long sentenceId
) {}