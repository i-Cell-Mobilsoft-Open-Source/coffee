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

import java.math.BigInteger;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for QueryMetaData complex type.
 *
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="QueryMetaData"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="totalRows" type="{http://www.w3.org/2001/XMLSchema}integer"/&gt;
 *         &lt;element name="maxPage" type="{http://www.w3.org/2001/XMLSchema}integer"/&gt;
 *         &lt;element name="rows" type="{http://www.w3.org/2001/XMLSchema}integer"/&gt;
 *         &lt;element name="page" type="{http://www.w3.org/2001/XMLSchema}integer"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 *
 * @since 1.0.0
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "QueryMetaData", propOrder = { "totalRows", "maxPage", "rows", "page" })
public class QueryMetaData {
    /**
     * Default constructor, constructs a new object.
     */
    public QueryMetaData() {
        super();
    }

    /**
     * Total number of rows
     */
    @XmlElement(required = true)
    protected BigInteger totalRows;
    /**
     * Total number of pages
     */
    @XmlElement(required = true)
    protected BigInteger maxPage;
    /**
     * Current row number
     */
    @XmlElement(required = true)
    protected BigInteger rows;
    /**
     * Current page number
     */
    @XmlElement(required = true)
    protected BigInteger page;

    /**
     * Gets the value of the totalRows property.
     * 
     * @return total row count
     */
    public BigInteger getTotalRows() {
        return totalRows;
    }

    /**
     * Sets the value of the totalRows property.
     *
     * @param value
     *            allowed object is {@link BigInteger }
     */
    public void setTotalRows(BigInteger value) {
        this.totalRows = value;
    }

    /**
     * Gets the value of the maxPage property.
     * 
     * @return max page number
     */
    public BigInteger getMaxPage() {
        return maxPage;
    }

    /**
     * Sets the value of the maxPage property.
     *
     * @param value
     *            allowed object is {@link BigInteger }
     */
    public void setMaxPage(BigInteger value) {
        this.maxPage = value;
    }

    /**
     * Gets the value of the rows property.
     *
     * @return row counter
     */
    public BigInteger getRows() {
        return rows;
    }

    /**
     * Sets the value of the rows property.
     *
     * @param value
     *            allowed object is {@link BigInteger }
     */
    public void setRows(BigInteger value) {
        this.rows = value;
    }

    /**
     * Gets the value of the page property.
     *
     * @return page index
     */
    public BigInteger getPage() {
        return page;
    }

    /**
     * Sets the value of the page property.
     *
     * @param value
     *            allowed object is {@link BigInteger }
     */
    public void setPage(BigInteger value) {
        this.page = value;
    }

}
