package org.opensourcecurrency.hack;

public class Provider {
	public String providerName;
	public String providerUrl;
	public String redirectUrl;
	public String clientId;
	public String clientSecret;
	
	public void setName(String name) {
		providerName = name;
	}
	
	public void setProviderUrl(String provider_url) {
		providerUrl = provider_url;
	}
	
	public void setRedirectUrl(String redirect_url) {
		redirectUrl = redirect_url;
	}
	
	public void setClientId(String client_id) {
		clientId = client_id;
	}
	
	public void setClientSecret(String client_secret) {
		clientSecret = client_secret;
	}

}
