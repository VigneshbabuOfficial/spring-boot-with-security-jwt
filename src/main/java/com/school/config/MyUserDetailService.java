package com.school.config;

import java.util.ArrayList;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailService implements UserDetailsService{

	private static final String LOG_STR = "MyUserDetailService.%s";

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		System.out.println(String.format(LOG_STR, "loadUserByUsername") + " , username = " + username);
		
		return new User("foo","foo",new ArrayList<>());
	}

}
