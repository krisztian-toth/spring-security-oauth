package hu.krisz.oauth.dao.repository;

import hu.krisz.oauth.dao.entity.token.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Repository for {@link RefreshToken}.
 *
 * @author krisztian.toth on 8-8-2019
 */
@Repository
@Transactional(readOnly = true)
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {

    /**
     * Finds a {@link RefreshToken} instance by the token.
     *
     * @param token the token to find the refresh token by
     * @return an {@link Optional}<{@link RefreshToken}>
     */
    Optional<RefreshToken> findByToken(String token);

    /**
     * Finds a {@link RefreshToken} instance by the owner's username.
     *
     * @param username the username to find the {@link RefreshToken} by
     * @return an {@link Optional}<{@link RefreshToken}>
     */
    Optional<RefreshToken> findByUsername(String username);
}
