package com.sap.mervyn.thread.annotation;

import java.lang.annotation.*;

@Documented
@Target(ElementType.TYPE)
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface ConcurrencyTest {
    int iterations() default 20000;
    int thinkTime() default 0;
}
