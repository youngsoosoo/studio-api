package com.studio.api.common;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponseDto<T>(String status, T data, ErrorResponseDto error) {

    public static <T> ApiResponseDto<T> ok(T data) {
        return new ApiResponseDto<>("success", data, null);
    }

    public static <T> ApiResponseDto<T> error(String code, String message) {
        return new ApiResponseDto<>("error", null, new ErrorResponseDto(code, message));
    }
}
