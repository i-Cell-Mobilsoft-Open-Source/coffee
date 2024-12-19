package hu.icellmobilsoft.coffee.module.failover;

import java.util.List;

/**
 * Abstract action for String message based failover endpoints.
 *
 * @author attila-kiss-it
 * @since 2.10.0
 */

public abstract class AbstractStringFailoverAction extends AbstractFailoverAction<String> {

    @Override
    protected List<String> convertMessagesToString(List<String> messagesToResend) {
        return messagesToResend;
    }

}
