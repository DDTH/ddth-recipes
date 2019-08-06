package com.github.ddth.recipes.apiservice;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * API authentication/authorization info.
 *
 * @author Thanh Nguyen <btnguyen2k@gmail.com>
 * @since v0.2.0
 */
public class ApiAuth implements Cloneable {
    /**
     * A null/empty API auth.
     */
    public final static ApiAuth NULL_API_AUTH = new ApiAuth(null, null);

    private String appId, accessToken;
    private Set<String> allowedApis = new HashSet<>();

    public ApiAuth(String appId) {
        this(appId, null);
    }

    public ApiAuth(String appId, String accessToken) {
        this.appId = appId;
        this.accessToken = accessToken;
    }

    /**
     * ID of application/client who is calling the API.
     *
     * @return
     */
    public String getAppId() {
        return appId;
    }

    /**
     * ID of application/client who is calling the API.
     *
     * @param appId
     * @return
     */
    public ApiAuth setAppId(String appId) {
        this.appId = appId;
        return this;
    }

    /**
     * Access token of the application/client who is calling the API.
     *
     * @return
     */
    public String getAccessToken() {
        return accessToken;
    }

    /**
     * Access token of the application/client who is calling the API.
     *
     * @param accessToken
     * @return
     */
    public ApiAuth setAccessToken(String accessToken) {
        this.accessToken = accessToken;
        return this;
    }

    /**
     * List of APIs the application/client is allowed to access.
     *
     * @return
     */
    public Set<String> getAllowedApis() {
        return Collections.unmodifiableSet(allowedApis);
    }

    /**
     * List of APIs the application/client is allowed to access.
     *
     * @param allowedApis
     * @return
     */
    public ApiAuth setAllowedApis(Collection<String> allowedApis) {
        this.allowedApis.clear();
        if (allowedApis != null) {
            this.allowedApis.addAll(allowedApis);
        }
        return this;
    }

    /**
     * Add API to allowed list.
     *
     * @param allowedApis
     * @return
     */
    public ApiAuth addAllowedApis(String... allowedApis) {
        if (allowedApis != null) {
            for (String allowedApi : allowedApis) {
                this.allowedApis.add(allowedApi);
            }
        }
        return this;
    }

    /**
     * Check if the application/client is allowed to access a specific API.
     *
     * @param apiName
     * @return
     */
    public boolean isApiAllowed(String apiName) {
        boolean allowedAllApis = allowedApis == null || allowedApis.size() == 0 || allowedApis.contains("*");
        return allowedAllApis || allowedApis.contains(apiName);
    }

    /**
     * Authenticate an API call.
     *
     * @param auth
     * @return
     */
    public boolean authenticate(String apiName, ApiAuth auth) {
        return StringUtils.equals(getAppId(), auth.getAppId()) && StringUtils
                .equals(getAccessToken(), auth.getAccessToken()) && isApiAllowed(apiName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        HashCodeBuilder hcb = new HashCodeBuilder(19, 81);
        hcb.append(getAppId()).append(getAccessToken());
        return hcb.toHashCode();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof ApiAuth) {
            ApiAuth other = (ApiAuth) obj;
            return StringUtils.equals(getAppId(), other.getAppId()) && StringUtils
                    .equals(getAccessToken(), other.getAccessToken());
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ApiAuth clone() {
        try {
            ApiAuth clone = (ApiAuth) super.clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * Note: {@link #accessToken} is masked.
     * </p>
     */
    @Override
    public String toString() {
        String maskedAccessToken = "***";
        if (accessToken != null && accessToken.length() > 4) {
            maskedAccessToken = accessToken.substring(0, 4) + maskedAccessToken;
        }
        ToStringBuilder tsb = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
        tsb.append("app-id", appId).append("access-token", maskedAccessToken).append("allowed-apis", allowedApis);
        return tsb.toString();
    }

    /**
     * Same as {@link #toString()} but {@link #accessToken} is fully displayed.
     *
     * @return
     */
    public String toStringNonSecure() {
        ToStringBuilder tsb = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE);
        tsb.append("app-id", appId).append("access-token", accessToken).append("allowed-apis", allowedApis);
        return tsb.toString();
    }
}
