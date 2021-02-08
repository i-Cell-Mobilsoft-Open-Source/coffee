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
package hu.icellmobilsoft.coffee.jpa.sql.batch.enums;

/**
 * Batch statement result code enum
 *
 * @author arnold.bucher
 * @since 1.0.0
 */
public enum Status {

    /**
     * Processed successfully, at least one row was affected (RESULT_CODE &gt; 0)
     */
    SUCCESS(1),
    /**
     * Processed successfully but no rows were affected (RESULT_CODE = 0)
     */
    SUCCESS_NO_UPDATE(0),
    /**
     * Processed successfully but the number of affected rows is unknown (RESULT_CODE = -2)
     */
    SUCCESS_NO_INFO(0),
    /**
     * Failed to execute (RESULT_CODE = -3)
     */
    EXECUTE_FAILED(0),
    /**
     * Default
     */
    UNKNOWN(0);

    // value to hold the number of affected rows
    private int rowsAffected;

    Status(int rowsAffected) {
        this.rowsAffected = rowsAffected;
    }

    /**
     * <p>
     * Setter for the field <code>rowsAffected</code>.
     * </p>
     * 
     * @param rowsAffected
     *            number of affected rows
     * @return {@link Status}
     */
    public Status setRowsAffected(int rowsAffected) {
        this.rowsAffected = rowsAffected;
        return this;
    }

    /**
     * <p>
     * Getter for the field <code>rowsAffected</code>.
     * </p>
     * 
     * @return number of affected rows
     */
    public int getRowsAffected() {
        return this.rowsAffected;
    }

}
