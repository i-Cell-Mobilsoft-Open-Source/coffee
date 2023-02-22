/*-
 * #%L
 * Coffee
 * %%
 * Copyright (C) 2020 - 2022 i-Cell Mobilsoft Zrt.
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
/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.deltaspike.core.util;

import jakarta.enterprise.inject.Typed;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Vector;

/**
 * Copy from deltaspike project
 */
@Typed()
public abstract class PropertyFileUtils
{
    /**
     * Constructor which prevents the instantiation of this class
     */
    private PropertyFileUtils()
    {
        // prevent instantiation
    }

    /**
     * Resolve property files
     * @param propertyFileName property file name
     * @return files URL
     * @throws IOException if error
     */
    public static Enumeration<URL> resolvePropertyFiles(String propertyFileName) throws IOException
    {
        if (propertyFileName != null && (propertyFileName.contains("://") || propertyFileName.startsWith("file:")))
        {
            // the given string is actually already an URL
            Vector<URL> propertyFileUrls = new Vector<URL>();
            URL url = new URL(propertyFileName);

            if (propertyFileName.startsWith("file:"))
            {
                try
                {
                    File file = new File(url.toURI());
                    if (file.exists())
                    {
                        propertyFileUrls.add(url);
                    }
                }
                catch (URISyntaxException e)
                {
                    throw new IllegalStateException("Property file URL is malformed", e);
                }
            }
            else
            {
                propertyFileUrls.add(url);
            }

            return propertyFileUrls.elements();
        }

        if (propertyFileName != null)
        {
            File file = new File(propertyFileName);
            if (file.exists())
            {
                return Collections.enumeration(Collections.singleton(file.toURI().toURL()));
            }
        }

        ClassLoader cl = ClassUtils.getClassLoader(null);

        Enumeration<URL> propertyFileUrls = cl.getResources(propertyFileName);

        //fallback - see DELTASPIKE-98
        if (!propertyFileUrls.hasMoreElements())
        {
            cl = PropertyFileUtils.class.getClassLoader();
            propertyFileUrls = cl.getResources(propertyFileName);
        }

        return propertyFileUrls;
    }

    /**
     * Load properties by URL
     * @param url url
     * @return properties
     */
    public static Properties loadProperties(URL url)
    {
        Properties props = new Properties();

        InputStream inputStream = null;
        try
        {
            inputStream = url.openStream();

            if (inputStream != null)
            {
                props.load(inputStream);
            }
        }
        catch (IOException e)
        {
            throw new IllegalStateException(e);
        }
        finally
        {
            try
            {
                if (inputStream != null)
                {
                    inputStream.close();
                }
            }
            catch (IOException e)
            {
                // no worries, means that the file is already closed
            }
        }

        return props;
    }

    /**
     * @param bundleName bundle name
     * @return the ResourceBundle for the current default Locale
     */
    public static ResourceBundle getResourceBundle(String bundleName)
    {
        return getResourceBundle(bundleName, Locale.getDefault());
    }

    /**
     * This uses the correct ThreadContextClassLoader if deployed in an Container.
     * @param bundleName bundle name
     * @param locale locale
     * @return the ResourceBundle for the current Locale
     */
    public static ResourceBundle getResourceBundle(String bundleName, Locale locale)
    {
        return ResourceBundle.getBundle(bundleName, locale, ClassUtils.getClassLoader(null));
    }
}
