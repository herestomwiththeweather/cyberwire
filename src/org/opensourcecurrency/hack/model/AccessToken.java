package org.opensourcecurrency.hack.model;

import java.util.List;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.opensourcecurrency.hack.db.DatabaseManager;

@DatabaseTable
public class AccessToken {
	@DatabaseField(generatedId=true)
	public int id;
	
	@DatabaseField
	public String token;
	
	@DatabaseField(foreign=true,foreignAutoRefresh=true)
	public Provider provider;
	
	@DatabaseField(foreign=true,foreignAutoRefresh=true)
	public RefreshToken refresh_token;
	
	@DatabaseField
	public String expires_at;
	
	@DatabaseField
	public String created_at;
	
	static public List<AccessToken> all() {
		return DatabaseManager.getInstance().getAllAccessTokens();
	}
	
	static public AccessToken create() {
		return DatabaseManager.getInstance().newAccessToken();
	}
	
	public boolean save() {
		DatabaseManager.getInstance().updateAccessToken(this);
		return true;
	}
	
	public void destroy() {
		DatabaseManager.getInstance().deleteAccessToken(this);
	}
}
