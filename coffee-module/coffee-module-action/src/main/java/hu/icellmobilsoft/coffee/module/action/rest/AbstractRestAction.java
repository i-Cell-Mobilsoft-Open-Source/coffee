package hu.icellmobilsoft.coffee.module.action.rest;

import hu.icellmobilsoft.coffee.cdi.trace.annotation.Traced;
import hu.icellmobilsoft.coffee.cdi.trace.constants.SpanAttribute;
import hu.icellmobilsoft.coffee.cdi.trace.spi.ITraceHandler;
import hu.icellmobilsoft.coffee.module.action.AbstractAction;
import hu.icellmobilsoft.coffee.se.api.exception.BaseException;
import hu.icellmobilsoft.coffee.tool.utils.validation.ParamValidatorUtil;
import jakarta.inject.Inject;

/**
 * Abstract implementation of an {@link IRestAction#process(Object)} function. The implementation invokes the {@link AbstractAction} methods in the
 * following order: {@link #init(Object)}, {@link #validateState()}, {@link #collectData()}, {@link #processData()}, {@link #saveData()}. If any error
 * occurres during these steps then the {@link #handleError(Object, BaseException)} method is called. Otherwise the {@link #publishNextEvent()} method
 * is called to trigger the next consumer action if necessary. All of these methods are traced with {@link ITraceHandler}.
 *
 * @param <T>
 *            the type of the processed request
 * @param <R>
 *            the type of the response
 *
 * @author attila-kiss-it
 * @since 2.10.0
 */
public abstract class AbstractRestAction<T, R> extends AbstractAction<T> implements IRestAction<T, R> {

    @Inject
    private ITraceHandler traceHandler;

    @Override
    public R process(T request) throws BaseException {

        ParamValidatorUtil.requireNonNull(request, "request");

        Class<?> enclosingClass = getEnclosingClass(getClass());
        Traced traced = new Traced.Literal(enclosingClass.getName(), SpanAttribute.Java.KIND, "");

        try {
            traceHandler.runWithTrace(() -> init(request), traced, enclosingClass.getSimpleName() + ".init()");
            traceHandler.runWithTrace(() -> validateState(), traced, enclosingClass.getSimpleName() + ".validateState()");
            traceHandler.runWithTrace(() -> collectData(), traced, enclosingClass.getSimpleName() + ".collectData()");
            traceHandler.runWithTrace(() -> processData(), traced, enclosingClass.getSimpleName() + ".processData()");
            traceHandler.runWithTrace(() -> saveData(), traced, enclosingClass.getSimpleName() + ".saveData()");
            return traceHandler.runWithTrace(() -> returnResponse(), traced, enclosingClass.getSimpleName() + ".returnResponse()");
        } catch (BaseException e) {
            return traceHandler.runWithTrace(() -> handleError(request, e), traced, enclosingClass.getSimpleName() + ".handleError()");
        }
    }

    private Class<?> getEnclosingClass(Class<?> clazz) {
        if (clazz.getName().contains("$")) {
            return getEnclosingClass(clazz.getSuperclass());
        }
        return clazz;
    }

    /**
     * Creates the response to return in case of successful processing.
     *
     * @return the response
     * @throws BaseException
     *             in case of error
     */
    protected abstract R returnResponse() throws BaseException;

    /**
     * Error handling function that should be called if the functions listed above ({@link #init(Object)}, {@link #validateState()},
     * {@link #collectData()}, {@link #processData()}, {@link #saveData()}, {@link #returnResponse()}) throw a {@link BaseException}.
     *
     * @param parameter
     *            the original parameter of the action
     * @param e
     *            the error
     * @return the error response
     * @throws BaseException
     *             the original error ({@code e}) or the error occurred during the error handling
     */
    protected abstract R handleError(T parameter, BaseException e) throws BaseException;

}
