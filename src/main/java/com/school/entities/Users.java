package com.school.entities;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

@Entity
@Table
@DynamicInsert
@DynamicUpdate
@SequenceGenerator(allocationSize = 1, name = "user_seq", sequenceName = "user_seq")
public class Users{

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
    private Long id;
    private String userName;
    private String password;
    private String email;
    private String token;
    private LocalDateTime tokenExpirationDate;
    @ColumnDefault(value = "false")
    private boolean loggedIn;
    
	public Users() {
		super();
	}
	public Users(Long id, String userName, String password, String email) {
		super();
		this.id = id;
		this.userName = userName;
		this.password = password;
		this.email = email;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public LocalDateTime getTokenExpirationDate() {
		return tokenExpirationDate;
	}
	public void setTokenExpirationDate(LocalDateTime tokenExpirationDate) {
		this.tokenExpirationDate = tokenExpirationDate;
	}
	public boolean isLoggedIn() {
		return loggedIn;
	}
	public void setLoggedIn(boolean loggedIn) {
		this.loggedIn = loggedIn;
	}
    
    
    
}
