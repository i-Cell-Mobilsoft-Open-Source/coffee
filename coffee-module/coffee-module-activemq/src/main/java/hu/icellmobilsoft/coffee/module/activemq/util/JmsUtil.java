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

import jakarta.ejb.EJBException;
import jakarta.jms.Connection;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.MessageProducer;
import jakarta.jms.TextMessage;

import org.apache.commons.lang3.exception.ExceptionUtils;

import hu.icellmobilsoft.coffee.se.logging.Logger;

/**
 * JMS (activemq specific) utils
 *
 * @author imre.scheffer
 * @since 1.0.0
 */
public class JmsUtil {

    private static Logger LOGGER = Logger.getLogger(JmsUtil.class);

    /**
     * Default constructor, constructs a new object.
     */
    public JmsUtil() {
        super();
    }

    /**
     * Returns id of given {@link Message}.
     *
     * @param m
     *            {@code Message}
     * @return message id or null if JMS exception occurs
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
     * Returns given property of given {@link Message}.
     *
     * @param m
     *            {@code Message}
     * @param propertyName
     *            desired property
     * @return desired property of the message or null if JMS exception occurs
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
     * Throws new JMS Exception ({@link EJBException}) with given error text.
     *
     * @param m
     *            JMS message which threw given {@code Exception}
     * @param errorText
     *            error description text
     * @return {@code EJBException} with given error text
     * @see #newJmsException(Message, String, Exception)
     */
    public static RuntimeException newJmsException(Message m, String errorText) {
        return newJmsException(m, errorText, null);
    }

    /**
     * Wraps given {@link Exception} to a new JMS Exception ({@link EJBException}) with given error text.
     * 
     * @param m
     *            JMS message which threw given {@code Exception}
     * @param errorText
     *            error description text
     * @param exception
     *            {@code Exception} to wrap
     * @return wrapped {@code Exception}
     */
    public static RuntimeException newJmsException(Message m, String errorText, Exception exception) {
        String mId = getMessageId(m);
        // elozo EE8: org.apache.activemq.command.Message aMessage = (org.apache.activemq.command.Message) m;
        // String redeliveryCounter = aMessage.getRedeliveryCounter();
        String redeliveryCounter = "unknown";
        String msg = String.format("error in processing message messageId: [%s], deliveryCount: [%s], errorText: [%s]\n exception: [%s]", mId,
                redeliveryCounter, errorText, exception == null ? "null" : ExceptionUtils.getStackTrace(exception));
        LOGGER.warn(msg);
        return new EJBException(msg, exception);
    }

    /**
     * Closes given {@link Connection}.
     *
     * @param connection
     *            connection to close
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
     * Closes given {@link MessageProducer}.
     * 
     * @param messageProducer
     *            message producer to close
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
     * Converts given {@link Message} to {@link TextMessage}.
     * 
     * @param message
     *            message to convert
     * @return {@code TextMessage} based on {@code message} or JMS exception if cannot be converted
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
