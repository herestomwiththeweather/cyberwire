package org.opensourcecurrency.hack.model;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import org.opensourcecurrency.hack.db.DatabaseManager;
import org.opensourcecurrency.hack.RestTask;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

@DatabaseTable
public class Provider {
	@DatabaseField(generatedId=true)
	public int id;
	
	@DatabaseField
	public String name;
	
	@ForeignCollectionField
	private ForeignCollection<Asset> assets;
	
	@ForeignCollectionField
	private ForeignCollection<AccessToken> access_tokens;
	
	@ForeignCollectionField
	private ForeignCollection<User> users;
	
	@DatabaseField
	public String url;
	
	@DatabaseField
	public String redirect_url;
	
	@DatabaseField
	public String client_id;
	
	@DatabaseField
	public String client_secret;
	
	@DatabaseField
	public String authorization_endpoint;
	
	@DatabaseField
	public String token_endpoint;
	
	@DatabaseField
	public String created_at;
	
	private AccessToken m_AccessToken = null;
	private static final String TAG = "OpenTransact";
	
	public void addAccessToken(String token, Integer expires_in, String rtoken) {
		Log.d(TAG,"XXX addAccessToken persisting: "+token);

		AccessToken access_token = AccessToken.create();
		access_token.token = token;
		access_token.provider = this;
		Date now = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		access_token.expires_at = dateFormat.format(new Date(now.getTime()+(expires_in*1000)));
		
		if(null != rtoken) {
			RefreshToken refresh_token = RefreshToken.create();
			refresh_token.token = rtoken;
			refresh_token.provider = this;
			refresh_token.save();
			access_token.refresh_token = refresh_token;
		} else if(null != m_AccessToken) {
			access_token.refresh_token = m_AccessToken.refresh_token;
  		    discardAccessToken();
		}
		access_token.save();
		m_AccessToken = access_token;
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
		Asset asset = Asset.create();
		asset.name = name;
		asset.url = url;
		asset.balance = balance;
		asset.provider = this;
		asset.save();
	}
	
	public void getUserInfo(Context context, String action) {
		String access_token = getAccessToken();
		Log.d(TAG,"getUserInfo() access_token: "+access_token);
        
		String about_user="/user_info";

    	try {
        	HttpGet userinfoRequest = new HttpGet(new URI(url + about_user));
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
			
			User user = User.create();
			user.name = name;
			user.provider = this;
			user.email = email;
			user.url = url;
			user.website_url = website_url;
			user.picture_url = picture_url;
			user.user_id = user_id;
			user.save();
		} catch (JSONException e) {
  		  e.printStackTrace();
  		}
	}
	
	public User getUser() {
		List<User> users = getUsers();
		if(0 == users.size()) {
			return null;
		} else {
			return users.get(0);
		}
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
		
    	Log.d(TAG,"onActivityResult clientid: " + client_id);
    	Log.d(TAG,"onActivityResult client secret: " + client_secret);
    	Log.d(TAG,"onActivityResult redirect_uri: " + redirect_url);
    	
   		try {
    		  String url = token_endpoint;
    		  HttpPost tokenRequest = new HttpPost(new URI(url));
    		  List<NameValuePair> parameters = new ArrayList<NameValuePair>();
    		  parameters.add(new BasicNameValuePair("client_id", client_id));
    		  parameters.add(new BasicNameValuePair("client_secret", client_secret));
    		  parameters.add(new BasicNameValuePair("refresh_token", getAccessTokenObject().refresh_token.token));
    		  parameters.add(new BasicNameValuePair("grant_type","refresh_token"));
    		  parameters.add(new BasicNameValuePair("redirect_uri",redirect_url));
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
			// XXX use most recent!
			m_AccessToken = getAccessTokens().get(0);
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
	
	public List<Asset> getAssets() {
		ArrayList<Asset> assetList = new ArrayList<Asset>();
		for (Asset asset : assets) {
			assetList.add(asset);
		}
		return assetList;
	}
	
	public List<AccessToken> getAccessTokens() {
		ArrayList<AccessToken> accessTokenList = new ArrayList<AccessToken>();
		for(AccessToken access_token : access_tokens) {
			accessTokenList.add(access_token);
		}
		return accessTokenList;
	}
	
	public List<User> getUsers() {
		ArrayList<User> userList = new ArrayList<User>();
		for (User user : users) {
			userList.add(user);
		}
		return userList;
	}
	
	static public Provider find(int providerId) {
		return DatabaseManager.getInstance().getProviderWithId(providerId);
	}
	
	static public Provider find_by_url(String url) {
		return DatabaseManager.getInstance().getProviderWithUrl(url);
	}
	
	static public List<Provider> all() {
		return DatabaseManager.getInstance().getAllProviders();
	}
	
	static public Provider create() {
		return DatabaseManager.getInstance().newProvider();
	}
	
	public boolean save() {
		DatabaseManager.getInstance().updateProvider(this);
		return true;
	}
	
	public void destroy() {
		DatabaseManager.getInstance().deleteProvider(this);
	}
}
