package com.memento.web.dto;

import com.memento.web.domain.History;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@AllArgsConstructor
public class UserResponseDto {
    private String id;
    private String name;
    private History historyList;
}
