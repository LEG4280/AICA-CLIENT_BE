package com.aica.aivoca.wordinfo.dto;

import com.aica.aivoca.word.dto.MeaningDto;

import java.util.List;

public record WordInfoDto(
        Long wordId,
        String word,
        List<MeaningDto> meanings
) {}