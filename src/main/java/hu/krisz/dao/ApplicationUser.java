package hu.krisz.dao;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;
import org.springframework.security.core.userdetails.User;

import java.time.LocalDateTime;
import java.util.Collection;

/**
 * Extension of {@link User} with some custom fields.
 *
 * @author krisztian.toth on 6-8-2019
 */
@SuppressWarnings("squid:S2160")
public class ApplicationUser extends User {
    private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

    private final String email;
    private final transient LocalDateTime createdAt;

    public ApplicationUser(String username, String password, Collection<? extends GrantedAuthority> authorities,
                           String email, LocalDateTime createdAt) {
        super(username, password, authorities);
        this.email = email;
        this.createdAt = createdAt;
    }

    @SuppressWarnings("squid:S00107")
    public ApplicationUser(String username, String password, boolean enabled, boolean accountNonExpired,
                           boolean credentialsNonExpired, boolean accountNonLocked,
                           Collection<? extends GrantedAuthority> authorities, String email, LocalDateTime createdAt) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.email = email;
        this.createdAt = createdAt;
    }

    public String getEmail() {
        return email;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public String toString() {
        return super.toString() +
                "; Email: " + email +
                "; CreatedAt: " + createdAt;
    }
}
