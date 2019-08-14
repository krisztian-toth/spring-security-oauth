package hu.krisz.dao.entity.client;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import java.util.Objects;

/**
 * Entity which represents an OAuth2 grant type. A grant types is basically a method to authenticate a user or client.
 * For details see https://oauth.net/2/grant-types/
 *
 * @author krisztian.toth on 14-8-2019
 */
@Entity
@Table(name = "oauth_grant_type")
public class OAuthGrantType {
    @Id
    private String grant;

    public OAuthGrantType(String grant) {
        this.grant = grant;
    }

    private OAuthGrantType() {
    }

    public String getGrant() {
        return grant;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OAuthGrantType that = (OAuthGrantType) o;
        return Objects.equals(grant, that.grant);
    }

    @Override
    public int hashCode() {
        return Objects.hash(grant);
    }

    @Override
    public String toString() {
        return "OAuthGrantType{" +
                "grant='" + grant + '\'' +
                '}';
    }
}
