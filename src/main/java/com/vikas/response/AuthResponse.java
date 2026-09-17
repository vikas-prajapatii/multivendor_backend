package com.vikas.response;

import com.vikas.domain.USER_ROLE;
import lombok.Data;

@Data
public class AuthResponse {
    private String jwt;
    private String jwtToken;
    private String message;
    private USER_ROLE role;
    private boolean status;

    public void setJwt(String jwt) {
        this.jwt = jwt;
        this.jwtToken = jwt;
    }

    public void setJwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
        this.jwt = jwtToken;
    }

    public String getJwt() {
        return jwt != null ? jwt : jwtToken;
    }

    public String getJwtToken() {
        return jwtToken != null ? jwtToken : jwt;
    }
}
