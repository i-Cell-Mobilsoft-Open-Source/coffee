/*-
 * #%L
 * Coffee
 * %%
 * Copyright (C) 2020 - 2024 i-Cell Mobilsoft Zrt.
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
package hu.icellmobilsoft.coffee.tool.utils.json;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import hu.icellmobilsoft.coffee.se.util.string.RandomUtil;

/**
 * Benchmark test for json util
 *
 * @author bucherarnold
 * @since 2.9.0
 */
@BenchmarkMode(Mode.SingleShotTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Fork(value = 2, jvmArgs = {"-Xms2G", "-Xmx2G", "--add-exports=java.xml/com.sun.org.apache.xerces.internal.jaxp.datatype=ALL-UNNAMED", "--add-opens=java.xml/com.sun.org.apache.xerces.internal.jaxp.datatype=ALL-UNNAMED"})
@Warmup(iterations = 1)
@Measurement(iterations = 2)
public class JsonUtilBenchmarkTest {

    private static final int RAW_JSON_DATA_COUNT = 100_000;
    private static final String TEST_OBJECT_AS_JSON = "{" + //
            "\"date\":1549898614051," + //
            "\"xmlGregorianCalendar\":\"2019-02-11T15:23:34.051Z\"," + //
            "\"bytes\":\"dGVzdFN0cmluZw==\"," + //
            "\"string\":\"test1\"," + //
            "\"clazz\":\"hu.icellmobilsoft.coffee.tool.utils.json.JsonUtilBenchmarkTest\"," + //
            "\"offsetDateTime\":\"2019-02-11T15:23:34.051Z\"," + //
            "\"offsetTime\":\"15:23:34.051Z\"," + //
            "\"localDate\":\"2019-02-11\"," + //
            "\"duration\":\"P1Y1M1DT1H1M1S\"," + //
            "\"yearMonth\":\"2010-05\"" + //
            "}";
    public List<String> rawJsonData = new ArrayList<>();
    public int jsonbOk;
    public int gsonOk;

    public static void main(String[] args) throws Exception {
        Options opt = new OptionsBuilder() //
                .include(JsonUtilBenchmarkTest.class.getSimpleName())
                .build();
        new Runner(opt).run();
    }

    @Setup
    public void setup() {
        System.out.println("Setting up...");

        for (int i = 0; i < RAW_JSON_DATA_COUNT; i++) {
            String rawJson = TEST_OBJECT_AS_JSON.replace("{REQUEST_ID}", RandomUtil.generateId());
            rawJsonData.add(rawJson);
        }
    }

    @Benchmark
    public List<TestObject> testJsonb() throws Exception {
        // prevent JIT compiler code-optimization
        List<TestObject> dto2Deserialized = new ArrayList<>(rawJsonData.size());

        for (String rawJson : rawJsonData) {
            try {
                TestObject dto = JsonUtil.toObject(rawJson, TestObject.class);

                String dtoJson = JsonUtil.toJson(dto);

                TestObject dto2 = JsonUtil.toObject(dtoJson, TestObject.class);

                dto2Deserialized.add(dto2);
                jsonbOk++;
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        return dto2Deserialized;
    }

    @Benchmark
    public List<TestObject> testGson() throws Exception {
        // prevent JIT compiler code-optimization
        List<TestObject> dto2Deserialized = new ArrayList<>(rawJsonData.size());

        for (String rawJson : rawJsonData) {
            try {
                TestObject dto = JsonUtil.toObjectGson(rawJson, TestObject.class);

                String dtoJson = JsonUtil.toJsonGson(dto);

                TestObject dto2 = JsonUtil.toObjectGson(dtoJson, TestObject.class);

                dto2Deserialized.add(dto2);
                gsonOk++;
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        return dto2Deserialized;
    }

    @TearDown
    public void tearDown() {
        System.out.println("Tearing down");
        System.out.println("jsonbOk = " + jsonbOk);
        System.out.println("gsonOk = " + gsonOk);
    }

}
