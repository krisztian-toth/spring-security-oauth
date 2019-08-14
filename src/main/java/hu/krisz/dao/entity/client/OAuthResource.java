package hu.krisz.dao.entity.client;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import java.util.Objects;

/**
 * Entity which represents a resource server's ID. In OAuth2 context the resource server handles authenticated requests
 * after the application has obtained an access token. If we want to limit the usage of access tokens created by certain
 * {@link OAuthClient}s to resources, we can specify those resource's IDs here. The defined resource IDs are usually
 * contained in the token's "aud" (audience) claim which can be validated by the resource. The usage of this entity is
 * optional.
 *
 * @author krisztian.toth on 14-8-2019
 */
@Entity
@Table(name = "oauth_resource", schema = "oauth")
public class OAuthResource {
    @Id
    private String resourceId;

    public OAuthResource(String resourceId) {
        this.resourceId = resourceId;
    }

    private OAuthResource() {
    }

    public String getResourceId() {
        return resourceId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OAuthResource that = (OAuthResource) o;
        return Objects.equals(resourceId, that.resourceId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(resourceId);
    }

    @Override
    public String toString() {
        return "OAuthResource{" +
                "resourceId='" + resourceId + '\'' +
                '}';
    }
}
