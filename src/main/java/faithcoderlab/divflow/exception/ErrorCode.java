package faithcoderlab.divflow.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    COMPANY_NOT_FOUND(HttpStatus.BAD_REQUEST, "존재하지 않는 회사명입니다."),
    TICKER_NOT_FOUND(HttpStatus.BAD_REQUEST, "존재하지 않는 회사 ticker 입니다."),
    USERNAME_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "이미 존재하는 아이디입니다."),
    ALREADY_EXISTS_TICKER(HttpStatus.BAD_REQUEST, "이미 존재하는 회사 ticker 입니다."),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "아이디 또는 비밀번호가 올바르지 않습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다.");

    private final HttpStatus status;
    private final String message;

}
