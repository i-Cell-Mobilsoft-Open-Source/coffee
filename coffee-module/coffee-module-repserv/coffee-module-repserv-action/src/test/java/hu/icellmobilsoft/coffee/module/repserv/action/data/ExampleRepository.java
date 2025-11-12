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
package hu.icellmobilsoft.coffee.module.repserv.action.data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import org.apache.deltaspike.data.api.Query;
import org.apache.deltaspike.data.api.QueryParam;
import org.apache.deltaspike.data.api.Repository;

@Repository
public interface ExampleRepository {

    @Query("""
            SELECT t FROM Test t
            WHERE t.param = :param
            AND t.p = :p
            """)
    Object test(String param, String p);

    @Query("""
            SELECT count(t) FROM Test t
            WHERE t.param1 = :param1
            AND t.param2 = :param2
            """)
    <T extends BigDecimal & Serializable, R> long count(@QueryParam("param1") String param1, @QueryParam("param2") T param2);

    @Query("SELECT t FROM Test t")
    List<Object> findAll();
}
