package org.alljeju.alljejuserver.config.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.FileNotFoundException;

/**
 * 로깅을 위한 Exception 처리 핸들러 _예외 처리 지속 추가
 * 컨트롤러에서 발생하는 Exception 에 대해 처리한다.
 */
@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(getClass());

//    @ExceptionHandler(value={IllegalStateException.class, IllegalArgumentException.class})
//    protected ResponseEntity<Object> handleIllegalException(RuntimeException ex, WebRequest request){
//        logger.error(ex.getMessage());
//        return buildResponseEntity(new HttpError(HttpStatus.NOT_FOUND,"비정상적인 접근입니다.", ex));
//    }
//
//    @ExceptionHandler(value={NullPointerException.class})
//    protected ResponseEntity<Object> handleNullPointException(NullPointerException ex, WebRequest request){
//        logger.error(ex.getMessage());
//        return buildResponseEntity(new HttpError(HttpStatus.NOT_FOUND,"처리할 데이터가 없습니다.", ex));
//    }
//
//    @ExceptionHandler(value={FileNotFoundException.class, ResourceAccessException.class})
//    protected ResponseEntity<Object> handleNotFoundException(RuntimeException ex, WebRequest request){
//        logger.error(ex.getMessage());
//        return buildResponseEntity(new HttpError(HttpStatus.NOT_FOUND,"파일을 찾을 수 없습니다.", ex));
//    }
//
//    @ExceptionHandler(value={AccessDeniedException.class})
//    protected ResponseEntity<Object> handleAccessDeniedException(RuntimeException ex, WebRequest request){
//        logger.error(ex.getMessage());
//        return buildResponseEntity(new HttpError(HttpStatus.NOT_FOUND,"접근 권한이 없습니다.", ex));
//    }
//
//    private ResponseEntity<Object> buildResponseEntity(HttpError httpError) {
//        return new ResponseEntity<>(httpError, httpError.getStatus());
//    }

}
