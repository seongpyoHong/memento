package com.memento.web.controller;

import com.memento.web.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;

@RestController
public class LoginController {
    @Autowired
    private UserService userService;

    @Autowired
    private HttpSession httpSession;

    @PostMapping("/save-user")
    public void saveUser(@RequestParam("name") String name) {
        Integer hashCode = name.hashCode();
        if (httpSession.getAttribute(hashCode.toString()) == null) {
            httpSession.setAttribute(hashCode.toString() , name);
            userService.saveUser(hashCode,name);
            System.out.println("here");
        }
    }

    //TODO: Move to Search Controller
    @GetMapping("/view-log")
    public void viewLog(@RequestParam("name") String name) {
        System.out.println("View Request");
        userService.saveToMainDB(name);
    }
}
