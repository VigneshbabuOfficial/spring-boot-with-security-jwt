package com.school.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@SuppressWarnings("deprecation")
@EnableWebSecurity
public class SecurityConfigurer extends WebSecurityConfigurerAdapter {

	private static final String LOG_STR = "SecurityConfigurer.%s";

	@Autowired
	private MyUserDetailService myUserDetailService;

	@Autowired
	private JwtRequestFilter jwtRequestFilter;

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {

		System.out.println(String.format(LOG_STR, "configure") + " , auth = " + auth);

		auth.userDetailsService(myUserDetailService);

	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return NoOpPasswordEncoder.getInstance();
	}

	@Override
	@Bean
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Override
	protected void configure(HttpSecurity httpSecurity) throws Exception {

		httpSecurity.csrf().disable().authorizeRequests().antMatchers("/authenticate/login","/authenticate/refresh","/authenticate/error").permitAll().anyRequest()
				.authenticated().and().exceptionHandling().and().sessionManagement()
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS);

		httpSecurity.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

	}

}
