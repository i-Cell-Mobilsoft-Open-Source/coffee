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

/**
 * Encapsulates a byte array with a name.
 *
 * @author imre.scheffer
 * @since 1.0.0
 */
public class AttachmentBean {

    private String name;
    private byte[] data;

    /**
     * No-args constructor for AttachmentBean.
     */
    public AttachmentBean() {
    }

    /**
     * Constructor for AttachmentBean with attachment name and data.
     *
     * @param name
     *            attachment name (filename)
     * @param data
     *            attachment data
     */
    public AttachmentBean(String name, byte[] data) {
        super();
        this.name = name;
        setData(data);
    }

    /**
     * Getter for the field {@code name}.
     *
     * @return {@code name}
     */
    public String getName() {
        return name;
    }

    /**
     * Setter for the field {@code name}.
     *
     * @param name
     *            name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getter for the field {@code data}.
     *
     * @return {@code data}
     */
    public byte[] getData() {
        return data;
    }

    /**
     * Setter for the field {@code data}.
     *
     * @param data
     *            data to set
     */
    public void setData(byte[] data) {
        this.data = Arrays.copyOf(data, data.length);
    }
}
