package hu.krisz.oauth.dao.entity.user;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import java.util.Objects;

/**
 * Table for permissions (e.g. view files, create folders).
 *
 * @author krisztian.toth on 7-8-2019
 */
@Entity
@Table(schema = "oauth_user")
public class Permission {
    @Id
    private String name;

    @Column(nullable = false)
    private String description;

    /**
     * Constructor for {@link Permission}.
     *
     * @param name the name of this permission
     * @param description a description about this permission (what does it grant exactly)
     */
    public Permission(String name, String description) {
        this.name = name;
        this.description = description;
    }

    /**
     * Default constructor for Hibernate.
     */
    private Permission() {}

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Permission that = (Permission) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "Permission{" +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
