package hu.krisz.oauth.functional;

import hu.krisz.oauth.dao.entity.token.RefreshToken;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.Instant;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Functional test class to test calls with grant type 'refresh_token'.
 *
 * @author krisztian.toth on 20-8-2019
 */
public class RefreshTokenGrantTest extends AbstractFunctionalTest {

    private static final String A_REFRESH_TOKEN = "refreshToken";
    private static final Instant NOW = Instant.now();
    private static final Instant NOW_PLUS_100_SECONDS = Instant.now().plusSeconds(100);
    private static final Instant NOW_MINUS_100_SECONDS = Instant.now().minusSeconds(100);

    @Test
    public void testGrantWithClientWithClientSecret() throws Exception {
        createOAuthClientWithSecret(AN_UNENCRYPTED_CLIENT_SECRET);
        createUserWith(true);
        createRefreshTokenWith(NOW_PLUS_100_SECONDS);

        String content = String.format("client_id=%s&client_secret=%s&refresh_token=%s&grant_type=%s",
                A_CLIENT_ID, AN_UNENCRYPTED_CLIENT_SECRET, A_REFRESH_TOKEN, REFRESH_TOKEN_GRANT_TYPE);

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

        // assert that the refresh token has changed after refreshing
        assertNotEquals(A_REFRESH_TOKEN, contentAsMap.get("refresh_token"));

        // assert that the previous refresh token does not exist in the database anymore
        assertNull(testEntityManager.find(RefreshToken.class, A_REFRESH_TOKEN));
    }

    @Test
    public void testGrantWithClientWithoutClientSecret() throws Exception {
        createOAuthClientWithSecret(AN_EMPTY_CLIENT_SECRET);
        createUserWith(true);
        createRefreshTokenWith(NOW_PLUS_100_SECONDS);

        String content = String.format("client_id=%s&refresh_token=%s&grant_type=%s",
                A_CLIENT_ID, A_REFRESH_TOKEN, REFRESH_TOKEN_GRANT_TYPE);

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

        // assert that the refresh token has changed after refreshing
        assertNotEquals(A_REFRESH_TOKEN, contentAsMap.get("refresh_token"));

        // assert that the previous refresh token does not exist in the database anymore
        assertNull(testEntityManager.find(RefreshToken.class, A_REFRESH_TOKEN));
    }

    @Test
    public void testGrantWhenUserNotFound() throws Exception {
        createOAuthClientWithSecret(AN_EMPTY_CLIENT_SECRET);
        createRefreshTokenWith(NOW_PLUS_100_SECONDS);

        String content = String.format("client_id=%s&refresh_token=%s&grant_type=%s",
                A_CLIENT_ID, A_REFRESH_TOKEN, REFRESH_TOKEN_GRANT_TYPE);

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

        assertEquals("unauthorized", contentAsMap.get("error"));
    }

    @Test
    public void testGrantWhenUserIsNotActive() throws Exception {
        createOAuthClientWithSecret(AN_EMPTY_CLIENT_SECRET);
        createUserWith(false);
        createRefreshTokenWith(NOW_PLUS_100_SECONDS);

        String content = String.format("client_id=%s&refresh_token=%s&grant_type=%s",
                A_CLIENT_ID, A_REFRESH_TOKEN, REFRESH_TOKEN_GRANT_TYPE);

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

        assertEquals("unauthorized", contentAsMap.get("error"));
        assertEquals("User is disabled", contentAsMap.get("error_description"));
    }

    @Test
    public void testGrantWhenClientNotFound() throws Exception {
        createUserWith(true);

        String content = String.format("client_id=%s&refresh_token=%s&grant_type=%s",
                A_CLIENT_ID, A_REFRESH_TOKEN, REFRESH_TOKEN_GRANT_TYPE);

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
        createOAuthClientWithSecret(AN_EMPTY_CLIENT_SECRET);

        String content = String.format("client_id=%s&refresh_token=%s&grant_type=%s",
                A_CLIENT_ID, A_REFRESH_TOKEN, REFRESH_TOKEN_GRANT_TYPE);

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
    public void testGrantWithInvalidRefreshToken() throws Exception {
        createOAuthClientWithSecret(AN_UNENCRYPTED_CLIENT_SECRET);
        createUserWith(true);

        String content = String.format("client_id=%s&client_secret=%s&refresh_token=%s&grant_type=%s",
                A_CLIENT_ID, AN_UNENCRYPTED_CLIENT_SECRET, A_REFRESH_TOKEN, REFRESH_TOKEN_GRANT_TYPE);

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
    public void testGrantWithExpiredRefreshToken() throws Exception {
        createOAuthClientWithSecret(AN_UNENCRYPTED_CLIENT_SECRET);
        createUserWith(true);
        createRefreshTokenWith(NOW_MINUS_100_SECONDS);

        String content = String.format("client_id=%s&client_secret=%s&refresh_token=%s&grant_type=%s",
                A_CLIENT_ID, AN_UNENCRYPTED_CLIENT_SECRET, A_REFRESH_TOKEN, REFRESH_TOKEN_GRANT_TYPE);

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

        assertEquals("invalid_token", contentAsMap.get("error"));
    }

    private void createRefreshTokenWith(Instant expiresAt) {
        testEntityManager.persistAndFlush(new RefreshToken(A_REFRESH_TOKEN, A_USERNAME, A_CLIENT_ID,
                PASSWORD_GRANT_TYPE, expiresAt, NOW));
    }
}
