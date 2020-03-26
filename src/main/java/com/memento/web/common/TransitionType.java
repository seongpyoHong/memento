package com.memento.web.common;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TransitionType {
    LINK("link",true), GENERATED("generated",true ),
    FORM_SUBMIT("form_submit",true), RELOAD("reload", true);

    private final String name;
    private final boolean isApplicable;
}
