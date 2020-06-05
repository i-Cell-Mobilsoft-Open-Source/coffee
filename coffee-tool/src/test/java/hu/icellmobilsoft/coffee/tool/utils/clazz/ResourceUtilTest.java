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
package hu.icellmobilsoft.coffee.tool.utils.clazz;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import hu.icellmobilsoft.coffee.cdi.logger.AppLogger;

/**
 * @author robert.kaplar
 */
@DisplayName("Testing ResourceUtil")
class ResourceUtilTest {

    @Test
    @DisplayName("Testing getAppName() if the given class is null")
    void testIfClazzIsNull() throws Exception {
        // given
        // when
        String result = ResourceUtil.getAppName(null);
        // then
        assertNull(result);
    }

    // Mivel a Class-t általánosan nem lehet mockolni, így speciálisan az AppLogger osztályt használjuk a teszthez, mivel az coffee-tool függőségként
    // az coffee-cdi-t behúzza, amiben az AppLogger is megtalálható. További limitáció, hogy csak a név elejét hasonlítjuk össze az elvárt eredményként
    // a jar verzióváltásait kikerülve.
    @Test
    @DisplayName("Testing getAppName() for the class AppLogger which loaded from coffee-cdi jar")
    void testCoffeeCdiJar() throws Exception {
        // given
        // when
        String result = ResourceUtil.getAppName(AppLogger.class);
        // then
        assertEquals("coffee", result.substring(0, 6));
    }

}
