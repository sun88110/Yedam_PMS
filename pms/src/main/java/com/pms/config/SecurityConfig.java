package com.pms.config;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
	
	private final UserDetailsService userDetailsService;
    private final ProjectAuthorizationManager projectAuthorizationManager;
    
	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	LoginSuccessHandler loginSucessHandler() {
		return new LoginSuccessHandler();
	}

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.csrf(csrf -> csrf.disable())
			.authorizeHttpRequests(auth -> auth
					.requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
					.requestMatchers("/coreui/**").permitAll()
					.requestMatchers("/home/**", "/user/**", "/error/**").permitAll()
					.requestMatchers("/settings/**").hasRole("ADMIN")
					.anyRequest().access(projectAuthorizationManager)
					//.anyRequest().authenticated()
					)
			.formLogin(form -> form
					.loginPage("/user/login")
					.loginProcessingUrl("/user/login")
					.usernameParameter("userId")
					.successHandler(loginSucessHandler())
					.permitAll()
					)
			.rememberMe(remember -> remember
					.rememberMeParameter("remember-me")
					.tokenValiditySeconds(60*60*24*30)
					.alwaysRemember(false)
					.userDetailsService(userDetailsService)
					.key("pms_remember_key")
					)
			.logout(logout -> logout
					.logoutUrl("/user/logout")
					.logoutSuccessUrl("/user/login")
					.invalidateHttpSession(true)
					);
		
		return http.build();
	}
}
