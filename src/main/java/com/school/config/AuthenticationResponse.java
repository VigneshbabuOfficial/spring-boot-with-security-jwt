package com.school.config;

import java.time.LocalDateTime;

public class AuthenticationResponse {

	private String token;
    private LocalDateTime tokenExpirationDate;
    private String message;

    public AuthenticationResponse(String token, LocalDateTime tokenExpirationDate,String message) {
        this.token = token;
        this.tokenExpirationDate = tokenExpirationDate;
        this.message = message;
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

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	

}
