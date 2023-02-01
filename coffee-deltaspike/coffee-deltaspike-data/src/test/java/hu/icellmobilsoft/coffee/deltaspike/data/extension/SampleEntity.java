/*-
 * #%L
 * Sampler
 * %%
 * Copyright (C) 2022 i-Cell Mobilsoft Zrt.
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
package hu.icellmobilsoft.coffee.deltaspike.data.extension;

import java.time.LocalDateTime;

import jakarta.enterprise.inject.Vetoed;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;

/**
 * 
 * Sample table entity
 * 
 * @author czenczl
 * @since 2.0.0
 */
@Vetoed
@Entity
@Table(name = "SAMPLE")
public class SampleEntity {

    private static final long serialVersionUID = 1L;

    /**
     * Primary key of the entity
     */
    @Id
    @Column(name = "X__ID", length = 30)
    private String id;

    /**
     * Value of sample input data value
     */
    @Column(name = "INPUT_VALUE", length = 30)
    @Size(max = 30)
    private String inputValue;

    /**
     * Value of sample local date
     */
    @Column(name = "LOCAL_DATE")
    private LocalDateTime localDateTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getInputValue() {
        return inputValue;
    }

    public void setInputValue(String inputValue) {
        this.inputValue = inputValue;
    }

    public LocalDateTime getLocalDateTime() {
        return localDateTime;
    }

    public void setLocalDateTime(LocalDateTime localDateTime) {
        this.localDateTime = localDateTime;
    }

}
