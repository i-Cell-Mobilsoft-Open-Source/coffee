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
package hu.icellmobilsoft.coffee.rest.url;

import java.util.Collections;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import hu.icellmobilsoft.coffee.rest.url.BaseServicePath;

/**
 * @author karoly.tamas
 *
 */
@DisplayName("Testing BaseServicePath")
public class BaseServicePathTest {

    @DisplayName("Testing path part join")
    @ParameterizedTest(name = "Testing path() with urlPart1:[{0}]; urlPart2:[{1}]; expected:[{2}]; result:[{3}]")
    // given
    @CsvSource(value = { //
            "part1,part2,part1part2,true", //
            "part1,part2,part1,false" //
    })
    void path(String urlPart1, String urlPart2, String expected, boolean result) {

        String path = BaseServicePath.path(urlPart1, urlPart2);

        Assertions.assertEquals(StringUtils.equals(path, expected), result);
    }

    @DisplayName("Testing query parameter fill")
    @ParameterizedTest(name = "Testing query() with url:[{0}]; paramKey:[{1}]; valueValue:[{2}]; expected:[{3}]; result:[{4}]")
    // given
    @CsvSource(value = { //
            "customer,customerId,11,customer?customerId=11,true", //
            "customer,customerId,11,customer,false", //
            "customer,customerId,,customer?customerId=null,true", //
            "customer,,,customer?null=null,true", //
            "tree,treeType,szőlő,tree?treeType=sz%C5%91l%C5%91,true", //
            "tree,szőlő,szőlő,tree?sz%C5%91l%C5%91=sz%C5%91l%C5%91,true" //

    })
    void query(String url, String paramKey, String paramValue, String expected, boolean result) {
        String pathWithQueryParams = BaseServicePath.query(url, Collections.singletonMap(paramKey, paramValue));
        Assertions.assertEquals(StringUtils.equals(pathWithQueryParams, expected), result);
    }

    @DisplayName("Testing query without parameter")
    @ParameterizedTest(name = "Testing query() without parameter with url:[{0}]; expected:[{1}]; result:[{2}]")
    // given
    @CsvSource(value = { //
            "customer,customer,true", //
            "customer,cust,false" //

    })
    void queryWithoutParameter(String url, String expected, boolean result) {
        String path = BaseServicePath.query("customer", null);
        Assertions.assertEquals(StringUtils.equals(path, expected), result);
    }

    @DisplayName("Testing path parameter fill")
    @ParameterizedTest(name = "Testing encodeValue() with url:[{0}]; param:[{1}]; value:[{2}]; expected:[{3}]; result:[{4}]")
    // given
    @CsvSource(value = { //
            "customer/{customerId},customerId,11,customer/11,true", //
            "customer/{customerId},customerId,11,customer/{customerId},false", //
            "tree/{treeType},treeType,szőlő,tree/sz%C5%91l%C5%91,true" //

    })
    void fillParam(String url, String param, String value, String expected, boolean result) {
        String pathWithParams = BaseServicePath.fillParam(url, param, value);
        Assertions.assertEquals(StringUtils.equals(pathWithParams, expected), result);
    }

}
