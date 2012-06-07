package org.opensourcecurrency.hack;

import android.content.Context;
import android.util.Log;

public class AccessToken {
	public Integer accessTokenId;
	public Integer refreshTokenId;
	public String token;
	public String refreshToken = null;
	private ProviderData providers;
	
	public String refreshToken(Context context) {
		if(null == refreshToken) {
			providers = new ProviderData(context);
			refreshToken = providers.getRefreshToken(refreshTokenId);
		}
		return refreshToken;
	}
	
	public void setId(Integer id) {
		accessTokenId = id;
	}
	
	public void setRefreshTokenId(Integer refresh_token_id) {
		refreshTokenId = refresh_token_id;
	}
	
	public void setToken(String access_token) {
		Log.d("OpenTransact","XXX setToken: " + access_token);
		token = access_token;
	}
}
