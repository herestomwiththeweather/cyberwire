package org.opensourcecurrency.hack.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.opensourcecurrency.hack.db.DatabaseManager;

@DatabaseTable
public class User {
	@DatabaseField(generatedId=true)
	public int id;
	
	@DatabaseField
	public String name;
	
	@DatabaseField(foreign=true,foreignAutoRefresh=true)
	public Provider provider;
	
	@DatabaseField
	public String url;
	
	@DatabaseField
	public String website_url;
	
	@DatabaseField
	public String picture_url;
	
	@DatabaseField
	public String email;
	
	@DatabaseField
	public String user_id;
	
	@DatabaseField
	public String created_at;
	
	static public User create() {
		return DatabaseManager.getInstance().newUser();
	}
	
	public boolean save() {
		DatabaseManager.getInstance().updateUser(this);
		return true;
	}
	
	public void destroy() {
		DatabaseManager.getInstance().deleteUser(this);
	}
}
