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

import hivemind.test.services.StringHolder;
import hivemind.test.services.impl.StringHolderImpl;

import org.apache.hivemind.annotations.definition.Implementation;
import org.apache.hivemind.annotations.definition.Module;
import org.apache.hivemind.annotations.definition.Service;
import org.apache.hivemind.internal.ServiceModel;

@Module(id = "testModule")
public class OverrideServiceModule extends AbstractAnnotatedModule
{
    @Service(id = "Test")
    public StringHolder getStringHolder() {
        return null;
    }

    @Implementation(serviceId = "Test", serviceModel=ServiceModel.PRIMITIVE)
    public StringHolder getStringHolderImpl() {
        return new StringHolderImpl();
    }
}
