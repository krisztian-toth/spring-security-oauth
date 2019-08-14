package hu.krisz.clientdetails;

import hu.krisz.dao.entity.client.OAuthGrantType;
import hu.krisz.dao.entity.client.OAuthResource;
import hu.krisz.dao.entity.client.OAuthScope;
import hu.krisz.dao.repository.ClientRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.security.oauth2.provider.NoSuchClientException;
import org.springframework.util.ClassUtils;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * An implementation of {@link ClientDetailsService} which uses Spring Data JPA repositories.
 *
 * @author krisztian.toth on 14-8-2019
 */
public class EntityBasedClientDetailsService implements ClientDetailsService {

    private static final Log LOGGER = LogFactory.getLog(EntityBasedClientDetailsService.class);
    private static final EntityBasedClientDetailsService.JsonMapper MAPPER = createJsonMapper();

    private final ClientRepository clientRepository;

    public EntityBasedClientDetailsService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Override
    public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {
        return clientRepository.findById(clientId)
                .map(client -> new ApplicationClient(
                        client.getClientId(),
                        client.getResourceIds().stream().map(OAuthResource::getResourceId).collect(Collectors.toSet()),
                        client.getClientSecret(),
                        client.getScopes().stream().map(OAuthScope::getScope).collect(Collectors.toSet()),
                        client.getAuthorizedGrantTypes().stream().map(OAuthGrantType::getGrantType).collect(Collectors.toSet()),
                        client.getWebServerRedirectUri() != null
                                ? Collections.singleton(client.getWebServerRedirectUri()) // TODO support multiple redirect URIs
                                : null,
                        client.getAuthorities().stream().map(a -> new SimpleGrantedAuthority(a.getAuthority())).collect(Collectors.toSet()),
                        client.getAccessTokenValidity(),
                        client.getRefreshTokenValidity(),
                        client.getAutoApprove(),
                        mapAdditionalInformation(client.getAdditionalInformation()).orElse(null)
                )).orElseThrow(() -> new NoSuchClientException("No client found with id=" + clientId));
    }

    @SuppressWarnings("unchecked")
    private Optional<Map<String, Object>> mapAdditionalInformation(String json) {
        Optional<Map<String, Object>> result = Optional.empty();
        try {
            result = Optional.of(MAPPER.read(json, Map.class));
        } catch (Exception e) {
            LOGGER.warn("Could not decode JSON for additional information: " + json, e);
        }
        return result;
    }

    @SuppressWarnings("squid:S00112")
    interface JsonMapper {
        String write(Object input) throws Exception;

        <T> T read(String input, Class<T> type) throws Exception;
    }

    private static EntityBasedClientDetailsService.JsonMapper createJsonMapper() {
        if (ClassUtils.isPresent("org.codehaus.jackson.map.ObjectMapper", null)) {
            return new EntityBasedClientDetailsService.JacksonMapper();
        } else if (ClassUtils.isPresent("com.fasterxml.jackson.databind.ObjectMapper", null)) {
            return new EntityBasedClientDetailsService.Jackson2Mapper();
        } else {
            return new EntityBasedClientDetailsService.NotSupportedJsonMapper();
        }
    }

    private static class JacksonMapper implements EntityBasedClientDetailsService.JsonMapper {
        private org.codehaus.jackson.map.ObjectMapper mapper = new org.codehaus.jackson.map.ObjectMapper();

        @Override
        public String write(Object input) throws Exception {
            return mapper.writeValueAsString(input);
        }

        @Override
        public <T> T read(String input, Class<T> type) throws Exception {
            return mapper.readValue(input, type);
        }
    }

    private static class Jackson2Mapper implements EntityBasedClientDetailsService.JsonMapper {
        private com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();

        @Override
        public String write(Object input) throws Exception {
            return mapper.writeValueAsString(input);
        }

        @Override
        public <T> T read(String input, Class<T> type) throws Exception {
            return mapper.readValue(input, type);
        }
    }

    private static class NotSupportedJsonMapper implements EntityBasedClientDetailsService.JsonMapper {
        @Override
        public String write(Object input) {
            throw new UnsupportedOperationException(
                    "Neither Jackson 1 nor 2 is available so JSON conversion cannot be done");
        }

        @Override
        public <T> T read(String input, Class<T> type) {
            throw new UnsupportedOperationException(
                    "Neither Jackson 1 nor 2 is available so JSON conversion cannot be done");
        }
    }
}
