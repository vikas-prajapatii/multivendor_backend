package com.vikas.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private String message;
    private boolean status;
    private boolean success;
    private T data;

    public ApiResponse(String message, boolean status) {
        this.message = message;
        this.status = status;
        this.success = status;
    }

    public boolean isSuccess() {
        return success || status;
    }

    public void setSuccess(boolean success) {
        this.success = success;
        this.status = success;
    }

    public void setStatus(boolean status) {
        this.status = status;
        this.success = status;
    }
}