package org.apache.hivemind.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Defines a submodule of an annotated module. The return type of the annotated method
 * defines the class of the submodule. This class must be an annotated module class itself.
 *  
 * @author Achim Huegen
 */
@Documented
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = ElementType.METHOD)
public @interface Submodule {
    String id();
}
