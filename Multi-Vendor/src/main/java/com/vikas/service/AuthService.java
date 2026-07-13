package com.vikas.service
;

import com.vikas.request.LoginRequest;
import com.vikas.response.AuthResponse;
import com.vikas.response.SignupRequest;

public interface AuthService  {
    void sentLoginOtp(String email) throws Exception;
    String createUser(SignupRequest req) throws Exception;
    AuthResponse signing(LoginRequest req);
}
