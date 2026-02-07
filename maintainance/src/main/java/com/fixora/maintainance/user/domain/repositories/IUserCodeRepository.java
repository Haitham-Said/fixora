package com.fixora.maintainance.user.domain.repositories;

import com.fixora.maintainance.user.domain.model.UserCode;

import java.util.Optional;

public interface IUserCodeRepository {
    
    /**
     * Saves or updates a user code
     * @param userCode The user code to save
     * @return The saved user code
     */
    UserCode save(UserCode userCode);
    
    /**
     * Finds user code by user ID
     * @param userId The user ID
     * @return Optional user code
     */
    Optional<UserCode> findByUserId(Long userId);
    
    /**
     * Finds user code by code string
     * @param code The code string
     * @return Optional user code
     */
    Optional<UserCode> findByCode(String code);
}

