package synergyhubback.common.exception;

import org.antlr.v4.runtime.misc.NotNull;
import synergyhubback.common.exception.type.ExceptionCode;

public class NotFoundException extends CustomException{

    public NotFoundException(final ExceptionCode exceptionCode) {
        super(exceptionCode);
    }
}
