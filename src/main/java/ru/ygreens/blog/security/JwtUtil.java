package ru.ygreens.blog.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import ru.ygreens.blog.service.UserDetailsImpl;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

public class JwtUtil {
    private static final Logger log = LoggerFactory.getLogger(JwtUtil.class);

    @Value("${app.jwt.secret}")
    private String secret;

    @Value("${app.jwt.expirationMs}")
    private int expirationMs;

    public String generateJwtToken(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        Key key = new SecretKeySpec(Base64.getDecoder().decode(secret),
                                    SignatureAlgorithm.HS512.getJcaName());

        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + expirationMs))
                .signWith(key)
                .compact();
    }

    public boolean validateJwtToken(String token) {
        Key key = new SecretKeySpec(Base64.getDecoder().decode(secret),
                SignatureAlgorithm.HS512.getJcaName());

        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);

            return true;
        }
        catch (MalformedJwtException | IllegalArgumentException e) {
            log.error(e.getMessage());
        }

        return false;
    }

    public String getUserNameFromJwtToken(String token) {
        Key key = new SecretKeySpec(Base64.getDecoder().decode(secret),
                SignatureAlgorithm.HS512.getJcaName());

        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}
