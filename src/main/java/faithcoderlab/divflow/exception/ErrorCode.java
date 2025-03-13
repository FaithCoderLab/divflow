package faithcoderlab.divflow.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    COMPANY_NOT_FOUND(HttpStatus.BAD_REQUEST, "존재하지 않는 회사명입니다.");

    private final HttpStatus status;
    private final String message;

}
