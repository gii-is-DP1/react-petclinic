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
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;

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

            .authorizeHttpRequests(auth -> auth
            // Recursos estáticos comunes (css, js, images, webjars…) públicos
            .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
            // H2 Console accesible
            .requestMatchers(PathRequest.toH2Console()).permitAll()
            .requestMatchers("/h2-console/**").permitAll()

            // Raíz / páginas públicas
            .requestMatchers("/", "/oups").permitAll()

            // Swagger / OpenAPI accesible
            .requestMatchers(
                "/v3/api-docs/**",
                "/swagger-ui.html",
                "/swagger-ui/**",
                "/swagger-resources/**"
            ).permitAll()

            // API pública
            .requestMatchers("/api/v1/auth/**").permitAll()
            .requestMatchers("/api/v1/developers").permitAll()
            .requestMatchers("/api/v1/plan").permitAll()
            .requestMatchers("/api/v1/clinics").permitAll()
            .requestMatchers("/api/v1/developers").permitAll()

            // API restringida para propietarios de mascotas:
            .requestMatchers("/api/v1/plan").hasAuthority("OWNER")

            // API restringida para administradores
            .requestMatchers("/api/v1/users/**").hasAuthority(ADMIN)
            .requestMatchers("/api/v1/clinicOwners/all").hasAuthority(ADMIN)
            .requestMatchers(HttpMethod.DELETE, "/api/v1/consultations/**").hasAuthority(ADMIN)
            .requestMatchers("/api/v1/owners/**").hasAuthority(ADMIN)
            .requestMatchers("/api/v1/pets/stats").hasAuthority(ADMIN)
            .requestMatchers("/api/v1/vets/stats").hasAuthority(ADMIN)

			// Otras reglas de controal de acceso:
			.requestMatchers("/api/v1/clinicOwners/**").hasAnyAuthority(ADMIN, CLINIC_OWNER)
			.requestMatchers("/api/v1/visits/**").authenticated()
			.requestMatchers("/api/v1/pets").authenticated()
			.requestMatchers("/api/v1/pets/**").authenticated()
			.requestMatchers("/api/v1/clinics/**").hasAnyAuthority(CLINIC_OWNER, ADMIN)
			.requestMatchers(HttpMethod.GET, "/api/v1/vets/**").authenticated()
			.requestMatchers("/api/v1/vets/**").hasAnyAuthority(ADMIN, "VET", CLINIC_OWNER)

            // El resto denegado
             .anyRequest().denyAll())

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
