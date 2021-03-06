package hu.krisz.oauth.functional;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Functional test class for testing the authorization endpoints with password grant.
 *
 * @author krisztian.toth on 19-8-2019
 */
public class PasswordGrantTest extends AbstractFunctionalTest {

    @Test
    public void testGrantWithClientWithClientSecret() throws Exception {
        createOAuthClientWithSecret(AN_UNENCRYPTED_CLIENT_SECRET);
        createUserWith(true);

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
        createOAuthClientWithSecret(AN_EMPTY_CLIENT_SECRET);
        createUserWith(true);

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
        assertEquals("Bad credentials", contentAsMap.get("error_description"));
    }

    @Test
    public void testGrantWhenUserIsNotActive() throws Exception {
        createOAuthClientWithSecret(AN_EMPTY_CLIENT_SECRET);
        createUserWith(false);

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
        assertEquals("User is disabled", contentAsMap.get("error_description"));
    }

    @Test
    public void testGrantWhenClientNotFound() throws Exception {
        createUserWith(true);

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
