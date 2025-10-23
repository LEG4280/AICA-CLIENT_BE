package com.aica.aivoca.global.exception.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum SuccessMessage {

    EMAIL_REQUEST_SUCCESS(HttpStatus.CREATED.value(), "이메일 인증코드가 전송되었습니다."),
    EMAIL_VERIFY_SUCCESS(HttpStatus.OK.value(), "이메일 인증이 완료되었습니다."),
    REGISTER_SUCCESS(HttpStatus.CREATED.value(), "회원가입에 성공하였습니다."),

    // 문장 관련
    SENTENCE_ADD_SUCCESS(HttpStatus.CREATED.value(), "문장이 성공적으로 추가되었습니다."),
    SENTENCE_GET_SUCCESS(HttpStatus.OK.value(), "문장 목록이 성공적으로 조회되었습니다."),
    SENTENCE_DELETE_SUCCESS(HttpStatus.OK.value(), "문장이 성공적으로 삭제되었습니다."),

    // 단어장 관련
    WORD_ADDED_TO_VOCABULARY(HttpStatus.OK.value(), "단어가 단어장에 성공적으로 추가되었습니다."),
    GET_WORD_SUCCESS(HttpStatus.OK.value(), "단어장을 성공적으로 조회했습니다."),
    WORD_DELETED_FROM_VOCABULARY(HttpStatus.OK.value(), "단어가 단어장에서 성공적으로 삭제되었습니다."),
    WORD_FOUND_IN_DB(HttpStatus.OK.value(), "DB에서 단어가 성공적으로 조회되었습니다."),
    WORD_SAVED_FROM_AI(HttpStatus.CREATED.value(), "AI를 통해 단어 정보를 조회하고 저장했습니다."),
    WORD_ALREADY_IN_VOCABULARY(HttpStatus.CREATED.value(), "단어장에 단어가 이미 있어 AICA 단어장에 추가되었습니다."),

    //로그인
    LOGIN_SUCCESS(HttpStatus.OK.value(), "로그인에 성공하였습니다."),
    TOKEN_REISSUE_SUCCESS(HttpStatus.CREATED.value(), "토큰이 재발행 되었습니다."),

    //로그아웃
    USER_LOGOUT_SUCCESS(HttpStatus.OK.value(), "성공적으로 로그아웃되었습니다."),

    //회원 관련
    USER_DELETE_SUCCESS(HttpStatus.OK.value(), "회원 삭제에 성공하였습니다."),

    // 회원 정보 수정 관련
    MEMBER_UPDATE_SUCCESS(HttpStatus.OK.value(), "회원 정보가 성공적으로 업데이트되었습니다."),
    PASSWORD_VERIFICATION_SUCCESS(HttpStatus.OK.value(), "현재 비밀번호 인증이 성공했습니다."),

    ;


    private final int code;
    private final String message;
}
