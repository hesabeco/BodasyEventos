package com.app.web.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.app.web.entities.Usuario;
import com.app.web.repository.UsuarioRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Configuration
public class SecurityConfig {
	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	@SuppressWarnings({ "deprecation" })
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.authorizeRequests(requests -> requests
				.requestMatchers("/").permitAll()
				.requestMatchers("/registro**").permitAll()
				.requestMatchers("/login**").permitAll()
				.requestMatchers("/img/**").permitAll()
				.requestMatchers("/css/**").permitAll()
				.requestMatchers("/js/**").permitAll()
				.requestMatchers("/styles/**").permitAll()
				.requestMatchers("/admin/**").hasAuthority("ADMIN")
				.requestMatchers("/cliente/**").hasAuthority("CLIENTE")
				.requestMatchers("/entrenador/**").hasAuthority("ENTRENADOR")
				.anyRequest().authenticated())
				.formLogin(login -> login
						.loginPage("/login")
						.successHandler((request, response, authentication) -> {
							redirectAfterLogin(authentication, response, request);
						})
						.permitAll())
				.logout(logout -> logout
						.invalidateHttpSession(true)
						.clearAuthentication(true)
						.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
						.logoutSuccessUrl("/login?logout")
						.permitAll());

		return http.build();
	}

	private void redirectAfterLogin(Authentication authentication, HttpServletResponse response,
			HttpServletRequest request) throws IOException {
		String username = authentication.getName();
		Usuario usuario = usuarioRepository.findByEmail(username);
		if (usuario != null) {
			if (usuario.isActivo()) {
				// El usuario está activo, redirige a la página deseada (por ejemplo, /inicio)
				HttpSession session = request.getSession();
				session.setAttribute("usuarioId", usuario.getId());
				response.sendRedirect("/inicio");
				return;
			} else {
				// El usuario está inactivo, redirige a una página de error o a la página de
				// inicio de sesión
				response.sendRedirect("/inactivo");
			}
		}
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}