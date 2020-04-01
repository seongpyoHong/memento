package com.memento.web.service;

import com.memento.web.domain.User;
import com.memento.web.domain.UserRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;

@Service
public class UserService {
    @Autowired
    private HttpSession session;
    @Autowired
    private UserRepository userRepository;

    public void saveUser(Integer hashCode,String name) {
        if (!userRepository.findByName(name).isPresent()) {
            userRepository.save(User.builder().id(getObjectId()).name(name).build());
        }
    }
    private String getObjectId() {
        return ObjectId.get().toString();
    }

}
