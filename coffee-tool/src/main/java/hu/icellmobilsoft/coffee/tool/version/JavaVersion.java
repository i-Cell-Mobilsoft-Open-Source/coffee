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
package hu.icellmobilsoft.coffee.tool.version;

/**
 * Helper class holding java version with pattern {major}.{feature}.{patch}.{patchUpdate}
 *
 * @author arnold.bucher
 * @since 1.0.0
 */
public class JavaVersion {

    private Integer major;
    private Integer feature;
    private String patch;
    private Integer patchUpdate;

    /**
     * <p>Getter for the field <code>major</code>.</p>
     */
    public Integer getMajor() {
        return major;
    }

    /**
     * <p>Setter for the field <code>major</code>.</p>
     */
    public void setMajor(Integer major) {
        this.major = major;
    }

    /**
     * <p>Getter for the field <code>feature</code>.</p>
     */
    public Integer getFeature() {
        return feature;
    }

    /**
     * <p>Setter for the field <code>feature</code>.</p>
     */
    public void setFeature(Integer feature) {
        this.feature = feature;
    }

    /**
     * <p>Getter for the field <code>patch</code>.</p>
     */
    public String getPatch() {
        return patch;
    }

    /**
     * <p>Setter for the field <code>patch</code>.</p>
     */
    public void setPatch(String patch) {
        this.patch = patch;
    }

    /**
     * <p>Getter for the field <code>patchUpdate</code>.</p>
     */
    public Integer getPatchUpdate() {
        return patchUpdate;
    }

    /**
     * <p>Setter for the field <code>patchUpdate</code>.</p>
     */
    public void setPatchUpdate(Integer patchUpdate) {
        this.patchUpdate = patchUpdate;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return "JavaVersion{" + "major=" + major + ", feature=" + feature + ", patch='" + patch + '\'' + ", patchUpdate=" + patchUpdate + '}';
    }

}
