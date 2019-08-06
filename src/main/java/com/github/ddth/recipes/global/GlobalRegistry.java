package com.github.ddth.recipes.global;

import java.util.Optional;
import java.util.Stack;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.ddth.commons.utils.ValueUtils;

/**
 * To store/retrieve/share data with application via static mechanism.
 *
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since 0.3.0
 */
public class GlobalRegistry {
    private final static Logger LOGGER = LoggerFactory.getLogger(GlobalRegistry.class);

    /*----------------------------------------------------------------------*/

    private final static Stack<Runnable> shutdownHooks = new Stack<>();

    /**
     * Add a shutdown hook, which to be called right before application's shutdown.
     *
     * @param r
     */
    public static void addShutdownHook(Runnable r) {
        synchronized (shutdownHooks) {
            shutdownHooks.add(r);
            if (shutdownHooks.size() == 1) {
                Runtime.getRuntime().addShutdownHook(new Thread(GlobalRegistry::shutdownHook));
            }
        }
    }

    private static void shutdownHook() {
        while (!shutdownHooks.isEmpty()) {
            try {
                shutdownHooks.pop().run();
            } catch (Exception e) {
                LOGGER.warn(e.getMessage(), e);
            }
        }
    }

    /*----------------------------------------------------------------------*/

    private final static ConcurrentMap<String, Object> storage = new ConcurrentHashMap<>();

    /**
     * Remove an item from global storage.
     *
     * @param key
     * @return the previous value associated with {@code key}, or {@code null} if there was no
     * mapping for {@code key}.
     */
    public static Object removeFromGlobalStorage(String key) {
        return storage.remove(key);
    }

    /**
     * Put an item to global storage.
     *
     * @param key
     * @param value
     * @return the previous value associated with {@code key}, or {@code null} if there was no
     * mapping for {@code key}.
     */
    public static Object putToGlobalStorage(String key, Object value) {
        if (value == null) {
            return removeFromGlobalStorage(key);
        } else {
            return storage.put(key, value);
        }
    }

    /**
     * Get an item from global storage.
     *
     * @param key
     * @return
     */
    public static Object getFromGlobalStorage(String key) {
        return storage.get(key);
    }

    /**
     * Get an item from global storage.
     *
     * @param key
     * @return
     */
    public static Optional<Object> getFromGlobalStorageOptional(String key) {
        return Optional.ofNullable(getFromGlobalStorage(key));
    }

    /**
     * Get an item from global storage.
     *
     * @param key
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T getFromGlobalStorage(String key, Class<T> clazz) {
        Object value = getFromGlobalStorage(key);
        return ValueUtils.convertValue(value, clazz);
    }

    /**
     * Get an item from global storage.
     *
     * @param key
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> Optional<T> getFromGlobalStorageOptional(String key, Class<T> clazz) {
        return Optional.ofNullable(getFromGlobalStorage(key, clazz));
    }
}
