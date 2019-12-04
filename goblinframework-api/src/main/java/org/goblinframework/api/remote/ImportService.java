package org.goblinframework.api.remote;

import java.lang.annotation.*;

@Documented
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ImportService {

  Class<?> interfaceClass();

  ServiceVersion version() default @ServiceVersion(enable = false);

  boolean enable() default true;

}
