package com.vikas.response;

import com.vikas.domain.USER_ROLE;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {

    private String accessToken;
    private String refreshToken;
    @Builder.Default
    private String tokenType = "Bearer";
    private USER_ROLE role;
    private String message;
    private String email;

    public String getJwt() {
        return accessToken;
    }

    public void setJwt(String jwt) {
        this.accessToken = jwt;
    }
}
