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

    public static <T> Response<T> success(String message, T data) {
        return getResponse(ResponseEnum.success.getCode(), message, data);
    }

    public static <T> Response<T> success(T data) {
        return getResponse(ResponseEnum.success.getCode(), ResponseEnum.success.getMessage(), data);
    }

    public static Response success() {
        return getResponse(ResponseEnum.success.getCode(), ResponseEnum.success.getMessage(), (Object) null);
    }

    public static Response error(String message) {
        return getResponse(ResponseEnum.error.getCode(), message, (Object) null);
    }

    public static Response error() {
        return getResponse(ResponseEnum.error.getCode(), ResponseEnum.error.getMessage(), (Object) null);
    }

    public static Response error(ResponseEnum responseEnum) {
        return getResponse(responseEnum.getCode(), responseEnum.getMessage(), (Object) null);
    }

}
