package com.github.adrian83.trends.config;

public class TwitterAuth {

	private String token;
	private String secret;
	private String customerKey;
	private String customerSecret;
	
	public TwitterAuth(String token, String secret, String customerKey, String customerSecret) {
		super();
		this.token = token;
		this.secret = secret;
		this.customerKey = customerKey;
		this.customerSecret = customerSecret;
	}

	public String getToken() {
		return token;
	}

	public String getSecret() {
		return secret;
	}

	public String getCustomerKey() {
		return customerKey;
	}

	public String getCustomerSecret() {
		return customerSecret;
	}
	
}