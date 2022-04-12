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

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;

import com.google.common.base.Joiner;

import hu.icellmobilsoft.coffee.cdi.logger.AppLogger;
import hu.icellmobilsoft.coffee.cdi.logger.ThisLogger;
import hu.icellmobilsoft.coffee.dto.exception.TechnicalException;
import hu.icellmobilsoft.coffee.dto.exception.enums.CoffeeFaultType;
import hu.icellmobilsoft.coffee.module.activemq.util.JmsUtil;

/**
 * Abstract JMS client class.
 *
 * @author imre.scheffer
 * @since 1.0.0
 */
@Dependent
public abstract class AbstractJmsClient implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Logger
     */
    @Inject
    @ThisLogger
    private AppLogger log;

    /**
     * Injected JMS handler
     */
    @Inject
    private JmsHandler jmsHandler;

    /**
     * Getter for the JMS {@link ConnectionFactory}.
     *
     * @return {@code ConnectionFactory}.
     */
    public abstract ConnectionFactory getConnectionFactory();

    /**
     * Getter for the JMS {@link Queue}.
     *
     * @return {@code Queue}.
     */
    public abstract Queue getQueue();

    /**
     * Sends JMS text message with given delay time and priority.
     *
     * @param content
     *            text message to send
     * @param delayInMillis
     *            message delay given in milliseconds
     * @param priority
     *            message priority
     * @throws TechnicalException
     *             if JMS exception occurs
     */
    public void sendSimpleDelayed(String content, long delayInMillis, int priority) throws TechnicalException {
        log.debug(">> JmsClient.sendSimpleDelayed(content: [{0}], delayInMillis: [{1}], priority: [{2}]", content, delayInMillis, priority);
        try {
            jmsHandler.init(getConnectionFactory());
            jmsHandler.setMessageProducer(getQueue());
            try {
                jmsHandler.sendSimpleDelayedTextMessage(content, delayInMillis, priority);
            } catch (JMSException e) {
                log.error("Error in jms TextMessage with content:[" + content + "],delayInMillis: [" + delayInMillis + "], priority: [" + priority
                        + "], sending:", e);
            }
        } catch (JMSException e) {
            throw new TechnicalException(CoffeeFaultType.OPERATION_FAILED, "Error in get jms session: " + e.getLocalizedMessage(), e);
        } finally {
            JmsUtil.close(jmsHandler.getMessageProducer());
            jmsHandler.closeConnection();
            log.debug("<< JmsClient.sendSimpleDelayed(content: [{0}], delayInMillis: [{1}], priority: [{2}]", content, delayInMillis, priority);
        }
    }

    /**
     * Sends JMS text message.
     *
     * @param content
     *            text message
     * @throws TechnicalException
     *             if JMS exception occurs
     */
    public void sendSimple(String content) throws TechnicalException {
        log.debug(">> JmsClient.sendSimple(content: [{0}]", content);
        try {
            jmsHandler.init(getConnectionFactory());
            jmsHandler.setMessageProducer(getQueue());
            try {
                jmsHandler.sendSimpleTextMessage(content);
            } catch (JMSException e) {
                log.error("Error in jms TextMessage with content:[" + content + "] sending:", e);
            }
        } catch (JMSException e) {
            throw new TechnicalException(CoffeeFaultType.OPERATION_FAILED, "Error in get jms session: " + e.getLocalizedMessage(), e);
        } finally {
            JmsUtil.close(jmsHandler.getMessageProducer());
            jmsHandler.closeConnection();
            log.debug("<< JmsClient.sendSimple(content: [{0}]", content);
        }
    }

    /**
     * Sends multiple JMS text messages in batch.
     *
     * @param contents
     *            {@link List} of text messages
     * @throws TechnicalException
     *             if JMS exception occurs
     */
    public void sendBatch(List<String> contents) throws TechnicalException {
        sendBatch(contents, null);
    }

    /**
     * Sends multiple JMS text messages in batch.
     *
     * @param contents
     *            {@link List} of text messages
     * @param propertyValues
     *            properties to add to the messages
     * @throws TechnicalException
     *             if JMS exception occurs
     */
    public void sendBatch(List<String> contents, Map<String, String> propertyValues) throws TechnicalException {
        log.debug(">> JmsClient.sendBatch(List<String>, Map<String, String>)");
        MessageProducer producer = null;
        try {
            jmsHandler.init(getConnectionFactory());
            // 1. verzio (tovabb konfigolhato)
            jmsHandler.getSession(true, Session.CLIENT_ACKNOWLEDGE);
            // producer kaphat tovabbi konfigot (lejarati ido stb...)
            producer = jmsHandler.createMessageProducer(getQueue());
            jmsHandler.setMessageProducer(producer);
            // 2. verzio (egyszeru)
            // jmsUtil.setMessageProducer(queue);

            // jms kuldese
            for (String content : contents) {
                try {
                    // 1. verzio (tovabb konfigolhato)
                    TextMessage message = jmsHandler.createTextMessage(content);
                    addProperties(message, propertyValues);
                    jmsHandler.sendJmsMessage(message);
                    // 2. verzio (egyszeru)
                    // jmsHandler.sendSimpleTextMessage(id);
                } catch (JMSException e) {
                    log.error("Error in jms TextMessage with content:[" + content + "], propertyValues: " + propertyValues != null
                            ? Joiner.on("|").withKeyValueSeparator("_").join(propertyValues) : "empty", e);
                }
                log.trace("Sent [{0}] jms message to [{1}] queue!", content, getQueue().getQueueName());
            }
        } catch (JMSException e) {
            throw new TechnicalException(CoffeeFaultType.OPERATION_FAILED, "Error in get jms session: " + e.getLocalizedMessage(), e);
        } finally {
            JmsUtil.close(producer);
            jmsHandler.closeConnection();
        }
    }

    private void addProperties(TextMessage message, Map<String, String> propertyValues) throws JMSException {
        if (message == null || propertyValues == null || propertyValues.isEmpty()) {
            return;
        }
        Iterator<Entry<String, String>> it = propertyValues.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> pair = (Map.Entry<String, String>) it.next();
            jmsHandler.addProperty(message, pair.getKey(), pair.getValue());
        }
    }

}
