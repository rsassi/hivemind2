// Copyright 2005 The Apache Software Foundation
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

package org.apache.examples.annotations.panorama;

import org.apache.hivemind.annotations.AnnotatedRegistryBuilder;
import org.apache.hivemind.annotations.TypedRegistry;

/**
 * Builds the Registry for Panorama example based on annotations, then exits. 
 * 
 * @author Achim Huegen
 */
public class PanoramaMain
{

    public static void main(String[] args)
    {
        AnnotatedRegistryBuilder builder = new AnnotatedRegistryBuilder();
        TypedRegistry registry = builder.constructRegistry(PanoramaModule.class);

        registry.shutdown();
    }
}
