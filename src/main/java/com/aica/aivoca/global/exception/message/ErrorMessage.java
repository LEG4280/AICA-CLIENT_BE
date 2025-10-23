package com.aica.aivoca.global.exception.message;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorMessage {
    INVALID_EMAIL_CODE(HttpStatus.UNAUTHORIZED.value(), "인증 코드가 일치하지 않거나 만료되었습니다."),
    EMAIL_NOT_VERIFIED(HttpStatus.UNAUTHORIZED.value(),"이메일 인증이 되지 않았습니다."),
    ALREADY_EXISTS_USER(HttpStatus.UNAUTHORIZED.value(),"이미 가입된 유저입니다."),
    DUPLICATED_USER_ID(HttpStatus.UNAUTHORIZED.value(),"이미 사용 중인 아이디입니다."),
    DUPLICATED_EMAIL(HttpStatus.UNAUTHORIZED.value(),"이미 사용 중인 이메일입니다."),

    // 문장 관련
    INVALID_REQUEST(HttpStatus.BAD_REQUEST.value(), "잘못된 요청입니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "사용자를 찾을 수 없습니다."),
    SENTENCE_ALREADY_EXISTS(HttpStatus.CONFLICT.value(), "이미 등록된 문장입니다."),
    SENTENCE_ID_ALREADY_EXISTS(HttpStatus.CONFLICT.value(), "이미 사용 중인 문장 ID입니다."),
    USER_ID_REQUIRED(HttpStatus.BAD_REQUEST.value(), "사용자 ID가 필요합니다."),
    SENTENCE_ID_REQUIRED(HttpStatus.BAD_REQUEST.value(), "문장 ID가 필요합니다."),
    SENTENCE_TEXT_REQUIRED(HttpStatus.BAD_REQUEST.value(), "문장 내용이 필요합니다."),
    SENTENCE_NOT_FOUND_BY_USER(HttpStatus.NOT_FOUND.value(), "해당 사용자의 저장된 문장이 없습니다."),
    SENTENCE_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "해당 문장을 찾을 수 없습니다."),
    FORBIDDEN_ACCESS(HttpStatus.FORBIDDEN.value(), "해당 문장을 삭제할 권한이 없습니다."),


    // 서버 오류
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(), "서버에 알 수 없는 오류가 발생했습니다."),

    //로그인 관련
    USER_ID_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "사용자 아이디를 찾을 수 없습니다."),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED.value(),"유효하지 않은 비밀번호입니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED.value(),"유효하지 않은 리프레시토큰입니다."),
    REFRESH_TOKEN_NOT_MATCH(HttpStatus.UNAUTHORIZED.value(),"리프레시토큰이 일치하지 않습니다."),

    // 단어 관련
    WORD_ID_REQUIRED(HttpStatus.BAD_REQUEST.value(), "단어 ID가 필요합니다."),
    WORD_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "해당 단어를 찾을 수 없습니다."),
    WORD_ALREADY_IN_VOCABULARY(HttpStatus.CONFLICT.value(), "이미 단어장에 존재하는 단어입니다."),
    VOCABULARY_LIST_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "해당 사용자의 단어장이 존재하지 않습니다."),
    WORD_NOT_FOUND_IN_VOCABULARY(HttpStatus.NOT_FOUND.value(), "단어장에서 해당 단어를 찾을 수 없습니다."),

    EMAIL_SEND_ERROR(HttpStatus.NOT_FOUND.value(), "이메일 전송 중 오류가 발생했습니다."),

    // 단어 의미 조회 실패 관련
    WORD_LOOKUP_FAILED(HttpStatus.INTERNAL_SERVER_ERROR.value(), "단어 의미를 불러오는 중 문제가 발생했습니다."),
    WORD_TEXT_REQUIRED(HttpStatus.BAD_REQUEST.value(), "단어 입력이 필요합니다."),
    INVALID_AI_RESPONSE(HttpStatus.BAD_REQUEST.value(), "AI가 반환한 데이터에 누락된 정보가 있습니다."),
    OPENAI_REQUEST_FAILED(HttpStatus.BAD_GATEWAY.value(), "OpenAI에게 단어 정보를 요청하는 데 실패했습니다."),
    INVALID_WORD_INPUT(HttpStatus.BAD_REQUEST.value(), "유효한 영어 단어를 입력해 주세요."),
    WORD_NOT_FOUND_IN_DICTIONARY(HttpStatus.BAD_REQUEST.value(), "입력한 단어는 사전에 존재하지 않습니다."),
    OPENAI_RESPONSE_PARSE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR.value(), "OpenAI 응답을 처리하는 데 실패했습니다."),
    OPENAI_RESPONSE_EMPTY(HttpStatus.BAD_REQUEST.value(), "OpenAI가 단어 정보를 포함하지 않은 응답을 보냈습니다."),

    // 회원 정보 수정 관련
    CURRENT_PASSWORD_MISMATCH(HttpStatus.FORBIDDEN.value(), "현재 비밀번호가 일치하지 않습니다."),
    NEW_PASSWORD_CONFIRMATION_MISMATCH(HttpStatus.FORBIDDEN.value(), "새로운 비밀번호 확인이 일치하지 않습니다."),
    NO_UPDATE_DATA_PROVIDED(HttpStatus.BAD_REQUEST.value(), "수정할 정보를 입력해주세요."), // 수정할 정보가 없는 경우
    PASSWORD_EMAIL_SIMULTANEOUS_CHANGE_NOT_ALLOWED(HttpStatus.BAD_REQUEST.value(), "비밀번호와 이메일은 동시에 변경할 수 없습니다."), // 동시 변경 불가
    PASSWORD_CHANGE_REQUIRED_FIELDS_MISSING(HttpStatus.BAD_REQUEST.value(), "비밀번호 변경 시 현재 비밀번호, 새 비밀번호, 새 비밀번호 확인을 모두 입력해주세요."), // 비밀번호 필수 필드 누락
    NEW_PASSWORD_SAME_AS_CURRENT(HttpStatus.BAD_REQUEST.value(), "새 비밀번호는 현재 비밀번호와 달라야 합니다."), // 새 비밀번호가 현재와 동일
    NEW_EMAIL_REQUIRED(HttpStatus.BAD_REQUEST.value(), "새로운 이메일을 입력해주세요."), // 새 이메일 누락
    PASSWORD_MIN_LENGTH_VIOLATION(HttpStatus.BAD_REQUEST.value(), "비밀번호는 최소 8자 이상이어야 합니다."), // DTO @Size와 연동 가능
    NEW_NICKNAME_SAME_AS_CURRENT(HttpStatus.BAD_REQUEST.value(), "새 닉네임은 현재 닉네임과 동일할 수 없습니다."),
    EMAIL_NOT_VERIFIED_FOR_CHANGE(HttpStatus.FORBIDDEN.value(), "새 이메일 인증이 완료되지 않았습니다. 이메일 인증을 먼저 완료해주세요."),
    NEW_EMAIL_SAME_AS_CURRENT(HttpStatus.BAD_REQUEST.value(), "새 이메일은 현재 이메일과 동일할 수 없습니다."),
    PASSWORD_VERIFICATION_REQUIRED(HttpStatus.FORBIDDEN.value(), "비밀번호 확인이 필요합니다. 먼저 비밀번호를 확인해주세요."),


    ;



    private final int code;
    private final String message;
}
