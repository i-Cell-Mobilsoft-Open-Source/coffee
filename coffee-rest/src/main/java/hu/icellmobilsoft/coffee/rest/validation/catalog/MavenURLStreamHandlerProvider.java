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
package hu.icellmobilsoft.coffee.rest.validation.catalog;

import java.net.URLStreamHandler;
import java.net.spi.URLStreamHandlerProvider;

import hu.icellmobilsoft.coffee.se.logging.Logger;
import hu.icellmobilsoft.coffee.tool.protocol.handler.MavenURLHandler;

/**
 * "maven" URL protocol olvasashoz szolgalo Provider. Regisztralni szukseges a
 * <code>src/main/resources/META-INF/services/java.net.spi.URLStreamHandlerProvider<code> fajlban
 *
 * @author imre.scheffer
 * @since 1.0.0
 */
public class MavenURLStreamHandlerProvider extends URLStreamHandlerProvider {

    private static final Logger LOGGER = Logger.getLogger(MavenURLStreamHandlerProvider.class);

    private static String PREFIX = "sun.net.www.protocol";

    /** {@inheritDoc} */
    @Override
    public URLStreamHandler createURLStreamHandler(String protocol) {
        LOGGER.info("MavenURLStreamHandlerProvider registered");
        // valahogy meg kell szerezni az eredeti Handler osztalyokat, nem szep megoldas,
        // de nem mukodik a TT-ben sem a "java.protocol.handler.pkgs" settings,
        // sem a src/main/resources/META-INF/services/java.net.spi.URLStreamHandlerProvider megoldas sem,
        // mert a jboss indulasnal beolvas sajat ModularURLStreamHandlerFactory osztaly
        return "maven".equals(protocol) ? new MavenURLHandler() : createDefaultURLStreamHandler(protocol);
    }

    /**
     * {@code URL$DefaultFactory} osztaly masolata
     * 
     * @param protocol
     * 
     * @return
     */
    private URLStreamHandler createDefaultURLStreamHandler(String protocol) {
        String name = PREFIX + "." + protocol + ".Handler";
        try {
            Object o = Class.forName(name).getDeclaredConstructor().newInstance();
            return (URLStreamHandler) o;
        } catch (ClassNotFoundException x) {
            // ignore
        } catch (Exception e) {
            // For compatibility, all Exceptions are ignored.
            // any number of exceptions can get thrown here
        }
        return null;
    }

}
