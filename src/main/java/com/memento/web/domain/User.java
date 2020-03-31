package com.memento.web.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.persistence.Id;
import java.util.ArrayList;
import java.util.List;

@ToString
@Getter
@Document(collection = "user")
public class User {
    @Id
    private String id;
    private String email;
    private String password;
    private String name;

    private List<History> historyList = new ArrayList<>();

    @Builder
    public User(String id, String email, String password, String name) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;
    }

}