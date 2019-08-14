package hu.krisz.dao.entity.client;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import java.util.Objects;

/**
 * Entity which represents a OAuth2 authority. Some grant types doesn't require a user for authentication. If the client
 * doesn't act on behalf of a user (e.g. client credentials grant type) then these authorities will be bound to the
 * authentication token instead of user authorities.
 *
 * @author krisztian.toth on 14-8-2019
 */
@Entity
@Table(name = "authority", schema = "oauth_client")
public class OAuthAuthority {
    @Id
    private String authority;

    public OAuthAuthority(String authority) {
        this.authority = authority;
    }

    private OAuthAuthority() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OAuthAuthority that = (OAuthAuthority) o;
        return Objects.equals(authority, that.authority);
    }

    @Override
    public int hashCode() {
        return Objects.hash(authority);
    }

    @Override
    public String toString() {
        return "OAuthAuthority{" +
                "authority='" + authority + '\'' +
                '}';
    }
}
