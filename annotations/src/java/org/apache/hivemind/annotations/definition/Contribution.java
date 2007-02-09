package org.apache.hivemind.annotations.definition;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a method in an annotated module as contribution to a configuration point.
 * The method can have one of two different signatures:
 * 1. A void return type and a single parameter that must be compatible with the type of the configuration. 
 * 2. No parameter and a return type that must be compatible with the type of the configuration. 
 *  
 * @author Achim Huegen
 */
@Documented
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = ElementType.METHOD)
public @interface Contribution {
    String configurationId();
}
