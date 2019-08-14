package hu.krisz.dao.repository;

import hu.krisz.dao.entity.client.OAuthClient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Repository for {@link OAuthClient}.
 *
 * @author krisztian.toth on 14-8-2019
 */
@Repository
@Transactional(readOnly = true)
public interface ClientRepository extends JpaRepository<OAuthClient, String> {

}
