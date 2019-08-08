package hu.krisz.dao.repository;

import hu.krisz.dao.entity.token.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Repository for {@link RefreshToken}.
 *
 * @author krisztian.toth on 8-8-2019
 */
@Repository
@Transactional(readOnly = true)
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {
}
