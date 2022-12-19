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
package hu.icellmobilsoft.coffee.jpa.service;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

import org.apache.commons.lang3.StringUtils;

import hu.icellmobilsoft.coffee.cdi.logger.AppLogger;
import hu.icellmobilsoft.coffee.cdi.logger.ThisLogger;
import hu.icellmobilsoft.coffee.cdi.util.ProxyUtils;
import hu.icellmobilsoft.coffee.dto.exception.BONotFoundException;
import hu.icellmobilsoft.coffee.dto.exception.BaseException;
import hu.icellmobilsoft.coffee.dto.exception.InvalidParameterException;
import hu.icellmobilsoft.coffee.dto.exception.TechnicalException;
import hu.icellmobilsoft.coffee.dto.exception.enums.CoffeeFaultType;
import hu.icellmobilsoft.coffee.tool.common.FunctionalInterfaces.BaseExceptionFunction;
import hu.icellmobilsoft.coffee.tool.common.FunctionalInterfaces.BaseExceptionFunction2;
import hu.icellmobilsoft.coffee.tool.common.FunctionalInterfaces.BaseExceptionFunction3;
import hu.icellmobilsoft.coffee.tool.common.FunctionalInterfaces.BaseExceptionFunction4;
import hu.icellmobilsoft.coffee.tool.common.FunctionalInterfaces.BaseExceptionFunction5;
import hu.icellmobilsoft.coffee.tool.common.FunctionalInterfaces.BaseExceptionSupplier;

/**
 * <p>
 * BaseService class.
 * </p>
 *
 * @author imre.scheffer
 * @param <T>
 *            entity
 * @since 1.0.0
 */
@Dependent
public class BaseService<T> {

    @SuppressWarnings("cdi-ambiguous-dependency")
    @Inject
    private EntityManager em;

    @Inject
    @ThisLogger
    private AppLogger log;

    /**
     * Overridable method for default not found fault type.
     *
     * @return ENTITY_NOT_FOUND enum
     */
    protected Enum<?> getDefaultNotFoundFaultTypeEnum() {
        return CoffeeFaultType.ENTITY_NOT_FOUND;
    }

    /**
     * Find entity by id and class.
     *
     * @param id
     *            String type id of the entity
     * @param clazz
     *            Entity class
     * @return entity
     * @throws BaseException
     *             exception
     */
    public T findById(String id, Class<T> clazz) throws BaseException {
        if (StringUtils.isBlank(id) || clazz == null) {
            log.warn("Entity Id is blank or clazz is null skipped to load!");
            throw new InvalidParameterException("id is blank or clazz is null!");
        }
        log.trace(">> BaseService.findById(id: [{0}], class: [{1}])", id, clazz.getCanonicalName());
        T entity = null;
        try {
            try {
                entity = getEntityManager().find(clazz, id);
            } catch (Exception e) {
                String msg = MessageFormat.format("Error occured in finding class: [{0}] by id: [{1}]: [{2}]", clazz.getCanonicalName(), id,
                        e.getLocalizedMessage());
                log.error(msg, e);
                throw new TechnicalException(CoffeeFaultType.REPOSITORY_FAILED, msg, e);
            }
            if (entity == null) {
                throw new BONotFoundException(getDefaultNotFoundFaultTypeEnum(),
                        MessageFormat.format("Entity id: [{0}] for class: [{1}] not found", id, clazz.getCanonicalName()));
            }
            return entity;
        } finally {
            log.trace("<< BaseService.findById(id: [{0}], class: [{1}])", id, clazz.getCanonicalName());
        }
    }

    /**
     * Find optional entity by id and class.
     *
     * @param id
     *            String type id of the entity
     * @param clazz
     *            Entity class
     * @return Optional entity (empty if entity is not found)
     * @throws BaseException
     *             exception
     */
    public Optional<T> findOptionalById(String id, Class<T> clazz) throws BaseException {
        if (StringUtils.isBlank(id) || clazz == null) {
            log.warn("Entity Id is blank or clazz is null skipped to load!");
            throw new InvalidParameterException("id is blank or clazz is null!");
        }
        log.trace(">> BaseService.findOptionalById(id: [{0}], class: [{1}])", id, clazz.getCanonicalName());
        T entity = null;
        try {
            try {
                entity = getEntityManager().find(clazz, id);
            } catch (Exception e) {
                String msg = MessageFormat.format("Error occured in finding class: [{0}] by id: [{1}]: [{2}]", clazz.getCanonicalName(), id,
                        e.getLocalizedMessage());
                log.error(msg, e);
                throw new TechnicalException(CoffeeFaultType.REPOSITORY_FAILED, msg, e);
            }
            return Optional.ofNullable(entity);
        } finally {
            log.trace("<< BaseService.findOptionalById(id: [{0}], class: [{1}])", id, clazz.getCanonicalName());
        }
    }

    /**
     * Find all entity by class.
     *
     * @param clazz
     *            Entity class
     * @return entity
     * @throws BaseException
     *             exception
     */
    public List<T> findAll(Class<T> clazz) throws BaseException {
        log.trace(">> BaseService.findAll(class: [{0}])", clazz.getCanonicalName());
        try {
            try {
                return getEntityManager().createQuery("FROM " + clazz.getSimpleName(), clazz).getResultList();
            } catch (Exception e) {
                String msg = MessageFormat.format("Error occured in finding all class: [{0}] : [{1}]", clazz.getCanonicalName(),
                        e.getLocalizedMessage());
                log.error(msg, e);
                throw new TechnicalException(CoffeeFaultType.REPOSITORY_FAILED, msg, e);
            }
        } finally {
            log.trace("<< BaseService.findAll(class: [{0}])", clazz.getCanonicalName());
        }
    }

    /**
     * Transaction required!
     *
     * @param entity
     *            entity to save
     * @return saved entity
     * @throws BaseException
     *             exception
     */
    public T save(T entity) throws BaseException {
        if (entity == null) {
            log.warn("Entity is null skipped to save!");
            throw new InvalidParameterException("entity is null!");
        }
        String entityName = entity.getClass().getSimpleName();
        log.debug(">> save([{0}]: [{1}]", entityName, entity);
        T savedEntity = null;
        try {
            savedEntity = getEntityManager().merge(entity);
            getEntityManager().flush();
            getEntityManager().refresh(savedEntity);
            log.debug("[{0}] entity has been saved", entityName);
            return savedEntity;
        } catch (Exception e) {
            String msg = MessageFormat.format("Error in saving [{0}]: [{1}]", entityName, e.getLocalizedMessage());
            log.error(msg, e);
            throw new TechnicalException(CoffeeFaultType.ENTITY_SAVE_FAILED, msg, e);
        } finally {
            log.debug("<< save([{0}]: [{1}]", entityName, savedEntity == null ? entity : savedEntity);
        }
    }

    /**
     * Refresh entity.
     *
     * @param entity
     *            entity to refresh
     * @return refreshed entity
     * @throws BaseException
     *             exception
     */
    public T refresh(T entity) throws BaseException {
        if (entity == null) {
            log.warn("Entity is null skipped to refresh!");
            throw new InvalidParameterException("entity is null!");
        }
        String entityName = entity.getClass().getSimpleName();
        log.debug(">> refresh([{0}]: [{1}]", entityName, entity);
        try {
            getEntityManager().refresh(entity);
            log.debug("[{0}] entity has been refreshed", entityName);
            return entity;
        } catch (Exception e) {
            String msg = MessageFormat.format("Error in refresh [{0}]: [{1}]", entityName, e.getLocalizedMessage());
            log.error(msg, e);
            throw new TechnicalException(CoffeeFaultType.REPOSITORY_FAILED, msg, e);
        } finally {
            log.debug("<< refresh([{0}]: [{1}]", entityName, entity);
        }
    }

    /**
     * Delete entity. Transaction required!
     *
     * @param entity
     *            entity to delete
     * @throws BaseException
     *             exception
     */
    public void delete(T entity) throws BaseException {
        if (entity == null) {
            log.warn("Entity is null skipped to delete!");
            throw new InvalidParameterException("entity is null!");
        }
        String entityName = entity.getClass().getSimpleName();
        log.debug(">> delete([{0}]: [{1}]", entityName, entity);
        try {
            getEntityManager().remove(entity);
            getEntityManager().flush();
            log.debug("[{0}] entity has been deleted", entityName);
        } catch (Exception e) {
            String msg = MessageFormat.format("Error in deleting [{0}]: [{1}]", entityName, e.getLocalizedMessage());
            log.error(msg, e);
            throw new TechnicalException(CoffeeFaultType.ENTITY_DELETE_FAILED, msg, e);
        } finally {
            log.debug("<< delete([{0}]: [{1}]", entityName, entity);
        }
    }

    /**
     * Getter of {@link EntityManager}.
     *
     * @return {@link EntityManager}
     */
    protected EntityManager getEntityManager() {
        return em;
    }

    /**
     * Transforms given {@link String} to a like parameter.
     *
     * @param string
     *            input {@link String}
     * @return the input completed to a like parameter
     */
    public static String likeParameter(String string) {
        StringBuffer sb = new StringBuffer();
        sb.append("%").append(string).append("%");
        return sb.toString();
    }

    /**
     * Transforms given {@link String} to an after like parameter.
     *
     * @param string
     *            input {@link String}
     * @return the input completed to an after like parameter
     */
    public static String afterLikeParameter(String string) {
        StringBuffer sb = new StringBuffer();
        sb.append(string).append("%");
        return sb.toString();
    }

    /**
     * Returns {@link BaseException} with {@link CoffeeFaultType#WRONG_OR_MISSING_PARAMETERS} and given message.
     *
     * @param msg
     *            exception message
     * @return {@link BaseException}
     * @deprecated Use {@link InvalidParameterException} instead, forRemoval = true, since = "1.13.0"
     */
    @Deprecated(forRemoval = true, since = "1.13.0")
    public static BaseException newInvalidParameterException(String msg) {
        return new BaseException(CoffeeFaultType.WRONG_OR_MISSING_PARAMETERS, msg);
    }

    /**
     * Wraps call to parameter-less method.
     *
     * @param function
     *            the function doing business logic
     * @param methodName
     *            method name to log
     * @param <RESPONSE>
     *            response object type
     * @return what the function returns
     * @throws BaseException
     *             in case of any exception caught inside
     */
    protected <RESPONSE> RESPONSE wrap(BaseExceptionSupplier<RESPONSE> function, String methodName) throws BaseException {
        String methodInfo = getCalledMethodWithOnlyPathParams(methodName);
        logEnter(methodInfo);
        try {
            return function.get();
        } catch (NoResultException e) {
            throw notFound(methodInfo);
        } catch (Exception e) {
            throw repositoryFailed(e, methodInfo);
        } finally {
            logReturn(methodInfo);
        }
    }

    /**
     * Wraps call to parameter-less method with {@link Optional} response.
     *
     * @param function
     *            the function doing business logic
     * @param methodName
     *            method name to log
     * @param <RESPONSE>
     *            response object type
     * @return what the function returns wrapped in {@link Optional}
     * @throws BaseException
     *             in case of any exception caught inside
     */
    protected <RESPONSE> Optional<RESPONSE> wrapOptional(BaseExceptionSupplier<RESPONSE> function, String methodName) throws BaseException {
        String methodInfo = getCalledMethodWithOnlyPathParams(methodName);
        logEnter(methodInfo);
        try {
            return Optional.ofNullable(function.get());
        } catch (NoResultException e) {
            return Optional.empty();
        } catch (Exception e) {
            throw repositoryFailed(e, methodInfo);
        } finally {
            logReturn(methodInfo);
        }
    }

    private <P1, RESPONSE> RESPONSE wrap(BaseExceptionFunction<P1, RESPONSE> function, P1 p1, String methodName, String p1Name, boolean validate)
            throws BaseException {
        String methodInfo = getCalledMethodWithOnlyPathParams(methodName, p1Name);
        logEnter(methodInfo, p1);
        if (validate) {
            if (isNullOrBlankAnyParameter(p1)) {
                throw invalidParameter(methodInfo, p1);
            }
        }
        try {
            return function.apply(p1);
        } catch (NoResultException e) {
            throw notFound(methodInfo, p1);
        } catch (Exception e) {
            throw repositoryFailed(e, methodInfo, p1);
        } finally {
            logReturn(methodInfo, p1);
        }
    }

    private <P1, RESPONSE> Optional<RESPONSE> wrapOptional(BaseExceptionFunction<P1, RESPONSE> function, P1 p1, String methodName, String p1Name,
            boolean validate) throws BaseException {
        String methodInfo = getCalledMethodWithOnlyPathParams(methodName, p1Name);
        logEnter(methodInfo, p1);
        if (validate) {
            if (isNullOrBlankAnyParameter(p1)) {
                throw invalidParameter(methodInfo, p1);
            }
        }
        try {
            return Optional.ofNullable(function.apply(p1));
        } catch (NoResultException e) {
            return Optional.empty();
        } catch (Exception e) {
            throw repositoryFailed(e, methodInfo, p1);
        } finally {
            logReturn(methodInfo, p1);
        }
    }

    private <P1, RESPONSE> List<RESPONSE> wrapList(BaseExceptionFunction<P1, List<RESPONSE>> function, P1 p1, String methodName, String p1Name,
            boolean validate) throws BaseException {
        String methodInfo = getCalledMethodWithOnlyPathParams(methodName, p1Name);
        logEnter(methodInfo, p1);
        if (isNullOrBlankAnyParameter(p1)) {
            if (validate) {
                throw invalidParameter(methodInfo, p1);
            }
        }
        try {
            return function.apply(p1);
        } catch (Exception e) {
            throw repositoryFailed(e, methodInfo, p1);
        } finally {
            logReturn(methodInfo, p1);
        }
    }

    private <P1, P2, RESPONSE> RESPONSE wrap(BaseExceptionFunction2<P1, P2, RESPONSE> function, P1 p1, P2 p2, String methodName, String p1Name,
            String p2Name, boolean validate) throws BaseException {
        String methodInfo = getCalledMethodWithOnlyPathParams(methodName, p1Name, p2Name);
        logEnter(methodInfo, p1, p2);
        if (validate) {
            if (isNullOrBlankAnyParameter(p1, p2)) {
                throw invalidParameter(methodInfo, p1, p2);
            }
        }
        try {
            return function.apply(p1, p2);
        } catch (NoResultException e) {
            throw notFound(methodInfo, p1, p2);
        } catch (Exception e) {
            throw repositoryFailed(e, methodInfo, p1, p2);
        } finally {
            logReturn(methodInfo, p1, p2);
        }
    }

    private <P1, P2, RESPONSE> Optional<RESPONSE> wrapOptional(BaseExceptionFunction2<P1, P2, RESPONSE> function, P1 p1, P2 p2, String methodName,
            String p1Name, String p2Name, boolean validate) throws BaseException {
        String methodInfo = getCalledMethodWithOnlyPathParams(methodName, p1Name, p2Name);
        logEnter(methodInfo, p1, p2);
        if (validate) {
            if (isNullOrBlankAnyParameter(p1, p2)) {
                throw invalidParameter(methodInfo, p1, p2);
            }
        }
        try {
            return Optional.ofNullable(function.apply(p1, p2));
        } catch (NoResultException e) {
            return Optional.empty();
        } catch (Exception e) {
            throw repositoryFailed(e, methodInfo, p1, p2);
        } finally {
            logReturn(methodInfo, p1, p2);
        }
    }

    private <P1, P2, RESPONSE> List<RESPONSE> wrapList(BaseExceptionFunction2<P1, P2, List<RESPONSE>> function, P1 p1, P2 p2, String methodName,
            String p1Name, String p2Name, boolean validate) throws BaseException {
        String methodInfo = getCalledMethodWithOnlyPathParams(methodName, p1Name, p2Name);
        logEnter(methodInfo, p1, p2);
        if (isNullOrBlankAnyParameter(p1, p2)) {
            if (validate) {
                throw invalidParameter(methodInfo, p1, p2);
            }
        }
        try {
            return function.apply(p1, p2);
        } catch (Exception e) {
            throw repositoryFailed(e, methodInfo, p1, p2);
        } finally {
            logReturn(methodInfo, p1, p2);
        }
    }

    private <P1, P2, P3, RESPONSE> RESPONSE wrap(BaseExceptionFunction3<P1, P2, P3, RESPONSE> function, P1 p1, P2 p2, P3 p3, String methodName,
            String p1Name, String p2Name, String p3Name, boolean validate) throws BaseException {
        String methodInfo = getCalledMethodWithOnlyPathParams(methodName, p1Name, p2Name, p3Name);
        logEnter(methodInfo, p1, p2, p3);
        if (validate) {
            if (isNullOrBlankAnyParameter(p1, p2, p3)) {
                throw invalidParameter(methodInfo, p1, p2, p3);
            }
        }
        try {
            return function.apply(p1, p2, p3);
        } catch (NoResultException e) {
            throw notFound(methodInfo, p1, p2, p3);
        } catch (Exception e) {
            throw repositoryFailed(e, methodInfo, p1, p2, p3);
        } finally {
            logReturn(methodInfo, p1, p2, p3);
        }
    }

    private <P1, P2, P3, RESPONSE> Optional<RESPONSE> wrapOptional(BaseExceptionFunction3<P1, P2, P3, RESPONSE> function, P1 p1, P2 p2, P3 p3,
            String methodName, String p1Name, String p2Name, String p3Name, boolean validate) throws BaseException {
        String methodInfo = getCalledMethodWithOnlyPathParams(methodName, p1Name, p2Name, p3Name);
        logEnter(methodInfo, p1, p2, p3);
        if (validate) {
            if (isNullOrBlankAnyParameter(p1, p2, p3)) {
                throw invalidParameter(methodInfo, p1, p2, p3);
            }
        }
        try {
            return Optional.ofNullable(function.apply(p1, p2, p3));
        } catch (NoResultException e) {
            return Optional.empty();
        } catch (Exception e) {
            throw repositoryFailed(e, methodInfo, p1, p2, p3);
        } finally {
            logReturn(methodInfo, p1, p2, p3);
        }
    }

    private <P1, P2, P3, RESPONSE> List<RESPONSE> wrapList(BaseExceptionFunction3<P1, P2, P3, List<RESPONSE>> function, P1 p1, P2 p2, P3 p3,
            String methodName, String p1Name, String p2Name, String p3Name, boolean validate) throws BaseException {
        String methodInfo = getCalledMethodWithOnlyPathParams(methodName, p1Name, p2Name, p3Name);
        logEnter(methodInfo, p1, p2, p3);
        if (isNullOrBlankAnyParameter(p1, p2, p3)) {
            if (validate) {
                throw invalidParameter(methodInfo, p1, p2, p3);
            }
        }
        try {
            return function.apply(p1, p2, p3);
        } catch (Exception e) {
            throw repositoryFailed(e, methodInfo, p1, p2, p3);
        } finally {
            logReturn(methodInfo, p1, p2, p3);
        }
    }

    private <P1, P2, P3, P4, RESPONSE> RESPONSE wrap(BaseExceptionFunction4<P1, P2, P3, P4, RESPONSE> function, P1 p1, P2 p2, P3 p3, P4 p4,
            String methodName, String p1Name, String p2Name, String p3Name, String p4Name, boolean validate) throws BaseException {
        String methodInfo = getCalledMethodWithOnlyPathParams(methodName, p1Name, p2Name, p3Name, p4Name);
        logEnter(methodInfo, p1, p2, p3, p4);
        if (validate) {
            if (isNullOrBlankAnyParameter(p1, p2, p3, p4)) {
                throw invalidParameter(methodInfo, p1, p2, p3, p4);
            }
        }
        try {
            return function.apply(p1, p2, p3, p4);
        } catch (NoResultException e) {
            throw notFound(methodInfo, p1, p2, p3, p4);
        } catch (Exception e) {
            throw repositoryFailed(e, methodInfo, p1, p2, p3, p4);
        } finally {
            logReturn(methodInfo, p1, p2, p3, p4);
        }
    }

    private <P1, P2, P3, P4, RESPONSE> Optional<RESPONSE> wrapOptional(BaseExceptionFunction4<P1, P2, P3, P4, RESPONSE> function, P1 p1, P2 p2, P3 p3,
            P4 p4, String methodName, String p1Name, String p2Name, String p3Name, String p4Name, boolean validate) throws BaseException {
        String methodInfo = getCalledMethodWithOnlyPathParams(methodName, p1Name, p2Name, p3Name, p4Name);
        logEnter(methodInfo, p1, p2, p3, p4);
        if (validate) {
            if (isNullOrBlankAnyParameter(p1, p2, p3, p4)) {
                throw invalidParameter(methodInfo, p1, p2, p3, p4);
            }
        }
        try {
            return Optional.ofNullable(function.apply(p1, p2, p3, p4));
        } catch (NoResultException e) {
            return Optional.empty();
        } catch (Exception e) {
            throw repositoryFailed(e, methodInfo, p1, p2, p3, p4);
        } finally {
            logReturn(methodInfo, p1, p2, p3, p4);
        }
    }

    private <P1, P2, P3, P4, RESPONSE> List<RESPONSE> wrapList(BaseExceptionFunction4<P1, P2, P3, P4, List<RESPONSE>> function, P1 p1, P2 p2, P3 p3,
            P4 p4, String methodName, String p1Name, String p2Name, String p3Name, String p4Name, boolean validate) throws BaseException {
        String methodInfo = getCalledMethodWithOnlyPathParams(methodName, p1Name, p2Name, p3Name, p4Name);
        logEnter(methodInfo, p1, p2, p3, p4);
        if (isNullOrBlankAnyParameter(p1, p2, p3, p4)) {
            if (validate) {
                throw invalidParameter(methodInfo, p1, p2, p3, p4);
            }
        }
        try {
            return function.apply(p1, p2, p3, p4);
        } catch (Exception e) {
            throw repositoryFailed(e, methodInfo, p1, p2, p3, p4);
        } finally {
            logReturn(methodInfo, p1, p2, p3, p4);
        }
    }

    private <P1, P2, P3, P4, P5, RESPONSE> RESPONSE wrap(BaseExceptionFunction5<P1, P2, P3, P4, P5, RESPONSE> function, P1 p1, P2 p2, P3 p3, P4 p4,
            P5 p5, String methodName, String p1Name, String p2Name, String p3Name, String p4Name, String p5Name, boolean validate)
            throws BaseException {
        String methodInfo = getCalledMethodWithOnlyPathParams(methodName, p1Name, p2Name, p3Name, p4Name, p5Name);
        logEnter(methodInfo, p1, p2, p3, p4, p5);
        if (validate) {
            if (isNullOrBlankAnyParameter(p1, p2, p3, p4, p5)) {
                throw invalidParameter(methodInfo, p1, p2, p3, p4, p5);
            }
        }
        try {
            return function.apply(p1, p2, p3, p4, p5);
        } catch (NoResultException e) {
            throw notFound(methodInfo, p1, p2, p3, p4, p5);
        } catch (Exception e) {
            throw repositoryFailed(e, methodInfo, p1, p2, p3, p4, p5);
        } finally {
            logReturn(methodInfo, p1, p2, p3, p4, p5);
        }
    }

    private <P1, P2, P3, P4, P5, RESPONSE> Optional<RESPONSE> wrapOptional(BaseExceptionFunction5<P1, P2, P3, P4, P5, RESPONSE> function, P1 p1,
            P2 p2, P3 p3, P4 p4, P5 p5, String methodName, String p1Name, String p2Name, String p3Name, String p4Name, String p5Name,
            boolean validate) throws BaseException {
        String methodInfo = getCalledMethodWithOnlyPathParams(methodName, p1Name, p2Name, p3Name, p4Name, p5Name);
        logEnter(methodInfo, p1, p2, p3, p4, p5);
        if (validate) {
            if (isNullOrBlankAnyParameter(p1, p2, p3, p4, p5)) {
                throw invalidParameter(methodInfo, p1, p2, p3, p4, p5);
            }
        }
        try {
            return Optional.ofNullable(function.apply(p1, p2, p3, p4, p5));
        } catch (NoResultException e) {
            return Optional.empty();
        } catch (Exception e) {
            throw repositoryFailed(e, methodInfo, p1, p2, p3, p4, p5);
        } finally {
            logReturn(methodInfo, p1, p2, p3, p4, p5);
        }
    }

    private <P1, P2, P3, P4, P5, RESPONSE> List<RESPONSE> wrapList(BaseExceptionFunction5<P1, P2, P3, P4, P5, List<RESPONSE>> function, P1 p1, P2 p2,
            P3 p3, P4 p4, P5 p5, String methodName, String p1Name, String p2Name, String p3Name, String p4Name, String p5Name, boolean validate)
            throws BaseException {
        String methodInfo = getCalledMethodWithOnlyPathParams(methodName, p1Name, p2Name, p3Name, p4Name, p5Name);
        logEnter(methodInfo, p1, p2, p3, p4, p5);
        if (isNullOrBlankAnyParameter(p1, p2, p3, p4, p5)) {
            if (validate) {
                throw invalidParameter(methodInfo, p1, p2, p3, p4, p5);
            }
        }
        try {
            return function.apply(p1, p2, p3, p4, p5);
        } catch (Exception e) {
            throw repositoryFailed(e, methodInfo, p1, p2, p3, p4, p5);
        } finally {
            logReturn(methodInfo, p1, p2, p3, p4, p5);
        }
    }

    /**
     * Wraps the business logic method call in order to handle common logging and exception handling with one parameter.
     *
     * @param function
     *            the function doing business logic
     * @param p1
     *            parameter of the function call
     * @param methodName
     *            method name to log
     * @param p1Name
     *            parameter name to log
     * @param <RESPONSE>
     *            response object type
     * @param <P1>
     *            type of the parameter
     * @return what the function returns
     * @throws BaseException
     *             in case of any exception caught inside
     */
    protected <P1, RESPONSE> RESPONSE wrap(BaseExceptionFunction<P1, RESPONSE> function, P1 p1, String methodName, String p1Name)
            throws BaseException {
        return wrap(function, p1, methodName, p1Name, false);
    }

    /**
     * Wraps the business logic method call in order to handle common logging and exception handling with one non-empty parameter.
     *
     * @param function
     *            the function doing business logic
     * @param p1
     *            parameter of the function call
     * @param methodName
     *            method name to log
     * @param p1Name
     *            parameter name to log
     * @param <RESPONSE>
     *            response object type
     * @param <P1>
     *            type of the parameter
     * @return what the function returns
     * @throws BaseException
     *             in case of any exception caught inside
     */
    protected <P1, RESPONSE> RESPONSE wrapValidated(BaseExceptionFunction<P1, RESPONSE> function, P1 p1, String methodName, String p1Name)
            throws BaseException {
        return wrap(function, p1, methodName, p1Name, true);
    }

    /**
     * Wraps the business logic method call in order to handle common logging and exception handling with one parameter and an {@link Optional}
     * response.
     *
     * @param function
     *            the function doing business logic
     * @param p1
     *            parameter of the function call
     * @param methodName
     *            method name to log
     * @param p1Name
     *            parameter name to log
     * @param <RESPONSE>
     *            response object type
     * @param <P1>
     *            type of the parameter
     * @return what the function returns
     * @throws BaseException
     *             in case of any exception caught inside
     */
    protected <P1, RESPONSE> Optional<RESPONSE> wrapOptional(BaseExceptionFunction<P1, RESPONSE> function, P1 p1, String methodName, String p1Name)
            throws BaseException {
        return wrapOptional(function, p1, methodName, p1Name, false);
    }

    /**
     * Wraps the business logic method call in order to handle common logging and exception handling with one non-empty parameter and an
     * {@link Optional} response.
     *
     * @param function
     *            the function doing business logic
     * @param p1
     *            parameter of the function call
     * @param methodName
     *            method name to log
     * @param p1Name
     *            parameter name to log
     * @param <RESPONSE>
     *            response object type
     * @param <P1>
     *            type of the parameter
     * @return what the function returns
     * @throws BaseException
     *             in case of any exception caught inside
     */
    protected <P1, RESPONSE> Optional<RESPONSE> wrapOptionalValidated(BaseExceptionFunction<P1, RESPONSE> function, P1 p1, String methodName,
            String p1Name) throws BaseException {
        return wrapOptional(function, p1, methodName, p1Name, true);
    }

    /**
     * Wraps the business logic method call in order to handle common logging and exception handling with one parameter and a {@link List} response
     *
     * @param function
     *            the function doing business logic
     * @param p1
     *            parameter of the function call
     * @param methodName
     *            method name to log
     * @param p1Name
     *            parameter name to log
     * @param <RESPONSE>
     *            response object type
     * @param <P1>
     *            type of the parameter
     * @return what the function returns
     * @throws BaseException
     *             in case of any exception caught inside
     */
    protected <P1, RESPONSE> List<RESPONSE> wrapList(BaseExceptionFunction<P1, List<RESPONSE>> function, P1 p1, String methodName, String p1Name)
            throws BaseException {
        return wrapList(function, p1, methodName, p1Name, false);
    }

    /**
     * Wraps the business logic method call in order to handle common logging and exception handling with one non-empty parameter and a {@link List}
     * response.
     *
     * @param function
     *            the function doing business logic
     * @param p1
     *            parameter of the function call
     * @param methodName
     *            method name to log
     * @param p1Name
     *            parameter name to log
     * @param <RESPONSE>
     *            response object type
     * @param <P1>
     *            type of the parameter
     * @return what the function returns
     * @throws BaseException
     *             in case of any exception caught inside
     */
    protected <P1, RESPONSE> List<RESPONSE> wrapListValidated(BaseExceptionFunction<P1, List<RESPONSE>> function, P1 p1, String methodName,
            String p1Name) throws BaseException {
        return wrapList(function, p1, methodName, p1Name, true);
    }

    /**
     * Wraps the business logic method call in order to handle common logging and exception handling with two parameters.
     *
     * @param function
     *            the function doing business logic
     * @param p1
     *            first parameter of the function call
     * @param p2
     *            second parameter of the function call
     * @param methodName
     *            method name to log
     * @param p1Name
     *            first parameter name to log
     * @param p2Name
     *            second parameter name to log
     * @param <RESPONSE>
     *            response object type
     * @param <P1>
     *            type of the first parameter
     * @param <P2>
     *            type of the second parameter
     * @return what the function returns
     * @throws BaseException
     *             in case of any exception caught inside
     */
    protected <P1, P2, RESPONSE> RESPONSE wrap(BaseExceptionFunction2<P1, P2, RESPONSE> function, P1 p1, P2 p2, String methodName, String p1Name,
            String p2Name) throws BaseException {
        return wrap(function, p1, p2, methodName, p1Name, p2Name, false);
    }

    /**
     * Wraps the business logic method call in order to handle common logging and exception handling with two non-empty parameters.
     *
     * @param function
     *            the function doing business logic
     * @param p1
     *            first parameter of the function call
     * @param p2
     *            second parameter of the function call
     * @param methodName
     *            method name to log
     * @param p1Name
     *            first parameter name to log
     * @param p2Name
     *            second parameter name to log
     * @param <RESPONSE>
     *            response object type
     * @param <P1>
     *            type of the first parameter
     * @param <P2>
     *            type of the second parameter
     * @return what the function returns
     * @throws BaseException
     *             in case of any exception caught inside
     */
    protected <P1, P2, RESPONSE> RESPONSE wrapValidated(BaseExceptionFunction2<P1, P2, RESPONSE> function, P1 p1, P2 p2, String methodName,
            String p1Name, String p2Name) throws BaseException {
        return wrap(function, p1, p2, methodName, p1Name, p2Name, true);
    }

    /**
     * Wraps the business logic method call in order to handle common logging and exception handling with two parameters and an {@link Optional}
     * response.
     *
     * @param function
     *            the function doing business logic
     * @param p1
     *            first parameter of the function call
     * @param p2
     *            second parameter of the function call
     * @param methodName
     *            method name to log
     * @param p1Name
     *            first parameter name to log
     * @param p2Name
     *            second parameter name to log
     * @param <RESPONSE>
     *            response object type
     * @param <P1>
     *            type of the first parameter
     * @param <P2>
     *            type of the second parameter
     * @return what the function returns
     * @throws BaseException
     *             in case of any exception caught inside
     */
    protected <P1, P2, RESPONSE> Optional<RESPONSE> wrapOptional(BaseExceptionFunction2<P1, P2, RESPONSE> function, P1 p1, P2 p2, String methodName,
            String p1Name, String p2Name) throws BaseException {
        return wrapOptional(function, p1, p2, methodName, p1Name, p2Name, false);
    }

    /**
     * Wraps the business logic method call in order to handle common logging and exception handling with two non-empty parameters and an
     * {@link Optional} response.
     *
     * @param function
     *            the function doing business logic
     * @param p1
     *            first parameter of the function call
     * @param p2
     *            second parameter of the function call
     * @param methodName
     *            method name to log
     * @param p1Name
     *            first parameter name to log
     * @param p2Name
     *            second parameter name to log
     * @param <RESPONSE>
     *            response object type
     * @param <P1>
     *            type of the first parameter
     * @param <P2>
     *            type of the second parameter
     * @return what the function returns
     * @throws BaseException
     *             in case of any exception caught inside
     */
    protected <P1, P2, RESPONSE> Optional<RESPONSE> wrapOptionalValidated(BaseExceptionFunction2<P1, P2, RESPONSE> function, P1 p1, P2 p2,
            String methodName, String p1Name, String p2Name) throws BaseException {
        return wrapOptional(function, p1, p2, methodName, p1Name, p2Name, true);
    }

    /**
     * Wraps the business logic method call in order to handle common logging and exception handling with two parameters and a {@link List} response.
     *
     * @param function
     *            the function doing business logic
     * @param p1
     *            first parameter of the function call
     * @param p2
     *            second parameter of the function call
     * @param methodName
     *            method name to log
     * @param p1Name
     *            first parameter name to log
     * @param p2Name
     *            second parameter name to log
     * @param <RESPONSE>
     *            response object type
     * @param <P1>
     *            type of the first parameter
     * @param <P2>
     *            type of the second parameter
     * @return what the function returns
     * @throws BaseException
     *             in case of any exception caught inside
     */
    protected <P1, P2, RESPONSE> List<RESPONSE> wrapList(BaseExceptionFunction2<P1, P2, List<RESPONSE>> function, P1 p1, P2 p2, String methodName,
            String p1Name, String p2Name) throws BaseException {
        return wrapList(function, p1, p2, methodName, p1Name, p2Name, false);
    }

    /**
     * Wraps the business logic method call in order to handle common logging and exception handling with two non-empty parameters and a {@link List}
     * response.
     *
     * @param function
     *            the function doing business logic
     * @param p1
     *            first parameter of the function call
     * @param p2
     *            second parameter of the function call
     * @param methodName
     *            method name to log
     * @param p1Name
     *            first parameter name to log
     * @param p2Name
     *            second parameter name to log
     * @param <RESPONSE>
     *            response object type
     * @param <P1>
     *            type of the first parameter
     * @param <P2>
     *            type of the second parameter
     * @return what the function returns
     * @throws BaseException
     *             in case of any exception caught inside
     */
    protected <P1, P2, RESPONSE> List<RESPONSE> wrapListValidated(BaseExceptionFunction2<P1, P2, List<RESPONSE>> function, P1 p1, P2 p2,
            String methodName, String p1Name, String p2Name) throws BaseException {
        return wrapList(function, p1, p2, methodName, p1Name, p2Name, true);
    }

    /**
     * Wraps the business logic method call in order to handle common logging and exception handling with three parameters and a {@link List}
     * response.
     *
     * @param function
     *            the function doing business logic
     * @param p1
     *            first parameter of the function call
     * @param p2
     *            second parameter of the function call
     * @param p3
     *            third parameter of the function call
     * @param methodName
     *            method name to log
     * @param p1Name
     *            first parameter name to log
     * @param p2Name
     *            second parameter name to log
     * @param p3Name
     *            third parameter name to log
     * @param <RESPONSE>
     *            response object type
     * @param <P1>
     *            type of the first parameter
     * @param <P2>
     *            type of the second parameter
     * @param <P3>
     *            type of the third parameter
     * @return what the function returns
     * @throws BaseException
     *             in case of any exception caught inside
     */
    protected <P1, P2, P3, RESPONSE> RESPONSE wrap(BaseExceptionFunction3<P1, P2, P3, RESPONSE> function, P1 p1, P2 p2, P3 p3, String methodName,
            String p1Name, String p2Name, String p3Name) throws BaseException {
        return wrap(function, p1, p2, p3, methodName, p1Name, p2Name, p3Name, false);
    }

    /**
     * Wraps the business logic method call in order to handle common logging and exception handling with three non-empty parameters.
     *
     * @param function
     *            the function doing business logic
     * @param p1
     *            first parameter of the function call
     * @param p2
     *            second parameter of the function call
     * @param p3
     *            third parameter of the function call
     * @param methodName
     *            method name to log
     * @param p1Name
     *            first parameter name to log
     * @param p2Name
     *            second parameter name to log
     * @param p3Name
     *            third parameter name to log
     * @param <RESPONSE>
     *            response object type
     * @param <P1>
     *            type of the first parameter
     * @param <P2>
     *            type of the second parameter
     * @param <P3>
     *            type of the third parameter
     * @return what the function returns
     * @throws BaseException
     *             in case of any exception caught inside
     */
    protected <P1, P2, P3, RESPONSE> RESPONSE wrapValidated(BaseExceptionFunction3<P1, P2, P3, RESPONSE> function, P1 p1, P2 p2, P3 p3,
            String methodName, String p1Name, String p2Name, String p3Name) throws BaseException {
        return wrap(function, p1, p2, p3, methodName, p1Name, p2Name, p3Name, true);
    }

    /**
     * Wraps the business logic method call in order to handle common logging and exception handling with three parameters and an {@link Optional}
     * response.
     *
     * @param function
     *            the function doing business logic
     * @param p1
     *            first parameter of the function call
     * @param p2
     *            second parameter of the function call
     * @param p3
     *            third parameter of the function call
     * @param methodName
     *            method name to log
     * @param p1Name
     *            first parameter name to log
     * @param p2Name
     *            second parameter name to log
     * @param p3Name
     *            third parameter name to log
     * @param <RESPONSE>
     *            response object type
     * @param <P1>
     *            type of the first parameter
     * @param <P2>
     *            type of the second parameter
     * @param <P3>
     *            type of the third parameter
     * @return what the function returns
     * @throws BaseException
     *             in case of any exception caught inside
     */
    protected <P1, P2, P3, RESPONSE> Optional<RESPONSE> wrapOptional(BaseExceptionFunction3<P1, P2, P3, RESPONSE> function, P1 p1, P2 p2, P3 p3,
            String methodName, String p1Name, String p2Name, String p3Name) throws BaseException {
        return wrapOptional(function, p1, p2, p3, methodName, p1Name, p2Name, p3Name, false);
    }

    /**
     * Wraps the business logic method call in order to handle common logging and exception handling with three non-empty parameters and an
     * {@link Optional} response.
     *
     * @param function
     *            the function doing business logic
     * @param p1
     *            first parameter of the function call
     * @param p2
     *            second parameter of the function call
     * @param p3
     *            third parameter of the function call
     * @param methodName
     *            method name to log
     * @param p1Name
     *            first parameter name to log
     * @param p2Name
     *            second parameter name to log
     * @param p3Name
     *            third parameter name to log
     * @param <RESPONSE>
     *            response object type
     * @param <P1>
     *            type of the first parameter
     * @param <P2>
     *            type of the second parameter
     * @param <P3>
     *            type of the third parameter
     * @return what the function returns
     * @throws BaseException
     *             in case of any exception caught inside
     */
    protected <P1, P2, P3, RESPONSE> Optional<RESPONSE> wrapOptionalValidated(BaseExceptionFunction3<P1, P2, P3, RESPONSE> function, P1 p1, P2 p2,
            P3 p3, String methodName, String p1Name, String p2Name, String p3Name) throws BaseException {
        return wrapOptional(function, p1, p2, p3, methodName, p1Name, p2Name, p3Name, true);
    }

    /**
     * Wraps the business logic method call in order to handle common logging and exception handling with three parameters and a {@link List}
     * response.
     *
     * @param function
     *            the function doing business logic
     * @param p1
     *            first parameter of the function call
     * @param p2
     *            second parameter of the function call
     * @param p3
     *            third parameter of the function call
     * @param methodName
     *            method name to log
     * @param p1Name
     *            first parameter name to log
     * @param p2Name
     *            second parameter name to log
     * @param p3Name
     *            third parameter name to log
     * @param <RESPONSE>
     *            response object type
     * @param <P1>
     *            type of the first parameter
     * @param <P2>
     *            type of the second parameter
     * @param <P3>
     *            type of the third parameter
     * @return what the function returns
     * @throws BaseException
     *             in case of any exception caught inside
     */
    protected <P1, P2, P3, RESPONSE> List<RESPONSE> wrapList(BaseExceptionFunction3<P1, P2, P3, List<RESPONSE>> function, P1 p1, P2 p2, P3 p3,
            String methodName, String p1Name, String p2Name, String p3Name) throws BaseException {
        return wrapList(function, p1, p2, p3, methodName, p1Name, p2Name, p3Name, false);
    }

    /**
     * Wraps the business logic method call in order to handle common logging and exception handling with three non-empty parameters and a
     * {@link List} response.
     *
     * @param function
     *            the function doing business logic
     * @param p1
     *            first parameter of the function call
     * @param p2
     *            second parameter of the function call
     * @param p3
     *            third parameter of the function call
     * @param methodName
     *            method name to log
     * @param p1Name
     *            first parameter name to log
     * @param p2Name
     *            second parameter name to log
     * @param p3Name
     *            third parameter name to log
     * @param <RESPONSE>
     *            response object type
     * @param <P1>
     *            type of the first parameter
     * @param <P2>
     *            type of the second parameter
     * @param <P3>
     *            type of the third parameter
     * @return what the function returns
     * @throws BaseException
     *             in case of any exception caught inside
     */
    protected <P1, P2, P3, RESPONSE> List<RESPONSE> wrapListValidated(BaseExceptionFunction3<P1, P2, P3, List<RESPONSE>> function, P1 p1, P2 p2,
            P3 p3, String methodName, String p1Name, String p2Name, String p3Name) throws BaseException {
        return wrapList(function, p1, p2, p3, methodName, p1Name, p2Name, p3Name, true);
    }

    /**
     * Wraps the business logic method call in order to handle common logging and exception handling with four parameters.
     *
     * @param function
     *            the function doing business logic
     * @param p1
     *            first parameter of the function call
     * @param p2
     *            second parameter of the function call
     * @param p3
     *            third parameter of the function call
     * @param p4
     *            fourth parameter of the function call
     * @param methodName
     *            method name to log
     * @param p1Name
     *            first parameter name to log
     * @param p2Name
     *            second parameter name to log
     * @param p3Name
     *            third parameter name to log
     * @param p4Name
     *            fourth parameter name to log
     * @param <RESPONSE>
     *            response object type
     * @param <P1>
     *            type of the first parameter
     * @param <P2>
     *            type of the second parameter
     * @param <P3>
     *            type of the third parameter
     * @param <P4>
     *            type of the fourth parameter
     * @return what the function returns
     * @throws BaseException
     *             in case of any exception caught inside
     */
    protected <P1, P2, P3, P4, RESPONSE> RESPONSE wrap(BaseExceptionFunction4<P1, P2, P3, P4, RESPONSE> function, P1 p1, P2 p2, P3 p3, P4 p4,
            String methodName, String p1Name, String p2Name, String p3Name, String p4Name) throws BaseException {
        return wrap(function, p1, p2, p3, p4, methodName, p1Name, p2Name, p3Name, p4Name, false);
    }

    /**
     * Wraps the business logic method call in order to handle common logging and exception handling with four non-empty parameters.
     *
     * @param function
     *            the function doing business logic
     * @param p1
     *            first parameter of the function call
     * @param p2
     *            second parameter of the function call
     * @param p3
     *            third parameter of the function call
     * @param p4
     *            fourth parameter of the function call
     * @param methodName
     *            method name to log
     * @param p1Name
     *            first parameter name to log
     * @param p2Name
     *            second parameter name to log
     * @param p3Name
     *            third parameter name to log
     * @param p4Name
     *            fourth parameter name to log
     * @param <RESPONSE>
     *            response object type
     * @param <P1>
     *            type of the first parameter
     * @param <P2>
     *            type of the second parameter
     * @param <P3>
     *            type of the third parameter
     * @param <P4>
     *            type of the fourth parameter
     * @return what the function returns
     * @throws BaseException
     *             in case of any exception caught inside
     */
    protected <P1, P2, P3, P4, RESPONSE> RESPONSE wrapValidated(BaseExceptionFunction4<P1, P2, P3, P4, RESPONSE> function, P1 p1, P2 p2, P3 p3, P4 p4,
            String methodName, String p1Name, String p2Name, String p3Name, String p4Name) throws BaseException {
        return wrap(function, p1, p2, p3, p4, methodName, p1Name, p2Name, p3Name, p4Name, true);
    }

    /**
     * Wraps the business logic method call in order to handle common logging and exception handling with four parameters and an {@link Optional}
     * response.
     *
     * @param function
     *            the function doing business logic
     * @param p1
     *            first parameter of the function call
     * @param p2
     *            second parameter of the function call
     * @param p3
     *            third parameter of the function call
     * @param p4
     *            fourth parameter of the function call
     * @param methodName
     *            method name to log
     * @param p1Name
     *            first parameter name to log
     * @param p2Name
     *            second parameter name to log
     * @param p3Name
     *            third parameter name to log
     * @param p4Name
     *            fourth parameter name to log
     * @param <RESPONSE>
     *            response object type
     * @param <P1>
     *            type of the first parameter
     * @param <P2>
     *            type of the second parameter
     * @param <P3>
     *            type of the third parameter
     * @param <P4>
     *            type of the fourth parameter
     * @return what the function returns
     * @throws BaseException
     *             in case of any exception caught inside
     */
    protected <P1, P2, P3, P4, RESPONSE> Optional<RESPONSE> wrapOptional(BaseExceptionFunction4<P1, P2, P3, P4, RESPONSE> function, P1 p1, P2 p2,
            P3 p3, P4 p4, String methodName, String p1Name, String p2Name, String p3Name, String p4Name) throws BaseException {
        return wrapOptional(function, p1, p2, p3, p4, methodName, p1Name, p2Name, p3Name, p4Name, false);
    }

    /**
     * Wraps the business logic method call in order to handle common logging and exception handling with four non-empty parameters and an
     * {@link Optional} response.
     *
     * @param function
     *            the function doing business logic
     * @param p1
     *            first parameter of the function call
     * @param p2
     *            second parameter of the function call
     * @param p3
     *            third parameter of the function call
     * @param p4
     *            fourth parameter of the function call
     * @param methodName
     *            method name to log
     * @param p1Name
     *            first parameter name to log
     * @param p2Name
     *            second parameter name to log
     * @param p3Name
     *            third parameter name to log
     * @param p4Name
     *            fourth parameter name to log
     * @param <RESPONSE>
     *            response object type
     * @param <P1>
     *            type of the first parameter
     * @param <P2>
     *            type of the second parameter
     * @param <P3>
     *            type of the third parameter
     * @param <P4>
     *            type of the fourth parameter
     * @return what the function returns
     * @throws BaseException
     *             in case of any exception caught inside
     */
    protected <P1, P2, P3, P4, RESPONSE> Optional<RESPONSE> wrapOptionalValidated(BaseExceptionFunction4<P1, P2, P3, P4, RESPONSE> function, P1 p1,
            P2 p2, P3 p3, P4 p4, String methodName, String p1Name, String p2Name, String p3Name, String p4Name) throws BaseException {
        return wrapOptional(function, p1, p2, p3, p4, methodName, p1Name, p2Name, p3Name, p4Name, true);
    }

    /**
     * Wraps the business logic method call in order to handle common logging and exception handling with four parameters and a {@link List} response.
     *
     * @param function
     *            the function doing business logic
     * @param p1
     *            first parameter of the function call
     * @param p2
     *            second parameter of the function call
     * @param p3
     *            third parameter of the function call
     * @param p4
     *            fourth parameter of the function call
     * @param methodName
     *            method name to log
     * @param p1Name
     *            first parameter name to log
     * @param p2Name
     *            second parameter name to log
     * @param p3Name
     *            third parameter name to log
     * @param p4Name
     *            fourth parameter name to log
     * @param <RESPONSE>
     *            response object type
     * @param <P1>
     *            type of the first parameter
     * @param <P2>
     *            type of the second parameter
     * @param <P3>
     *            type of the third parameter
     * @param <P4>
     *            type of the fourth parameter
     * @return what the function returns
     * @throws BaseException
     *             in case of any exception caught inside
     */
    protected <P1, P2, P3, P4, RESPONSE> List<RESPONSE> wrapList(BaseExceptionFunction4<P1, P2, P3, P4, List<RESPONSE>> function, P1 p1, P2 p2, P3 p3,
            P4 p4, String methodName, String p1Name, String p2Name, String p3Name, String p4Name) throws BaseException {
        return wrapList(function, p1, p2, p3, p4, methodName, p1Name, p2Name, p3Name, p4Name, false);
    }

    /**
     * Wraps the business logic method call in order to handle common logging and exception handling with four non-empty parameters and a {@link List}
     * response.
     *
     * @param function
     *            the function doing business logic
     * @param p1
     *            first parameter of the function call
     * @param p2
     *            second parameter of the function call
     * @param p3
     *            third parameter of the function call
     * @param p4
     *            fourth parameter of the function call
     * @param methodName
     *            method name to log
     * @param p1Name
     *            first parameter name to log
     * @param p2Name
     *            second parameter name to log
     * @param p3Name
     *            third parameter name to log
     * @param p4Name
     *            fourth parameter name to log
     * @param <RESPONSE>
     *            response object type
     * @param <P1>
     *            type of the first parameter
     * @param <P2>
     *            type of the second parameter
     * @param <P3>
     *            type of the third parameter
     * @param <P4>
     *            type of the fourth parameter
     * @return what the function returns
     * @throws BaseException
     *             in case of any exception caught inside
     */
    protected <P1, P2, P3, P4, RESPONSE> List<RESPONSE> wrapListValidated(BaseExceptionFunction4<P1, P2, P3, P4, List<RESPONSE>> function, P1 p1,
            P2 p2, P3 p3, P4 p4, String methodName, String p1Name, String p2Name, String p3Name, String p4Name) throws BaseException {
        return wrapList(function, p1, p2, p3, p4, methodName, p1Name, p2Name, p3Name, p4Name, true);
    }

    /**
     * Wraps the business logic method call in order to handle common logging and exception handling with five parameters.
     *
     * @param function
     *            the function doing business logic
     * @param p1
     *            first parameter of the function call
     * @param p2
     *            second parameter of the function call
     * @param p3
     *            third parameter of the function call
     * @param p4
     *            fourth parameter of the function call
     * @param p5
     *            fifth parameter of the function call
     * @param methodName
     *            method name to log
     * @param p1Name
     *            first parameter name to log
     * @param p2Name
     *            second parameter name to log
     * @param p3Name
     *            third parameter name to log
     * @param p4Name
     *            fourth parameter name to log
     * @param p5Name
     *            fifth parameter name to log
     * @param <RESPONSE>
     *            response object type
     * @param <P1>
     *            type of the first parameter
     * @param <P2>
     *            type of the second parameter
     * @param <P3>
     *            type of the third parameter
     * @param <P4>
     *            type of the fourth parameter
     * @param <P5>
     *            type of the fifth parameter
     * @return what the function returns
     * @throws BaseException
     *             in case of any exception caught inside
     */
    protected <P1, P2, P3, P4, P5, RESPONSE> RESPONSE wrap(BaseExceptionFunction5<P1, P2, P3, P4, P5, RESPONSE> function, P1 p1, P2 p2, P3 p3, P4 p4,
            P5 p5, String methodName, String p1Name, String p2Name, String p3Name, String p4Name, String p5Name) throws BaseException {
        return wrap(function, p1, p2, p3, p4, p5, methodName, p1Name, p2Name, p3Name, p4Name, p5Name, false);
    }

    /**
     * Wraps the business logic method call in order to handle common logging and exception handling with five non-empty parameters.
     *
     * @param function
     *            the function doing business logic
     * @param p1
     *            first parameter of the function call
     * @param p2
     *            second parameter of the function call
     * @param p3
     *            third parameter of the function call
     * @param p4
     *            fourth parameter of the function call
     * @param p5
     *            fifth parameter of the function call
     * @param methodName
     *            method name to log
     * @param p1Name
     *            first parameter name to log
     * @param p2Name
     *            second parameter name to log
     * @param p3Name
     *            third parameter name to log
     * @param p4Name
     *            fourth parameter name to log
     * @param p5Name
     *            fifth parameter name to log
     * @param <RESPONSE>
     *            response object type
     * @param <P1>
     *            type of the first parameter
     * @param <P2>
     *            type of the second parameter
     * @param <P3>
     *            type of the third parameter
     * @param <P4>
     *            type of the fourth parameter
     * @param <P5>
     *            type of the fifth parameter
     * @return what the function returns
     * @throws BaseException
     *             in case of any exception caught inside
     */
    protected <P1, P2, P3, P4, P5, RESPONSE> RESPONSE wrapValidated(BaseExceptionFunction5<P1, P2, P3, P4, P5, RESPONSE> function, P1 p1, P2 p2,
            P3 p3, P4 p4, P5 p5, String methodName, String p1Name, String p2Name, String p3Name, String p4Name, String p5Name) throws BaseException {
        return wrap(function, p1, p2, p3, p4, p5, methodName, p1Name, p2Name, p3Name, p4Name, p5Name, true);
    }

    /**
     * Wraps the business logic method call in order to handle common logging and exception handling with five parameters and an {@link Optional}
     * response.
     *
     * @param function
     *            the function doing business logic
     * @param p1
     *            first parameter of the function call
     * @param p2
     *            second parameter of the function call
     * @param p3
     *            third parameter of the function call
     * @param p4
     *            fourth parameter of the function call
     * @param p5
     *            fifth parameter of the function call
     * @param methodName
     *            method name to log
     * @param p1Name
     *            first parameter name to log
     * @param p2Name
     *            second parameter name to log
     * @param p3Name
     *            third parameter name to log
     * @param p4Name
     *            fourth parameter name to log
     * @param p5Name
     *            fifth parameter name to log
     * @param <RESPONSE>
     *            response object type
     * @param <P1>
     *            type of the first parameter
     * @param <P2>
     *            type of the second parameter
     * @param <P3>
     *            type of the third parameter
     * @param <P4>
     *            type of the fourth parameter
     * @param <P5>
     *            type of the fifth parameter
     * @return what the function returns
     * @throws BaseException
     *             in case of any exception caught inside
     */
    protected <P1, P2, P3, P4, P5, RESPONSE> Optional<RESPONSE> wrapOptional(BaseExceptionFunction5<P1, P2, P3, P4, P5, RESPONSE> function, P1 p1,
            P2 p2, P3 p3, P4 p4, P5 p5, String methodName, String p1Name, String p2Name, String p3Name, String p4Name, String p5Name)
            throws BaseException {
        return wrapOptional(function, p1, p2, p3, p4, p5, methodName, p1Name, p2Name, p3Name, p4Name, p5Name, false);
    }

    /**
     * Wraps the business logic method call in order to handle common logging and exception handling with five non-empty parameters and an
     * {@link Optional} response.
     *
     * @param function
     *            the function doing business logic
     * @param p1
     *            first parameter of the function call
     * @param p2
     *            second parameter of the function call
     * @param p3
     *            third parameter of the function call
     * @param p4
     *            fourth parameter of the function call
     * @param p5
     *            fifth parameter of the function call
     * @param methodName
     *            method name to log
     * @param p1Name
     *            first parameter name to log
     * @param p2Name
     *            second parameter name to log
     * @param p3Name
     *            third parameter name to log
     * @param p4Name
     *            fourth parameter name to log
     * @param p5Name
     *            fifth parameter name to log
     * @param <RESPONSE>
     *            response object type
     * @param <P1>
     *            type of the first parameter
     * @param <P2>
     *            type of the second parameter
     * @param <P3>
     *            type of the third parameter
     * @param <P4>
     *            type of the fourth parameter
     * @param <P5>
     *            type of the fifth parameter
     * @return what the function returns
     * @throws BaseException
     *             in case of any exception caught inside
     */
    protected <P1, P2, P3, P4, P5, RESPONSE> Optional<RESPONSE> wrapOptionalValidated(BaseExceptionFunction5<P1, P2, P3, P4, P5, RESPONSE> function,
            P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, String methodName, String p1Name, String p2Name, String p3Name, String p4Name, String p5Name)
            throws BaseException {
        return wrapOptional(function, p1, p2, p3, p4, p5, methodName, p1Name, p2Name, p3Name, p4Name, p5Name, true);
    }

    /**
     * Wraps the business logic method call in order to handle common logging and exception handling with five parameters and a {@link List} response.
     *
     * @param function
     *            the function doing business logic
     * @param p1
     *            first parameter of the function call
     * @param p2
     *            second parameter of the function call
     * @param p3
     *            third parameter of the function call
     * @param p4
     *            fourth parameter of the function call
     * @param p5
     *            fifth parameter of the function call
     * @param methodName
     *            method name to log
     * @param p1Name
     *            first parameter name to log
     * @param p2Name
     *            second parameter name to log
     * @param p3Name
     *            third parameter name to log
     * @param p4Name
     *            fourth parameter name to log
     * @param p5Name
     *            fifth parameter name to log
     * @param <RESPONSE>
     *            response object type
     * @param <P1>
     *            type of the first parameter
     * @param <P2>
     *            type of the second parameter
     * @param <P3>
     *            type of the third parameter
     * @param <P4>
     *            type of the fourth parameter
     * @param <P5>
     *            type of the fifth parameter
     * @return what the function returns
     * @throws BaseException
     *             in case of any exception caught inside
     */
    protected <P1, P2, P3, P4, P5, RESPONSE> List<RESPONSE> wrapList(BaseExceptionFunction5<P1, P2, P3, P4, P5, List<RESPONSE>> function, P1 p1,
            P2 p2, P3 p3, P4 p4, P5 p5, String methodName, String p1Name, String p2Name, String p3Name, String p4Name, String p5Name)
            throws BaseException {
        return wrapList(function, p1, p2, p3, p4, p5, methodName, p1Name, p2Name, p3Name, p4Name, p5Name, false);
    }

    /**
     * Wraps the business logic method call in order to handle common logging and exception handling with five non-empty parameters and a {@link List}
     * response.
     *
     * @param function
     *            the function doing business logic
     * @param p1
     *            first parameter of the function call
     * @param p2
     *            second parameter of the function call
     * @param p3
     *            third parameter of the function call
     * @param p4
     *            fourth parameter of the function call
     * @param p5
     *            fifth parameter of the function call
     * @param methodName
     *            method name to log
     * @param p1Name
     *            first parameter name to log
     * @param p2Name
     *            second parameter name to log
     * @param p3Name
     *            third parameter name to log
     * @param p4Name
     *            fourth parameter name to log
     * @param p5Name
     *            fifth parameter name to log
     * @param <RESPONSE>
     *            response object type
     * @param <P1>
     *            type of the first parameter
     * @param <P2>
     *            type of the second parameter
     * @param <P3>
     *            type of the third parameter
     * @param <P4>
     *            type of the fourth parameter
     * @param <P5>
     *            type of the fifth parameter
     * @return what the function returns
     * @throws BaseException
     *             in case of any exception caught inside
     */
    protected <P1, P2, P3, P4, P5, RESPONSE> List<RESPONSE> wrapListValidated(BaseExceptionFunction5<P1, P2, P3, P4, P5, List<RESPONSE>> function,
            P1 p1, P2 p2, P3 p3, P4 p4, P5 p5, String methodName, String p1Name, String p2Name, String p3Name, String p4Name, String p5Name)
            throws BaseException {
        return wrapList(function, p1, p2, p3, p4, p5, methodName, p1Name, p2Name, p3Name, p4Name, p5Name, true);
    }

    private <P> boolean isNullOrBlankAnyParameter(Object... params) {
        for (Object param : params) {
            if (param == null || (param instanceof String && StringUtils.isBlank((String) param))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Adds log entry for method return if trace is enabled.
     *
     * @param methodInfo
     *            method info format {@link String} with method and param names, and placeholders for parameter values
     * @param params
     *            parameter values in matching order with methodInfo
     */
    protected void logReturn(String methodInfo, Object... params) {
        if (log.isTraceEnabled()) {
            log.trace("<<" + methodInfo, prepareParametersToLog(params));
        }
    }

    /**
     * Adds log entry for method enter if trace is enabled.
     *
     * @param methodInfo
     *            method info format {@link String} with method and param names, and placeholders for param values
     * @param params
     *            parameter values in matching order with methodInfo
     */
    protected void logEnter(String methodInfo, Object... params) {
        if (log.isTraceEnabled()) {
            log.trace(">>" + methodInfo, prepareParametersToLog(params));
        }
    }

    /**
     * Puts any number of input param {@link Object}s in an array. Note: collection parameters are handled on one level of depth. This method could be
     * rewritten in a recursive way if necessary.
     *
     * @param params
     *            input params
     * @return array of params
     */
    private Object[] prepareParametersToLog(Object... params) {
        if (params == null) {
            return null;
        }
        Object[] copy = Arrays.copyOf(params, params.length);
        for (int i = 0; i < copy.length; i++) {
            if (copy[i] == null) {
                continue;
            }
            if (copy[i] instanceof Collection<?>) {
                copy[i] = new StringBuilder().append("Collection[")
                        .append(((Collection<?>) copy[i]).stream().map(String::valueOf).collect(Collectors.joining(","))).append("]").toString();
            }
            if (copy[i] instanceof Map<?, ?>) {
                copy[i] = new StringBuilder().append("Map[").append(((Map<?, ?>) copy[i]).entrySet().stream()
                        .map(e -> "[" + e.getKey() + "," + e.getValue() + "]").collect(Collectors.joining(","))).append("]").toString();
            }
            if (copy[i].getClass().isArray()) {
                copy[i] = new StringBuilder().append("Array").append(Arrays.toString((Object[]) copy[i])).toString();
            }
        }
        return copy;
    }

    /**
     * Creates {@link BONotFoundException}.
     *
     * @param methodInfo
     *            method info format {@link String} with method and param names, and placeholders for param values
     * @param params
     *            parameter values in matching order with methodInfo
     * @return {@link BONotFoundException} with message from methodInfo.
     */
    protected BONotFoundException notFound(String methodInfo, Object... params) {
        return new BONotFoundException(getDefaultNotFoundFaultTypeEnum(),
                MessageFormat.format("Entry for " + methodInfo + " not found!", prepareParametersToLog(params)));
    }

    /**
     * Creates {@link TechnicalException} with {@link CoffeeFaultType#REPOSITORY_FAILED} fault type.
     *
     * @param e
     *            cause {@link Exception}
     * @param methodInfo
     *            method info format {@link String} with method and param names, and placeholders for param values
     * @param params
     *            parameter values in matching order with methodInfo
     * @return {@link TechnicalException} with message from methodInfo.
     */
    protected TechnicalException repositoryFailed(Exception e, String methodInfo, Object... params) {
        StringBuilder sb = new StringBuilder();
        sb.append("Error occurred in ").append(methodInfo).append(" : [").append(e.getLocalizedMessage()).append("]");
        String msg = MessageFormat.format(sb.toString(), prepareParametersToLog(params));
        return new TechnicalException(CoffeeFaultType.REPOSITORY_FAILED, msg, e);
    }

    /**
     * Creates {@link InvalidParameterException} with formatted exception message by method info.
     *
     * @param methodInfo
     *            method info format {@link String} with method and param names, and placeholders for param values
     * @param params
     *            parameter values in matching order with methodInfo
     * @return {@link BaseException} with message from methodInfo.
     */
    protected BaseException invalidParameter(String methodInfo, Object... params) {
        String msg = MessageFormat.format("At least one incoming parameter in " + methodInfo + " is null or blank!", params);
        return new InvalidParameterException(msg);
    }

    /**
     *
     * @param methodName
     *            the REST method name e.g getCustomerInfoByUserId
     * @param paramNames
     *            the REST param names of {@link javax.ws.rs.PathParam}s e.g userId,balanceId
     * @return e.g. " getCustomerInfoByUserId(userId: [{0}])"
     */
    private String getCalledMethodWithOnlyPathParams(String methodName, String... paramNames) {
        return getCalledMethodWithParamsBase(methodName, paramNames) + ")";
    }

    /**
     * Concatenates given method name and param names into loggable formatted method info.
     *
     * @param methodName
     *            method name eg. "getCustomerInfoByUserId"
     * @param paramNames
     *            eg. "userId"
     * @return method info e.g. " getCustomerInfoByUserId(userId: [{0}])"
     */
    protected String getCalledMethodWithParamsBase(String methodName, String... paramNames) {
        StringBuilder methodInfo = new StringBuilder(" ").append(getOriginalClassName()).append(".").append(methodName).append("(");
        int index = 0;
        if (paramNames != null) {
            for (String paramName : paramNames) {
                if (index > 0) {
                    methodInfo.append(", ");
                }
                methodInfo.append(paramName).append(": [{").append(index++).append("}]");
            }
        }
        return methodInfo.toString();
    }

    /**
     * Returns original, un-proxied class name.
     *
     * @return class name
     */
    protected String getOriginalClassName() {
        return ProxyUtils.getUnproxiedClass(getClass()).getSimpleName();
    }
}
