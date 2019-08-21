package hu.krisz.dao.entity.user;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Table for roles (e.g. admin, owner, editor).
 * A role can have any permissions just as a permission can be part of any role.
 * Using eager join on permissions as we always want to load the permissions for a given role when we fetch it.
 *
 * @author krisztian.toth on 7-8-2019
 */
@Entity
@Table(schema = "oauth_user")
public class Role {
    @Id
    private String name;

    @ManyToMany(cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE,
            CascadeType.REFRESH,
            CascadeType.DETACH
    }, fetch = FetchType.EAGER)
    @JoinTable(
            name = "role_permission",
            schema = "oauth_user",
            joinColumns = @JoinColumn(name = "role", referencedColumnName = "name"),
            inverseJoinColumns = @JoinColumn(name = "permission", referencedColumnName = "name")
    )
    private List<Permission> permissions = new ArrayList<>();

    /**
     * Constructor for {@link Role}.
     *
     * @param name name of the role
     */
    public Role(String name) {
        this.name = name;
    }

    /**
     * Default constructor for Hibernate.
     */
    private Role() {}

    /**
     * Adds a permission to this role and vice versa.
     *
     * @param permission the permission to add to this role
     */
    public void addPermission(Permission permission) {
        permissions.add(permission);
    }

    public String getName() {
        return name;
    }

    public List<Permission> getPermissions() {
        return permissions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Role role = (Role) o;
        return Objects.equals(name, role.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "Role{" +
                ", name='" + name + '\'' +
                ", permissions=" + permissions +
                '}';
    }
}
