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
package hu.icellmobilsoft.coffee.grpc.metrics.api.constants;

/**
 * Grpc Metric constant definitions
 * 
 * @author Imre Scheffer
 * @since 2.5.0
 */
public interface IGrpcMetricConstant {

    /**
     * Grpc Metric Server constant definitions
     * 
     * @author Imre Scheffer
     * @since 2.5.0
     */
    interface Server {
        /**
         * Metric key of Coffee gRPC server communication: Count of requests received
         */
        String METADATA_NAME_REQUEST_RECEIVED = "coffee_grpc_server_requests_received_messages";
        /**
         * Metric key of Coffee gRPC server communication: Count of sent responses
         */
        String METADATA_NAME_RESPONSE_SENT = "coffee_grpc_server_responses_sent_messages";
        /**
         * Metric key of Coffee gRPC server communication: Request-Response durations in seconds
         */
        String METADATA_NAME_TIMER = "coffee_grpc_server_processing_duration_seconds";
    }

    /**
     * Grpc Metric Client constant definitions
     * 
     * @author Imre Scheffer
     * @since 2.5.0
     */
    interface Client {
        /**
         * Metric key of Coffee gRPC client communication: Count of sent responses
         */
        String METADATA_NAME_REQUEST_SENT = "coffee_grpc_client_requests_sent_messages";
        /**
         * Metric key of Coffee gRPC client communication: Count of requests received
         */
        String METADATA_NAME_RESPONSE_RECEIVED = "coffee_grpc_client_responses_received_messages";
        /**
         * Metric key of Coffee gRPC client communication: Request-Response durations in seconds
         */
        String METADATA_NAME_TIMER = "coffee_grpc_client_processing_duration_seconds";
    }

    /**
     * Grpc Metric Tag constant definitions
     * 
     * @author Imre Scheffer
     * @since 2.5.0
     */
    interface Tag {
        /**
         * Grpc communication {@value #TAG_METHOD}
         */
        String TAG_METHOD = "method";
        /**
         * Grpc communication {@value #TAG_METHOD_TYPE}
         */
        String TAG_METHOD_TYPE = "methodType";
        /**
         * Grpc communication {@value #TAG_SERVICE}
         */
        String TAG_SERVICE = "service";
        /**
         * Grpc communication {@value #TAG_STATUS}
         */
        String TAG_STATUS = "status";
    }
}
