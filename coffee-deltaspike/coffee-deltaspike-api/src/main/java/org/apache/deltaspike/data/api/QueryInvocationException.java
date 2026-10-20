/*-
 * #%L
 * Coffee
 * %%
 * Copyright (C) 2020 - 2022 i-Cell Mobilsoft Zrt.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.deltaspike.data.api;

import java.lang.reflect.Method;

import org.apache.deltaspike.data.spi.QueryInvocationContext;

public class QueryInvocationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public QueryInvocationException(Throwable t, QueryInvocationContext context) {
        super(createMessage(context, t), t);
    }

    public QueryInvocationException(String message, QueryInvocationContext context) {
        super(createMessage(context));
    }

    public QueryInvocationException(Throwable t, Class<?> proxy, Method method) {
        super(createMessage(proxy, method, t), t);
    }

    private static String createMessage(QueryInvocationContext context) {
        return "Failed calling Repository: [" + //
                "Repository=" + context.getRepositoryClass().getName() + "," + //
                "entity=" + context.getEntityClass().getName() + "," + //
                "method=" + context.getMethod().getName() + ",";
    }

    private static String createMessage(QueryInvocationContext context, Throwable t) {
        return createMessage(context) + "exception=" + t.getClass() + ",message=" + t.getMessage();
    }

    private static String createMessage(Class<?> repoClass, Method method, Throwable t) {
        return "Exception calling Repository: [" + //
                "Repository=" + repoClass + "," + //
                "method=" + method.getName() + "]," + //
                "exception=" + t.getClass() + ",message=" + t.getMessage();
    }
}
