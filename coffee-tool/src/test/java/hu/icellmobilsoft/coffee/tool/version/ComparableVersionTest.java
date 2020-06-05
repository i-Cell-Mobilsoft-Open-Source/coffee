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

import java.util.Locale;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import hu.icellmobilsoft.coffee.tool.version.ComparableVersion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

/**
 * Test ComparableVersion.
 *
 * @author <a href="mailto:hboutemy@apache.org">Hervé Boutemy</a>
 * @author imre.scheffer - modify to own test system, fix junit upgrade changes, formatting
 * @see <a href=
 * "https://github.com/apache/maven/blob/master/maven-artifact/src/test/java/org/apache/maven/artifact/versioning/ComparableVersionTest.java">Original
 * source</a>
 */
@SuppressWarnings("unchecked")
@DisplayName("Testing ComparableVersion")
public class ComparableVersionTest {

    private Comparable newComparable(String version) {
        ComparableVersion ret = new ComparableVersion(version);
        String canonical = ret.getCanonical();
        String parsedCanonical = new ComparableVersion(canonical).getCanonical();

        //        System.out.println("canonical( " + version + " ) = " + canonical);
        assertEquals(canonical, parsedCanonical, "canonical( " + version + " ) = " + canonical + " -> canonical: " + parsedCanonical);

        return ret;
    }

    private static final String[] VERSIONS_QUALIFIER = { "1-alpha2snapshot", "1-alpha2", "1-alpha-123", "1-beta-2", "1-beta123", "1-m2", "1-m11",
            "1-rc", "1-cr2", "1-rc123", "1-SNAPSHOT", "1", "1-sp", "1-sp2", "1-sp123", "1-abc", "1-def", "1-pom-1", "1-1-snapshot", "1-1", "1-2",
            "1-123" };

    private static final String[] VERSIONS_NUMBER = { "2.0", "2-1", "2.0.a", "2.0.0.a", "2.0.2", "2.0.123", "2.1.0", "2.1-a", "2.1b", "2.1-c",
            "2.1-1", "2.1.0.1", "2.2", "2.123", "11.a2", "11.a11", "11.b2", "11.b11", "11.m2", "11.m11", "11", "11.a", "11b", "11c", "11m" };

    private void checkVersionsOrder(String[] versions) {
        Comparable[] c = new Comparable[versions.length];
        for (int i = 0; i < versions.length; i++) {
            c[i] = newComparable(versions[i]);
        }

        for (int i = 1; i < versions.length; i++) {
            Comparable low = c[i - 1];
            for (int j = i; j < versions.length; j++) {
                Comparable high = c[j];
                assertTrue(low.compareTo(high) < 0, "expected " + low + " < " + high);
                assertTrue(high.compareTo(low) > 0, "expected " + high + " > " + low);
            }
        }
    }

    private void checkVersionsEqual(String v1, String v2) {
        Comparable c1 = newComparable(v1);
        Comparable c2 = newComparable(v2);
        assertTrue(c1.compareTo(c2) == 0, "expected " + v1 + " == " + v2);
        assertTrue(c2.compareTo(c1) == 0, "expected " + v2 + " == " + v1);
        assertTrue(c1.hashCode() == c2.hashCode(), "expected same hashcode for " + v1 + " and " + v2);
        assertTrue(c1.equals(c2), "expected " + v1 + ".equals( " + v2 + " )");
        assertTrue(c2.equals(c1), "expected " + v2 + ".equals( " + v1 + " )");
    }

    private void checkVersionsOrder(String v1, String v2) {
        Comparable c1 = newComparable(v1);
        Comparable c2 = newComparable(v2);
        assertTrue(c1.compareTo(c2) < 0, "expected " + v1 + " < " + v2);
        assertTrue(c2.compareTo(c1) > 0, "expected " + v2 + " > " + v1);
    }

    @Test
    @DisplayName("Testing versions qualifier")
    public void testVersionsQualifier() {
        checkVersionsOrder(VERSIONS_QUALIFIER);
    }

    @Test
    @DisplayName("Testing versions number")
    public void testVersionsNumber() {
        checkVersionsOrder(VERSIONS_NUMBER);
    }

    @Test
    @DisplayName("Testing versions equals")
    public void testVersionsEqual() {
        newComparable("1.0-alpha");
        checkVersionsEqual("1", "1");
        checkVersionsEqual("1", "1.0");
        checkVersionsEqual("1", "1.0.0");
        checkVersionsEqual("1.0", "1.0.0");
        checkVersionsEqual("1", "1-0");
        checkVersionsEqual("1", "1.0-0");
        checkVersionsEqual("1.0", "1.0-0");
        // no separator between number and character
        checkVersionsEqual("1a", "1-a");
        checkVersionsEqual("1a", "1.0-a");
        checkVersionsEqual("1a", "1.0.0-a");
        checkVersionsEqual("1.0a", "1-a");
        checkVersionsEqual("1.0.0a", "1-a");
        checkVersionsEqual("1x", "1-x");
        checkVersionsEqual("1x", "1.0-x");
        checkVersionsEqual("1x", "1.0.0-x");
        checkVersionsEqual("1.0x", "1-x");
        checkVersionsEqual("1.0.0x", "1-x");

        // aliases
        checkVersionsEqual("1ga", "1");
        checkVersionsEqual("1final", "1");
        checkVersionsEqual("1cr", "1rc");

        // special "aliases" a, b and m for alpha, beta and milestone
        checkVersionsEqual("1a1", "1-alpha-1");
        checkVersionsEqual("1b2", "1-beta-2");
        checkVersionsEqual("1m3", "1-milestone-3");

        // case insensitive
        checkVersionsEqual("1X", "1x");
        checkVersionsEqual("1A", "1a");
        checkVersionsEqual("1B", "1b");
        checkVersionsEqual("1M", "1m");
        checkVersionsEqual("1Ga", "1");
        checkVersionsEqual("1GA", "1");
        checkVersionsEqual("1Final", "1");
        checkVersionsEqual("1FinaL", "1");
        checkVersionsEqual("1FINAL", "1");
        checkVersionsEqual("1Cr", "1Rc");
        checkVersionsEqual("1cR", "1rC");
        checkVersionsEqual("1m3", "1Milestone3");
        checkVersionsEqual("1m3", "1MileStone3");
        checkVersionsEqual("1m3", "1MILESTONE3");
    }

    @Test
    @DisplayName("Testing version comparing")
    public void testVersionComparing() {
        checkVersionsOrder("1", "2");
        checkVersionsOrder("1.5", "2");
        checkVersionsOrder("1", "2.5");
        checkVersionsOrder("1.0", "1.1");
        checkVersionsOrder("1.1", "1.2");
        checkVersionsOrder("1.0.0", "1.1");
        checkVersionsOrder("1.0.1", "1.1");
        checkVersionsOrder("1.1", "1.2.0");

        checkVersionsOrder("1.0-alpha-1", "1.0");
        checkVersionsOrder("1.0-alpha-1", "1.0-alpha-2");
        checkVersionsOrder("1.0-alpha-1", "1.0-beta-1");

        checkVersionsOrder("1.0-beta-1", "1.0-SNAPSHOT");
        checkVersionsOrder("1.0-SNAPSHOT", "1.0");
        checkVersionsOrder("1.0-alpha-1-SNAPSHOT", "1.0-alpha-1");

        checkVersionsOrder("1.0", "1.0-1");
        checkVersionsOrder("1.0-1", "1.0-2");
        checkVersionsOrder("1.0.0", "1.0-1");

        checkVersionsOrder("2.0-1", "2.0.1");
        checkVersionsOrder("2.0.1-klm", "2.0.1-lmn");
        checkVersionsOrder("2.0.1", "2.0.1-xyz");

        checkVersionsOrder("2.0.1", "2.0.1-123");
        checkVersionsOrder("2.0.1-xyz", "2.0.1-123");
    }

    /**
     * Test <a href="https://issues.apache.org/jira/browse/MNG-5568">MNG-5568</a> edge case which was showing transitive inconsistency: since A &gt; B
     * and B &gt; C then we should have A &gt; C otherwise sorting a list of ComparableVersions() will in some cases throw runtime exception; see
     * Netbeans issues <a href="https://netbeans.org/bugzilla/show_bug.cgi?id=240845">240845</a> and
     * <a href="https://netbeans.org/bugzilla/show_bug.cgi?id=226100">226100</a>
     */
    @Test
    @DisplayName("Testing MNG-5568 issue")
    public void testMng5568() {
        String a = "6.1.0";
        String b = "6.1.0rc3";
        String c = "6.1H.5-beta"; // this is the unusual version string, with 'H' in the middle

        checkVersionsOrder(b, a); // classical
        checkVersionsOrder(b, c); // now b < c, but before MNG-5568, we had b > c
        checkVersionsOrder(a, c);
    }

    @Test
    @DisplayName("Testing Locale independent")
    public void testLocaleIndependent() {
        Locale orig = Locale.getDefault();
        Locale[] locales = { Locale.ENGLISH, new Locale("tr"), Locale.getDefault() };
        try {
            for (Locale locale : locales) {
                Locale.setDefault(locale);
                checkVersionsEqual("1-abcdefghijklmnopqrstuvwxyz", "1-ABCDEFGHIJKLMNOPQRSTUVWXYZ");
            }
        } finally {
            Locale.setDefault(orig);
        }
    }

    @Test
    @DisplayName("Testing reuse")
    public void testReuse() {
        ComparableVersion c1 = new ComparableVersion("1");
        c1.parseVersion("2");

        Comparable c2 = newComparable("2");

        assertEquals(c1, c2, "reused instance should be equivalent to new instance");
    }
}
