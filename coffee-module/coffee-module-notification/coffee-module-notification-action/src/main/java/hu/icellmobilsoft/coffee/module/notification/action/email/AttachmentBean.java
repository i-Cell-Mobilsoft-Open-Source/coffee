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
package hu.icellmobilsoft.coffee.module.notification.action.email;

import java.util.Arrays;

import javax.enterprise.inject.Vetoed;

/**
 * Tulajdonkeppen csak arra szolgal hogy a byte[] tombnek legyen neve.<br>
 *
 * @author imre.scheffer
 * @since 1.0.0
 */
@Vetoed
public class AttachmentBean {

    private String name;
    private byte[] data;

    /**
     * <p>Constructor for AttachmentBean.</p>
     */
    public AttachmentBean() {
    }

    /**
     * <p>Constructor for AttachmentBean.</p>
     */
    public AttachmentBean(String name, byte[] data) {
        super();
        this.name = name;
        setData(data);
    }

    /**
     * <p>Getter for the field <code>name</code>.</p>
     */
    public String getName() {
        return name;
    }

    /**
     * <p>Setter for the field <code>name</code>.</p>
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * <p>Getter for the field <code>data</code>.</p>
     */
    public byte[] getData() {
        return data;
    }

    /**
     * <p>Setter for the field <code>data</code>.</p>
     */
    public void setData(byte[] data) {
        this.data = Arrays.copyOf(data, data.length);
    }
}
