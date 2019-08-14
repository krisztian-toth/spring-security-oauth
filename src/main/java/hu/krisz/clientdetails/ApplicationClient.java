package hu.krisz.clientdetails;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.provider.ClientDetails;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Custom implementation of {@link ClientDetails}.
 *
 * @author krisztian.toth on 14-8-2019
 */
@SuppressWarnings("squid:S1948")
public class ApplicationClient implements ClientDetails {
    private static final long serialVersionUID = 42L;

    private final String clientId;
    private final Set<String> resourceIds;
    private final String clientSecret;
    private final Set<String> scopes;
    private final Set<String> authorizedGrantTypes;
    private final Set<String> registeredRedirectUri;
    private final Collection<GrantedAuthority> grantedAuthorities;
    private final Integer accessTokenValiditySeconds;
    private final Integer refreshTokenValiditySeconds;
    private final Boolean autoApprove;
    private final Map<String, Object> additionalInformation;

    public ApplicationClient(String clientId, Set<String> resourceIds, String clientSecret, Set<String> scopes,
                             Set<String> authorizedGrantTypes, Set<String> registeredRedirectUri,
                             Collection<GrantedAuthority> grantedAuthorities, Integer accessTokenValiditySeconds,
                             Integer refreshTokenValiditySeconds, Boolean autoApprove,
                             Map<String, Object> additionalInformation) {
        this.clientId = clientId;
        this.resourceIds = resourceIds;
        this.clientSecret = clientSecret;
        this.scopes = scopes;
        this.authorizedGrantTypes = authorizedGrantTypes;
        this.registeredRedirectUri = registeredRedirectUri;
        this.grantedAuthorities = grantedAuthorities;
        this.accessTokenValiditySeconds = accessTokenValiditySeconds;
        this.refreshTokenValiditySeconds = refreshTokenValiditySeconds;
        this.autoApprove = autoApprove;
        this.additionalInformation = additionalInformation;
    }

    @Override
    public String getClientId() {
        return clientId;
    }

    @Override
    public Set<String> getResourceIds() {
        return resourceIds;
    }

    @Override
    public boolean isSecretRequired() {
        return clientSecret != null;
    }

    @Override
    public String getClientSecret() {
        return clientSecret;
    }

    @Override
    public boolean isScoped() {
        return scopes != null && !scopes.isEmpty();
    }

    @Override
    public Set<String> getScope() {
        return scopes;
    }

    @Override
    public Set<String> getAuthorizedGrantTypes() {
        return authorizedGrantTypes;
    }

    @Override
    public Set<String> getRegisteredRedirectUri() {
        return registeredRedirectUri;
    }

    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        return grantedAuthorities;
    }

    @Override
    public Integer getAccessTokenValiditySeconds() {
        return accessTokenValiditySeconds;
    }

    @Override
    public Integer getRefreshTokenValiditySeconds() {
        return refreshTokenValiditySeconds;
    }

    @Override
    public boolean isAutoApprove(String scope) {
        // TODO think of a more sophisticated way
        boolean result = false;
        if (Boolean.TRUE.equals(autoApprove)) {
            result = true;
        }
        return result;
    }

    @Override
    public Map<String, Object> getAdditionalInformation() {
        return additionalInformation;
    }
}
