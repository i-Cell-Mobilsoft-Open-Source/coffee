package hu.icellmobilsoft.coffee.module.action.consumer;

import hu.icellmobilsoft.coffee.se.api.exception.BaseException;

/**
 * Action defining a consumer processing.
 *
 * @param <T>
 *            the type of the processed parameter
 *
 * @author attila-kiss-it
 * @since 2.10.0
 */
public interface IConsumerAction<T> {

    /**
     * The function that starts the execution of the process.
     *
     * @param parameter
     *            the parameter of the consumer
     * @throws BaseException
     *             in case of error
     */
    void process(T parameter) throws BaseException;

}
