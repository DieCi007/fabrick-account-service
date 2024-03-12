package it.fabrick.account.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@ControllerAdvice
public class GlobalErrorHandler {
    public static final String UNKNOWN_ERROR = "UNDISCLOSED";
    public static final String TS_KEY = "timeStamp";
    public static final String STATUS_KEY = "status";
    public static final String ERROR_KEY = "error";
    public static final String EXCEPTION_KEY = "exception";
    public static final String MESSAGE_KEY = "message";
    public static final String EXCEPTION_CODE_KEY = "exceptionCode";
    public static final String PATH_KEY = "path";

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> otherExceptions(Exception ex, WebRequest request) {
        ResponseStatus annotation = AnnotationUtils.findAnnotation(ex.getClass(), ResponseStatus.class);
        if (null != annotation) {
            return fromException(ex, request, annotation.code());
        }
        return unhandledExceptions(ex, request);
    }

    @ExceptionHandler(value = {
            MultipartException.class,
            HttpMessageNotReadableException.class,
            MissingRequestHeaderException.class,
            MissingServletRequestParameterException.class,
            MissingServletRequestPartException.class,
            MethodArgumentTypeMismatchException.class,
            MethodArgumentNotValidException.class,
            ConstraintViolationException.class})
    @ResponseBody
    public ResponseEntity<Map<String, Object>> badRequestException(Exception ex, WebRequest request) {
        return fromException(ex, request, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {
            HttpStatusCodeException.class
    })
    @ResponseBody
    public ResponseEntity<Map<String, Object>> clientErrorException(HttpStatusCodeException ex, WebRequest request) {
        log.error(ex.getMessage() + ": " + ex.getResponseBodyAsString());
        return fromException(ex, request, HttpStatus.valueOf(ex.getStatusCode().value()));
    }

    @ExceptionHandler(value = HttpRequestMethodNotSupportedException.class)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> methodNotSupported(Exception ex, WebRequest request) {
        return fromException(ex, request, HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(value = ThirdPartyException.class)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> thirdPartyException(ThirdPartyException ex, WebRequest request) {
        // we could add exception source (ex: Fabrick) to the response
        return fromException(ex, request, HttpStatus.valueOf(ex.getHttpStatus().value()));
    }

    private ResponseEntity<Map<String, Object>> fromException(Exception ex, WebRequest request, HttpStatus httpStatus) {
        Map<String, Object> result = fromException(httpStatus, ex, request);
        return new ResponseEntity<>(result, httpStatus);
    }

    private static Map<String, Object> fromException(HttpStatus httpStatus, Exception ex, WebRequest request) {
        Map<String, Object> result = new HashMap<>();
        result.put(TS_KEY, System.currentTimeMillis());
        result.put(STATUS_KEY, httpStatus.value());
        result.put(ERROR_KEY, httpStatus.getReasonPhrase());
        result.put(EXCEPTION_KEY, ex.getClass().getSimpleName());
        result.put(MESSAGE_KEY, ex.getMessage());
        result.put(PATH_KEY, getPath(request));

        if (ex instanceof RequestException rex) {
            // add exceptionCode and attributes
            result.put(EXCEPTION_CODE_KEY, rex.getExceptionCode());
        }

        log.info(String.format("%s [%s]: %s", result.get(EXCEPTION_KEY), result.get(EXCEPTION_CODE_KEY), result.get(MESSAGE_KEY)));
        return result;
    }

    private static Map<String, Object> maskingException(HttpStatus httpStatus, String message, WebRequest request) {
        Map<String, Object> result = new HashMap<>();
        result.put(TS_KEY, System.currentTimeMillis());
        result.put(STATUS_KEY, httpStatus.value());
        result.put(ERROR_KEY, httpStatus.getReasonPhrase());
        result.put(EXCEPTION_KEY, "Undisclosed");
        result.put(MESSAGE_KEY, message);
        result.put(PATH_KEY, getPath(request));
        return result;
    }

    private static String getPath(WebRequest request) {
        String path = request.getDescription(false);
        if (!path.isEmpty()) {
            path = path.replaceFirst("uri=", "");
        }
        return path;
    }

    private ResponseEntity<Map<String, Object>> unhandledExceptions(Exception ex, WebRequest request) {
        log.error(ex.getMessage(), ex);
        Map<String, Object> result = maskingException(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "We cannot handle your request now. Please try again later",
                request);
        return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
