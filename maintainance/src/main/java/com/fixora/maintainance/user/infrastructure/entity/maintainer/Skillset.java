package com.fixora.maintainance.user.infrastructure.entity.maintainer;

import jakarta.persistence.*;

@Entity
@Table(name = "skillsets")
public class Skillset {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;
}