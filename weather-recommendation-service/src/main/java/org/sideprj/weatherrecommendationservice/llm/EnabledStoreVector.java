package org.sideprj.weatherrecommendationservice.llm;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface EnabledStoreVector {

    @AliasFor("value")
    String[] metadata() default {};

    @AliasFor("metadata")
    String[] value() default {};
}
