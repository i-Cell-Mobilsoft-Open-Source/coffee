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
     * Default constructor, constructs a new object.
     */
    public JavaVersion() {
        super();
    }

    /**
     * Getter for the field <code>major</code>.
     *
     * @return {@code major}
     */
    public Integer getMajor() {
        return major;
    }

    /**
     * Setter for the field <code>major</code>.
     * 
     * @param major
     *            {@code major}
     */
    public void setMajor(Integer major) {
        this.major = major;
    }

    /**
     * Getter for the field <code>feature</code>.
     * 
     * @return {@code feature}
     */
    public Integer getFeature() {
        return feature;
    }

    /**
     * Setter for the field <code>feature</code>.
     *
     * @param feature
     *            {@code feature}
     */
    public void setFeature(Integer feature) {
        this.feature = feature;
    }

    /**
     * Getter for the field <code>patch</code>.
     *
     * @return {@code patch}
     */
    public String getPatch() {
        return patch;
    }

    /**
     * Setter for the field <code>patch</code>.
     *
     * @param patch
     *            {@code patch}
     */
    public void setPatch(String patch) {
        this.patch = patch;
    }

    /**
     * Getter for the field <code>patchUpdate</code>.
     *
     * @return {@code patchUpdate}
     */
    public Integer getPatchUpdate() {
        return patchUpdate;
    }

    /**
     * Setter for the field <code>patchUpdate</code>.
     * 
     * @param patchUpdate
     *            {@code patchUpdate}
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
