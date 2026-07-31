package com.bloodbank.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JwtService {

    @Value("${app.jwt.secret}")
    private String JwtSecret;

    @Value("${app.jwt.expiration.ms}")
    private long jwtExpirationMs;

    private SecretKey signingKey;

    @PostConstruct
    private void init() {
        signingKey = Keys.hmacShaKeyFor(
                JwtSecret.getBytes(StandardCharsets.UTF_8)
        );
    }

     public String generateToken(Authentication authentication) {

        UserDetails user =
                (UserDetails) authentication.getPrincipal();
          authentication.getAuthorities();

        List<String> roles = user.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return  buildToken(
                user.getUsername(),
                roles
        );
    }

    public String extractUsername(String token){

        try{

            return extractClaim(
                    token,
                    Claims::getSubject
            );

        }catch (JwtException e){
            return null;
        }
    }

    public boolean isTokenValid(
            String token,
            UserDetails userDetails
    ) {

       String username = extractUsername(token);

       return username != null
               && username.equals(userDetails.getUsername())
               && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token)
                .before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(
                token,
                Claims::getExpiration
        );
    }

    private <T> T extractClaim(
            String token,
            Function<Claims, T> resolver
    ) {
        Claims claims = Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return resolver.apply(claims);
    }

    private String buildToken(
            String username,
            List<String> roles
    ) {
        return Jwts.builder()
                .subject(username)
                .claim("roles", roles)
                .issuedAt(new Date())
                .expiration(
                        new Date(
                                System.currentTimeMillis()
                                       + jwtExpirationMs
                        )
                )
                .signWith(
                        signingKey,
                        Jwts.SIG.HS256
                )
                .compact();
    }
}
