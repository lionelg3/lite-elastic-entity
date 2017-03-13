package com.gmail.lionelg3.elastic.io.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;

/**
 * Created by lionel on 13/03/2017.
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value = { FIELD, METHOD })
public @interface Id {
}
