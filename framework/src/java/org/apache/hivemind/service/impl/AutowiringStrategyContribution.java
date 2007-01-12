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

package org.apache.hivemind.service.impl;

import org.apache.hivemind.Orderable;
import org.apache.hivemind.service.AutowiringStrategy;

/**
 * Contribution to the <code>org.apache.hivemind.service.Autowiring</code>
 * configuration extension point; used to provide
 * a {@link org.apache.hivemind.service.AutowiringStrategy} implementation
 * and advice on ordering the service.
 *
 * @author Achim Huegen
 */
public class AutowiringStrategyContribution implements Orderable
{
    private String _name;
    private String _precedingNames;
    private String _followingNames;

    private AutowiringStrategy _strategy;

    public AutowiringStrategyContribution()
    {
    }
    
    public AutowiringStrategyContribution(AutowiringStrategy strategy, String name, String precedingNames, String followingNames)
    {
        _strategy = strategy;
        _name = name;
        _precedingNames = precedingNames;
        _followingNames = followingNames;
    }

    public AutowiringStrategy getStrategy()
    {
        return _strategy;
    }

    public void setStrategy(AutowiringStrategy strategy)
    {
        _strategy = strategy;
    }

    public String getFollowingNames()
    {
        return _followingNames;
    }

    public String getName()
    {
        return _name;
    }

    public String getPrecedingNames()
    {
        return _precedingNames;
    }

    public void setFollowingNames(String string)
    {
        _followingNames = string;
    }

    public void setName(String string)
    {
        _name = string;
    }

    public void setPrecedingNames(String string)
    {
        _precedingNames = string;
    }

}
