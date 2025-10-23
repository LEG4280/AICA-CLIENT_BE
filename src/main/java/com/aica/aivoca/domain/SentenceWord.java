package com.aica.aivoca.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Builder;

@Entity
@Table(name = "sentence_word")
@IdClass(SentenceWordId.class)
@Getter
@NoArgsConstructor
public class SentenceWord {


    @Id
    @Column(name = "sentence_id")
    private Long sentenceId;

    @Id
    @Column(name = "user_id")
    private Long userId;

    @Id
    @Column(name = "word_id")
    private Long wordId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "sentence_id", referencedColumnName = "sentence_id", insertable = false, updatable = false),
            @JoinColumn(name = "user_id", referencedColumnName = "user_id", insertable = false, updatable = false)
    })
    private Sentence sentence;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "word_id", insertable = false, updatable = false)
    private Word word;

    @Builder
    public SentenceWord(Long sentenceId, Long userId, Long wordId, Sentence sentence, Word word) {
        this.sentenceId = sentenceId;
        this.userId = userId;
        this.wordId = wordId;
        this.sentence = sentence;
        this.word = word;
    }
}
