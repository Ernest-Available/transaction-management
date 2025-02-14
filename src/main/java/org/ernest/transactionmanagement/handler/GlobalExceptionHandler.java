package org.ernest.transactionmanagement.handler;


import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.ernest.transactionmanagement.exception.BusenessException;
import org.ernest.transactionmanagement.response.Response;
import org.ernest.transactionmanagement.response.ResponseFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(BusenessException.class)
    @ResponseBody
    public ResponseEntity<Response> handleRrException(BusenessException e) {
        log.error("Handling BusenessException Error Message: {}", e.getMessage(), e);
        return new ResponseEntity<>(ResponseFactory.error(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Response> handleAll(Exception ex, WebRequest request) {
        log.error("Handling Exception Error Message: {}", ex.getMessage(), ex);
        if (StringUtils.isNotEmpty(ex.getMessage())) {
            return new ResponseEntity<>(ResponseFactory.error(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(ResponseFactory.error(ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}