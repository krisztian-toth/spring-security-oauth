package hu.krisz.dao.entity.client;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import java.util.Objects;

/**
 * Entity which represents an OAuth2 scope. Scopes are useful when granting access for 3rd party apps to user's
 * accounts. For more information see https://oauth.net/2/scope/
 *
 * @author krisztian.toth on 14-8-2019
 */
@Entity
@Table(name = "oauth_scope", schema = "oauth")
public class OAuthScope {
    @Id
    private String scope;

    public OAuthScope(String scope) {
        this.scope = scope;
    }

    private OAuthScope() {
    }

    public String getScope() {
        return scope;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OAuthScope that = (OAuthScope) o;
        return Objects.equals(scope, that.scope);
    }

    @Override
    public int hashCode() {
        return Objects.hash(scope);
    }

    @Override
    public String toString() {
        return "OAuthScope{" +
                "scope='" + scope + '\'' +
                '}';
    }
}
