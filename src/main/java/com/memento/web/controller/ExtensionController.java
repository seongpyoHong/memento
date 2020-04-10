package com.memento.web.controller;

import com.memento.web.config.jwtUtill.JwtUtill;
import com.memento.web.service.ExtensionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;

@Controller
public class ExtensionController {
    @Autowired
    private ExtensionService extensionService;

    @Autowired
    private HttpSession httpSession;

    @Autowired
    private JwtUtill jwtUtill;

    @PostMapping("/save-user")
    public void saveUser(@RequestHeader("email") String email,
                         HttpServletResponse httpServletResponse) throws UnsupportedEncodingException {
        Integer hashCode = email.hashCode();
        String token = jwtUtill.createToken(email);
        httpSession.setAttribute(email, token);

        extensionService.saveUser(hashCode, email);
        httpServletResponse.addHeader("token", token);
    }

    @PostMapping("/close-window")
    public void closeWindow(@RequestParam("name") String name) {
        System.out.println("Close Window");
        extensionService.saveToMainDB(name);
    }

    @PostMapping("/stop-worker")
    public void stopWorker(@RequestParam("name") String name) {
        System.out.println("Close Window");
        extensionService.saveToMainDB(name);
    }
}
