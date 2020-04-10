package com.memento.web.config.jwtUtill;

import java.io.UnsupportedEncodingException;

public interface JwtUtill {
    String createToken(String email) throws UnsupportedEncodingException;
    void verifyToken(String token, String email) throws UnsupportedEncodingException;
}
