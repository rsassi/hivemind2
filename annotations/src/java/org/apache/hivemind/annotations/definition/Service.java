package org.apache.hivemind.annotations.definition;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.apache.hivemind.internal.ServiceModel;

/**
 * Marks a method in an annotated module as service point. 
 * The return type of the method defines the service interface. 
 * The method is used factory method for the construction of service implementations.
 * 
 * @author Achim Huegen
 */
@Documented
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = ElementType.METHOD)
public @interface Service {
    String id();
    String serviceModel() default ServiceModel.SINGLETON;
}
