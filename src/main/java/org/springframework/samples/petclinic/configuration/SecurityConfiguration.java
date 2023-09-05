package org.springframework.samples.petclinic.configuration;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.samples.petclinic.configuration.jwt.AuthEntryPointJwt;
import org.springframework.samples.petclinic.configuration.jwt.AuthTokenFilter;
import org.springframework.samples.petclinic.configuration.services.UserDetailsServiceImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

	@Autowired
	UserDetailsServiceImpl userDetailsService;

	@Autowired
	private AuthEntryPointJwt unauthorizedHandler;

	@Autowired
	DataSource dataSource;

	private static final String ADMIN = "ADMIN";
	private static final String CLINIC_OWNER = "CLINIC_OWNER";

	@Bean
	public AuthTokenFilter authenticationJwtTokenFilter() {
		return new AuthTokenFilter();
	}

	

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	protected SecurityFilterChain configure(HttpSecurity http) throws Exception {
		
		http.authorizeHttpRequests(authorizeRequests ->	authorizeRequests
				.requestMatchers("/resources/**", "/webjars/**", "/h2-console/**", "/static/**", "/swagger-resources/**").permitAll()
				.requestMatchers( "/api/v1/clinics","/", "/oups","/api/v1/auth/**","/v3/api-docs/**","/swagger-ui.html","/swagger-ui/**").permitAll()												
				.requestMatchers("/api/v1/plan").hasAuthority("OWNER")
				.requestMatchers("/api/v1/users/**").hasAuthority(ADMIN)
				.requestMatchers("/api/v1/clinicOwners/all").hasAuthority(ADMIN)
				.requestMatchers("/api/v1/clinicOwners/**").hasAnyAuthority(ADMIN, CLINIC_OWNER)
				.requestMatchers(HttpMethod.DELETE, "/api/v1/consultations/{consultationId:[0-9]\\d+}").hasAuthority(ADMIN)
				.requestMatchers("/api/v1/owners/*/pets/**").authenticated()
				.requestMatchers("/api/v1/owners/**").hasAuthority(ADMIN)
				.requestMatchers("/api/v1/visits/","/api/v1/visits/**").hasAnyAuthority(CLINIC_OWNER, ADMIN) 
				.requestMatchers(HttpMethod.GET, "/api/v1/pets/stats").hasAuthority(ADMIN)
				.requestMatchers("/api/v1/clinics/**").hasAnyAuthority(CLINIC_OWNER, ADMIN)
				.requestMatchers(HttpMethod.GET, "/api/v1/vets/stats").hasAuthority(ADMIN)
				.requestMatchers(HttpMethod.GET, "/api/v1/vets/**").authenticated()
				.requestMatchers("/api/v1/vets/**").hasAnyAuthority(ADMIN, "VET", CLINIC_OWNER) 
				.anyRequest().authenticated());
		http
			.cors()
		.and()
			.csrf().disable()		
			.exceptionHandling().authenticationEntryPoint(unauthorizedHandler)
		.and()
			.sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		http.headers().frameOptions().sameOrigin();
		http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
		// http.addFilterAfter(new SpaWebFilter(),
		// UsernamePasswordAuthenticationFilter.class);
		return http.build();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception{
		return config.getAuthenticationManager();
	}

	// public void addResourceHandlers(ResourceHandlerRegistry registry) {
	// registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
	//
	// registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
	// }

}
