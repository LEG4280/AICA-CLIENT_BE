package com.aica.aivoca.word.dto;

import com.aica.aivoca.domain.Meaning;
import com.aica.aivoca.word.repository.ExampleSentenceRepository;
import com.aica.aivoca.word.repository.MeaningPartOfSpeechRepository;

import java.util.List;

public record WordResponseDto(
        Long userId,
        Long sentenceId,
        String word,
        List<MeaningResponseDto> meanings
) {
    public static WordResponseDto from(
            Long userId,
            Long sentenceId,
            String word,
            List<Meaning> meanings,
            MeaningPartOfSpeechRepository mpsRepository,
            ExampleSentenceRepository exampleSentenceRepository
    ) {
        List<MeaningResponseDto> meaningDtos = meanings.stream()
                .map(meaning -> {
                    List<String> partList = mpsRepository.findAllByMeaning(meaning).stream()
                            .map(mps -> mps.getPartOfSpeech().getName())
                            .toList();

                    List<ExampleResponseDto> exampleDtos = exampleSentenceRepository.findAllByMeaning(meaning).stream()
                            .map(example -> new ExampleResponseDto(
                                    example.getExamSentence(),
                                    example.getExamMeaning()
                            ))
                            .toList();

                    return new MeaningResponseDto(
                            meaning.getMeanId(),
                            meaning.getMean(),
                            partList,
                            exampleDtos
                    );
                })
                .toList();

        return new WordResponseDto(userId, sentenceId, word, meaningDtos);
    }

    public record MeaningResponseDto(
            Long meaningId,
            String meaning,
            List<String> partOfSpeech,
            List<ExampleResponseDto> examples
    ) {}

    public record ExampleResponseDto(
            String sentence,
            String meaning
    ) {}
}