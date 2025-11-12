package com.fixora.maintainance.user.infrastructure.entity.maintainer;

import com.fixora.maintainance.user.infrastructure.entity.UserEntity;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "maintainers")
public class MaintainerEntity {
    @Id
    @Column(name = "user_id")
    private Long userId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private UserEntity userEntity;

    private BigDecimal rate;
    private Boolean availability;
    private Double latitude;
    private Double longitude;

    @Column(name = "profile_status")
    private String profileStatus;

    public Long getUserId() {
        return userId;
    }

    public UserEntity getUser() {
        return userEntity;
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

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setUserEntity(UserEntity userEntity) {
        this.userEntity = userEntity;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public void setAvailability(Boolean availability) {
        this.availability = availability;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public void setProfileStatus(String profileStatus) {
        this.profileStatus = profileStatus;
    }
}
