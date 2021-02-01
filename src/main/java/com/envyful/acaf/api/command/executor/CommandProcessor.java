package com.envyful.acaf.api.command.executor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CommandProcessor {

    String value();

    int minArgs() default 0;

    boolean executeAsync() default true;

}
