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
package hu.icellmobilsoft.coffee.tool.protocol.handler;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

import org.apache.commons.lang3.StringUtils;

/**
 * Handling URL protocol for this format:
 *
 * <pre>
 * maven:hu.icellmobilsoft.coffee:coffee-dto-xsd:jar::!/xsd/hu/icellmobilsoft/coffee/dto/common/common.xsd
 * </pre>
 *
 * Format is: <code>maven:groupId:atifactId:package:version:!file_path</code>
 * <ul>
 * <li>protocol - URL schema protocol, in this case "maven"</li>
 * <li>hu.icellmobilsoft.coffee.dto.xsd - maven groupId</li>
 * <li>coffee-dto-xsd - maven artifactId</li>
 * <li>jar - maven package</li>
 * <li>maven version</li>
 * </ul>
 *
 * @author imre.scheffer
 * @since 1.0.0
 */
public class MavenURLHandler extends URLStreamHandler {

    private static final String SEPARATOR = "!";
    private static final String DIR_SEPARATOR = "/";

    /**
     * Default constructor, constructs a new object.
     */
    public MavenURLHandler() {
        super();
    }

    /** {@inheritDoc} */
    @Override
    protected URLConnection openConnection(URL url) throws IOException {
        String path = url.getPath();
        // We cut until the first "!" and then look for the remainder in the classpath.
        if (StringUtils.contains(path, SEPARATOR)) {
            path = StringUtils.substringAfter(path, SEPARATOR);
        }
        // Some runtime packaging (like Quarkus) may not be able to find the resource in the classpath.
        // Quarkus dev mode vs package works differently.
        // In dev mode, the resource is found in the classpath, but in the package needs to be searched for in the relative path.
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL classPathUrl = classLoader.getResource(path);
        if (classPathUrl == null && StringUtils.startsWith(path, DIR_SEPARATOR)) {
            // If it cannot be found in the classpath, try to search for it using the relative path
            classPathUrl = classLoader.getResource(StringUtils.substringAfter(path, DIR_SEPARATOR));
        }

        // Later on, you can simply search within the actual class.
        return classPathUrl.openConnection();
    }
}
