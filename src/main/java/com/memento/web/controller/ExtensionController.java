package com.memento.web.controller;

import com.memento.web.service.ExtensionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;

@Controller
public class ExtensionController {
    private final ExtensionService extensionService;
    private final HttpSession httpSession;

    public ExtensionController(ExtensionService extensionService, HttpSession httpSession) {
        this.extensionService = extensionService;
        this.httpSession = httpSession;
    }

    @PostMapping("/save-user")
    public void saveUser(@RequestParam("name") String name) {
        Integer hashCode = name.hashCode();
        if (httpSession.getAttribute(hashCode.toString()) == null) {
            httpSession.setAttribute(hashCode.toString() , name);
            extensionService.saveUser(hashCode,name);
        }
    }

    @PostMapping("/close-window")
    public void closeWindow(@RequestParam("name") String name) {
        extensionService.saveToMainDB(name);
    }

    @PostMapping("/stop-worker")
    public void stopWorker(@RequestParam("name") String name) {
        extensionService.saveToMainDB(name);
    }
}
