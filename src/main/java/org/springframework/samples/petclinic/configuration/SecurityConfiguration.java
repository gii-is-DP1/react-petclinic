package org.springframework.samples.petclinic.configuration;

import static org.springframework.security.config.Customizer.withDefaults;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.samples.petclinic.configuration.jwt.AuthEntryPointJwt;
import org.springframework.samples.petclinic.configuration.jwt.AuthTokenFilter;
import org.springframework.samples.petclinic.configuration.services.UserDetailsServiceImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

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
	protected SecurityFilterChain configure(HttpSecurity http) throws Exception {
		
		http
			.cors(withDefaults())		
			.csrf(AbstractHttpConfigurer::disable)		
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))			
			.headers((headers) -> headers.frameOptions((frameOptions) -> frameOptions.disable()))
			.exceptionHandling((exepciontHandling) -> exepciontHandling.authenticationEntryPoint(unauthorizedHandler))			
			
			.authorizeHttpRequests(authorizeRequests ->	authorizeRequests
			.requestMatchers(AntPathRequestMatcher.antMatcher("/resources/**")).permitAll()
			.requestMatchers(AntPathRequestMatcher.antMatcher("/webjars/**")).permitAll() 
			.requestMatchers(AntPathRequestMatcher.antMatcher("/static/**")).permitAll() 
			.requestMatchers(AntPathRequestMatcher.antMatcher("/swagger-resources/**")).permitAll()			
			.requestMatchers(AntPathRequestMatcher.antMatcher("/api/v1/clinics")).permitAll()			
			.requestMatchers(AntPathRequestMatcher.antMatcher("/")).permitAll()
			.requestMatchers(AntPathRequestMatcher.antMatcher("/oups")).permitAll()
			.requestMatchers(AntPathRequestMatcher.antMatcher("/api/v1/auth/**")).permitAll()
			.requestMatchers(AntPathRequestMatcher.antMatcher("/v3/api-docs/**")).permitAll()
			.requestMatchers(AntPathRequestMatcher.antMatcher("/swagger-ui.html")).permitAll()
			.requestMatchers(AntPathRequestMatcher.antMatcher("/swagger-ui/**")).permitAll()												
			.requestMatchers(AntPathRequestMatcher.antMatcher("/api/v1/developers")).permitAll()												
			.requestMatchers(AntPathRequestMatcher.antMatcher("/api/v1/plan")).hasAuthority("OWNER")
			.requestMatchers(AntPathRequestMatcher.antMatcher("/api/v1/users/**")).hasAuthority(ADMIN)
			.requestMatchers(AntPathRequestMatcher.antMatcher("/api/v1/clinicOwners/all")).hasAuthority(ADMIN)
			.requestMatchers(AntPathRequestMatcher.antMatcher("/api/v1/clinicOwners/**")).hasAnyAuthority(ADMIN, CLINIC_OWNER)
			.requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.DELETE, "/api/v1/consultations/**")).hasAuthority(ADMIN)
			.requestMatchers(AntPathRequestMatcher.antMatcher("/api/v1/owners/**")).hasAuthority(ADMIN)
			.requestMatchers(AntPathRequestMatcher.antMatcher("/api/v1/visits/**")).authenticated()			
			.requestMatchers(AntPathRequestMatcher.antMatcher( "/api/v1/pets/stats")).hasAuthority(ADMIN)
			.requestMatchers(AntPathRequestMatcher.antMatcher("/api/v1/pets")).authenticated()
			.requestMatchers(AntPathRequestMatcher.antMatcher("/api/v1/pets/**")).authenticated()
			.requestMatchers(AntPathRequestMatcher.antMatcher("/api/v1/clinics/**")).hasAnyAuthority(CLINIC_OWNER, ADMIN)
			.requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/api/v1/vets/stats")).hasAuthority(ADMIN)
			.requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/api/v1/vets/**")).authenticated()
			.requestMatchers(AntPathRequestMatcher.antMatcher("/api/v1/vets/**")).hasAnyAuthority(ADMIN, "VET", CLINIC_OWNER) 
			.requestMatchers(AntPathRequestMatcher.antMatcher("/h2-console/**")).permitAll()
			.anyRequest().authenticated())					
			
			.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);		
		return http.build();
	}

	@Bean
	public AuthTokenFilter authenticationJwtTokenFilter() {
		return new AuthTokenFilter();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception{
		return config.getAuthenticationManager();
	}	


	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	
	
}
