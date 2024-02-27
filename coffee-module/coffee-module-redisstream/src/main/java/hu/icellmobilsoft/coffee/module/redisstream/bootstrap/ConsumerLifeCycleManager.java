/*-
 * #%L
 * Coffee
 * %%
 * Copyright (C) 2020 - 2024 i-Cell Mobilsoft Zrt.
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
package hu.icellmobilsoft.coffee.module.redisstream.bootstrap;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.BeforeDestroyed;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;

import hu.icellmobilsoft.coffee.se.logging.Logger;

/**
 * Managing the lifecycle of Redis consumers and assisting with graceful shutdown logic.
 * 
 * @author czenczl
 * @since 2.5.0
 */
@ApplicationScoped
public class ConsumerLifeCycleManager {

    /**
     * Default constructor, constructs a new object.
     */
    public ConsumerLifeCycleManager() {
    }

    /**
     * Boolean variable indicating the end condition for consumer loop
     */
    public static boolean ENDLOOP;
    /**
     * Variable indicating the running consumer count
     */
    public static final AtomicInteger CONSUMER_COUNTER = new AtomicInteger(0);
    /**
     * Semaphore used for coordinating threads. The initial value of this semaphore is set to 0.
     */
    public static final Semaphore SEMAPHORE = new Semaphore(0);

    @Inject
    private Logger log;

    /**
     * When a context is about to be destroyed, we gracefully wait for the redis consumers to finish processes.
     * 
     * @param init
     *            ignored
     */
    public void stop(@Observes @BeforeDestroyed(ApplicationScoped.class) Object init) {
        log.info("Initiate shutdown stopping consumers");
        int consumerCount = CONSUMER_COUNTER.get();
        log.info("Stopping redis consumers, count: [{0}]", consumerCount);
        // break consumer loop
        stopLoop();

        if (consumerCount < 1) {
            return;
        }

        // wait for all consuemr to finish
        try {
            SEMAPHORE.acquire();
            log.info("Redis consumers stopped");
        } catch (InterruptedException e) {
            log.error("Error occured while waiting for consumers to finish while shutting down", e);
        }
    }

    /**
     * Stop endless stream reading
     */
    public static void stopLoop() {
        ENDLOOP = true;
    }

}
