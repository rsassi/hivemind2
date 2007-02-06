package org.apache.hivemind.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a method in an annotated module as configuration point.
 * The return type of the method defines the configuration type. 
 * The method is used as factory method for the construction of the configuration.
 * It can return null if no initial contribution is to be provided. 
 *  
 * @author Achim Huegen
 */
@Documented
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = ElementType.METHOD)
public @interface Configuration {
    String id();
}
