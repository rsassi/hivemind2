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

package org.apache.hivemind.annotations;

import hivemind.test.services.ServiceAutowireTarget;
import hivemind.test.services.StringHolder;

public class TestAnnotatedModules extends AnnotationTestCase
{
    public void testAutowiring()
    {
        TypedRegistry registry = constructRegistry(AutowiringModule.class);
        ServiceAutowireTarget service = registry.getService(ServiceAutowireTarget.class);
        assertNotNull(service.getStringHolder());
    }
    
    public void testSubmodule()
    {
        TypedRegistry registry = constructRegistry(Supermodule.class);
        StringHolder service = registry.getService("super.sub.StringHolder", StringHolder.class);
        assertNotNull(service);
    }

}
