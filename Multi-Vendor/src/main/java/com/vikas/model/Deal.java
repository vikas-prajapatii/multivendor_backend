package com.vikas.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Deal {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String discount;
    @OneToOne
    private HomeCategory category;
}
