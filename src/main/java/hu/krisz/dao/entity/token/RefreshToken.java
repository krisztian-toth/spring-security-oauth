package hu.krisz.dao.entity.token;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import java.time.Instant;
import java.util.Objects;

/**
 * Entity which represents a refresh token for a user.
 *
 * @author krisztian.toth on 8-8-2019
 */
@Entity
@Table(schema = "oauth_token")
public class RefreshToken {
    @Id
    @Column(nullable = false)
    private String token;

    /**
     * User for which the token was issued.
     */
    @Column(unique = true)
    private String username;

    /**
     * Client which issued the token. Used to validate the client which issues the refresh is the same which
     * originally requested the token.
     */
    @Column
    private String clientId;

    /**
     * When the token expires.
     */
    @Column
    private Instant expiresAt;

    /**
     * When the token was issued.
     */
    private Instant issuedAt;

    public RefreshToken(String token, String username, String clientId, Instant expiresAt, Instant issuedAt) {
        this.token = token;
        this.username = username;
        this.clientId = clientId;
        this.expiresAt = expiresAt;
        this.issuedAt = issuedAt;
    }

    /**
     * NoArgs constructor, required by Hibernate.
     */
    private RefreshToken() {
    }

    public String getToken() {
        return token;
    }

    public String getUsername() {
        return username;
    }

    public String getClientId() {
        return clientId;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public Instant getIssuedAt() {
        return issuedAt;
    }

    @Override
    public boolean equals(Object o) {
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
                "token='" + token + '\'' +
                ", username='" + username + '\'' +
                ", clientId='" + clientId + '\'' +
                ", expiresAt=" + expiresAt +
                ", issuedAt=" + issuedAt +
                '}';
    }
}
