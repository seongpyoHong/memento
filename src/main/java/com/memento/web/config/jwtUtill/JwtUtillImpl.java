package com.memento.web.config.jwtUtill;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.Date;

@Component
public class JwtUtillImpl implements JwtUtill {
    private final Date EXPIRED_TIME = new Date(System.currentTimeMillis() + 1000*60);
    private final String ISSUER = "LYH";

    @Override
    public String createToken(String email) throws UnsupportedEncodingException {
        return JWT.create()
                .withIssuer(ISSUER)
                .withExpiresAt(EXPIRED_TIME)
                .sign(Algorithm.HMAC256(email));
    }

    @Override
    public void verifyToken(String token, String email) throws UnsupportedEncodingException {
        JWTVerifier verifier = JWT.require(Algorithm.HMAC256(email))
                .withIssuer(ISSUER)
                .build();

        verifier.verify(token);
    }
}
