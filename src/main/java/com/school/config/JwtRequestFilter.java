package com.school.config;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.school.config.util.JwtUtil;
import com.school.utils.CommonConstants;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

	@Autowired
	private MyUserDetailService userDetailsService;

	@Autowired
	private JwtUtil jwtUtil;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {

		final String authorizationHeader = request.getHeader("Authorization");

		String username = null;
		String jwt = null;

		try {

			if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ") && !"/authenticate/refresh".equals(request.getServletPath()) && !"/authenticate/error".equals(request.getServletPath()) ) {
				jwt = authorizationHeader.substring(7);
				username = jwtUtil.extractUsername(jwt);
			}
			
		} catch (Exception e) {
			
			System.out.println(" JwtRequestFilter.doFilterInternal - " + authorizationHeader +", message ="+e.getMessage()+", exception ="+e);

			if(e.getMessage().contains("JWT expired at")) {
				System.out.println(" JwtRequestFilter.doFilterInternal - request.getContextPath() " + request.getContextPath());
				response.sendRedirect(request.getContextPath() + "/authenticate/error");
			}
		}

		System.out.println(" JwtRequestFilter.doFilterInternal - " + authorizationHeader + " , jwt - " + jwt
				+ " , username = " + username);
		System.out.println(" request - " + request.getServletPath());

		if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

			UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

			if (jwtUtil.validateToken(jwt, userDetails) && userDetailsService.isValidUser(jwt,username)) {

				UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
						userDetails, null, userDetails.getAuthorities());
				usernamePasswordAuthenticationToken
						.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
			}
		}
		chain.doFilter(request, response);
	}

}
