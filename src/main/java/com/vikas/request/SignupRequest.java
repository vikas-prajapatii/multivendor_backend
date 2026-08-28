package com.vikas.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SignupRequest {
    private String fullName;
    private String email;
    private String otp;

    public String getFirstName() {
        if (fullName == null) {
            return "";
        }
        String[] parts = fullName.split(" ", 2);
        return parts[0];
    }

    public String getLastName() {
        if (fullName == null) {
            return "";
        }
        String[] parts = fullName.split(" ", 2);
        return parts.length > 1 ? parts[1] : "";
    }
}
