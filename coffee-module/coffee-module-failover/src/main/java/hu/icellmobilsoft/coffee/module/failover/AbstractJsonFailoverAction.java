package hu.icellmobilsoft.coffee.module.failover;

import java.util.List;

import hu.icellmobilsoft.coffee.se.api.exception.BaseException;
import hu.icellmobilsoft.coffee.tool.utils.json.JsonUtil;

/**
 * Abstract action for JSON String message based failover endpoints.
 *
 * @author attila-kiss-it
 * @since 2.10.0
 */
public abstract class AbstractJsonFailoverAction<T> extends AbstractFailoverAction<T> {

    @Override
    protected List<String> convertMessagesToString(List<T> messagesToResend) throws BaseException {
        return JsonUtil.toJsonList(messagesToResend);
    }

}
