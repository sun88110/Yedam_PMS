package com.pms.user.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.pms.user.entity.PwrTokenEntity;

public interface PwrTokenRepository extends CrudRepository<PwrTokenEntity, String> {

    Optional<PwrTokenEntity> findByTokenValue(String tokenValue);
    
    Optional<PwrTokenEntity> findByUserId(String userId);
}
