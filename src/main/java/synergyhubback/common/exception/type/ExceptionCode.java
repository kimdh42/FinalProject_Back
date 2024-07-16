package synergyhubback.common.exception.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ExceptionCode {
    FAIL_LOGIN(4000, "로그인에 실패하였습니다."),
    UNAUTHORIZED(4002, "인증 되지 않은 요청입니다."),
    ACCESS_DENIED(4003, "허가 되지 않은 요청입니다."),
    NOT_FOUND_REFRESH_TOKEN(4001, "해당 리프레시 토큰이 유효하지 않습니다."),
    NOT_FOUND_PAR_DEPT_CODE(4010, "상위부서를 찾을 수 없습니다."),
    NOT_FOUND_SUB_DEPT_CODE(4011, "하위부서를 찾을 수 없습니다."),
    DEPT_RELATIONS_NOT_FOUND(4012, "부서 관계를 찾을 수 없습니다."),
    NOT_FOUND_EMPLOYEE(4013, "직원을 찾을 수 없습니다."),
    NOT_FOUND_INFORMATION(4014, "해당 정보를 찾을 수 없습니다.");


    private final int code;
    private final String message;
}
