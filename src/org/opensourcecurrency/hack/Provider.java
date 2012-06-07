package org.opensourcecurrency.hack;

import static org.opensourcecurrency.hack.ConstantsAssets.ASSET_PROVIDER_ID;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

public class Provider {
	public Integer providerId;
	public String providerName;
	public String providerUrl;
	public String redirectUrl;
	public String clientId;
	public String clientSecret;
	public String authorizationEndpoint;
	public String tokenEndpoint;
	
	private AccessToken m_AccessToken = null;
	
	private ProviderData m_providers;
	private static final String TAG = "OpenTransact";
	
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
	
	public void setAuthorizationEndpoint(String authorization_endpoint) {
		authorizationEndpoint = authorization_endpoint;
	}
	
	public void setTokenEndpoint(String token_endpoint) {
		tokenEndpoint = token_endpoint;
	}
	
	public void addAccessToken(String token, Integer expires_in, String refresh_token) {
		Log.d(TAG,"XXX addAccessToken persisting: "+token);
		Integer refresh_token_id = 0;
		if(null != refresh_token) {
			refresh_token_id = m_providers.addRefreshToken(providerId,refresh_token);
		} else if(null != m_AccessToken) {
			refresh_token_id = m_AccessToken.refreshTokenId;
  		    discardAccessToken();
		}
		m_providers.addAccessToken(providerId,token,expires_in,refresh_token_id);
	}
	
    public String addAccessToken(Context context, Intent intent) {
		String response = intent.getStringExtra(RestTask.HTTP_RESPONSE);
		
		String access_token = null;
		String refresh_token = null;
		Integer expires_in = 0;
		
		if(null == response) {
			return "";
		}

		try {
          JSONObject access_token_response = new JSONObject(response);
      	  access_token = access_token_response.getString("access_token");
      	  Log.d(TAG," access token: " + access_token);

      	  if(access_token_response.has("refresh_token")) {
          	  refresh_token = access_token_response.getString("refresh_token");
          	  Log.d(TAG,"refresh token: " + refresh_token);
      	  }
      	  
      	  if(access_token_response.has("expires_in")) {
          	  expires_in = access_token_response.getInt("expires_in");
          	  Log.d(TAG,"   expires_in: " + expires_in);
      	  }
      	  
      	  addAccessToken(access_token, expires_in, refresh_token);
  		} catch (JSONException e) {
  		  e.printStackTrace();
  		  return "";
  		}
		
    	return access_token;
    }
    
	public void addAsset(String name, String url, String balance) {
		m_providers.addAsset(providerId,name,url,balance);
	}
	
	public void getUserInfo(Context context, String action) {
		String access_token = getAccessToken();
		Log.d(TAG,"getUserInfo() access_token: "+access_token);
        
		String about_user="/user_info";

    	try {
        	HttpGet userinfoRequest = new HttpGet(new URI(providerUrl + about_user));
      		userinfoRequest.setHeader("Accept","application/json");
      		userinfoRequest.setHeader("Authorization","Bearer " + access_token);
        	RestTask task = new RestTask(context, action);
        	task.execute(userinfoRequest);
    	} catch(Exception e) {
        	e.printStackTrace();    		
    	}
	}
	
	public void addUser(JSONObject r) {
		try {
			String name = r.getString("name");
			String email = r.getString("email");
			String url = r.getString("profile");
			String website_url = r.getString("website");
			String picture_url = r.getString("picture");
			String user_id = r.getString("user_id");
			m_providers.addUser(providerId,name,email,url,website_url,picture_url,user_id);
		} catch (JSONException e) {
  		  e.printStackTrace();
  		}
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
	
	public User getUser() {
		return m_providers.getUser(providerId);
	}
	
	public boolean handleNetworkError(Context context, Intent intent, String action) {
		if(intent.getStringExtra(RestTask.HTTP_ERROR).equals("Unauthorized")) {
			Log.d(TAG,"handleNetworkError() unauthorized access token. sending refresh token...");
			postRefreshToken(context,action);
          	return true;
		} else {
    		Toast toast = Toast.makeText(context, "Error communicating with provider", Toast.LENGTH_LONG);
    		toast.setGravity(Gravity.CENTER, 0, 0);
    		toast.show();
			return false;
		}
	}
	
	public boolean postRefreshToken(Context context, String action) {
		
    	Log.d(TAG,"onActivityResult clientid: " + clientId);
    	Log.d(TAG,"onActivityResult client secret: " + clientSecret);
    	Log.d(TAG,"onActivityResult redirect_uri: " + redirectUrl);
    	
   		try {
    		  String url = tokenEndpoint;
    		  HttpPost tokenRequest = new HttpPost(new URI(url));
    		  List<NameValuePair> parameters = new ArrayList<NameValuePair>();
    		  parameters.add(new BasicNameValuePair("client_id", clientId));
    		  parameters.add(new BasicNameValuePair("client_secret", clientSecret));
    		  parameters.add(new BasicNameValuePair("refresh_token", getAccessTokenObject().refreshToken(context)));
    		  parameters.add(new BasicNameValuePair("grant_type","refresh_token"));
    		  parameters.add(new BasicNameValuePair("redirect_uri",redirectUrl));
    		  tokenRequest.setEntity(new UrlEncodedFormEntity(parameters));
    		  
    		  RestTask task = new RestTask(context, action);
    		  task.execute(tokenRequest);

    		} catch (Exception e) {
    			e.printStackTrace();
    		}
   		
		return true;
	}
	
	public void discardAccessToken() {
		m_AccessToken = null;
	}

	public AccessToken getAccessTokenObject() {
		if(null == m_AccessToken) {
			m_AccessToken = m_providers.getAccessToken(providerId);
		}
		return m_AccessToken;
	}
	
	public String getAccessToken() {
		// get first unexpired access token associated with this provider
		if(null==getAccessTokenObject()) {
			return "";
		} else {
			return getAccessTokenObject().token;
		}
	}
}
