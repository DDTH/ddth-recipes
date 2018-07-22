package com.github.ddth.recipes.apiservice.auth;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * An implementation of trust manager that doesn't actually verify the certificates and just passes
 * everything.
 *
 * <p>This is very bad practice and should NOT be used in production, i.e. for non-production
 * environments only!</p>
 *
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since 0.2.0
 */
public class TrustAllTrustManager implements X509TrustManager {

    public final static TrustAllTrustManager INSTANCE = new TrustAllTrustManager();
    public final static TrustAllTrustManager[] TRUST_ALL_CERTS = new TrustAllTrustManager[] {
            INSTANCE };
    public final static SSLContext TRUST_ALL_SSL_CONTEXT;
    public final static SSLSocketFactory TRUST_ALL_SSL_SOCKET_FACTORY;

    static {
        try {
            TRUST_ALL_SSL_CONTEXT = SSLContext.getInstance("SSL");
            TRUST_ALL_SSL_CONTEXT.init(null, TRUST_ALL_CERTS, new SecureRandom());
            TRUST_ALL_SSL_SOCKET_FACTORY = TRUST_ALL_SSL_CONTEXT.getSocketFactory();
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void checkClientTrusted(X509Certificate[] x509Certificates, String s)
            throws CertificateException {
        //EMPTY
    }

    @Override
    public void checkServerTrusted(X509Certificate[] x509Certificates, String s)
            throws CertificateException {
        //EMPTY
    }

    private final static X509Certificate[] ACCEPTED_ISSUERS = new X509Certificate[] {};

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return ACCEPTED_ISSUERS;
    }
}
