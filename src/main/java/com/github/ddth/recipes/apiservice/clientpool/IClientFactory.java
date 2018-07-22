package com.github.ddth.recipes.apiservice.clientpool;

/**
 * Factory to create API client.
 *
 * @param <C>
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since 0.2.0
 */
public interface IClientFactory<C> {
    /**
     * Create a new API client object.
     *
     * @param serverIndexHash
     * @return
     * @throws Exception
     */
    C create(int serverIndexHash) throws Exception;

    /**
     * Destroy an API client object.
     *
     * @param client
     */
    void destroy(C client);

    /**
     * Return number of servers.
     *
     * @return
     */
    int getNumServers();

    /**
     * Return {@code true} if exception is retryable.
     *
     * @param t
     * @return
     */
    boolean isRetryable(Throwable t);
}
