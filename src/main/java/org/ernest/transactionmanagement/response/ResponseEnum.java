package org.ernest.transactionmanagement.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;


public enum ResponseEnum {
    // Operation successful
    success(200, "Operation successful"),
    // Closure
    closure(301, "Banned"),
    // Error
    error(400, "Bad request"),
    // Please login
    please_login(401, "Unauthorized"),
    // Forbidden
    forbidden(403, "Forbidden"),
    // Object not exist
    obj_not_exist(404, "Object not found"),
    // Failure
    failure(500, "Internal server error");

    private int code;
    private String message;

    ResponseEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
