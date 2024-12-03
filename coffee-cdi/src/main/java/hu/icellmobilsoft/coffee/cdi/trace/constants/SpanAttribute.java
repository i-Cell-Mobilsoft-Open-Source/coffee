/*-
 * #%L
 * Coffee
 * %%
 * Copyright (C) 2020 - 2021 i-Cell Mobilsoft Zrt.
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
package hu.icellmobilsoft.coffee.cdi.trace.constants;

/**
 *
 * Used for open-telemetry span attribute values
 *
 * @author czenczl
 * @since 2.5.0
 *
 */
public interface SpanAttribute {

    // We use the open telemetry-based values due to loose coupling.
    // https://github.com/open-telemetry/opentelemetry-java/blob/main/api/all/src/main/java/io/opentelemetry/api/trace/SpanKind.java
    /**
     * Default value. Indicates that the span is used internally.
     */
    String INTERNAL = "INTERNAL";

    /**
     * Indicates that the span covers server-side handling of an RPC or other remote request.
     */
    String SERVER = "SERVER";

    /**
     * Indicates that the span covers the client-side wrapper around an RPC or other remote request.
     */
    String CLIENT = "CLIENT";

    /**
     * Indicates that the span describes producer sending a message to a broker. Unlike client and server, there is no direct critical path latency
     * relationship between producer and consumer spans.
     */
    String PRODUCER = "PRODUCER";

    /**
     * Indicates that the span describes consumer receiving a message from a broker. Unlike client and server, there is no direct critical path
     * latency relationship between producer and consumer spans.
     */
    String CONSUMER = "CONSUMER";

    /**
     * component key for telemetry
     */
    String COMPONENT_KEY = "component";

    /**
     * db type key for telemetry
     */
    String DB_TYPE_KEY = "db.type";

    /**
     * Default redis span attribute
     */
    interface Redis {

        /**
         * Redis db type span attribute ({@value})
         */
        String DB_TYPE = "redis";

        /**
         * Redis Stream trace keys
         */
        interface Stream {
            /**
             * Redis trace kind span attribute ({@value})
             */
            String KIND = CONSUMER;
            /**
             * Redis trace component span attribute ({@value})
             */
            String COMPONENT = "redis-stream";
        }

        /**
         * Redis Jedis trace keys
         */
        interface Jedis {
            /**
             * Redis trace KIND span attribute({@value})
             */
            String KIND = CLIENT;
            /**
             * Redis trace component span attribute({@value})
             */
            String COMPONENT = "jedis";
        }

    }

    /**
     * Default Etcd trace span attribute
     */
    interface Etcd {

        /**
         * Etcd type trace span attribute({@value})
         */
        String DB_TYPE = "etcd";

        /**
         * Etcd Stream trace keys
         */
        interface Jetcd {
            /**
             * Etcd trace kind span attribute ({@value})
             */
            String KIND = CLIENT;
            /**
             * Etcd trace component span attribute ({@value}) for io.etcd:jetcd-core driver
             */
            String COMPONENT = "jetcd";
        }
    }

    /**
     * Default gRPC trace span attribute
     *
     * @since 2.1.0
     */
    interface Grpc {
        /**
         * gRPC trace component span attribute ({@value})
         */
        String COMPONENT = "gRPC";
    }

    /**
     * Default Relational database span attribute
     *
     * @since 2.1.0
     */
    interface Database {
        /**
         * Database trace component span attribute ({@value})
         */
        String DB_TYPE = "relational";

        /**
         * Database trace kind span attribute ({@value})
         */
        String KIND = CLIENT;

        /**
         * Database trace component span attribute ({@value})
         */
        String COMPONENT = "database";
    }

    /**
     * Java span attribute.
     *
     * @since 2.10.0
     */
    interface Java {
        /**
         * Java trace KIND span attribute ({@value}
         */
        String KIND = "java";
    }

}
