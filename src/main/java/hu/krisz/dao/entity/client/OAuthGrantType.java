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
@Table(name = "grant_type", schema = "oauth_client")
public class OAuthGrantType {
    @Id
    private String grantType;

    public OAuthGrantType(String grantType) {
        this.grantType = grantType;
    }

    private OAuthGrantType() {
    }

    public String getGrantType() {
        return grantType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OAuthGrantType that = (OAuthGrantType) o;
        return Objects.equals(grantType, that.grantType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(grantType);
    }

    @Override
    public String toString() {
        return "OAuthGrantType{" +
                "grantType='" + grantType + '\'' +
                '}';
    }
}
