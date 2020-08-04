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
package hu.icellmobilsoft.coffee.module.activemq.util;

import javax.ejb.EJBException;
import javax.enterprise.inject.Vetoed;
import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.TextMessage;

import org.apache.commons.lang3.exception.ExceptionUtils;

import hu.icellmobilsoft.coffee.se.logging.Logger;

/**
 * JMS (activemq specific) utils
 *
 * @author imre.scheffer
 * @since 1.0.0
 */
@Vetoed
public class JmsUtil {

    private static Logger LOGGER = hu.icellmobilsoft.coffee.cdi.logger.LogProducer.getStaticDefaultLogger(JmsUtil.class);

    /**
     * <p>getMessageId.</p>
     */
    public static String getMessageId(Message m) {
        try {
            return m.getJMSMessageID();
        } catch (JMSException ex) {
            LOGGER.warn("error in getMessageId(): " + ex.getLocalizedMessage());
            return null;
        }
    }

    /**
     * <p>getMessageStringProperty.</p>
     */
    public static String getMessageStringProperty(Message m, String propertyName) {
        try {
            return m.getStringProperty(propertyName);
        } catch (JMSException ex) {
            LOGGER.warn("error in getMessageStringProperty() propertyName: [" + propertyName + "]: " + ex.getLocalizedMessage());
            return null;
        }
    }

    /**
     * <p>newJmsException.</p>
     */
    public static RuntimeException newJmsException(Message m, String errorText) {
        return newJmsException(m, errorText, null);
    }

    /**
     * <p>newJmsException.</p>
     */
    public static RuntimeException newJmsException(Message m, String errorText, Exception exception) {
        String mId = getMessageId(m);
        org.apache.activemq.command.Message aMessage = (org.apache.activemq.command.Message) m;
        String msg = String.format("error in processing message messageId: [%s], deliveryCount: [%s], errorText: [%s]\n exception: [%s]", mId,
                aMessage == null ? "null" : aMessage.getRedeliveryCounter(), errorText,
                exception == null ? "null" : ExceptionUtils.getStackTrace(exception));
        LOGGER.warn(msg);
        return new EJBException(msg, exception);
    }

    /**
     * <p>close.</p>
     */
    public static void close(Connection connection) {
        if (connection != null) {
            // ez zar producert is es sessiont is
            try {
                connection.close();
            } catch (JMSException e) {
                LOGGER.warn("Error in close jms connection: ", e);
            }
        }
    }

    /**
     * <p>close.</p>
     */
    public static void close(MessageProducer messageProducer) {
        if (messageProducer != null) {
            try {
                messageProducer.close();
            } catch (JMSException e) {
                LOGGER.warn("Error in close jms producer: ", e);
            }
        }
    }

    /**
     * <p>getTextMessage.</p>
     */
    public static TextMessage getTextMessage(Message message) {
        if (message instanceof TextMessage) {
            return (TextMessage) message;
        } else {
            String msg = "JMS message is not TextMessage! message class: [" + message.getClass() + "], message: [" + message + "]";
            throw JmsUtil.newJmsException(message, msg);
        }
    }
}
