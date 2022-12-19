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
package org.apache.deltaspike.core.api.projectstage;

/**
 * A marker interface for custom ProjectStage holders. A ProjectStage holder is a class which contains one or more
 * {@link ProjectStage}s.
 *
 * Any custom ProjectStageHolder must get registered via the {@link java.util.ServiceLoader} mechanism. Simply create a
 * file
 * <pre>
 *     META-INF/services/org.apache.deltaspike.core.api.projectstage.ProjectStageHolder
 * </pre> and write the fully qualified class name of your ProjectStageHolder into it.
 * @deprecated remove it after remove deltaspike data 
 */
@Deprecated(since = "2.0.0", forRemoval = true)
public interface ProjectStageHolder
{
}
