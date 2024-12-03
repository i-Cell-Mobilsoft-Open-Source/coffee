package hu.icellmobilsoft.coffee.module.action.rest;

import hu.icellmobilsoft.coffee.se.api.exception.BaseException;

/**
 * Action defining a REST operation.
 *
 * @param <T>
 *            the type of the processed request
 * @param <R>
 *            the type of the response
 * 
 * @author attila-kiss-it
 * @since 2.10.0
 */
public interface IRestAction<T, R> {

    /**
     * The function that starts the execution of the operation.
     *
     * @param request
     *            the request
     * @return the response
     * @throws BaseException
     *             in case of error
     */
    R process(T request) throws BaseException;

}
