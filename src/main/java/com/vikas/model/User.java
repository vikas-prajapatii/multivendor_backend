package com.vikas.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.vikas.domain.AuthProvider;
import com.vikas.domain.USER_ROLE;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String profileImage;

    private USER_ROLE role = USER_ROLE.ROLE_CUSTOMER;

    @Enumerated(EnumType.STRING)
    private AuthProvider provider = AuthProvider.LOCAL;

    private String providerId;

    private Boolean enabled = true;

    private String resetToken;

    private LocalDateTime resetTokenExpiry;

    @OneToMany
    private Set<Address> addresses = new HashSet<>();

    @ManyToMany
    @JsonIgnore
    private Set<Coupon> usedCoupons = new HashSet<>();

    public String getFullName() {
        return (firstName != null ? firstName : "") + (lastName != null && !lastName.isEmpty() ? " " + lastName : "");
    }

    public void setFullName(String fullName) {
        if (fullName == null) {
            this.firstName = "";
            this.lastName = "";
            return;
        }
        String[] parts = fullName.trim().split(" ", 2);
        this.firstName = parts[0];
        this.lastName = parts.length > 1 ? parts[1] : "";
    }

    public void setMobile(String mobile) {
        this.phoneNumber = mobile;
    }

    public String getMobile() {
        return this.phoneNumber;
    }

    public Boolean getEnabled() {
        return enabled != null ? enabled : true;
    }
}
