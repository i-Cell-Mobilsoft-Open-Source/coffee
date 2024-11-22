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
package hu.icellmobilsoft.coffee.cdi.logger;

import java.util.regex.Pattern;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * LogContainer logging tests
 * 
 * @author peter.szabo
 */
@DisplayName("LogContainer logging tests")
public class LogContainerLogTests {

    @Test
    @DisplayName("Test Trace level logs")
    public void testTraceLogs() {
        LogContainer logContainer = new LogContainer();
        String logLevel = "Trace";
        try {
            logContainer.trace(logLevel + " message");
            logContainer.trace(logLevel + " message with param:{0}", "param1");
            throw new Exception("Test exception");
        } catch (Exception e) {
            logContainer.trace(logLevel + " Exception log.", e);
        }
        testLog(logContainer, logLevel);
    }

    @Test
    @DisplayName("Test Debug level logs")
    public void testDebugLogs() {
        LogContainer logContainer = new LogContainer();
        String logLevel = "Debug";
        try {
            logContainer.debug(logLevel + " message");
            logContainer.debug(logLevel + " message with param:{0}", "param1");
            throw new Exception("Test exception");
        } catch (Exception e) {
            logContainer.debug(logLevel + " Exception log.", e);
        }
        testLog(logContainer, logLevel);
    }

    @Test
    @DisplayName("Test Info level logs")
    public void testInfoLogs() {
        LogContainer logContainer = new LogContainer();
        String logLevel = "Info";
        try {
            logContainer.info(logLevel + " message");
            logContainer.info(logLevel + " message with param:{0}", "param1");
            throw new Exception("Test exception");
        } catch (Exception e) {
            logContainer.info(logLevel + " Exception log.", e);
        }
        testLog(logContainer, logLevel);
    }

    @Test
    @DisplayName("Test Warn level logs")
    public void testWarnLogs() {
        LogContainer logContainer = new LogContainer();
        String logLevel = "Warn";
        try {
            logContainer.warn(logLevel + " message");
            logContainer.warn(logLevel + " message with param:{0}", "param1");
            throw new Exception("Test exception");
        } catch (Exception e) {
            logContainer.warn(logLevel + " Exception log.", e);
        }
        testLog(logContainer, logLevel);
    }

    @Test
    @DisplayName("Test Error level logs")
    public void testErrorLogs() {
        LogContainer logContainer = new LogContainer();
        String logLevel = "Error";
        try {
            logContainer.error(logLevel + " message");
            logContainer.error(logLevel + " message with param:{0}", "param1");
            throw new Exception("Test exception");
        } catch (Exception e) {
            logContainer.error(logLevel + " Exception log.", e);
        }
        testLog(logContainer, logLevel);
    }

    @Test
    @DisplayName("Test logs size")
    public void testLogsSize() {
        LogContainer logContainer = new LogContainer();
        for (int i = 0; i < 90_000; i++) {
            logContainer.info("Message [%d]", i);
        }

        String[] logContainerRows = logContainer.toString().split("\\n");
        Assertions.assertTrue(logContainerRows.length <= 10_000);
    }

    private void testLog(LogContainer logContainer, String logLevel) {
        String[] logContainerRows = logContainer.toString().split("\\n");
        Assertions.assertTrue(
                Pattern.matches("\\[\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d{0,9}\\] " + logLevel.toUpperCase() + ": " + logLevel + " message",
                        logContainerRows[0]));
        Assertions.assertTrue(Pattern.matches(
                "\\[\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d{0,9}\\] " + logLevel.toUpperCase() + ": " + logLevel + " message with param:param1",
                logContainerRows[1]));

        Assertions.assertTrue(Pattern.matches("\\[\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}\\.\\d{0,9}\\] " + logLevel.toUpperCase() + ": " + logLevel
                + " Exception log. > stacktrace: ", logContainerRows[2]));
        Assertions.assertTrue(logContainerRows[3].startsWith("[java.lang.Exception: Test exception"));
    }
}
