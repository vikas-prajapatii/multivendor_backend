package com.vikas.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode

public class Coupon {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String code;

    private double discountPercentage;

    private LocalDate validityStartDate;

    private LocalDate validityEndDate;

    private double minimumOrderValue;

    private boolean isActive = true;
    @ManyToMany(mappedBy = "usedCoupons")
    private Set<User> usedUsers = new HashSet<>();


}
