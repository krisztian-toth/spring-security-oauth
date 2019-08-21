package hu.krisz.dao.entity.user;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Entity which represents a simple user in the application. Note that it's not in the default schema.
 *
 * @author krisztian.toth on 7-8-2019
 */
@Entity
@Table(name = "app_user", schema = "oauth_user")
public class User {

    @Id
    @GeneratedValue
    private UUID id;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(name = "company_name", nullable = false)
    private String companyName;

    @Column(nullable = false)
    private Boolean enabled;

    @ManyToMany(cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE,
            CascadeType.REFRESH,
            CascadeType.DETACH
    })
    @JoinTable(
            name = "user_role",
            schema = "oauth_user",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private List<Role> roles = new ArrayList<>();

    /**
     * Constructor for {@link User}.
     *
     * @param username the username
     * @param password the (encoded) password
     * @param companyName the company name
     * @param enabled whether the user enabled or not
     */
    public User(String username, String password, String companyName, Boolean enabled) {
        this.username = username;
        this.password = password;
        this.companyName = companyName;
        this.enabled = enabled;
    }

    /**
     * Default constructor for Hibernate.
     */
    private User() {}

    /**
     * Adds a {@link Role} to the user.
     *
     * @param role the {@link Role} to add
     */
    public void addRole(Role role) {
        this.roles.add(role);
    }

    public UUID getId() {
        return id;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getCompanyName() {
        return companyName;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public List<Role> getRoles() {
        return roles;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(username, user.username) &&
                Objects.equals(companyName, user.companyName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, companyName);
    }

    @Override
    public String toString() {
        return "AppUser{" +
                "id=" + id +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", companyName='" + companyName + '\'' +
                ", enabled='" + enabled + '\'' +
                '}';
    }
}
