package org.opensourcecurrency.hack.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.opensourcecurrency.hack.db.DatabaseManager;

@DatabaseTable
public class RefreshToken {
	@DatabaseField(generatedId=true)
	public int id;
	
	@DatabaseField
	public String token;
	
	@DatabaseField(foreign=true,foreignAutoRefresh=true)
	public Provider provider;
	
	@DatabaseField
	public String expires_at;
	
	@DatabaseField
	public String created_at;
	
	static public RefreshToken create() {
		return DatabaseManager.getInstance().newRefreshToken();
	}
	
	public boolean save() {
		DatabaseManager.getInstance().updateRefreshToken(this);
		return true;
	}
	
	public void destroy() {
		DatabaseManager.getInstance().deleteRefreshToken(this);
	}
}
