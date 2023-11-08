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
package hu.icellmobilsoft.coffee.rest.cdi;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import jakarta.enterprise.inject.Model;

/**
 * Common request scope container.
 *
 * @author imre.scheffer
 * @since 1.0.0
 */
@Model
public class BaseRequestContainer implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Request object
     */
    private Object requestObject;

    /**
     * Request scoped object map
     */
    private Map<String, Object> objectMap;

    /**
     * Default constructor, constructs a new object.
     */
    public BaseRequestContainer() {
        super();
    }

    /**
     * Getter for the field <code>objectMap</code>.
     * 
     * @return object map
     */
    public Map<String, Object> getObjectMap() {
        if (objectMap == null) {
            objectMap = new HashMap<>();
        }
        return objectMap;
    }

    /**
     * Getter for the field <code>requestObject</code>.
     * 
     * @return request object
     */
    public Object getRequestObject() {
        return requestObject;
    }

    /**
     * Setter for the field <code>requestObject</code>.
     * 
     * @param requestObject
     *            request object
     */
    public void setRequestObject(Object requestObject) {
        this.requestObject = requestObject;
    }
}
