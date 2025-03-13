package faithcoderlab.divflow.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    COMPANY_NOT_FOUND(HttpStatus.BAD_REQUEST, "존재하지 않는 회사명입니다."),
    TICKER_NOT_FOUND(HttpStatus.BAD_REQUEST, "존재하지 않는 회사 ticker 입니다."),
    ALREADY_EXISTS_TICKER(HttpStatus.BAD_REQUEST, "이미 존재하는 회사 ticker 입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다.");

    private final HttpStatus status;
    private final String message;

}
