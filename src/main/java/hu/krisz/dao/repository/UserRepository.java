package hu.krisz.dao.repository;

import hu.krisz.dao.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository for {@link User}.
 *
 * @author krisztian.toth on 7-8-2019
 */
@Repository
@Transactional(readOnly = true)
public interface UserRepository extends JpaRepository<User, UUID> {

    /**
     * Finds a user by their username.
     *
     * @param username the user's username
     * @return an {@link Optional}<{@link User}>
     */
    Optional<User> findByUsername(String username);
}
