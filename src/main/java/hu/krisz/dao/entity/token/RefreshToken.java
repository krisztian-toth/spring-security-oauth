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
@Table(schema = "oauth")
public class RefreshToken {
    @Id
    @Column(nullable = false)
    private String token;

    private Instant expiresAt;

    @CreatedDate
    private Instant issuedAt;

    public RefreshToken(final String token, final Instant expiresAt) {
        this.token = token;
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

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public Instant getIssuedAt() {
        return issuedAt;
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
                "token='" + token + '\'' +
                ", expiresAt=" + expiresAt +
                ", issuedAt=" + issuedAt +
                '}';
    }
}
