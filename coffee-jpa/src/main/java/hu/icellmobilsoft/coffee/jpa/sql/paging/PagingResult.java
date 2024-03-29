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
package hu.icellmobilsoft.coffee.jpa.sql.paging;

import java.util.List;

/**
 * <p>
 * PagingResult class.
 * </p>
 *
 * @author Karoly
 * @param <T>
 *            entity
 * @since 1.0.0
 */
public class PagingResult<T> {

    private List<T> results;
    private QueryMetaData metaData;

    /**
     * Default constructor, constructs a new object.
     */
    public PagingResult() {
        super();
    }

    /**
     * <p>
     * Getter for the field <code>results</code>.
     * </p>
     *
     * @return paging result list
     */
    public List<T> getResults() {
        return results;
    }

    /**
     * <p>
     * Setter for the field <code>results</code>.
     * </p>
     * 
     * @param results
     *            paging result list
     */
    public void setResults(List<T> results) {
        this.results = results;
    }

    /**
     * <p>
     * getDetails.
     * </p>
     * 
     * @return query metadata
     */
    public QueryMetaData getDetails() {
        return metaData;
    }

    /**
     * <p>
     * setDetails.
     * </p>
     *
     * @param metaData
     *            query metadata
     */
    public void setDetails(QueryMetaData metaData) {
        this.metaData = metaData;
    }
}
