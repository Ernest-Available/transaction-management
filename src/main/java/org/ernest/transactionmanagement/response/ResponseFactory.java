package org.ernest.transactionmanagement.response;


import lombok.NoArgsConstructor;



@NoArgsConstructor
public class ResponseFactory {

    public static <T> Response<T> getResponse(int code, String message, T data) {
        Response response = new Response();
        response.setCode(code);
        response.setMessage(message);
        response.setData(data);
        return response;
    }

    public static <T> Response<T> getResponse(ResponseEnum responseEnum, T data) {
        return getResponse(responseEnum.getCode(), responseEnum.getMessage(), data);
    }

    public static Response getResponse(ResponseEnum responseEnum) {
        return getResponse(responseEnum.getCode(), responseEnum.getMessage(), (Object) null);
    }

    public static <T> Response<T> getSuccess(String message, T data) {
        return getResponse(ResponseEnum.success.getCode(), message, data);
    }

    public static <T> Response<T> getSuccessData(T data) {
        return getResponse(ResponseEnum.success.getCode(), ResponseEnum.success.getMessage(), data);
    }


    public static Response getSuccessMessage(String message) {
        return getResponse(ResponseEnum.success.getCode(), message, (Object) null);
    }

    public static Response getSuccess() {
        return getResponse(ResponseEnum.success.getCode(), ResponseEnum.success.getMessage(), (Object) null);
    }

    public static Response getError(String message) {
        return getResponse(ResponseEnum.error.getCode(), message, (Object) null);
    }

    public static Response getError() {
        return getResponse(ResponseEnum.error.getCode(), ResponseEnum.error.getMessage(), (Object) null);
    }

    public static Response getError(ResponseEnum responseEnum) {
        return getResponse(responseEnum.getCode(), responseEnum.getMessage(), (Object) null);
    }

}
