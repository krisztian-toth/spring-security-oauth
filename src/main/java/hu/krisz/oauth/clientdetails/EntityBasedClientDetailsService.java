package hu.krisz.oauth.clientdetails;

import hu.krisz.oauth.dao.repository.ClientRepository;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.security.oauth2.provider.NoSuchClientException;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;

/**
 * An implementation of {@link ClientDetailsService} which uses Spring Data JPA repositories.
 *
 * @author krisztian.toth on 14-8-2019
 */
public class EntityBasedClientDetailsService implements ClientDetailsService {

    private final ClientRepository clientRepository;

    public EntityBasedClientDetailsService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Override
    public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {
        return clientRepository.findById(clientId)
                .map(client -> {
                    BaseClientDetails baseClientDetails = new BaseClientDetails(
                            client.getClientId(),
                            client.getResourceIds(),
                            client.getScopes(),
                            client.getAuthorizedGrantTypes(),
                            client.getAuthorities(),
                            client.getWebServerRedirectUris()
                    );
                    baseClientDetails.setClientSecret(client.getClientSecret());
                    baseClientDetails.setAccessTokenValiditySeconds(client.getAccessTokenValidity());
                    baseClientDetails.setRefreshTokenValiditySeconds(client.getRefreshTokenValidity());
                    return baseClientDetails;
                }).orElseThrow(() -> new NoSuchClientException("No client found with id=" + clientId));
    }
}
