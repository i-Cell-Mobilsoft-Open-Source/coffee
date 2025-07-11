/*-
 * #%L
 * Coffee
 * %%
 * Copyright (C) 2020 - 2025 i-Cell Mobilsoft Zrt.
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
package hu.icellmobilsoft.coffee.module.docgen.sqlcatalog.data;

/**
 * Data class for collected sql documentation data
 * 
 * @param className
 *            name of the repository class
 * @param methodName
 *            name of the repository method
 * @param jpql
 *            query in jpql format
 * @param comment
 *            query comment
 * @author janos.boroczki
 * @since 2.12.0
 */
public record SqlCatalogData(String className, String methodName, String jpql, String comment) {
}
