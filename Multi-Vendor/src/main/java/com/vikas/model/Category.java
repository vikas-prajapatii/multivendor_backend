package com.vikas.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.NotFound;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    @NotNull
    @Column(unique = true)
    private String categoryId;
    @ManyToOne
    private Category parentCategory;
    @NotNull
    private Integer level;


}