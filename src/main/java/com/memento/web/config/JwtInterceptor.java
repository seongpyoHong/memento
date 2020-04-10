package com.memento.web.config;

import com.memento.web.config.jwtUtill.JwtUtill;
import com.memento.web.domain.User;
import com.memento.web.domain.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Component
public class JwtInterceptor implements HandlerInterceptor {
    @Autowired
    JwtUtill jwtUtill;
    @Autowired
    private HttpSession httpSession;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String HEADER_USER_EMAIL = "email";
        String HEADER_TOKEN_KEY = "token";
        if (httpSession.getAttribute(request.getHeader(HEADER_USER_EMAIL)) != null){
            jwtUtill.verifyToken(request.getHeader(HEADER_TOKEN_KEY), request.getHeader(HEADER_USER_EMAIL));
            return true;
        }
        return false;
    }
}
