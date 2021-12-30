/*-
 * #%L
 * Coffee
 * %%
 * Copyright (C) 2020 - 2021 i-Cell Mobilsoft Zrt.
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
package hu.icellmobilsoft.coffee.module.csv;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import com.opencsv.bean.CsvDate;

import hu.icellmobilsoft.coffee.module.csv.annotation.CsvBindByNamePosition;
import hu.icellmobilsoft.coffee.module.csv.annotation.CsvCustomBindByNamePosition;
import hu.icellmobilsoft.coffee.module.csv.localization.LocalizationConverter;

/**
 * Example class for helping the {@link CsvUtil#toLocalizedCsv} test
 * 
 * @author martin.nagy
 * @since 1.8.0
 */
public class LocalizedTestBean {

    public enum Status {
        RECEIVED, IN_PROGRESS, DONE
    }

    @CsvBindByNamePosition(position = 0, column = "IDENTIFIER")
    private long id;

    @CsvBindByNamePosition(position = 4)
    private String name;

    @CsvCustomBindByNamePosition(position = 2, converter = LocalizationConverter.class)
    private boolean active;

    @CsvDate("yyyy-MM-dd")
    @CsvBindByNamePosition(position = 3)
    private LocalDate creationDate;

    @CsvCustomBindByNamePosition(position = 1, converter = LocalizationConverter.class)
    private Status status;

    public LocalizedTestBean() {
    }

    public LocalizedTestBean(long id, String name, boolean active, LocalDate creationDate, Status status) {
        this.id = id;
        this.name = name;
        this.active = active;
        this.creationDate = creationDate;
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        LocalizedTestBean testBean = (LocalizedTestBean) o;
        return id == testBean.id && active == testBean.active && Objects.equals(name, testBean.name)
                && Objects.equals(creationDate, testBean.creationDate) && status == testBean.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, active, creationDate, status);
    }

    @Override
    public String toString() {
        return "LocalizedTestBean{" + "id=" + id + ", name='" + name + '\'' + ", active=" + active + ", creationDate=" + creationDate + ", status="
                + status + '}';
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isActive() {
        return active;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public Status getStatus() {
        return status;
    }
}
