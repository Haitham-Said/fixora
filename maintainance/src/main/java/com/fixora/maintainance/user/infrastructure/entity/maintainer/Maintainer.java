package com.fixora.maintainance.user.infrastructure.entity.maintainer;

import com.fixora.maintainance.user.infrastructure.entity.User;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "maintainers")
public class Maintainer {
    @Id
    @Column(name = "user_id")
    private Long userId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    private BigDecimal rate;
    private Boolean availability;
    private Double latitude;
    private Double longitude;

    @Column(name = "profile_status")
    private String profileStatus;

    public Long getUserId() {
        return userId;
    }

    public User getUser() {
        return user;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public Boolean getAvailability() {
        return availability;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public String getProfileStatus() {
        return profileStatus;
    }
}
