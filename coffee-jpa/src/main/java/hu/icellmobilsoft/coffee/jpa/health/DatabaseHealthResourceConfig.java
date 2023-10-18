/*-
 * #%L
 * Coffee
 * %%
 * Copyright (C) 2020 - 2023 i-Cell Mobilsoft Zrt.
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
package hu.icellmobilsoft.coffee.jpa.health;

/**
 * DataSource resource configurations used to specify the connection required for checking availability.
 * 
 * @author czenczl
 * @since 2.2.0
 *
 */
public class DatabaseHealthResourceConfig {

    private String builderName;
    private String datasourceUrl;
    private String dsName;
    private String datasourcePrefix;
    private Long connectTimeoutSec;

    /**
     * Default constructor, constructs a new object.
     */
    public DatabaseHealthResourceConfig() {
        super();
    }

    /**
     * Gets the the health check builder name e.g. "postgres"
     * 
     * @return health check builder name
     */
    public String getBuilderName() {
        return builderName;
    }

    /**
     * Sets the the health check builder name e.g. "postgres"
     * 
     * @param builderName
     *            health check builder name
     */
    public void setBuilderName(String builderName) {
        this.builderName = builderName;
    }

    /**
     * Gets the database datasource url e.g. "jdbc:postgresql://service-postgredb:5432/service_db?currentSchema=service"
     * 
     * @return database datasource url
     */
    public String getDatasourceUrl() {
        return datasourceUrl;
    }

    /**
     * Sets the database datasource url e.g. "jdbc:postgresql://service-postgredb:5432/service_db?currentSchema=service"
     * 
     * @param datasourceUrl
     *            datasource url
     */
    public void setDatasourceUrl(String datasourceUrl) {
        this.datasourceUrl = datasourceUrl;
    }

    /**
     * Gets the database datasource name e.g. "icellmobilsoftDS"
     * 
     * @return datasource name
     */
    public String getDsName() {
        return dsName;
    }

    /**
     * Sets the database datasource name e.g. "icellmobilsoftDS"
     * 
     * @param dsName
     *            datasource name
     */
    public void setDsName(String dsName) {
        this.dsName = dsName;
    }

    /**
     * Gets the database datasource prefix e.g. "java:jboss/datasources/"
     * 
     * @return datasource prefix
     */
    public String getDatasourcePrefix() {
        return datasourcePrefix;
    }

    /**
     * Sets the database datasource prefix e.g. "java:jboss/datasources/"
     * 
     * @param datasourcePrefix
     *            datasource prefix
     */
    public void setDatasourcePrefix(String datasourcePrefix) {
        this.datasourcePrefix = datasourcePrefix;
    }

    /**
     * Gets the connection timeout in sec
     * 
     * @return connection time out
     */
    public Long getConnectTimeoutSec() {
        return connectTimeoutSec;
    }

    /**
     * Sets the connection timeout in sec
     * 
     * @param connectTimeoutSec
     *            connection time out
     */
    public void setConnectTimeoutSec(Long connectTimeoutSec) {
        this.connectTimeoutSec = connectTimeoutSec;
    }
}
