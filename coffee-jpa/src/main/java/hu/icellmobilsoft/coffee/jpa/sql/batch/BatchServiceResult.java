/*-
 * #%L
 * Coffee
 * %%
 * Copyright (C) 2020 i-Cell Mobilsoft Zrt.
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
package hu.icellmobilsoft.coffee.jpa.sql.batch;

import java.sql.Statement;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.enterprise.context.Dependent;

import org.apache.deltaspike.core.util.CollectionUtils;

import hu.icellmobilsoft.coffee.jpa.sql.batch.enums.BatchMode;
import hu.icellmobilsoft.coffee.jpa.sql.batch.enums.Status;

/**
 * Batch mentessek eredmenyet osszegyujto osztaly
 *
 * @author arnold.bucher
 * @since 1.0.0
 */
@Dependent
public class BatchServiceResult {

    private Map<BatchMode, LinkedHashMap<String, Status>> result = new HashMap<>();

    /**
     * Get all collected batch result (INSERT & UPDATE & DELETE)
     */
    public Map<String, Status> getResult() {
        Map<String, Status> allResults = new TreeMap<>();
        allResults.putAll(getResult(BatchMode.INSERT));
        allResults.putAll(getResult(BatchMode.UPDATE));
        allResults.putAll(getResult(BatchMode.DELETE));
        return allResults;
    }

    /**
     * Get collected batch result fot the given batch mode
     *
     * @param batchMode INSERT | UPDATE | DELETE
     * @return Map with key=EntityId, value=ResultStatus
     */
    public Map<String, Status> getResult(BatchMode batchMode) {
        if (!result.containsKey(batchMode)) {
            return Collections.emptyMap();
        }
        return result.get(batchMode);
    }

    /**
     * Add executing batch result
     *
     * @param batchMode   INSERT | UPDATE | DELETE
     * @param entityIds   input with entity ids
     * @param batchResult executing batch result
     */
    public void addBatchResult(BatchMode batchMode, List<String> entityIds, int[] batchResult) {
        if (CollectionUtils.isEmpty(entityIds)) {
            return;
        } else if (batchMode == null) {
            throw new IllegalArgumentException("BatchMode is missing!");
        } else if (entityIds.size() != batchResult.length) {
            throw new IllegalArgumentException("Each entityId must have an associated batchResult!");
        }

        for (int i = 0; i < entityIds.size(); i++) {
            int resultCode = batchResult[i];
            if (resultCode > 0) {
                put(batchMode, entityIds.get(i), Status.SUCCESS.setRowsAffected(resultCode));
            } else {
                switch (resultCode) {
                case 0:
                    put(batchMode, entityIds.get(i), Status.SUCCESS_NO_UPDATE);
                    break;
                case Statement.SUCCESS_NO_INFO:
                    put(batchMode, entityIds.get(i), Status.SUCCESS_NO_INFO);
                    break;
                case Statement.EXECUTE_FAILED:
                    put(batchMode, entityIds.get(i), Status.EXECUTE_FAILED);
                    break;
                default:
                    put(batchMode, entityIds.get(i), Status.UNKNOWN);

                }
            }
        }
    }

    private void put(BatchMode batchMode, String entityId, Status status) {
        if (result.containsKey(batchMode)) {
            LinkedHashMap<String, Status> resultByBatchMode = result.get(batchMode);
            resultByBatchMode.put(entityId, status);
        } else {
            LinkedHashMap<String, Status> resultByBatchMode = new LinkedHashMap<>();
            resultByBatchMode.put(entityId, status);
            result.put(batchMode, resultByBatchMode);
        }
    }

}
