package com.pms.project;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import com.pms.config.CustomUserDetails;
import com.pms.user.entity.UserEntity;

@SpringBootTest
@AutoConfigureMockMvc
public class ProjectSecurityTest {

	@Autowired
	private MockMvc mockMvc;
	
	private CustomUserDetails createCustomUser(String userId, boolean isAdmin) {
		UserEntity user = UserEntity.builder()
									.userId(userId)
									.passwd("1234")
									.admin(isAdmin)
									.build();
        return new CustomUserDetails(user);
    }

	@Test
	@DisplayName("프로젝트 멤버 접근 확인")
	public void projectMemberCheck() throws Exception {
		mockMvc.perform(get("/project/user/PMS100/list")
                		.with(user(createCustomUser("song", false)))
                		.with(csrf()))
        		.andExpect(status().isNotFound());
	}
	
	@Test
	@DisplayName("권한 없는 유저의 접근 확인(403 error)")
	public void unauthorizedCheck() throws Exception {
		mockMvc.perform(get("/project/user/PMS100/list")
						.with(user(createCustomUser("tester", false)))
						.with(csrf()))
				.andExpect(status().isForbidden());
	}
	
	@Test
	@DisplayName("관리자 접근 확인")
	public void adminCheck() throws Exception {
		mockMvc.perform(get("/project/user/PMS100/list")
						.with(user(createCustomUser("admin", true)))
						.with(csrf()))
				.andExpect(status().isNotFound());
	}
}
