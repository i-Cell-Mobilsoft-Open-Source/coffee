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
     * <p>init.</p>
     */
    public void init(ConnectionFactory connectionFactory) {
        clear();
        this.connectionFactory = connectionFactory;
    }

    /**
     * <p>Getter for the field <code>connection</code>.</p>
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
     * JMS session letrehozasa, es Session.CLIENT_ACKNOWLEDG
     *
     * @param transacted
     * @return JMS session
     * @throws JMSException
     */
    public Session getSession(boolean transacted) throws JMSException {
        return getSession(transacted, Session.CLIENT_ACKNOWLEDGE);
    }

    /**
     * JMS session letrehozasa, default transacted true, es Session.CLIENT_ACKNOWLEDG
     *
     * @return JMS session
     * @throws JMSException
     */
    public Session getSession() throws JMSException {
        return getSession(true);
    }

    /**
     * JMS session letrehozasa
     *
     * @return JMS session
     * @throws JMSException
     */
    public Session getSession(boolean transacted, int trnManagemant) throws JMSException {
        if (session == null) {
            session = getConnection().createSession(transacted, trnManagemant);
        }
        return session;
    }

    /**
     * <p>getMessageProducer.</p>
     */
    public MessageProducer getMessageProducer() {
        return producer;
    }

    /**
     * <p>setMessageProducer.</p>
     */
    public void setMessageProducer(MessageProducer producer) {
        this.producer = producer;
    }

    /**
     * Producer manualis letrehozasa. Ha nem kell kulon konfigolni letrehozas utan akkor hasznalhato a {@link #setMessageProducer(Queue)} is
     * DeliveryMode set to Persistent!
     *
     * @param queue
     * @return JMS producer
     * @throws JMSException
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
     * <p>setMessageProducer.</p>
     */
    public void setMessageProducer(Queue queue) throws JMSException {
        producer = createMessageProducer(queue);
    }

    /**
     * <p>createTextMessage.</p>
     */
    public TextMessage createTextMessage(String text) throws JMSException {
        TextMessage message = getSession().createTextMessage();
        message.setText(text);
        return message;
    }

    /**
     * <p>createTextMessage.</p>
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
     * <p>addProperty.</p>
     */
    public void addProperty(TextMessage textMessage, String property, String propertyValue) throws JMSException {
        if (textMessage == null) {
            return;
        }
        textMessage.setStringProperty(property, propertyValue);
    }

    /**
     * JMS Text message elkuldese. Producert be kell allitani elotte vagy a {@link #setMessageProducer(Queue)} vagy a
     * {@link #setMessageProducer(MessageProducer)} segitsegevel
     *
     * @param message
     *            message objektum
     * @throws JMSException
     */
    public void sendJmsMessage(Message message) throws JMSException {
        if (getMessageProducer() == null) {
            throw new NullPointerException("MessageProducer must be set");
        }
        getMessageProducer().send(message);
    }

    /**
     * <p>closeConnection.</p>
     */
    public void closeConnection() {
        JmsUtil.close(connection);
        clear();
    }

    /**
     * <p>clear.</p>
     */
    public void clear() {
        session = null;
        producer = null;
        connection = null;
    }

    /**
     * JMS Text message elkuldese. Producert be kell allitani elotte vagy a {@link #setMessageProducer(Queue)} vagy a
     * {@link #setMessageProducer(MessageProducer)} segitsegevel
     *
     * @param text
     *            kuldott szoveg a message-ben
     * @throws JMSException
     */
    public void sendSimpleTextMessage(String text) throws JMSException {
        TextMessage message = createTextMessage(text);
        sendJmsMessage(message);
    }

    /**
     * <p>sendSimpleDelayedTextMessage.</p>
     */
    public void sendSimpleDelayedTextMessage(String text, long delayInMillis, int priority) throws JMSException {
        TextMessage message = createTextMessage(text, delayInMillis, priority);
        sendJmsMessage(message);
    }

}
