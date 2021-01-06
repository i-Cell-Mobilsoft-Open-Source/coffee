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
package hu.icellmobilsoft.coffee.module.activemq.producer;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ScheduledMessage;

import hu.icellmobilsoft.coffee.cdi.logger.AppLogger;
import hu.icellmobilsoft.coffee.cdi.logger.ThisLogger;
import hu.icellmobilsoft.coffee.module.activemq.util.JmsUtil;

/**
 * JMS handler
 *
 * @author imre.scheffer
 * @since 1.0.0
 */
@Dependent
public class JmsHandler {

    @Inject
    @ThisLogger
    private AppLogger log;

    private ConnectionFactory connectionFactory;

    private Connection connection;
    private Session session;
    private MessageProducer producer;

    /**
     * Clears all fields then sets the field {@code connectionFactory}.
     * 
     * @param connectionFactory
     *            {@link ConnectionFactory} to set
     */
    public void init(ConnectionFactory connectionFactory) {
        clear();
        this.connectionFactory = connectionFactory;
    }

    /**
     * Getter for the field {@code connection}.
     *
     * @return {@code connection}
     * @throws JMSException
     *             if connection is not available
     */
    public Connection getConnection() throws JMSException {
        if (connectionFactory == null) {
            throw new IllegalStateException("ConnectionFactory must be set");
        }
        if (connection == null) {
            connection = connectionFactory.createConnection();
        }
        return connection;
    }

    /**
     * Creates JMS {@link Session} with {@link Session#CLIENT_ACKNOWLEDGE} session management.
     *
     * @param transacted
     *            whether session should be transacted
     * @return JMS session
     * @throws JMSException
     *             if session cannot be created
     */
    public Session getSession(boolean transacted) throws JMSException {
        return getSession(transacted, Session.CLIENT_ACKNOWLEDGE);
    }

    /**
     * Creates transacted JMS {@link Session} with {@link Session#CLIENT_ACKNOWLEDGE} transaction management.
     *
     * @return JMS session
     * @throws JMSException
     *             if session cannot be created
     */
    public Session getSession() throws JMSException {
        return getSession(true);
    }

    /**
     * Creates JMS {@link Session}.
     *
     * @param transacted
     *            whether session should be transacted
     * @param trnManagemant
     *            transaction management type (see constant fields of {@link Session})
     * @return JMS session
     * @throws JMSException
     *             if session cannot be created
     */
    public Session getSession(boolean transacted, int trnManagemant) throws JMSException {
        if (session == null) {
            session = getConnection().createSession(transacted, trnManagemant);
        }
        return session;
    }

    /**
     * Getter for the field {@code producer}.
     * 
     * @return {@link MessageProducer}
     */
    public MessageProducer getMessageProducer() {
        return producer;
    }

    /**
     * Setter for the field {@code producer}.
     * 
     * @param producer
     *            {@link MessageProducer} to set
     */
    public void setMessageProducer(MessageProducer producer) {
        this.producer = producer;
    }

    /**
     * Creates {@link MessageProducer} for given {@link Queue} manually. Sets delivery mode to {@link DeliveryMode#PERSISTENT}. If further
     * configurations are not needed for the returned producer, then {@link #setMessageProducer(Queue)} can be used instead.
     *
     * @param queue
     *            JMS {@code Queue}
     * @return JMS producer
     * @throws JMSException
     *             if producer cannot be created
     */
    public MessageProducer createMessageProducer(Queue queue) throws JMSException {
        if (queue == null) {
            throw new NullPointerException("Queue must be set");
        }
        MessageProducer p = getSession().createProducer(queue);
        p.setDeliveryMode(DeliveryMode.PERSISTENT);
        return p;
    }

    /**
     * Creates {@link MessageProducer} for given {@link Queue}. Sets delivery mode to {@link DeliveryMode#PERSISTENT}.
     *
     * @param queue
     *            JMS {@code Queue}
     * @throws JMSException
     *             if producer cannot be created
     * @see #createMessageProducer(Queue)
     */
    public void setMessageProducer(Queue queue) throws JMSException {
        producer = createMessageProducer(queue);
    }

    /**
     * Creates JMS text message.
     *
     * @param text
     *            text message
     * @return JMS {@link TextMessage}
     * @throws JMSException
     *             if message cannot be created
     */
    public TextMessage createTextMessage(String text) throws JMSException {
        TextMessage message = getSession().createTextMessage();
        message.setText(text);
        return message;
    }

    /**
     * Creates JMS text message with given delay time and priority.
     *
     * @param text
     *            text message
     * @param delayInMillis
     *            message delay given in milliseconds
     * @param priority
     *            message priority
     * @return JMS {@link TextMessage}
     * @throws JMSException
     *             if message cannot be created
     */
    public TextMessage createTextMessage(String text, long delayInMillis, int priority) throws JMSException {
        TextMessage message = createTextMessage(text);
        if (delayInMillis > 0) {
            message.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY, delayInMillis);
        }
        message.setJMSPriority(priority);
        return message;
    }

    /**
     * Sets given property value for given {@link TextMessage}.
     * 
     * @param textMessage
     *            {@code TextMessage} to add property to
     * @param property
     *            property to set
     * @param propertyValue
     *            value to set property to
     * @throws JMSException
     *             if property cannot be set
     */
    public void addProperty(TextMessage textMessage, String property, String propertyValue) throws JMSException {
        if (textMessage == null) {
            return;
        }
        textMessage.setStringProperty(property, propertyValue);
    }

    /**
     * Sends JMS {@link Message}. Producer must be set before calling this by {@link #setMessageProducer(Queue)} or
     * {@link #setMessageProducer(MessageProducer)}.
     *
     * @param message
     *            message object
     * @throws JMSException
     *             if message cannot be sent
     * @throws NullPointerException
     *             if producer is null
     */
    public void sendJmsMessage(Message message) throws JMSException {
        if (getMessageProducer() == null) {
            throw new NullPointerException("MessageProducer must be set");
        }
        getMessageProducer().send(message);
    }

    /**
     * Closes JMS connection.
     */
    public void closeConnection() {
        JmsUtil.close(connection);
        clear();
    }

    /**
     * Clears session, producer and connection fields.
     */
    public void clear() {
        session = null;
        producer = null;
        connection = null;
    }

    /**
     * Sends JMS text message. Producer must be set before calling this by {@link #setMessageProducer(Queue)} or
     * {@link #setMessageProducer(MessageProducer)}.
     *
     * @param text
     *            text message
     * @throws JMSException
     *             if message cannot be created or sent
     * @see #createTextMessage(String)
     */
    public void sendSimpleTextMessage(String text) throws JMSException {
        TextMessage message = createTextMessage(text);
        sendJmsMessage(message);
    }

    /**
     * Sends JMS text message with given delay time and priority.
     * 
     * @param text
     *            text message
     * @param delayInMillis
     *            message delay given in milliseconds
     * @param priority
     *            message priority
     * @throws JMSException
     *             if message cannot be created or sent
     * @see #createTextMessage(String, long, int)
     */
    public void sendSimpleDelayedTextMessage(String text, long delayInMillis, int priority) throws JMSException {
        TextMessage message = createTextMessage(text, delayInMillis, priority);
        sendJmsMessage(message);
    }

}
