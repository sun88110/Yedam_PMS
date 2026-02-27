package com.pms.user.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

import lombok.Builder;
import lombok.Getter;

@Getter
@RedisHash(value = "pw_reset_token")
public class PwrTokenEntity {

	@Id
	private String tokenId;

	@Indexed
	private String tokenValue;

	private String userId;

	@TimeToLive
	private Long expiration;

	@Builder
	public PwrTokenEntity(String userId, String tokenValue, Long expiration) {
		this.tokenId = tokenValue; 
		this.userId = userId;
		this.tokenValue = tokenValue;
		this.expiration = expiration;
	}
}
