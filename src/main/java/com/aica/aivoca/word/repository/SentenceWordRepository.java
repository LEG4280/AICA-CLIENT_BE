package com.aica.aivoca.word.repository;

import com.aica.aivoca.domain.Sentence;
import com.aica.aivoca.domain.SentenceWord;
import com.aica.aivoca.domain.SentenceWordId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SentenceWordRepository extends JpaRepository<SentenceWord, SentenceWordId> {
    List<SentenceWord> findByUserIdAndWordId(Long userId, Long wordId);

    void deleteByUserId(Long userId);

    void deleteAllByUserIdAndWordId(Long userId, Long wordId);

    List<SentenceWord> findBySentence_Id(Long sentenceId);

    void deleteBySentence(Sentence sentence);
}
