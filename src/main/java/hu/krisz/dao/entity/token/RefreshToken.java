package hu.krisz.dao.entity.token;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import java.util.Objects;

/**
 * Entity which represents a refresh token for a user.
 *
 * @author krisztian.toth on 8-8-2019
 */
@Entity
@Table(schema = "oauth")
public class RefreshToken {
    @Id
    private String username;

    @Column(nullable = false)
    private String token;

    public RefreshToken(final String username, final String token) {
        this.username = username;
        this.token = token;
    }

    private RefreshToken() {}

    public String getUsername() {
        return username;
    }

    public String getToken() {
        return token;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RefreshToken that = (RefreshToken) o;
        return Objects.equals(token, that.token);
    }

    @Override
    public int hashCode() {
        return Objects.hash(token);
    }

    @Override
    public String toString() {
        return "RefreshToken{" +
                "username='" + username + '\'' +
                ", token='" + token + '\'' +
                '}';
    }
}
