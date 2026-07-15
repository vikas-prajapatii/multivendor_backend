package com.vikas.service
;

import com.vikas.domain.USER_ROLE;
import com.vikas.request.LoginRequest;
import com.vikas.response.AuthResponse;
import com.vikas.response.SignupRequest;

public interface AuthService  {
    void sentLoginOtp(String email, USER_ROLE role) throws Exception;
    String createUser(SignupRequest req) throws Exception;
    AuthResponse signing(LoginRequest req);
}
