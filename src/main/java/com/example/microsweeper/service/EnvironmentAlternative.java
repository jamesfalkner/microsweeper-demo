package com.example.microsweeper.service;

import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Stereotype;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;



@Stereotype
@Alternative
@Target(TYPE)
@Retention(RUNTIME)
public @interface EnvironmentAlternative  {
    EnvironmentType[] value();
}
