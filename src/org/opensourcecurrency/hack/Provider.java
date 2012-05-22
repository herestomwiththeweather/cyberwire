package org.opensourcecurrency.hack;

import static org.opensourcecurrency.hack.ConstantsAssets.ASSET_PROVIDER_ID;

import java.util.ArrayList;

public class Provider {
	public Integer providerId;
	public String providerName;
	public String providerUrl;
	public String redirectUrl;
	public String clientId;
	public String clientSecret;
	
	private ProviderData m_providers;
	
	public Provider(ProviderData providers) {
		m_providers = providers;
	}
	
	public void setId(Integer id) {
		providerId = id;
	}
	
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
	
	public void addAccessToken(String token, Integer expires_in, String refresh_token) {
		// XXX right now, passing null for refresh token id
		m_providers.addAccessToken(providerId,token,expires_in,null);
	}
	
	public void addAsset(String name, String url) {
		m_providers.addAsset(providerId,name,url);
	}
	
	public ArrayList<Asset> getAssets() {
		ArrayList<Asset> assets;
		
    	try {
    		assets = m_providers.getAssets(ASSET_PROVIDER_ID + " = ?", new String[] {providerId.toString()});
    	} finally {
    		m_providers.close();
    	}
    	
    	return assets;
	}

	public String getAccessToken() {
		// get first unexpired access token associated with this provider
		AccessToken token;
		token = m_providers.getAccessToken(providerId);
		if(null==token) {
			return "";
		} else {
			return token.token;
		}
	}
}
