/*-
 * #%L
 * Coffee
 * %%
 * Copyright (C) 2020 - 2026 i-Cell Mobilsoft Zrt.
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
package hu.icellmobilsoft.coffee.module.config.watcher;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;

import hu.icellmobilsoft.coffee.se.logging.Logger;

/**
 * Provides a simplified interface for watching file modifications. Creates a new thread which blocks until the given file is modified. The
 * {@link #close()} method can be used to stop the thread.
 *
 * @author martin.nagy
 * @since 2.10.1
 */
public class ConfigFileWatcher implements AutoCloseable {
    private static final Logger log = Logger.getLogger(ConfigFileWatcher.class);

    private final Thread thread;
    private final WatchService watchService;
    private final Collection<FileChangeListener> listeners = new ArrayList<>();
    private final AtomicBoolean running = new AtomicBoolean(true);

    /**
     * Constructs a ConfigFileWatcher for the specified file path. A new thread is created to monitor file changes continuously.
     *
     * @param path
     *            the file path to be monitored for changes
     */
    public ConfigFileWatcher(Path path) {
        try {
            watchService = path.getFileSystem().newWatchService();
            path.getParent().register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot create watch service for path: " + path, e);
        }

        thread = new Thread(() -> {
            pollEvents(path);
            try {
                watchService.close();
            } catch (IOException e) {
                throw new UncheckedIOException("Cannot close watch service for path: " + path, e);
            }
            log.info("Watch service closed for path: " + path);
        }, "ConfigFileWatcher[" + path + "]");
        thread.start();
    }

    private void pollEvents(Path path) {
        while (true) {
            WatchKey key = null;
            try {
                key = watchService.take();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                if (!running.get()) {
                    return;
                }
            }

            for (WatchEvent<?> event : key.pollEvents()) {
                try {
                    if (Files.isSameFile(path, path.getParent().resolve((Path) event.context()))) {
                        listeners.forEach(listener -> listener.onFileChange(path));
                    }
                } catch (Exception e) {
                    log.error("Error occurred while notifying listeners for path change: " + path, e);
                }
            }

            boolean valid = key.reset();
            if (!valid) {
                throw new IllegalStateException("Watch key is cancelled or watch service is already closed for path: " + path);
            }
        }
    }

    /**
     * Registers a FileChangeListener that will be notified when the monitored file is modified.
     *
     * @param listener
     *            the listener to be added to the collection of listeners notified of file changes
     */
    public void addListener(FileChangeListener listener) {
        listeners.add(listener);
    }

    @Override
    public void close() {
        running.set(false);
        thread.interrupt();
    }

    /**
     * Functional interface to listen for file change events. Implementations of this interface are used to define actions executed when a file being
     * monitored is modified.
     */
    @FunctionalInterface
    public interface FileChangeListener {

        /**
         * Handles notifications when a file monitored for changes is modified. This method is invoked to perform actions in response to file change
         * events.
         *
         * @param path
         *            the file path of the modified file that triggered the event
         */
        void onFileChange(Path path);
    }
}
