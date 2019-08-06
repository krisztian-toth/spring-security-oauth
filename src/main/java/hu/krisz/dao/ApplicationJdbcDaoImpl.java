package hu.krisz.dao;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.jdbc.JdbcDaoImpl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

/**
 * @author krisztian.toth on 6-8-2019
 */
public class ApplicationJdbcDaoImpl extends JdbcDaoImpl {
    private static final String DEF_USERS_BY_USERNAME_QUERY = "select username,password,email,enabled,created_at "
            + "from users " + "where username = ?";

    @Override
    protected List<UserDetails> loadUsersByUsername(String username) {
        return getJdbcTemplate().query(DEF_USERS_BY_USERNAME_QUERY,
                new String[] { username }, new RowMapper<UserDetails>() {
                    @Override
                    public UserDetails mapRow(ResultSet rs, int rowNum)
                            throws SQLException {
                        String username = rs.getString(1);
                        String password = rs.getString(2);
                        String email = rs.getString(3);
                        boolean enabled = rs.getBoolean(4);
                        LocalDateTime createdAt = LocalDateTime.ofInstant(rs.getTimestamp(5).toInstant(), ZoneId.of("UTC"));
                        return new ApplicationUser(username, password, enabled, true, true, true,
                                AuthorityUtils.NO_AUTHORITIES, email, createdAt);
                    }
                });
    }

    @Override
    protected UserDetails createUserDetails(String username, UserDetails userFromUserQuery,
                                            List<GrantedAuthority> combinedAuthorities) {
        ApplicationUser castedUserFromQuery = (ApplicationUser) userFromUserQuery;
        return new ApplicationUser(userFromUserQuery.getUsername(), userFromUserQuery.getPassword(),
                userFromUserQuery.isEnabled(), true, true, true,
                combinedAuthorities, castedUserFromQuery.getEmail(), castedUserFromQuery.getCreatedAt());
    }
}
