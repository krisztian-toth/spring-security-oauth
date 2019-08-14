package hu.krisz.dao.entity.client;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * An entity which represents an OAuth2 client.
 *
 * @author krisztian.toth on 14-8-2019
 */
@Entity
@Table(name = "client", schema = "oauth_client")
public class OAuthClient {
    @Id
    private String clientId;

    @Column
    private String clientSecret;

    @ManyToMany(cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE,
            CascadeType.REFRESH,
            CascadeType.DETACH
    })
    @JoinTable(
            name = "client_resource",
            schema = "oauth_client",
            joinColumns = @JoinColumn(name = "client_id"),
            inverseJoinColumns = @JoinColumn(name = "resource_id")
    )
    private List<OAuthResource> resourceIds = new ArrayList<>();

    @ManyToMany(cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE,
            CascadeType.REFRESH,
            CascadeType.DETACH
    })
    @JoinTable(
            name = "client_scope",
            schema = "oauth_client",
            joinColumns = @JoinColumn(name = "client_id"),
            inverseJoinColumns = @JoinColumn(name = "scope")
    )
    private List<OAuthScope> scopes = new ArrayList<>();

    @ManyToMany(cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE,
            CascadeType.REFRESH,
            CascadeType.DETACH
    })
    @JoinTable(
            name = "client_grant",
            schema = "oauth_client",
            joinColumns = @JoinColumn(name = "client_id"),
            inverseJoinColumns = @JoinColumn(name = "grant")
    )
    private List<OAuthGrantType> authorizedGrantTypes = new ArrayList<>();

    @ManyToMany(cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE,
            CascadeType.REFRESH,
            CascadeType.DETACH
    })
    @JoinTable(
            name = "client_authority",
            schema = "oauth_client",
            joinColumns = @JoinColumn(name = "client_id"),
            inverseJoinColumns = @JoinColumn(name = "authority")
    )
    private List<OAuthAuthority> authorities;

    @Column
    private String webServerRedirectUri;

    @Column(nullable = false)
    private Integer accessTokenValidity;

    @Column(nullable = false)
    private Integer refreshTokenValidity;

    @Column
    private String additionalInformation;

    @Column
    private Boolean autoApprove;

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public List<OAuthResource> getResourceIds() {
        return resourceIds;
    }

    public List<OAuthScope> getScopes() {
        return scopes;
    }

    public List<OAuthGrantType> getAuthorizedGrantTypes() {
        return authorizedGrantTypes;
    }

    public List<OAuthAuthority> getAuthorities() {
        return authorities;
    }

    public String getWebServerRedirectUri() {
        return webServerRedirectUri;
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

    public Boolean getAutoApprove() {
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
                ", clientSecret=[PROTECTED]'\'" +
                ", webServerRedirectUri='" + webServerRedirectUri + '\'' +
                ", accessTokenValidity=" + accessTokenValidity +
                ", refreshTokenValidity=" + refreshTokenValidity +
                ", additionalInformation='" + additionalInformation + '\'' +
                ", autoApprove=" + autoApprove +
                '}';
    }
}
