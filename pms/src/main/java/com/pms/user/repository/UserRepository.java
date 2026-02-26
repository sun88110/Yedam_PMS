package com.pms.user.repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.pms.user.entity.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, String> {

	@Modifying
	@Query("""
			UPDATE UserEntity u
			SET u.lastlogin = :lastLogin
			WHERE u.userId = :userId
			""")
	int updateLastLogin(@Param("userId") String userId, @Param("lastLogin") LocalDateTime lastLogin);

	@Query(value = """
			SELECT COUNT(*)
			FROM users u
			JOIN members m ON u.user_id = m.user_id
			JOIN g_project gp ON m.groupid = gp.group_no
			JOIN project p ON gp.project_no = p.project_no
			WHERE m.user_id = :userId
			AND p.project_code = :projectCode
			""", nativeQuery = true)
	int existsByUserId(@Param("userId") String userId, @Param("projectCode") String projectCode);

}
