// Copyright 2007 The Apache Software Foundation
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