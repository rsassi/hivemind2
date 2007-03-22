// Copyright 2004, 2005 The Apache Software Foundation
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.apache.hivemind.annotations;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.apache.hivemind.ClassResolver;
import org.apache.hivemind.impl.MessageFormatter;
import org.apache.hivemind.service.MethodSignature;


/**
 * Used to format messages used in errors and log output for classes within the impl package.
 * 
 * @author Achim Huegen
 */
public class AnnotationsMessages
{
    private static final MessageFormatter _formatter = new MessageFormatter(AnnotationsMessages.class,
            "AnnotationsStrings");

    static String unableToFindModuleClass(String moduleClassName, ClassResolver resolver)
    {
        return _formatter.format("unable-to-find-module-class", resolver, moduleClassName);
    }
    
    public static String moduleClassHasInvalidModifiers(Class moduleClass, int invalidModifiers)
    {
        String modifierStr = Modifier.toString(invalidModifiers);
        return _formatter.format("module-class-has-invalid-modifiers", moduleClass.getName(), modifierStr);
    }
    
    public static String moduleClassIsPackagePrivate(Class moduleClass)
    {
        return _formatter.format("module-class-is-package-private", moduleClass.getName());
    }

    public static String annotatedMethodHasInvalidModifiers(Method method, String methodType, int invalidModifiers)
    {
        MethodSignature methodSig = new MethodSignature(method);
        String modifierStr = Modifier.toString(invalidModifiers);
        return _formatter.format("annotated-method-has-invalid-modifiers", methodSig.toString(), methodType, modifierStr);
    }

    public static String annotatedMethodIsProtectedAndNotAccessible(Method method, String methodType)
    {
        MethodSignature methodSig = new MethodSignature(method);
        return _formatter.format("annotated-method-protected-not-accessible", methodSig.toString());
    }
    
    public static String unableToCreateAnnotationsExtensionProvider(String processorClassName, Exception cause)
    {
        return _formatter.format("unable-to-create-annotations-extension-provider", processorClassName, cause);
    }

    public static String annotationsExtensionProviderWrongType(String processorClassName, Class requiredInterface)
    {
        return _formatter.format("annotations-extension-provider-wrong-type", processorClassName, requiredInterface.getName());
    }

}