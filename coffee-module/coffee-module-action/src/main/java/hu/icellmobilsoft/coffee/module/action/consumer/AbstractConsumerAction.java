package hu.icellmobilsoft.coffee.module.action.consumer;

import java.text.MessageFormat;

import hu.icellmobilsoft.coffee.cdi.logger.AppLogger;
import hu.icellmobilsoft.coffee.cdi.logger.ThisLogger;
import hu.icellmobilsoft.coffee.cdi.trace.annotation.Traced;
import hu.icellmobilsoft.coffee.cdi.trace.constants.SpanAttribute;
import hu.icellmobilsoft.coffee.cdi.trace.spi.ITraceHandler;
import hu.icellmobilsoft.coffee.module.action.AbstractAction;
import hu.icellmobilsoft.coffee.se.api.exception.BaseException;
import hu.icellmobilsoft.coffee.tool.utils.validation.ParamValidatorUtil;
import jakarta.inject.Inject;

/**
 * Abstract implementation of an {@link IConsumerAction#process(Object)} function. The implementation invokes the {@link AbstractAction} methods in
 * the following order: {@link #init(Object)}, {@link #validateState()}, {@link #collectData()}, {@link #processData()}, {@link #saveData()}. If any
 * error occurres during these steps then the {@link #handleError(Object, BaseException)} method is called. Otherwise the {@link #publishNextEvent()}
 * method is called to trigger the next consumer action if necessary. All of these methods are traced with {@link ITraceHandler}.
 *
 * @param <T>
 *            the type of the processed parameter
 *
 * @author attila-kiss-it
 * @since 2.10.0
 */
public abstract class AbstractConsumerAction<T> extends AbstractAction<T> implements IConsumerAction<T> {

    @Inject
    @ThisLogger
    private AppLogger logger;

    @Inject
    private ITraceHandler traceHandler;

    @Override
    public void process(T parameter) throws BaseException {

        if (parameter instanceof String stringParameter) {
            ParamValidatorUtil.requireNonBlank(stringParameter, "parameter");
        } else {
            ParamValidatorUtil.requireNonNull(parameter, "parameter");
        }

        Class<?> enclosingClass = getEnclosingClass(getClass());
        Traced traced = new Traced.Literal(enclosingClass.getName(), SpanAttribute.Java.KIND, "");

        try {
            traceHandler.runWithTrace(() -> init(parameter), traced, enclosingClass.getSimpleName() + ".init()");
            traceHandler.runWithTrace(() -> validateState(), traced, enclosingClass.getSimpleName() + ".validateState()");
            traceHandler.runWithTrace(() -> collectData(), traced, enclosingClass.getSimpleName() + ".collectData()");
            traceHandler.runWithTrace(() -> processData(), traced, enclosingClass.getSimpleName() + ".processData()");
            traceHandler.runWithTrace(() -> saveData(), traced, enclosingClass.getSimpleName() + ".saveData()");
            traceHandler.runWithTrace(() -> safePublishNextEvent(parameter), traced, enclosingClass.getSimpleName() + ".safePublishEvents()");
        } catch (BaseException e) {
            traceHandler.runWithTrace(() -> handleError(parameter, e), traced, enclosingClass.getSimpleName() + ".handleError()");
        }
    }

    private Class<?> getEnclosingClass(Class<?> clazz) {
        if (clazz.getName().contains("$")) {
            return getEnclosingClass(clazz.getSuperclass());
        }
        return clazz;
    }

    private void safePublishNextEvent(T parameter) {
        try {
            publishNextEvent();
        } catch (BaseException e) {
            logger.error(
                    MessageFormat.format("Consumer finished successfully, but failed to publish next event for consumer parameter [{0}]!", parameter),
                    e);
        }
    }

    /**
     * Triggering the next event that continues the processing if necessary. The {@link AbstractConsumerAction} guarantees if any
     * {@link BaseException} occurres during this method implementation, it will not effect the outcome of this action.
     *
     * @throws BaseException
     *             in case of error
     *
     * @see #safePublishNextEvent(Object)
     */
    protected abstract void publishNextEvent() throws BaseException;

    /**
     * Error handling function that is called if any {@link BaseException} is thrown from ({@link #init(Object)}, {@link #validateState()},
     * {@link #collectData()}, {@link #processData()}, {@link #saveData()}).
     *
     * @param parameter
     *            the original parameter of the action
     * @param e
     *            the error
     * @throws BaseException
     *             the original error ({@code e}) or the error occurred during the error handling
     */
    protected abstract void handleError(T parameter, BaseException e) throws BaseException;

}
