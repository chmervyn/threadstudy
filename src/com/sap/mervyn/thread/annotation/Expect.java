package com.sap.mervyn.thread.annotation;

import javax.xml.bind.Element;
import java.lang.annotation.*;

@Documented
@Target(ElementType.METHOD)
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface Expect {
    int expected();
    String desc();
}
