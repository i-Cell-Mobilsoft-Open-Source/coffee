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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for QueryMetaData complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="QueryMetaData">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="totalRows" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *         &lt;element name="maxPage" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *         &lt;element name="rows" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *         &lt;element name="page" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 * @since 1.0.0
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "QueryMetaData", propOrder = {
    "totalRows",
    "maxPage",
    "rows",
    "page"
})
public class QueryMetaData {

    @XmlElement(required = true)
    protected BigInteger totalRows;
    @XmlElement(required = true)
    protected BigInteger maxPage;
    @XmlElement(required = true)
    protected BigInteger rows;
    @XmlElement(required = true)
    protected BigInteger page;

    /**
     * Gets the value of the totalRows property.
     */
    public BigInteger getTotalRows() {
        return totalRows;
    }

    /**
     * Sets the value of the totalRows property.
     *
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     */
    public void setTotalRows(BigInteger value) {
        this.totalRows = value;
    }

    /**
     * Gets the value of the maxPage property.
     */
    public BigInteger getMaxPage() {
        return maxPage;
    }

    /**
     * Sets the value of the maxPage property.
     *
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     */
    public void setMaxPage(BigInteger value) {
        this.maxPage = value;
    }

    /**
     * Gets the value of the rows property.
     */
    public BigInteger getRows() {
        return rows;
    }

    /**
     * Sets the value of the rows property.
     *
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     */
    public void setRows(BigInteger value) {
        this.rows = value;
    }

    /**
     * Gets the value of the page property.
     */
    public BigInteger getPage() {
        return page;
    }

    /**
     * Sets the value of the page property.
     *
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     */
    public void setPage(BigInteger value) {
        this.page = value;
    }

}
