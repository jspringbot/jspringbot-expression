/*
 * Copyright (c) 2012. JSpringBot. All Rights Reserved.
 *
 * See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The JSpringBot licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jspringbot.keyword.expression;

import org.jspringbot.Keyword;
import org.jspringbot.keyword.expression.plugin.DefaultVariableProviderImpl;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import javax.annotation.Resource;

public abstract class AbstractExpressionKeyword implements Keyword, InitializingBean {

    @Autowired
    protected ExpressionHelper helper;
    @Autowired
    protected ApplicationContext applicationContext;
    protected DefaultVariableProviderImpl defaultVariableProvider;
    protected DefaultVariableProviderImpl globalVariableProvider;

    @Override
    public void afterPropertiesSet() throws Exception {
        defaultVariableProvider = applicationContext.getBean("defaultVariableProvider", DefaultVariableProviderImpl.class);
        globalVariableProvider = applicationContext.getBean("globalVariableProvider", DefaultVariableProviderImpl.class);
    }
}
