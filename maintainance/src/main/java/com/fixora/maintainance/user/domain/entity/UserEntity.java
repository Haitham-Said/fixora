package com.fixora.maintainance.user.domain.entity;

import com.fixora.maintainance.user.domain.model.Role;
import com.fixora.maintainance.user.domain.model.UserStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "USERS")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY )
    private Long id;

    @Column(name = "email",unique = true)
    private String email;

    @Column(name = "name")
    private String name;

    @Column(name= "phone",nullable = false,unique = true)
    private String phone;

    @Column(name = "password_hash",nullable = false)
    private String password;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "role",nullable = false)
    private Role role;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "status")
    private UserStatus status=UserStatus.ACTIVE;

    @Column(name="created_at")
    private LocalDateTime createdAt;

    @Column(name="company_id")
    private Integer companyId;

    protected UserEntity() {
        // JPA needs empty constructor
    }

    public UserEntity(String email, String passwordHash, Role role) {
        this.email = email;
        this.password = passwordHash;
        this.role = role;
    }

    public Integer getCompanyId() {
        return companyId;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getPassword() {
        return password;
    }

    public Role getRole() {
        return role;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public UserStatus getStatus() {
        return status;
    }
}
