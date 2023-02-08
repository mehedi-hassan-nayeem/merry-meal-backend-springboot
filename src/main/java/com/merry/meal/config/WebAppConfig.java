package com.merry.meal.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.merry.meal.security.CustomUserService;
import com.merry.meal.security.JwtAuthenticationEntryPoint;
import com.merry.meal.security.JwtRequestFilter;
import com.merry.meal.security.oauth.CustomRequestResolver;
import com.merry.meal.security.oauth.OAuth2FailHandler;
import com.merry.meal.security.oauth.OAuth2SuccessHandler;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, jsr250Enabled = true, prePostEnabled = true)
public class WebAppConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private JwtRequestFilter jwtAuthenticationEntryPoint;
	@Autowired
	private CustomUserService customUserService;
	
	@Autowired
	private OAuth2SuccessHandler oAuth2SuccessHandler;
	@Autowired
	private OAuth2FailHandler oAuth2FailHandler;
	
	@Autowired
	private ClientRegistrationRepository clientRegistrationRepository;

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(customUserService).passwordEncoder(passwordEncoder());
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Bean
	public OAuth2AuthorizationRequestResolver authorizationRequestResolver() {
		OAuth2AuthorizationRequestResolver defaultAuthorizationRequestResolver = new DefaultOAuth2AuthorizationRequestResolver(
				clientRegistrationRepository, "/oauth2/authorize");
		return new CustomRequestResolver(defaultAuthorizationRequestResolver);
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.cors().disable().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
				.csrf()
				.disable().formLogin().disable().httpBasic().disable()
				.authorizeRequests()
				.antMatchers("/", "/meal", "/auth/**", "/oauth2/**").permitAll()
				.antMatchers(HttpMethod.GET, "/api/v1/partners/**").permitAll()
				.antMatchers(HttpMethod.GET, "/api/v1/users/image/**").permitAll()
				.antMatchers(HttpMethod.GET, "/api/v1/partners/meals/image/**").permitAll()
				.anyRequest().authenticated().and().oauth2Login()
				.successHandler(oAuth2SuccessHandler).failureHandler(oAuth2FailHandler)
				.authorizationEndpoint().authorizationRequestResolver(authorizationRequestResolver());

		http.exceptionHandling().authenticationEntryPoint(new JwtAuthenticationEntryPoint());
		http.addFilterBefore(jwtAuthenticationEntryPoint, UsernamePasswordAuthenticationFilter.class);
	}
}
