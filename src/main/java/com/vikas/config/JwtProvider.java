package com.vikas.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class JwtProvider {

    private final JwtServiceImpl jwtService;
    private final SecretKey fallbackKey = Keys.hmacShaKeyFor(JWT_CONSTANT.SECRET_KEY.getBytes());

    public String generateToken(Authentication auth) {
        Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
        String roles = populateAuthorities(authorities);

        return Jwts.builder()
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + 86400000))
                .setSubject(auth.getName())
                .claim("email", auth.getName())
                .claim("authorities", roles)
                .signWith(jwtService.getSigningKey())
                .compact();
    }

    public String getEmailFromToken(String jwt) {
        if (jwt != null && jwt.startsWith("Bearer ")) {
            jwt = jwt.substring(7);
        }

        try {
            Claims claims = jwtService.extractAllClaims(jwt);
            Object email = claims.get("email");
            if (email != null) {
                return String.valueOf(email);
            }
            return claims.getSubject();
        } catch (Exception e) {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(fallbackKey)
                    .build()
                    .parseClaimsJws(jwt)
                    .getBody();
            Object email = claims.get("email");
            return email != null ? String.valueOf(email) : claims.getSubject();
        }
    }

    private String populateAuthorities(Collection<? extends GrantedAuthority> authorities) {
        Set<String> auths = new HashSet<>();
        for (GrantedAuthority authority : authorities) {
            auths.add(authority.getAuthority());
        }
        return String.join(",", auths);
    }
}