package hu.icellmobilsoft.coffee.module.mp.restclient.exception;

import hu.icellmobilsoft.coffee.dto.common.commonservice.BaseExceptionResultType;
import hu.icellmobilsoft.coffee.dto.exception.BaseException;
import hu.icellmobilsoft.coffee.dto.exception.enums.Severity;

/**
 * Exception for mp rest client response exception mapping.
 *
 * @author adam.magyari
 * @since 1.2.0
 */
public class ResponseException extends BaseException {

    private String service;
    private String className;
    private String exception;

    public ResponseException(String message) {
        super(message);
    }

    public ResponseException(String message, Throwable e) {
        super(message, e);
    }

    public ResponseException(Enum<?> faultTypeEnum, String message) {
        super(faultTypeEnum, message);
    }

    public ResponseException(Enum<?> faultTypeEnum, String message, Throwable e) {
        super(faultTypeEnum, message, e);
    }

    public ResponseException(Enum<?> faultTypeEnum, String message, Throwable e, Severity severity) {
        super(faultTypeEnum, message, e, severity);
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }

    public static ResponseException fromExceptionResult(BaseExceptionResultType baseExceptionResultType) {
        ResponseException exception = new ResponseException(FaultTypeParser.parseFaultType(baseExceptionResultType.getFaultType()), baseExceptionResultType.getMessage());
        exception.setService(baseExceptionResultType.getService());
        exception.setClassName(baseExceptionResultType.getClassName());
        exception.setException(baseExceptionResultType.getException());
        if (baseExceptionResultType.isSetCausedBy()) {
            exception.initCause(fromExceptionResult(baseExceptionResultType.getCausedBy()));
        }
        return exception;
    }
}
