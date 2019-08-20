package hu.krisz.functional;

import hu.krisz.dao.entity.client.OAuthClient;
import hu.krisz.dao.entity.user.Permission;
import hu.krisz.dao.entity.user.Role;
import hu.krisz.dao.entity.user.User;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.lang.Nullable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Abstract test class which shares helper methods and common functionality with functional tests.
 *
 * @author krisztian.toth on 20-8-2019
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestEntityManager
@Transactional
public abstract class AbstractFunctionalTest {
    static final String OAUTH_TOKEN_ENDPOINT = "/oauth/token";

    static final String A_USERNAME = "username";
    static final String UNENCRYPTED_PASSWORD = "pass123";

    private static final String A_COMPANY_NAME = "company";

    static final String A_CLIENT_ID = "clientId";
    static final String AN_EMPTY_CLIENT_SECRET = null;
    static final String AN_UNENCRYPTED_CLIENT_SECRET = "clientSecret123";

    private static final String RESOURCE_IDS = null;
    private static final String SCOPES = "profile";
    private static final String GRANT_TYPES = "password,refresh_token";
    private static final String AUTHORITIES = null;
    private static final String WEB_SERVER_REDIRECT_URIS = null;
    private static final Integer ACCESS_TOKEN_VALIDITY = 100;
    private static final Integer REFRESH_TOKEN_VALIDITY = 500;
    private static final String ADDITIONAL_INFORMATION = null;
    private static final String AUTO_APPROVE = "true";

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected TestEntityManager testEntityManager;

    @Autowired
    protected PasswordEncoder passwordEncoder;

    ObjectMapper objectMapper = new ObjectMapper();


    /**
     * Creates an {@link OAuthClient} entity with the given client secret.
     *
     * @param clientSecret the client secret
     */
    void createOAuthClientWithSecret(@Nullable String clientSecret) {
        String encryptedClientSecret = null;
        if (clientSecret != null) {
            encryptedClientSecret = passwordEncoder.encode(clientSecret);
        }

        testEntityManager.persistAndFlush(new OAuthClient(A_CLIENT_ID, encryptedClientSecret, RESOURCE_IDS, SCOPES,
                GRANT_TYPES, AUTHORITIES, WEB_SERVER_REDIRECT_URIS, ACCESS_TOKEN_VALIDITY,
                REFRESH_TOKEN_VALIDITY, ADDITIONAL_INFORMATION, AUTO_APPROVE));
    }

    /**
     * Creates a {@link User} entity with a role and associated permission.
     */
    void createUser() {
        String encryptedPassword = passwordEncoder.encode(UNENCRYPTED_PASSWORD);
        User user = new User(A_USERNAME, encryptedPassword, A_COMPANY_NAME, true);
        Role role = new Role(Role.Name.USER);
        String permissionName = "permissionName";
        String permissionDescription = "permissionDescription";
        Permission permission = new Permission(permissionName, permissionDescription);

        role.addPermission(permission);
        user.addRole(role);
        testEntityManager.persistAndFlush(user);
    }
}
