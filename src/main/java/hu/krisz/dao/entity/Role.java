package hu.krisz.dao.entity;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Table for roles (e.g. admin, owner, editor).
 * A role can have any permissions just as a permission can be part of any role.
 * Using eager join on permissions as we always want to load the permissions for a given role when we fetch it.
 *
 * @author krisztian.toth on 7-8-2019
 */
@Entity
@Table(schema = "oauth")
public class Role {
    @Id
    @GeneratedValue
    private UUID id;

    @Enumerated(EnumType.STRING)
    private Name name;

    @ManyToMany(cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE,
            CascadeType.REFRESH,
            CascadeType.DETACH
    }, fetch = FetchType.EAGER)
    @JoinTable(
            name = "role_permissions",
            schema = "oauth",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    private List<Permission> permissions = new ArrayList<>();

    /**
     * Adds a permission to this role and vice versa.
     *
     * @param permission the permission to add to this role
     */
    public void addPermission(Permission permission) {
        permissions.add(permission);
    }

    public UUID getId() {
        return id;
    }

    public Name getName() {
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
                "id=" + id +
                ", name='" + name + '\'' +
                ", permissions=" + permissions +
                '}';
    }

    /**
     * Represents the possible roles in the application.
     */
    public enum Name {
        /**
         * Administrators of the application.
         */
        ADMIN,

        /**
         * Managers for a given company.
         */
        MANAGER,

        /**
         * Waiters in company.
         */
        WAITER,

        /**
         * Staff in company.
         */
        STAFF;
    }
}
