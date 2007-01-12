package org.apache.hivemind;

import org.apache.hivemind.schema.Translator;

/**
 * Manages translators for {@link org.apache.hivemind.impl.RegistryInfrastructureImpl}.
 * Translators may be a shared, cached instance.
 * (Translators should be stateless). Translators are identified by a constructor, which may be
 * the name of a translator defined in the <code>hivemind.Translators</code> extension point
 * (a single builtin translator, <code>class</code>, is hardcoded). Alternately, the name may
 * consist of a translator name, a comma, and an initializer string for the service (example:
 * <code>int,min=5</code>).
 * 
 * @author Howard Lewis Ship
 */
public interface TranslatorManager
{
    public abstract Translator getTranslator(String constructor);

}