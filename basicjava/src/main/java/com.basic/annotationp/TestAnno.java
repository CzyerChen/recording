package com.basic.annotationp;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TestAnno {
    public int num() default 1;
    public String name() default "";
}
