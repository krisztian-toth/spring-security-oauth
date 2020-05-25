package hu.krisz.oauth.functional;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Functional test class for testing the authorization endpoints with client credentials grant.
 *
 * @author krisztian.toth on 20-8-2019
 */
public class ClientCredentialsGrantTest extends AbstractFunctionalTest {

    @Test
    public void testGrantWithClientWithClientCredentials() throws Exception {
        createOAuthClientWithSecret(AN_UNENCRYPTED_CLIENT_SECRET);

        String content = String.format("client_id=%s&client_secret=%s&grant_type=%s",
                A_CLIENT_ID, AN_UNENCRYPTED_CLIENT_SECRET, CLIENT_CREDENTIALS_GRANT_TYPE);

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

        // there should not be a refresh token when using the client credentials grant type
        assertNull(contentAsMap.get("refresh_token"));
    }

    /**
     * NOTE: even though this passes, client credentials grant type should never be allowed on clients without a client
     * secret
     */
    @Test
    public void testGrantWithClientWithoutClientCredentials() throws Exception {
        createOAuthClientWithSecret(AN_EMPTY_CLIENT_SECRET);

        String content = String.format("client_id=%s&grant_type=%s",
                A_CLIENT_ID, CLIENT_CREDENTIALS_GRANT_TYPE);

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

        // there should not be a refresh token when using the client credentials grant type
        assertNull(contentAsMap.get("refresh_token"));
    }

    @Test
    public void testGrantWhenClientNotFound() throws Exception {
        createUserWith(true);

        String content = String.format("client_id=%s&username=%s&password=%s&grant_type=%s",
                A_CLIENT_ID, A_USERNAME, UNENCRYPTED_PASSWORD, CLIENT_CREDENTIALS_GRANT_TYPE);

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
        createUserWith(true);

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
}
