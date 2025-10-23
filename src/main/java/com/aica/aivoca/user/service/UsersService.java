package com.aica.aivoca.user.service;

import com.aica.aivoca.auth.repository.EmailVerificationRepository; // 👈 이 import 문은 여전히 필요합니다.
import com.aica.aivoca.domain.Users;
import com.aica.aivoca.global.exception.BusinessException;
import com.aica.aivoca.global.exception.message.ErrorMessage;
import com.aica.aivoca.sentence.repository.SentenceRepository;
import com.aica.aivoca.user.dto.PasswordVerificationRequestDto;
import com.aica.aivoca.user.dto.UserUpdateRequestDto;
import com.aica.aivoca.user.dto.UsersInfoResponse;
import com.aica.aivoca.word.repository.SentenceWordRepository;
import com.aica.aivoca.user.repository.UsersRepository;
import com.aica.aivoca.word.repository.VocabularyListRepository;
import com.aica.aivoca.word.repository.VocabularyListWordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UsersService {

    private final UsersRepository userRepository;
    private final VocabularyListRepository vocabularyListRepository;
    private final SentenceRepository sentenceRepository;
    private final SentenceWordRepository sentenceWordRepository;
    private final VocabularyListWordRepository vocabularyListWordRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailVerificationRepository emailVerificationRepository;
    private final RedisTemplate<String, String> redisTemplate;

    @Transactional(readOnly = true)
    public UsersInfoResponse getUserInfo(Long userId) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorMessage.USER_NOT_FOUND)); // USER_ID_NOT_FOUND는 USER_NOT_FOUND로 통합하는 게 좋습니다.

        return new UsersInfoResponse(user.getId(), user.getUserId(), user.getEmail(), user.getUserNickname());
    }


    @Transactional
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new BusinessException(ErrorMessage.USER_NOT_FOUND);
        }

        // 0. 단어장-단어 연결 테이블 먼저 삭제
        vocabularyListWordRepository.deleteByVocabularyList_UserId(userId);

        // 1. 문장-단어 연결 먼저 삭제
        sentenceWordRepository.deleteByUserId(userId);

        // 2. 사용자 문장 삭제
        sentenceRepository.deleteByUserId(userId);

        // 3. 사용자 단어장 삭제
        vocabularyListRepository.deleteByUsers_Id(userId);

        // 4. 사용자 계정 삭제
        userRepository.deleteById(userId);
    }



    @Transactional(readOnly = true)
    public void verifyCurrentPassword(Long userId, PasswordVerificationRequestDto requestDto) {
        Users user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorMessage.USER_NOT_FOUND));

        if (!passwordEncoder.matches(requestDto.currentPassword(), user.getPassword())) {
            throw new BusinessException(ErrorMessage.CURRENT_PASSWORD_MISMATCH);
        }
        redisTemplate.opsForValue().set("password_verified:" + userId, String.valueOf(true), Duration.ofMinutes(5));
    }

    @Transactional
    public Map<String, String> updateUser(Long id, UserUpdateRequestDto requestDto) {
        // 인증 여부 체크
        Boolean isVerified = Boolean.valueOf(redisTemplate.opsForValue().get("password_verified:" + id));
        if (isVerified == null || !isVerified) {
            throw new BusinessException(ErrorMessage.PASSWORD_VERIFICATION_REQUIRED);
        }



        Users user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorMessage.USER_NOT_FOUND));

        Map<String, String> updatedInfo = new HashMap<>();

        boolean isPasswordUpdate = requestDto.newPassword() != null && !requestDto.newPassword().isEmpty();
        boolean isEmailUpdate = requestDto.newEmail() != null && !requestDto.newEmail().isEmpty();
        boolean isNicknameUpdate = requestDto.newNickname() != null && !requestDto.newNickname().isEmpty();

        if (!isPasswordUpdate && !isEmailUpdate && !isNicknameUpdate) {
            throw new BusinessException(ErrorMessage.NO_UPDATE_DATA_PROVIDED);
        }

        if (isPasswordUpdate) {
            updatePassword(user, requestDto, updatedInfo);
        }

        if (isEmailUpdate) {
            updateEmail(user, requestDto, updatedInfo);
        }

        if (isNicknameUpdate) {
            updateNickname(user, requestDto, updatedInfo);
        }
        userRepository.save(user);
        redisTemplate.delete("password_verified:" + id);  // 1회 사용 후 인증 상태 삭제
        return updatedInfo;
    }

    private void updatePassword(Users user, UserUpdateRequestDto requestDto, Map<String, String> updatedInfo) {
        // newPassword가 null이 아니고 비어있지 않은 경우에만 비밀번호 변경 로직 실행
        if (requestDto.newPassword() != null && !requestDto.newPassword().isEmpty()) {
            if (!requestDto.newPassword().equals(requestDto.confirmNewPassword())) {
                throw new BusinessException(ErrorMessage.NEW_PASSWORD_CONFIRMATION_MISMATCH);
            }
            if (passwordEncoder.matches(requestDto.newPassword(), user.getPassword())) {
                throw new BusinessException(ErrorMessage.NEW_PASSWORD_SAME_AS_CURRENT);
            }
            user.setPassword(passwordEncoder.encode(requestDto.newPassword()));
        } else if (requestDto.confirmNewPassword() != null && !requestDto.confirmNewPassword().isEmpty()) {
            // newPassword는 null/빈 문자열인데 confirmNewPassword만 값이 있는 경우 오류 처리
            throw new BusinessException(ErrorMessage.PASSWORD_CHANGE_REQUIRED_FIELDS_MISSING);
        }
    }

    private void updateEmail(Users user, UserUpdateRequestDto requestDto, Map<String, String> updatedInfo) {
        // newEmail이 null이 아니고 비어있지 않은 경우에만 이메일 변경 로직 실행
        if (requestDto.newEmail() != null && !requestDto.newEmail().isEmpty()) {
            if (user.getEmail().equals(requestDto.newEmail())) {
                throw new BusinessException(ErrorMessage.NEW_EMAIL_SAME_AS_CURRENT);
            }
            if (userRepository.existsByEmail(requestDto.newEmail())) {
                throw new BusinessException(ErrorMessage.DUPLICATED_EMAIL);
            }

            // 이메일이 EmailAuthController를 통해 미리 인증되었는지 확인
            if (!emailVerificationRepository.isVerified(requestDto.newEmail())) {
                throw new BusinessException(ErrorMessage.EMAIL_NOT_VERIFIED_FOR_CHANGE);
            }

            user.setEmail(requestDto.newEmail());
            updatedInfo.put("userEmail", user.getEmail());
            // 이메일 변경 후 Redis에 저장된 인증 기록 삭제
            emailVerificationRepository.deleteVerification(requestDto.newEmail());
        }
    }

    private void updateNickname(Users user, UserUpdateRequestDto requestDto, Map<String, String> updatedInfo) {
        // newNickname이 null이 아니고 비어있지 않은 경우에만 닉네임 변경 로직 실행
        if (requestDto.newNickname() != null && !requestDto.newNickname().isEmpty()) {
            if (user.getUserNickname().equals(requestDto.newNickname())) {
                throw new BusinessException(ErrorMessage.NEW_NICKNAME_SAME_AS_CURRENT);
            }
            user.setUserNickname(requestDto.newNickname());
            updatedInfo.put("userNickname", user.getUserNickname());
        }
    }
}