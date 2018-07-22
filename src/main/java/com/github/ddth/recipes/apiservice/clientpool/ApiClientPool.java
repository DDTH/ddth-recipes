package com.github.ddth.recipes.apiservice.clientpool;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.BaseObjectPoolConfig;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import java.io.Closeable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Random;
import java.util.UUID;

/**
 * Pool of API clients.
 *
 * @param <C>
 *         Client class
 * @param <I>
 *         Client interface
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since 0.2.0
 */
public class ApiClientPool<C, I> implements Closeable {
    private Class<C> clientClass;
    private Class<I> clientInterface;
    private RetryPolicy retryPolicy;
    private ObjectPool<I> clientPool;
    private IClientFactory<C> clientFactory;
    private GenericObjectPoolConfig poolConfig;

    public ApiClientPool() {
        // EMPTY
    }

    public ApiClientPool(Class<C> clientClass, Class<I> clientInterface,
            IClientFactory<C> clientFactory) {
        this(clientClass, clientInterface, clientFactory, null, null);
    }

    public ApiClientPool(Class<C> clientClass, Class<I> clientInterface,
            IClientFactory<C> clientFactory, GenericObjectPoolConfig poolConfig) {
        this(clientClass, clientInterface, clientFactory, poolConfig, null);
    }

    public ApiClientPool(Class<C> clientClass, Class<I> clientInterface,
            IClientFactory<C> clientFactory, RetryPolicy retryPolicy) {
        this(clientClass, clientInterface, clientFactory, null, retryPolicy);
    }

    public ApiClientPool(Class<C> clientClass, Class<I> clientInterface,
            IClientFactory<C> clientFactory, GenericObjectPoolConfig poolConfig,
            RetryPolicy retryPolicy) {
        this.clientClass = clientClass;
        this.clientInterface = clientInterface;
        this.poolConfig = poolConfig;
        this.clientFactory = clientFactory;
        this.retryPolicy = retryPolicy;
    }

    /*----------------------------------------------------------------------*/

    /**
     * Getter for {@link #clientClass}.
     *
     * @return
     */
    public Class<C> getClientClass() {
        return clientClass;
    }

    /**
     * Setter for {@link #clientClass}.
     *
     * @param clientClass
     * @return
     */
    public ApiClientPool<C, I> setClientClass(Class<C> clientClass) {
        this.clientClass = clientClass;
        return this;
    }

    /**
     * Getter for {@link #clientInterface}.
     *
     * @return
     */
    public Class<I> getClientInterface() {
        return clientInterface;
    }

    /**
     * Setter for {@link #clientInterface}.
     *
     * @param clientInterface
     * @return
     */
    public ApiClientPool<C, I> setClientInterface(Class<I> clientInterface) {
        this.clientInterface = clientInterface;
        return this;
    }

    /**
     * Getter for {@link #retryPolicy}.
     *
     * @return
     */
    public RetryPolicy getRetryPolicy() {
        return retryPolicy;
    }

    /**
     * Setter for {@link #retryPolicy}.
     *
     * @param retryPolicy
     * @return
     */
    public ApiClientPool<C, I> setRetryPolicy(RetryPolicy retryPolicy) {
        this.retryPolicy = retryPolicy;
        return this;
    }

    /**
     * Getter for {@link #clientFactory}.
     *
     * @return
     */
    public IClientFactory<C> getClientFactory() {
        return clientFactory;
    }

    /**
     * Setter for {@link #clientFactory}.
     *
     * @param clientFactory
     * @return
     */
    public ApiClientPool<C, I> setClientFactory(IClientFactory<C> clientFactory) {
        this.clientFactory = clientFactory;
        return this;
    }

    /**
     * Getter for {@link #poolConfig}.
     *
     * @return
     */
    public BaseObjectPoolConfig getPoolConfig() {
        return poolConfig;
    }

    /**
     * Setter for {@link #poolConfig}.
     *
     * @param poolConfig
     * @return
     */
    public ApiClientPool<C, I> setPoolConfig(GenericObjectPoolConfig poolConfig) {
        this.poolConfig = poolConfig;
        return this;
    }

    /*----------------------------------------------------------------------*/
    public ApiClientPool<C, I> init() {
        if (clientPool == null) {
            if (clientFactory == null) {
                throw new IllegalStateException(
                        "No " + IClientFactory.class.getName() + " instance found!");
            }
            if (retryPolicy == null) {
                retryPolicy = RetryPolicy.DEFAULT_RETRY_POLICY;
            }
            clientPool = new GenericObjectPool<>(new ClientPooledFactory());
            if (poolConfig == null) {
                ((GenericObjectPool<I>) clientPool).setBlockWhenExhausted(true);
                ((GenericObjectPool<I>) clientPool).setTestOnBorrow(false);
                ((GenericObjectPool<I>) clientPool).setTestWhileIdle(true);
            } else {
                ((GenericObjectPool<I>) clientPool).setConfig(poolConfig);
            }
        }
        return this;
    }

    public void destroy() {
        if (clientPool != null) {
            try {
                clientPool.close();
            } finally {
                clientPool = null;
            }
        }
    }

    @Override
    public void close() {
        destroy();
    }

    /**
     * Obtains an API client object from pool.
     *
     * @return
     * @throws Exception
     */
    public I borrowObject() throws Exception {
        return clientPool.borrowObject();
    }

    /**
     * Returns a borrowed API client object back to pool.
     *
     * @param borrowedClient
     */
    public void returnObject(I borrowedClient) {
        if (borrowedClient != null) {
            try {
                clientPool.returnObject(borrowedClient);
            } catch (Exception e) {
                throw e instanceof RuntimeException
                        ? (RuntimeException) e
                        : new RuntimeException(e);
            }
        }
    }

    /*----------------------------------------------------------------------*/
    private final class ClientPooledFactory extends BasePooledObjectFactory<I> {
        @Override
        public I create() {
            Object proxyObj = Proxy.newProxyInstance(clientInterface.getClassLoader(),
                    new Class<?>[] { clientInterface },
                    new ReconnectingClientProxy(retryPolicy.clone()));
            return (I) proxyObj;
        }

        @Override
        public PooledObject<I> wrap(I obj) {
            return new DefaultPooledObject<>(obj);
        }

        @Override
        public void destroyObject(PooledObject<I> pooledObj) {
            I obj = pooledObj.getObject();
            if (Proxy.isProxyClass(obj.getClass())) {
                InvocationHandler iv = Proxy.getInvocationHandler(obj);
                if (iv instanceof ApiClientPool.ReconnectingClientProxy) {
                    ((ReconnectingClientProxy) iv).destroy();
                }
            }
        }
    }

    /*----------------------------------------------------------------------*/

    private Random RANDOM = new Random(System.currentTimeMillis());

    /**
     * Helper proxy class. Attempts to call method on proxy object wrapped in try/catch. If it
     * fails, it attempts a reconnect and tries the method again.
     *
     * <p>
     * Credit: http://blog.liveramp.com/2014/04/10/reconnecting-thrift-client/
     * </p>
     */
    protected class ReconnectingClientProxy implements InvocationHandler {
        private RetryPolicy retryPolicy;
        private UUID id = UUID.randomUUID();
        private C clientObj;

        public ReconnectingClientProxy(RetryPolicy retryPolicy) {
            this.retryPolicy = retryPolicy;
        }

        /**
         * {@inheritDoc}
         */
        @SuppressWarnings("unchecked")
        @Override
        public boolean equals(Object obj) {
            ReconnectingClientProxy other = (ReconnectingClientProxy) obj;
            return id.equals(other.id);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int hashCode() {
            return id.hashCode();
        }

        public void destroy() {
            if (clientObj != null) {
                try {
                    clientFactory.destroy(clientObj);
                } catch (Exception e) {
                }
            }
            clientObj = null;
        }

        /**
         * Creates a new API client object.
         *
         * @param serverIndexHash
         * @return
         * @throws Exception
         */
        private C newClientObj(int serverIndexHash) throws Exception {
            return clientFactory.create(serverIndexHash);
        }

        private C getClientObj(boolean renew, int serverIndexHash) throws Exception {
            if (clientObj == null || renew) {
                if (clientObj != null) {
                    clientFactory.destroy(clientObj);
                }
                clientObj = newClientObj(serverIndexHash);
            }
            return clientObj;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (StringUtils.equals("hashCode", method.getName())) {
                return hashCode();
            }
            if (StringUtils.equals("equals", method.getName())) {
                return equals(args[0]);
            }
            if (StringUtils.equals("toString", method.getName())) {
                return toString();
            }

            retryPolicy.reset();
            return invokeWithRetries(proxy, method, args);
        }

        private int calcServerIndexHash() {
            int serverIndexHash = 0;
            int numServers = clientFactory.getNumServers();
            switch (retryPolicy.getRetryType()) {
            case FAILOVER:
                serverIndexHash = retryPolicy.getCounter();
                break;
            case ROUND_ROBIN:
                if (retryPolicy.getCounter() == 0) {
                    serverIndexHash = RANDOM.nextInt(Short.MAX_VALUE) % numServers;
                } else {
                    serverIndexHash = retryPolicy.getLastServerIndexHash() + 1;
                }
                retryPolicy.setLastServerIndexHash(serverIndexHash);
                break;
            case RANDOM_FAILOVER:
                if (retryPolicy.getCounter() == 0 || numServers < 2) {
                    serverIndexHash = 0;
                } else {
                    serverIndexHash = 1 + (RANDOM.nextInt(Short.MAX_VALUE) % (numServers - 1));
                }
                break;
            case RANDOM:
            default:
                serverIndexHash = RANDOM.nextInt(Short.MAX_VALUE);
                break;
            }
            return serverIndexHash;
        }

        private Object invokeWithRetries(Object proxy, Method method, Object[] args)
                throws Throwable {
            boolean hasError = false;
            while (!retryPolicy.isMaxRetriesExceeded()) {
                C oldClientObj = clientObj;
                int serverIndexHash = calcServerIndexHash();
                try {
                    C clientObj = getClientObj(hasError, serverIndexHash);
                    return method.invoke(clientObj, args);
                } catch (InvocationTargetException e) {
                    hasError = true;
                    Throwable target = e.getTargetException();
                    if (clientFactory.isRetryable(target)) {
                        if (clientObj == oldClientObj) {
                            retryPolicy.sleep();
                        } else {
                            retryPolicy.incCounter();
                        }
                        if (retryPolicy.isMaxRetriesExceeded()) {
                            throw target;
                        } else {
                            continue;
                        }
                    }
                    throw e;
                }
            }
            return null;
        }

    }

}
