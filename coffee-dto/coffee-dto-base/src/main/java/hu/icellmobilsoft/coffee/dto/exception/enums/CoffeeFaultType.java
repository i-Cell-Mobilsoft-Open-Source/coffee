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
package hu.icellmobilsoft.coffee.dto.exception.enums;

import hu.icellmobilsoft.coffee.cdi.annotation.FaultTypeCode;

/**
 * Coffee jakartaEE solution set container for Exceptions type
 *
 * @author imre.scheffer
 * @since 1.0.0
 */
@FaultTypeCode
public enum CoffeeFaultType {

    /**
     * Generic, not defined exception
     */
    GENERIC_EXCEPTION,
    /**
     * Error code for java.lang.IllegalArgumentException
     */
    ILLEGAL_ARGUMENT_EXCEPTION,
    /**
     * Error code for javax.ws.rs.NotAllowedException
     */
    NOT_ALLOWED_EXCEPTION,
    /**
     * Error code for javax.ws.rs.NotAcceptableException
     */
    NOT_ACCEPTABLE_EXCEPTION,
    /**
     * Rest level exception
     */
    OPERATION_FAILED,
    /**
     * ServiceImpl level if we want concrete exception
     */
    ENTITY_NOT_FOUND,
    /**
     * Not authorized
     */
    NOT_AUTHORIZED,
    /**
     * Forbidden
     */
    FORBIDDEN,
    /**
     * ServiceImpl level if we want concrete exception
     */
    ENTITY_SAVE_FAILED,
    /**
     * ServiceImpl level if we want concrete exception
     */
    ENTITY_DELETE_FAILED,
    /**
     * DTO conversion fail
     * 
     * @deprecated use {@link hu.icellmobilsoft.coffee.se.api.exception.enums.CoffeeFaultType#DTO_CONVERSION_FAILED}
     */
    @Deprecated
    DTO_CONVERSION_FAILED,
    /**
     * Service call failed!
     */
    SERVICE_CALL_FAILED,
    /**
     * repositroy operation failed
     */
    REPOSITORY_FAILED,
    /**
     * redis operation failed
     */
    REDIS_OPERATION_FAILED,
    /**
     * WRONG_OR_MISSING_PARAMETERS
     */
    WRONG_OR_MISSING_PARAMETERS,
    /**
     * INVALID_USER_TOKEN
     */
    INVALID_USER_TOKEN,
    /**
     * INVALID_USER_STATE
     */
    INVALID_USER_STATE,
    /**
     * INVALID_USER
     */
    INVALID_USER,
    /**
     * INVALID_USERTYPE
     */
    INVALID_USERTYPE,
    /**
     * USER_NOT_FOUND
     */
    USER_NOT_FOUND,
    /**
     * INVALID_TOKEN
     */
    INVALID_TOKEN,
    /**
     * INVALID_TOKEN_TYPE
     */
    INVALID_TOKEN_TYPE,
    /**
     * no session
     */
    NO_SESSION,
    /**
     * Incorrect password!
     */
    INCORRECT_PASSWORD,
    /**
     * Email send error!
     */
    EMAIL_SEND_ERROR,
    /**
     * Password missing!
     **/
    PASSWORD_MISSING,
    /**
     * Invalid request!
     */
    INVALID_REQUEST,
    /**
     * Email already exists!
     */
    EMAIL_ALREADY_EXISTS,
    /**
     * Invalid login credential!
     */
    INVALID_LOGIN_CREDENTIALS,
    /**
     * User banned!
     */
    USER_BANNED,
    /**
     * User inactive!
     */
    USER_INACTIVE,
    /**
     * IP banned!
     */
    IP_BANNED,
    /**
     * Access denied!
     */
    ACCESS_DENIED,
    /**
     * User is missing!
     */
    MISSING_USER_ID,
    /**
     * Password required!
     */
    PASSWORD_REQUIRED,
    /**
     * The given password is too weak!
     */
    WEAK_PASSWORD,
    /**
     * The requested state is not allowed!
     */
    INVALID_STATE,
    /**
     * Invalid input!
     */
    INVALID_INPUT,
    /**
     * Unknown item!
     */
    UNKNOWN_ITEM,
    /**
     * Entity already exist!
     */
    ALREADY_EXIST,
    /**
     * CSV generate fault!
     */
    CSV_GENERATE_FAULT,
    /**
     * Decompression error
     */
    GZIP_DECOMPRESSION_ERROR,
    /**
     * Service unavailable
     */
    SERVICE_UNAVAILABLE,
    /**
     * Xml validation failed
     */
    INVALID_XML,
    /**
     * Invalid one time password
     */
    INVALID_ONE_TIME_PASSWORD,
    /**
     * Rest client exception
     */
    REST_CLIENT_EXCEPTION,
    /**
     * Optimistic lock exception
     */
    OPTIMISTIC_LOCK_EXCEPTION,
    /**
     * Document decipher(decoding) failed
     */
    FAILED_TO_DECIPHER_DOCUMENT,
    /**
     * Document cipher(encoding) failed
     */
    FAILED_TO_CIPHER_DOCUMENT,
}
