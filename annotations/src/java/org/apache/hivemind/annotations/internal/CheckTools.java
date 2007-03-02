package org.apache.hivemind.annotations.internal;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.apache.hivemind.ApplicationRuntimeException;
import org.apache.hivemind.annotations.AnnotationsMessages;

public class CheckTools
{
    /**
     * Ensures that an annotated method has only allowed modifiers.
     * By default Modifier.PUBLIC and Modifier.PROTECTED are allowed.
     * @param method  the method
     * @param allowedModifiers  allowed {@link Modifier modifiers}. 
     * @param methodType  used in error messages to describe what the method is used for
     */
    public static void checkMethodModifiers(Method method, int allowedModifiers, String methodType)
    {
        // These modifiers are allowed
        final int validModifiers = Modifier.PUBLIC | Modifier.PROTECTED | allowedModifiers;
        
        int invalidModifiers = method.getModifiers() & ~validModifiers;
        if (invalidModifiers > 0) {
            throw new ApplicationRuntimeException(AnnotationsMessages.annotatedMethodHasInvalidModifiers(method, methodType, invalidModifiers));
        }

        // TODO: Check for package access
        
        // Check for setAccessible-Errors when Modifier.PROTECTED is used
        if (Modifier.isProtected(method.getModifiers())) {
            // Try to set method accessible
            try
            {
                method.setAccessible(true);
            }
            catch (SecurityException e)
            {
                throw new ApplicationRuntimeException(AnnotationsMessages.annotatedMethodIsProtectedAndNotAccessible(method, methodType));
            }
        }
    }
}
