package com.devcommunity.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	private JwtFilter jwtFilter;
	private UserDetailsService userDetailsService;

	public SecurityConfig(JwtFilter jwtFilter, UserDetailsService userDetailsService) {
		this.jwtFilter = jwtFilter;
		this.userDetailsService = userDetailsService;
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
	    return http
	        .cors(Customizer.withDefaults())
	        .csrf(AbstractHttpConfigurer::disable)
	        .authorizeHttpRequests(request -> request
	            .requestMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll()
	            .requestMatchers(
	                "/api/auth/login",
	                "/api/auth/register",
	                "/api/posts/all",
	                "/api/posts/topic/**",
	                "/api/dev/reputation/max",
	                "/api/dev/**",
	                "/api/votes/getVotesByPostId/**"
	            ).permitAll()
	            .anyRequest().authenticated()
	        )
	        // Remove this in production if you don't want Basic Auth fallback
	        .httpBasic(Customizer.withDefaults())
	        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
	        .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
	        .build();
	}


	@Bean
	public AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider provider = new DaoAuthenticationProvider(); // NOSONAR
		provider.setPasswordEncoder(new BCryptPasswordEncoder(12));
		provider.setUserDetailsService(userDetailsService); // NOSONAR

		return provider;
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();

	}

	@Bean
	public CorsFilter corsFilter() {
		CorsConfiguration config = new CorsConfiguration();
		config.addAllowedOrigin("http://localhost:5173"); // React frontend origin
		config.addAllowedHeader("*");
		config.addAllowedMethod("*");
		config.setAllowCredentials(true);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config);
		return new CorsFilter(source);
	}

}