package hu.krisz.dao;

import hu.krisz.dao.entity.AppUser;
import hu.krisz.dao.entity.Role;
import hu.krisz.dao.repository.UserRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * An implementation of {@link UserDetailsService} which uses Spring Data JPA repositories.
 *
 * @author krisztian.toth on 6-8-2019
 */
public class EntityBasedUserDetailsService implements UserDetailsService {
    private static final Log LOGGER = LogFactory.getLog(EntityBasedUserDetailsService.class);

    private final UserRepository userRepository;

    public EntityBasedUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Loads the user by their ID instead of username, as username is not necessarily unique.
     *
     * @param userId the ID identifying the user whose data is required
     * @return a fully populated user record (never <code>null</code>)
     * @throws UsernameNotFoundException if the user could not be found or the user has no GrantedAuthority
     */
    @Override
    public UserDetails loadUserByUsername(String userId) {
        return userRepository.findById(UUID.fromString(userId))
                .map(this::createUserDetailsFrom)
                .orElseThrow(() -> {
                    LOGGER.debug("user with ID " + userId + "was not found");
                    return new UsernameNotFoundException("user with ID " + userId + "was not found");
                });
    }

    private UserDetails createUserDetailsFrom(AppUser user) {
        List<GrantedAuthority> permissions = getGrantedAuthoritiesFrom(user.getRoles());
        if (permissions.isEmpty()) {
            LOGGER.debug("User '" + user.getId() + "' has no authorities and will be treated as 'not found'");
            throw new UsernameNotFoundException("user " + user.getId() + " has no GrantedAuthority.");
        }

        return new ApplicationUser(user.getId(), user.getCreatedAt(), user.getUpdatedAt(), user.getUsername(),
                user.getPassword(), user.getCompanyName(), true, true, true, user.getEnabled(),
                permissions);
    }

    private List<GrantedAuthority> getGrantedAuthoritiesFrom(List<Role> roles) {
        return roles.stream()
                .map(Role::getPermissions)
                .flatMap(Collection::stream)
                .map(permission -> new SimpleGrantedAuthority(permission.getName()))
                .collect(Collectors.toList());
    }
}
