package org.opensourcecurrency.hack;

import static android.provider.BaseColumns._ID;
import static org.opensourcecurrency.hack.ConstantsProviders.PROVIDERS_TABLE_NAME;
import static org.opensourcecurrency.hack.ConstantsProviders.NAME;
import static org.opensourcecurrency.hack.ConstantsProviders.PROVIDER_URL;
import static org.opensourcecurrency.hack.ConstantsProviders.REDIRECT_URL;
import static org.opensourcecurrency.hack.ConstantsProviders.CLIENT_ID;
import static org.opensourcecurrency.hack.ConstantsProviders.CLIENT_SECRET;
import static org.opensourcecurrency.hack.ConstantsProviders.AUTHORIZATION_ENDPOINT;
import static org.opensourcecurrency.hack.ConstantsProviders.TOKEN_ENDPOINT;
import static org.opensourcecurrency.hack.ConstantsProviders.PROVIDER_CREATED_AT;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;

import static org.opensourcecurrency.hack.ConstantsAssets.ASSETS_TABLE_NAME;
import static org.opensourcecurrency.hack.ConstantsAssets.ASSET_PROVIDER_ID;
import static org.opensourcecurrency.hack.ConstantsAssets.ASSET_URL;
import static org.opensourcecurrency.hack.ConstantsAssets.ASSET_NAME;
import static org.opensourcecurrency.hack.ConstantsAssets.ASSET_BALANCE;
import static org.opensourcecurrency.hack.ConstantsAssets.ASSET_CREATED_AT;

import static org.opensourcecurrency.hack.ConstantsUsers.USERS_TABLE_NAME;
import static org.opensourcecurrency.hack.ConstantsUsers.USER_PROVIDER_ID;
import static org.opensourcecurrency.hack.ConstantsUsers.USER_URL;
import static org.opensourcecurrency.hack.ConstantsUsers.USER_WEBSITE_URL;
import static org.opensourcecurrency.hack.ConstantsUsers.USER_PICTURE_URL;
import static org.opensourcecurrency.hack.ConstantsUsers.USER_NAME;
import static org.opensourcecurrency.hack.ConstantsUsers.USER_EMAIL;
import static org.opensourcecurrency.hack.ConstantsUsers.USER_USER_ID;
import static org.opensourcecurrency.hack.ConstantsUsers.USER_CREATED_AT;

import static org.opensourcecurrency.hack.ConstantsAccessTokens.ACCESS_TOKENS_TABLE_NAME;
import static org.opensourcecurrency.hack.ConstantsAccessTokens.ACCESS_TOKEN_PROVIDER_ID;
import static org.opensourcecurrency.hack.ConstantsAccessTokens.REFRESH_TOKEN_ID;
import static org.opensourcecurrency.hack.ConstantsAccessTokens.ACCESS_TOKEN;
import static org.opensourcecurrency.hack.ConstantsAccessTokens.ACCESS_TOKEN_EXPIRES_AT;
import static org.opensourcecurrency.hack.ConstantsAccessTokens.ACCESS_TOKEN_CREATED_AT;

import static org.opensourcecurrency.hack.ConstantsRefreshTokens.REFRESH_TOKENS_TABLE_NAME;
import static org.opensourcecurrency.hack.ConstantsRefreshTokens.REFRESH_TOKEN_PROVIDER_ID;
import static org.opensourcecurrency.hack.ConstantsRefreshTokens.REFRESH_TOKEN;
import static org.opensourcecurrency.hack.ConstantsRefreshTokens.REFRESH_TOKEN_EXPIRES_AT;
import static org.opensourcecurrency.hack.ConstantsRefreshTokens.REFRESH_TOKEN_CREATED_AT;

import org.opensourcecurrency.hack.Provider;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;
import android.util.Log;


public class ProviderData extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "providers.db";
	private static final int DATABASE_VERSION = 1;
	private static final String TAG = "OpenTransact";

	private static String[] FROM = { _ID, NAME,PROVIDER_URL,REDIRECT_URL,CLIENT_ID,CLIENT_SECRET,AUTHORIZATION_ENDPOINT,TOKEN_ENDPOINT,PROVIDER_CREATED_AT };
	private static String[] TOKENS_FROM = { _ID, ACCESS_TOKEN_PROVIDER_ID,REFRESH_TOKEN_ID,ACCESS_TOKEN,ACCESS_TOKEN_EXPIRES_AT,ACCESS_TOKEN_CREATED_AT };
    private static String[] ASSETS_FROM = { _ID, ASSET_PROVIDER_ID, ASSET_URL, ASSET_NAME, ASSET_BALANCE, ASSET_CREATED_AT };
    private static String[] USERS_FROM = { _ID, USER_PROVIDER_ID, USER_URL, USER_WEBSITE_URL, USER_PICTURE_URL, USER_NAME, USER_EMAIL, USER_USER_ID, USER_CREATED_AT };
    private static String[] REFRESHTOKENS_FROM = { _ID, REFRESH_TOKEN, REFRESH_TOKEN_CREATED_AT };
    
	public ProviderData(Context ctx) {
	      super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
    	Log.d(TAG,"SQLiteOpenHelper#onCreate");

	      db.execSQL("CREATE TABLE " + PROVIDERS_TABLE_NAME + " (" + _ID
	              + " INTEGER PRIMARY KEY AUTOINCREMENT, " + NAME 
	              + " TEXT NOT NULL, " + PROVIDER_URL
	              + " TEXT UNIQUE NOT NULL, " + REDIRECT_URL
	              + " TEXT NOT NULL, " + CLIENT_ID
	              + " TEXT NOT NULL, " + CLIENT_SECRET
	              + " TEXT NOT NULL, " + AUTHORIZATION_ENDPOINT
	              + " TEXT NOT NULL, " + TOKEN_ENDPOINT
	              + " TEXT NOT NULL," + PROVIDER_CREATED_AT + " DATE);");
	      
	      db.execSQL("CREATE TABLE " + USERS_TABLE_NAME + " (" + _ID
	              + " INTEGER PRIMARY KEY AUTOINCREMENT, " + USER_PROVIDER_ID 
	              + " INTEGER, " + USER_URL
	              + " TEXT NOT NULL," + USER_WEBSITE_URL
	              + " TEXT NOT NULL," + USER_PICTURE_URL
	              + " TEXT NOT NULL," + USER_NAME
	              + " TEXT NOT NULL," + USER_EMAIL
	              + " TEXT NOT NULL," + USER_USER_ID
	              + " TEXT NOT NULL," + USER_CREATED_AT + " DATE);");
	      
	      db.execSQL("CREATE TABLE " + ASSETS_TABLE_NAME + " (" + _ID
	              + " INTEGER PRIMARY KEY AUTOINCREMENT, " + ASSET_PROVIDER_ID 
	              + " INTEGER, " + ASSET_URL
	              + " TEXT UNIQUE NOT NULL," + ASSET_NAME
	              + " TEXT NOT NULL," + ASSET_BALANCE
	              + " TEXT NOT NULL," + ASSET_CREATED_AT + " DATE);");
	      
	      db.execSQL("CREATE TABLE " + ACCESS_TOKENS_TABLE_NAME + " (" + _ID
	              + " INTEGER PRIMARY KEY AUTOINCREMENT, " + ACCESS_TOKEN_PROVIDER_ID
	              + " INTEGER, " + REFRESH_TOKEN_ID
	              + " INTEGER, " + ACCESS_TOKEN
	              + " TEXT NOT NULL," + ACCESS_TOKEN_EXPIRES_AT
	              + " DATE, " + ACCESS_TOKEN_CREATED_AT + " DATE);");
	      
	      db.execSQL("CREATE TABLE " + REFRESH_TOKENS_TABLE_NAME + " (" + _ID
	              + " INTEGER PRIMARY KEY AUTOINCREMENT, " + REFRESH_TOKEN_PROVIDER_ID
	              + " INTEGER, " + REFRESH_TOKEN
	              + " TEXT NOT NULL," + REFRESH_TOKEN_EXPIRES_AT
	              + " DATE, " + REFRESH_TOKEN_CREATED_AT + " DATE);");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}
	
	public ArrayList<Provider> getProviders(String selection, String [] selectionArgs) {
    	Log.d(TAG,"XXX ProviderData#getProviders");
		SQLiteDatabase db = getReadableDatabase();
	    ArrayList<Provider> resultList = new ArrayList<Provider>();

		Cursor cursor = db.query(PROVIDERS_TABLE_NAME, FROM, selection, selectionArgs, null, null, null);
    	Log.d(TAG,"XXX ProviderData#getProviders count: " + cursor.getCount());
	    while (cursor.moveToNext())
	    {			
	        try {
	            Provider ob = new Provider(this);
	            ob.setId(cursor.getInt(cursor.getColumnIndex(_ID)));
	            ob.setName(cursor.getString(cursor.getColumnIndex(NAME)));
	            ob.setProviderUrl(cursor.getString(cursor.getColumnIndex(PROVIDER_URL)));
	            ob.setRedirectUrl(cursor.getString(cursor.getColumnIndex(REDIRECT_URL)));
	            ob.setClientId(cursor.getString(cursor.getColumnIndex(CLIENT_ID)));
	            ob.setClientSecret(cursor.getString(cursor.getColumnIndex(CLIENT_SECRET)));
	            ob.setAuthorizationEndpoint(cursor.getString(cursor.getColumnIndex(AUTHORIZATION_ENDPOINT)));
	            ob.setTokenEndpoint(cursor.getString(cursor.getColumnIndex(TOKEN_ENDPOINT)));
	            resultList.add(ob);
	        } catch (Exception e) {
      			e.printStackTrace();
	        }
	    }

	    cursor.close();
	    db.close();
	    
	    return resultList;
	}
	
	public Asset getCurrentAsset(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

		return getAsset(prefs.getString("assetProviderPref",""));
	}
	
    public Asset getAsset(String asset_url) {
		Log.d(TAG,"XXX getAsset: " + asset_url);
		
		Asset asset = null;
		ArrayList<Asset> assets;
		
    	try {
    		assets = getAssets(ASSET_URL + " = ?", new String[] {asset_url});
    	} finally {
    		close();
    	}
    	if(0 != assets.size()) {
        	asset = assets.get(0);
        	asset.setProvider(getProviderById(asset.m_providerId));
    	}

    	return asset;
    }
	
    public Provider getProvider(String providername) {
		Log.d(TAG,"XXX getProvider: " + providername);
		
		Provider provider = null;
		ArrayList<Provider> assetProviders;
		
    	try {
    		assetProviders = getProviders(PROVIDER_URL + " = ?", new String[] {providername});
    	} finally {
    		close();
    	}
    	if(0 != assetProviders.size()) {
        	provider = assetProviders.get(0);
    	}

    	return provider;
    }
    
    public Provider getProviderById(Integer providerId) {
		Log.d(TAG,"XXX getProviderById: " + providerId);
		
		Provider provider;
		ArrayList<Provider> assetProviders;
		
    	try {
    		assetProviders = getProviders(_ID + " = ?", new String[] {providerId.toString()});
    	} finally {
    		close();
    	}
    	
    	provider = assetProviders.get(0);
    	return provider;
    }
    
	public Provider addProvider(String name, String provider_url, String redirect_url, String client_id, String client_secret, String authorization_endpoint, String token_endpoint) {
    	Log.d(TAG,"ProviderData#addProvider: " + name);

		SQLiteDatabase db = getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(NAME, name);
		values.put(PROVIDER_URL, provider_url);
		values.put(REDIRECT_URL, redirect_url);
		values.put(CLIENT_ID, client_id);
		values.put(CLIENT_SECRET, client_secret);
		values.put(AUTHORIZATION_ENDPOINT, authorization_endpoint);
		values.put(TOKEN_ENDPOINT, token_endpoint);
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		values.put(PROVIDER_CREATED_AT, dateFormat.format(new Date()));
		
		//values.put(PROVIDER_CREATED_AT, System.currentTimeMillis());
		db.insertOrThrow(PROVIDERS_TABLE_NAME, null, values);
		
		return getProvider(provider_url);
	}
	
	public ArrayList<AccessToken> getAccessTokens(String selection, String [] selectionArgs) {
    	Log.d(TAG,"XXX ProviderData#getAccessTokens");
		SQLiteDatabase db = getReadableDatabase();
	    ArrayList<AccessToken> resultList = new ArrayList<AccessToken>();

		Cursor cursor = db.query(ACCESS_TOKENS_TABLE_NAME, TOKENS_FROM, selection, selectionArgs, null, null, "created_at DESC");
    	Log.d(TAG,"XXX ProviderData#getAccessTokens count: " + cursor.getCount());
	    while (cursor.moveToNext())
	    {			
	        try {
	            AccessToken ob = new AccessToken();
	            ob.setId(cursor.getInt(cursor.getColumnIndex(_ID)));
	            ob.setToken(cursor.getString(cursor.getColumnIndex(ACCESS_TOKEN)));
	            ob.setRefreshTokenId(cursor.getInt(cursor.getColumnIndex(REFRESH_TOKEN_ID)));
	            resultList.add(ob);
	        } catch (Exception e) {
      			e.printStackTrace();
	        }
	    }

	    cursor.close();
	    db.close();
	    
	    return resultList;
	}
	
	public ArrayList<User> getUsers(String selection, String [] selectionArgs) {
    	Log.d(TAG,"XXX ProviderData#getUsers");
		SQLiteDatabase db = getReadableDatabase();
	    ArrayList<User> resultList = new ArrayList<User>();

		Cursor cursor = db.query(USERS_TABLE_NAME, USERS_FROM, selection, selectionArgs, null, null, null);
    	Log.d(TAG,"XXX ProviderData#getUsers count: " + cursor.getCount());
	    while (cursor.moveToNext())
	    {			
	        try {
	            User ob = new User();
	            ob.setId(cursor.getInt(cursor.getColumnIndex(_ID)));
	            ob.setEmail(cursor.getString(cursor.getColumnIndex(USER_EMAIL)));
	            resultList.add(ob);
	        } catch (Exception e) {
      			e.printStackTrace();
	        }
	    }

	    cursor.close();
	    db.close();
	    
	    return resultList;
	}
	
	public User getUser(Integer provider_id) {
		Log.d(TAG,"XXX getUser: " + provider_id);
		
		User user;
		ArrayList<User> users;
		
    	try {
    		users = getUsers(USER_PROVIDER_ID + " = ?", new String[] {provider_id.toString()});
    	} finally {
    		close();
    	}
    	if(0 == users.size()) {
    		user = null;
    	} else {
    		user = users.get(0);
    	}
    	return user;
	}
	
	public String getRefreshToken(Integer refresh_token_id) {
		Log.d(TAG,"XXX getRefreshToken() refresh_token_id: " + refresh_token_id);
    	String refreshToken = "";
    	
    	SQLiteDatabase db = getReadableDatabase();

		Cursor cursor = db.query(REFRESH_TOKENS_TABLE_NAME, REFRESHTOKENS_FROM, _ID + " = ?", new String[] {refresh_token_id.toString()}, null, null, null);
    	Log.d(TAG,"XXX ProviderData#getRefreshToken count: " + cursor.getCount());
	    while (cursor.moveToNext())
	    {			
	        try {
	        	refreshToken = cursor.getString(cursor.getColumnIndex(REFRESH_TOKEN));
	    		Log.d(TAG,"XXX getRefreshToken() refresh_token: " + refreshToken);
	        } catch (Exception e) {
      			e.printStackTrace();
	        }
	    }

	    cursor.close();
	    db.close();

	    return refreshToken;
	}
	
    public AccessToken getAccessToken(Integer provider_id) {
		Log.d(TAG,"XXX getAccessToken: " + provider_id);
		
		AccessToken access_token;
		ArrayList<AccessToken> access_tokens;
		
    	try {
    		access_tokens = getAccessTokens(ACCESS_TOKEN_PROVIDER_ID + " = ?", new String[] {provider_id.toString()});
    	} finally {
    		close();
    	}
    	
		Log.d(TAG,"XXX getAccessToken: access_tokens.size():" + access_tokens.size());
		
    	if(0 == access_tokens.size()) {
    		access_token = null;
    	} else {
    		access_token = access_tokens.get(0);
    	}
    	return access_token;
    }
    
	public void addAccessToken(Integer provider_id, String access_token, Integer expires_in, Integer refresh_token_id) {
		Log.d(TAG,"ProviderData#addAccessToken  provider_id: " + provider_id);
		Log.d(TAG,"ProviderData#addAccessToken access_token: " + access_token);
		
		SQLiteDatabase db = getWritableDatabase();
		ContentValues values = new ContentValues();

		values.put(ACCESS_TOKEN_PROVIDER_ID, provider_id);
		values.put(REFRESH_TOKEN_ID, refresh_token_id);
		values.put(ACCESS_TOKEN, access_token);
		
		Date now = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		values.put(ACCESS_TOKEN_CREATED_AT, dateFormat.format(now));
		
		SimpleDateFormat expires_at_dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		values.put(ACCESS_TOKEN_EXPIRES_AT, expires_at_dateFormat.format(new Date(now.getTime()+(expires_in*1000))));
		
		//values.put(PROVIDER_CREATED_AT, System.currentTimeMillis());
		db.insertOrThrow(ACCESS_TOKENS_TABLE_NAME, null, values);
	}
	
	public Integer addRefreshToken(Integer provider_id, String refresh_token) {
		Log.d(TAG,"ProviderData#addRefreshToken  provider_id: " + provider_id);
		Log.d(TAG,"ProviderData#addRefreshToken refresh_token: " + refresh_token);
		
		SQLiteDatabase db = getWritableDatabase();
		ContentValues values = new ContentValues();
		
		values.put(REFRESH_TOKEN_PROVIDER_ID, provider_id);
		values.put(REFRESH_TOKEN, refresh_token);
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		values.put(REFRESH_TOKEN_CREATED_AT, dateFormat.format(new Date()));
		
		return((int)db.insertOrThrow(REFRESH_TOKENS_TABLE_NAME, null, values));
	}
	
	public ArrayList<Asset> getAssets(String selection, String [] selectionArgs) {
    	Log.d(TAG,"XXX ProviderData#getAssets");
		SQLiteDatabase db = getReadableDatabase();
	    ArrayList<Asset> resultList = new ArrayList<Asset>();

		Cursor cursor = db.query(ASSETS_TABLE_NAME, ASSETS_FROM, selection, selectionArgs, null, null, null);
    	Log.d(TAG,"XXX ProviderData#getAssets count: " + cursor.getCount());
	    while (cursor.moveToNext())
	    {			
	        try {
	            Asset ob = new Asset();
	            ob.setId(cursor.getInt(cursor.getColumnIndex(_ID)));
	            ob.setUrl(cursor.getString(cursor.getColumnIndex(ASSET_URL)));
	            ob.setName(cursor.getString(cursor.getColumnIndex(ASSET_NAME)));
	            ob.setBalance(cursor.getString(cursor.getColumnIndex(ASSET_BALANCE)));
	            ob.setProviderId(cursor.getInt(cursor.getColumnIndex(ASSET_PROVIDER_ID)));
	            resultList.add(ob);
	        } catch (Exception e) {
      			e.printStackTrace();
	        }
	    }

	    cursor.close();
	    db.close();
	    
	    return resultList;
	}

	public void addUser(Integer provider_id, String name, String email, String url, String website_url, String picture_url, String user_id) {
		Log.d(TAG,"ProviderData#addUser name: " + name);
		Log.d(TAG,"ProviderData#addUser  email: " + email);
		
		SQLiteDatabase db = getWritableDatabase();
		ContentValues values = new ContentValues();
		
		values.put(USER_PROVIDER_ID, provider_id);
		values.put(USER_NAME, name);
		values.put(USER_EMAIL, email);
		values.put(USER_URL, url);
		values.put(USER_WEBSITE_URL, website_url);
		values.put(USER_PICTURE_URL, picture_url);
		values.put(USER_USER_ID, user_id);
		
		Date now = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		values.put(USER_CREATED_AT, dateFormat.format(now));
		
		db.insertOrThrow(USERS_TABLE_NAME, null, values);
	}
	
    public void addAsset(Integer provider_id, String name, String url, String balance) {
		Log.d(TAG,"ProviderData#addAsset    name: " + name);
		Log.d(TAG,"ProviderData#addAsset     url: " + url);
		Log.d(TAG,"ProviderData#addAsset balance: " + balance);
		
		SQLiteDatabase db = getWritableDatabase();
		ContentValues values = new ContentValues();
		
		values.put(ASSET_PROVIDER_ID, provider_id);
		values.put(ASSET_NAME, name);
		values.put(ASSET_URL, url);
		values.put(ASSET_BALANCE, balance);
		
		Date now = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		values.put(ASSET_CREATED_AT, dateFormat.format(now));
		
		db.insertOrThrow(ASSETS_TABLE_NAME, null, values);
    }
}
