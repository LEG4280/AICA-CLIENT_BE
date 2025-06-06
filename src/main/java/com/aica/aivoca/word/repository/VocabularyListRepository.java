package com.aica.aivoca.word.repository;

import com.aica.aivoca.domain.Users;
import com.aica.aivoca.domain.VocabularyList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VocabularyListRepository extends JpaRepository<VocabularyList, Long> {

    // 유저의 단어장 존재 여부 확인
    boolean existsByUsers(Users user);

    // 유저의 단어장 조회
    Optional<VocabularyList> findByUsers(Users user);

    // 유저 ID로 단어장 조회
    Optional<VocabularyList> findByUsers_Id(Long userId);

    void deleteByUsers_Id(Long userId);
}