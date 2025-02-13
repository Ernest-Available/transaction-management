package org.ernest.transactionmanagement.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@AllArgsConstructor
@Data
public class BusenessException extends RuntimeException {
    private Integer code;


    public BusenessException(String message) {
        super(message);
    }

    public BusenessException(Integer code, String message) {
        super(message);
        this.code = code;
    }
}
