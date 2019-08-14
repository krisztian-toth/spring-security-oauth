package hu.krisz.dao.entity.token;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import org.springframework.data.annotation.CreatedDate;

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

    @Column(unique = true)
    private String username;

    @Column
    private Instant expiresAt;

    @CreatedDate
    private Instant issuedAt;

    public RefreshToken(String token, String username, Instant expiresAt) {
        this.token = token;
        this.username = username;
        this.expiresAt = expiresAt;
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
                ", expiresAt=" + expiresAt +
                ", issuedAt=" + issuedAt +
                '}';
    }
}
