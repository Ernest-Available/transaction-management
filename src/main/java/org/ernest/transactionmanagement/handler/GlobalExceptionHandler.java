package org.ernest.transactionmanagement.handler;


import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.ernest.transactionmanagement.exception.BusenessException;
import org.ernest.transactionmanagement.response.Response;
import org.ernest.transactionmanagement.response.ResponseFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(BusenessException.class)
    @ResponseBody
    public ResponseEntity<Response> handleRrException(BusenessException e) {
        log.error("Handling BusenessException Error Message: {}", e.getMessage(), e);
        return new ResponseEntity<>(ResponseFactory.error(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Response> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        // 将 errors 拼接成指定格式的字符串
        StringBuilder errorMessageBuilder = new StringBuilder();
        errors.forEach((fieldName, errorMessage) -> {
            errorMessageBuilder.append(fieldName)
                    .append(": ")
                    .append(errorMessage)
                    .append(", ");
        });

        // 去掉最后一个多余的逗号和空格
        if (errorMessageBuilder.length() > 0) {
            errorMessageBuilder.setLength(errorMessageBuilder.length() - 2);
        }
        return new ResponseEntity<>(ResponseFactory.error(errorMessageBuilder.toString()), HttpStatus.BAD_REQUEST);
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