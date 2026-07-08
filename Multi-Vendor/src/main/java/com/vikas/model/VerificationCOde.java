package com.vikas.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class VerificationCOde {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String otp;
    private String email;
    @OneToOne
    private User user;

    @OneToOne
    private Seller seller;


}
