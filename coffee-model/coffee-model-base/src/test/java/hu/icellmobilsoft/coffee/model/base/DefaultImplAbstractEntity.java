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
package hu.icellmobilsoft.coffee.model.base;

import java.io.InputStream;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.SQLException;

import javax.enterprise.inject.Vetoed;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;

/**
 * Default implementation of {@link AbstractEntity}
 *
 * @author attila.kiss
 */
@Vetoed
@Entity
@Table(name = "dummy")
public class DefaultImplAbstractEntity extends AbstractEntity {

    @Column
    private byte[] byteArrayColumn;

    @Lob
    @Column
    private Blob blobColumn;

    @Lob
    @Column
    private Clob clobColumn;

    public byte[] getByteArrayColumn() {
        return byteArrayColumn;
    }

    public void setByteArrayColumn(byte[] byteArrayColumn) {
        this.byteArrayColumn = byteArrayColumn;
    }

    public Blob getBlobColumn() {
        return blobColumn;
    }

    public void setBlobColumn(Blob blobColumn) {
        this.blobColumn = blobColumn;
    }

    public Clob getClobColumn() {
        return clobColumn;
    }

    public void setClobColumn(Clob clobColumn) {
        this.clobColumn = clobColumn;
    }

    public InputStream getBlobStream() throws SQLException {
        return blobColumn == null ? null : blobColumn.getBinaryStream();
    }

}
