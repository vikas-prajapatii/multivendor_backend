package com.vikas.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.vikas.domain.USER_ROLE;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    private String email;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private USER_ROLE role  = USER_ROLE.ROLE_CUSTOMER;
    @OneToMany
    private Set<Add> addresses = new HashSet<>();
    @ManyToMany
    @JsonIgnore
    private Set<Coupon> usedCoupons = new HashSet<>();

}
