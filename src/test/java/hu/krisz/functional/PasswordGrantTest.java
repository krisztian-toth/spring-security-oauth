package hu.krisz.functional;

import hu.krisz.dao.entity.client.OAuthClient;
import hu.krisz.dao.entity.user.Permission;
import hu.krisz.dao.entity.user.Role;
import hu.krisz.dao.entity.user.User;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Functional test class for testing the authorization endpoints with password grant.
 *
 * @author krisztian.toth on 19-8-2019
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestEntityManager
@Transactional
public class PasswordGrantTest {
    private static final String OAUTH_TOKEN_ENDPOINT = "/oauth/token";

    private static final String A_USERNAME = "username";
    private static final String UNENCRYPTED_PASSWORD = "pass123";
    private static final String A_COMPANY_NAME = "company";

    private static final String A_CLIENT_ID = "clientId";
    private static final String AN_EMPTY_CLIENT_SECRET = null;
    private static final String AN_UNENCRYPTED_CLIENT_SECRET = "clientSecret123";
    private static final String RESOURCE_IDS = null;
    private static final String SCOPES = "profile";
    private static final String GRANT_TYPES = "password,refresh_token";
    private static final String PASSWORD_GRANT_TYPE = "password";
    private static final String AUTHORIZATION_CODE_GRANT_TYPE = "authorization_code";
    private static final String AUTHORITIES = null;
    private static final String WEB_SERVER_REDIRECT_URIS = null;
    private static final Integer ACCESS_TOKEN_VALIDITY = 100;
    private static final Integer REFRESH_TOKEN_VALIDITY = 500;
    private static final String ADDITIONAL_INFORMATION = null;
    private static final String AUTO_APPROVE = "true";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testGrantWithClientWithClientSecret() throws Exception {
        createUser();
        createOAuthClientWithSecret(AN_UNENCRYPTED_CLIENT_SECRET);

        String content = String.format("client_id=%s&client_secret=%s&username=%s&password=%s&grant_type=%s",
                A_CLIENT_ID, AN_UNENCRYPTED_CLIENT_SECRET, A_USERNAME, UNENCRYPTED_PASSWORD, PASSWORD_GRANT_TYPE);

        String contentAsString = mockMvc.perform(MockMvcRequestBuilders.post(OAUTH_TOKEN_ENDPOINT)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content(content))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON_UTF8))
                .andDo(MockMvcResultHandlers.print())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Map contentAsMap = objectMapper.readValue(contentAsString, Map.class);

        assertNotNull(contentAsMap.get("access_token"));
        assertNotNull(contentAsMap.get("refresh_token"));
    }

    @Test
    public void testGrantWithClientWithoutClientSecret() throws Exception {
        createUser();
        createOAuthClientWithSecret(AN_EMPTY_CLIENT_SECRET);

        String content = String.format("client_id=%s&username=%s&password=%s&grant_type=%s",
                A_CLIENT_ID, A_USERNAME, UNENCRYPTED_PASSWORD, PASSWORD_GRANT_TYPE);

        String contentAsString = mockMvc.perform(MockMvcRequestBuilders.post(OAUTH_TOKEN_ENDPOINT)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content(content))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON_UTF8))
                .andDo(MockMvcResultHandlers.print())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Map contentAsMap = objectMapper.readValue(contentAsString, Map.class);

        assertNotNull(contentAsMap.get("access_token"));
        assertNotNull(contentAsMap.get("refresh_token"));
    }

    @Test
    public void testGrantWhenUserNotFound() throws Exception {
        createOAuthClientWithSecret(AN_EMPTY_CLIENT_SECRET);

        String content = String.format("client_id=%s&username=%s&password=%s&grant_type=%s",
                A_CLIENT_ID, A_USERNAME, UNENCRYPTED_PASSWORD, PASSWORD_GRANT_TYPE);

        String contentAsString = mockMvc.perform(MockMvcRequestBuilders.post(OAUTH_TOKEN_ENDPOINT)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content(content))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON_UTF8))
                .andDo(MockMvcResultHandlers.print())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Map contentAsMap = objectMapper.readValue(contentAsString, Map.class);

        assertEquals("invalid_grant", contentAsMap.get("error"));
    }

    @Test
    public void testGrantWhenClientNotFound() throws Exception {
        createUser();

        String content = String.format("client_id=%s&username=%s&password=%s&grant_type=%s",
                A_CLIENT_ID, A_USERNAME, UNENCRYPTED_PASSWORD, PASSWORD_GRANT_TYPE);

        String contentAsString = mockMvc.perform(MockMvcRequestBuilders.post(OAUTH_TOKEN_ENDPOINT)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content(content))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON_UTF8))
                .andDo(MockMvcResultHandlers.print())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Map contentAsMap = objectMapper.readValue(contentAsString, Map.class);

        assertEquals("invalid_client", contentAsMap.get("error"));
    }

    @Test
    public void testGrantWhenGrantTypeNotSupportedByClient() throws Exception {
        createUser();
        createOAuthClientWithSecret(AN_EMPTY_CLIENT_SECRET);

        String content = String.format("client_id=%s&username=%s&password=%s&grant_type=%s",
                A_CLIENT_ID, A_USERNAME, UNENCRYPTED_PASSWORD, AUTHORIZATION_CODE_GRANT_TYPE);

        String contentAsString = mockMvc.perform(MockMvcRequestBuilders.post(OAUTH_TOKEN_ENDPOINT)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .content(content))
                .andExpect(MockMvcResultMatchers.status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.content()
                        .contentTypeCompatibleWith(MediaType.APPLICATION_JSON_UTF8))
                .andDo(MockMvcResultHandlers.print())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Map contentAsMap = objectMapper.readValue(contentAsString, Map.class);

        assertEquals("invalid_client", contentAsMap.get("error"));
    }

    private void createOAuthClientWithSecret(String clientSecret) {
        String encryptedClientSecret = null;
        if (clientSecret != null) {
            encryptedClientSecret = passwordEncoder.encode(clientSecret);
        }

        testEntityManager.persistAndFlush(new OAuthClient(A_CLIENT_ID, encryptedClientSecret, RESOURCE_IDS, SCOPES,
                GRANT_TYPES, AUTHORITIES, WEB_SERVER_REDIRECT_URIS, ACCESS_TOKEN_VALIDITY,
                REFRESH_TOKEN_VALIDITY, ADDITIONAL_INFORMATION, AUTO_APPROVE));
    }

    private void createUser() {
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
