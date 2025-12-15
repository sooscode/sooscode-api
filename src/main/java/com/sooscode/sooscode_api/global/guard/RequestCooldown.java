package com.sooscode.sooscode_api.global.guard;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestCooldown {

    /**
     * 요청 쿨타임 (초)
     * 기본값: 3초
     */
    long seconds() default 3;
}
