package hu.krisz.dao.entity.client;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import java.util.Objects;

/**
 * An entity which represents an OAuth2 client. Note that
 * {@link #resourceIds},
 * {@link #scopes},
 * {@link #authorizedGrantTypes},
 * {@link #authorities} and
 * {@link #webServerRedirectUris}
 * are comma separated values. This is sufficient when the number of clients are easily manageable but can be worth to
 * use a different approach if it grows big.
 *
 * @author krisztian.toth on 14-8-2019
 */
@Entity
@Table(name = "client", schema = "oauth_client")
public class OAuthClient {

    /**
     * The ID of the client we create.
     */
    @Id
    private String clientId;

    /**
     * The (usually encoded) secret of the client. Apps which cannot hide the client secret (e.g. SPA, mobile apps)
     * don't require a client secret and a supported grant type must be used, therefore this field is optional.
     */
    @Column
    private String clientSecret;

    /**
     * The resource server handles authenticated requests after an application with this client has obtained an access
     * token. If we want to limit the usage of access tokens created by certain {@link OAuthClient}s we can specify
     * those resource's IDs here. The defined resource IDs are usually contained in the token's "aud" (audience) claim
     * which can be validated by the resource server. The usage of this field is optional.
     */
    @Column
    private String resourceIds;

    /**
     * Scopes are useful when granting access for 3rd party apps to user's accounts. Should be provided.
     *
     * @see <a href="https://oauth.net/2/scope/">https://oauth.net/2/scope/</a>
     */
    @Column
    private String scopes;

    /**
     * A grant type is basically a method to authenticate a user or client. Should be provided.
     *
     * @see <a href="https://oauth.net/2/grant-types/">https://oauth.net/2/grant-types/</a>
     */
    @Column
    private String authorizedGrantTypes;

    /**
     * Some grant types doesn't require a user for authentication. If the client doesn't act on behalf of a user (e.g.
     * client credentials grant type) then these authorities will be bound to the authentication token instead of user
     * authorities. Optional.
     */
    @Column
    private String authorities;

    /**
     * Redirect URIs where the client is allowed to redirect to for authorization. Only relevant when a redirect is
     * necessary to complete the authorization grant, therefore this field is optional.
     */
    @Column
    private String webServerRedirectUris;

    /**
     * Validity for access token in seconds. Should be provided and short-lived.
     */
    @Column
    private Integer accessTokenValidity;

    /**
     * Validity for refresh token in seconds. Should be provided.
     */
    @Column
    private Integer refreshTokenValidity;

    /**
     * A serialised JSON object with any additional information of the client. Optional.
     */
    @Column
    private String additionalInformation;

    /**
     * Either the literal string "true" to skip OAuth user approval or scope patterns where you want to enable auto
     * approval. Optional.
     */
    @Column
    private String autoApprove;

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public String getResourceIds() {
        return resourceIds;
    }

    public String getScopes() {
        return scopes;
    }

    public String getAuthorizedGrantTypes() {
        return authorizedGrantTypes;
    }

    public String getAuthorities() {
        return authorities;
    }

    public String getWebServerRedirectUris() {
        return webServerRedirectUris;
    }

    public Integer getAccessTokenValidity() {
        return accessTokenValidity;
    }

    public Integer getRefreshTokenValidity() {
        return refreshTokenValidity;
    }

    public String getAdditionalInformation() {
        return additionalInformation;
    }

    public String getAutoApprove() {
        return autoApprove;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OAuthClient that = (OAuthClient) o;
        return Objects.equals(clientId, that.clientId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clientId);
    }

    @Override
    public String toString() {
        return "OAuthClient{" +
                "clientId='" + clientId + '\'' +
                ", clientSecret=[PROTECTED]" +
                ", resourceIds='" + resourceIds + '\'' +
                ", scopes='" + scopes + '\'' +
                ", authorizedGrantTypes='" + authorizedGrantTypes + '\'' +
                ", authorities='" + authorities + '\'' +
                ", webServerRedirectUri='" + webServerRedirectUris + '\'' +
                ", accessTokenValidity=" + accessTokenValidity +
                ", refreshTokenValidity=" + refreshTokenValidity +
                ", additionalInformation='" + additionalInformation + '\'' +
                ", autoApprove=" + autoApprove +
                '}';
    }
}
