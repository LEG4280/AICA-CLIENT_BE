package com.aica.aivoca.word.service;

import com.aica.aivoca.domain.*;
import com.aica.aivoca.global.exception.CustomException;
import com.aica.aivoca.global.exception.dto.SuccessStatusResponse;
import com.aica.aivoca.global.exception.message.ErrorMessage;
import com.aica.aivoca.global.exception.message.SuccessMessage;
import com.aica.aivoca.sentence.repository.SentenceRepository;
import com.aica.aivoca.user.repository.UsersRepository;
import com.aica.aivoca.word.dto.*;
import com.aica.aivoca.word.repository.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class WordService {

    private final UsersRepository usersRepository;
    private final SentenceRepository sentenceRepository;
    private final WordRepository wordRepository;
    private final VocabularyListRepository vocabularyListRepository;
    private final VocabularyListWordRepository vocabularyListWordRepository;
    private final MeaningRepository meaningRepository;
    private final MeaningPartOfSpeechRepository mpsRepository;
    private final ExampleSentenceRepository exampleSentenceRepository;
    private final SentenceWordRepository sentenceWordRepository;

    @PersistenceContext
    private EntityManager em;

    // ✅ 단어장에 단어 추가 + 단어-문장테이블 추가
    @Transactional
    public SuccessStatusResponse<List<WordResponseDto>> addWordToVocabulary(WordAddRequestDto requestDto, Long userId) {
        Users user = usersRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorMessage.USER_NOT_FOUND));

        Sentence sentence = sentenceRepository.findById(new SentenceId(requestDto.sentenceId(), userId))
                .orElseThrow(() -> new CustomException(ErrorMessage.SENTENCE_ID_REQUIRED));

        Word word = wordRepository.findById(requestDto.wordId())
                .orElseThrow(() -> new CustomException(ErrorMessage.WORD_NOT_FOUND));

        VocabularyList vocaList = vocabularyListRepository.findByUsers(user)
                .orElseGet(() -> {
                    VocabularyList newList = VocabularyList.builder()
                            .users(user)
                            .build();
                    em.persist(newList);
                    return newList;
                });

        boolean alreadyInVocabulary = vocabularyListWordRepository.existsByVocabularyListAndWord(vocaList, word);
        SuccessMessage message;

        if (!alreadyInVocabulary) {
            VocabularyListWord link = VocabularyListWord.builder()
                    .vocabularyList(vocaList)
                    .word(word)
                    .build();
            vocabularyListWordRepository.save(link);
            message = SuccessMessage.WORD_ADDED_TO_VOCABULARY;
        } else {
            message = SuccessMessage.WORD_ALREADY_IN_VOCABULARY;
        }

        // 문장-단어 연결 테이블에 추가
        SentenceWordId sentenceWordId = new SentenceWordId(
                requestDto.sentenceId(),
                userId,
                requestDto.wordId()
        );

        if (!sentenceWordRepository.existsById(sentenceWordId)) {
            SentenceWord sentenceWord = SentenceWord.builder()
                    .sentenceId(requestDto.sentenceId())
                    .userId(userId)
                    .wordId(requestDto.wordId())
                    .build();

            sentenceWordRepository.save(sentenceWord);
        }

        List<Meaning> meanings = meaningRepository.findAllByWord(word);

        WordResponseDto responseDto = WordResponseDto.from(
                user.getId(),
                sentence.getId(),
                word.getWord(),
                meanings,
                mpsRepository,
                exampleSentenceRepository
        );

        return SuccessStatusResponse.of(message, List.of(responseDto));
    }


    // ✅ 단어장 전체 단어 조회 + 문장아이디 추가
    @Transactional(readOnly = true)
    public SuccessStatusResponse<List<WordGetResponseDto>> getMyVocabularyWords(Long userId) {
        VocabularyList vocaList = vocabularyListRepository.findByUsers_Id(userId)
                .orElseThrow(() -> new CustomException(ErrorMessage.VOCABULARY_LIST_NOT_FOUND));

        List<VocabularyListWord> vocabWords = vocabularyListWordRepository.findByVocabularyList(vocaList);

        List<WordGetResponseDto> result = vocabWords.stream()
                .flatMap(vocabWord -> {
                    Word word = vocabWord.getWord();
                    List<Meaning> meanings = meaningRepository.findAllByWord(word);

                    List<MeaningDto> meaningDtos = meanings.stream()
                            .map(meaning -> {
                                List<String> parts = mpsRepository.findAllByMeaning(meaning).stream()
                                        .map(mp -> mp.getPartOfSpeech().getName())
                                        .toList();

                                List<ExampleSentenceDto> examples = exampleSentenceRepository.findAllByMeaning(meaning).stream()
                                        .map(ex -> new ExampleSentenceDto(ex.getExamSentence(), ex.getExamMeaning()))
                                        .toList();

                                return new MeaningDto(meaning.getMean(), parts, examples);
                            })
                            .toList();

                    // 각 sentenceId 별로 Dto 반환
                    return sentenceWordRepository.findByUserIdAndWordId(userId, word.getId())
                            .stream()
                            .map(sentenceWord -> new WordGetResponseDto(
                                    word.getId(),
                                    sentenceWord.getSentenceId(),
                                    word.getWord(),
                                    meaningDtos
                            ));
                })
                .toList();

        return SuccessStatusResponse.of(SuccessMessage.GET_WORD_SUCCESS, result);
    }


    // ✅ 단어장 단어 삭제 + 단어-문장 테이블 에서도 삭제
    @Transactional
    public SuccessStatusResponse<Void> deleteWordFromVocabulary(Long wordId, Long userId) {
        VocabularyList vocaList = vocabularyListRepository.findByUsers_Id(userId)
                .orElseThrow(() -> new CustomException(ErrorMessage.VOCABULARY_LIST_NOT_FOUND));

        VocabularyListWord vocabWord = vocabularyListWordRepository.findByVocabularyList_UserIdAndWord_Id(
                        vocaList.getUserId(), wordId)
                .orElseThrow(() -> new CustomException(ErrorMessage.WORD_NOT_FOUND_IN_VOCABULARY));

        sentenceWordRepository.deleteAllByUserIdAndWordId(userId, wordId);

        vocabularyListWordRepository.delete(vocabWord);

        return SuccessStatusResponse.of(SuccessMessage.WORD_DELETED_FROM_VOCABULARY);
    }
}
