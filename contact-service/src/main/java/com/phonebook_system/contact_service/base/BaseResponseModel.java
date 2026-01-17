package com.phonebook_system.contact_service.base;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BaseResponseModel<T> {

    private boolean success;
    private String message;
    private T data;
    private String errorCode;

    // Private constructor (factory pattern için)
    private BaseResponseModel() {
    }

    // ---------- Factory Methods ----------

    /** Data dönmeyen başarılı response */
    public static <T> BaseResponseModel<T> ok() {
        BaseResponseModel<T> response = new BaseResponseModel<>();
        response.success = true;
        response.message = "OK";
        return response;
    }

    /** Başarılı response */
    public static <T> BaseResponseModel<T> success(T data) {
        BaseResponseModel<T> response = new BaseResponseModel<>();
        response.success = true;
        response.data = data;
        response.message = "Success";
        return response;
    }

    /** Başarılı ama mesajlı response */
    public static <T> BaseResponseModel<T> success(T data, String message) {
        BaseResponseModel<T> response = new BaseResponseModel<>();
        response.success = true;
        response.data = data;
        response.message = message;
        return response;
    }

    /** Hatalı response */
    public static <T> BaseResponseModel<T> fault(String message) {
        BaseResponseModel<T> response = new BaseResponseModel<>();
        response.success = false;
        response.message = message;
        return response;
    }

    /** Hatalı response (error code ile) */
    public static <T> BaseResponseModel<T> fault(String message, String errorCode) {
        BaseResponseModel<T> response = new BaseResponseModel<>();
        response.success = false;
        response.message = message;
        response.errorCode = errorCode;
        return response;
    }

    /** Result'u response'a çevirir (null kontrolü yapar) */
    public static <T> BaseResponseModel<T> resultToResponse(T result) {
        if (result == null) {
            return fault("Result not found");
        }
        return success(result);
    }
}
