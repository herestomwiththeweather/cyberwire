package org.opensourcecurrency.hack;

import static android.provider.BaseColumns._ID;
import static org.opensourcecurrency.hack.ConstantsProviders.PROVIDERS_TABLE_NAME;
import static org.opensourcecurrency.hack.ConstantsProviders.NAME;
import static org.opensourcecurrency.hack.ConstantsProviders.PROVIDER_URL;
import static org.opensourcecurrency.hack.ConstantsProviders.REDIRECT_URL;
import static org.opensourcecurrency.hack.ConstantsProviders.CLIENT_ID;
import static org.opensourcecurrency.hack.ConstantsProviders.CLIENT_SECRET;
import static org.opensourcecurrency.hack.ConstantsProviders.PROVIDER_CREATED_AT;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;

import static org.opensourcecurrency.hack.ConstantsAssets.ASSETS_TABLE_NAME;
import static org.opensourcecurrency.hack.ConstantsAssets.ASSET_PROVIDER_ID;
import static org.opensourcecurrency.hack.ConstantsAssets.ASSET_URL;
import static org.opensourcecurrency.hack.ConstantsAssets.ASSET_CREATED_AT;

import static org.opensourcecurrency.hack.ConstantsAccessTokens.ACCESS_TOKENS_TABLE_NAME;
import static org.opensourcecurrency.hack.ConstantsAccessTokens.ACCESS_TOKEN_PROVIDER_ID;
import static org.opensourcecurrency.hack.ConstantsAccessTokens.REFRESH_TOKEN_ID;
import static org.opensourcecurrency.hack.ConstantsAccessTokens.ACCESS_TOKEN;
import static org.opensourcecurrency.hack.ConstantsAccessTokens.ACCESS_TOKEN_EXPIRES_AT;
import static org.opensourcecurrency.hack.ConstantsAccessTokens.ACCESS_TOKEN_CREATED_AT;

import static org.opensourcecurrency.hack.ConstantsRefreshTokens.REFRESH_TOKENS_TABLE_NAME;
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

	private static String[] FROM = { _ID, NAME,PROVIDER_URL,REDIRECT_URL,CLIENT_ID,CLIENT_SECRET,PROVIDER_CREATED_AT };

	public ProviderData(Context ctx) {
	      super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
    	Log.d(TAG,"SQLiteOpenHelper#onCreate");

	      db.execSQL("CREATE TABLE " + PROVIDERS_TABLE_NAME + " (" + _ID
	              + " INTEGER PRIMARY KEY AUTOINCREMENT, " + NAME 
	              + " TEXT NOT NULL, " + PROVIDER_URL
	              + " TEXT NOT NULL, " + REDIRECT_URL
	              + " TEXT NOT NULL, " + CLIENT_ID
	              + " TEXT NOT NULL, " + CLIENT_SECRET
	              + " TEXT NOT NULL," + PROVIDER_CREATED_AT + " DATE);");
	      
	      db.execSQL("CREATE TABLE " + ASSETS_TABLE_NAME + " (" + _ID
	              + " INTEGER PRIMARY KEY AUTOINCREMENT, " + ASSET_PROVIDER_ID 
	              + " INTEGER, " + ASSET_URL
	              + " TEXT NOT NULL," + ASSET_CREATED_AT + " DATE);");
	      
	      db.execSQL("CREATE TABLE " + ACCESS_TOKENS_TABLE_NAME + " (" + _ID
	              + " INTEGER PRIMARY KEY AUTOINCREMENT, " + ACCESS_TOKEN_PROVIDER_ID
	              + " INTEGER, " + REFRESH_TOKEN_ID
	              + " INTEGER, " + ACCESS_TOKEN
	              + " TEXT NOT NULL," + ACCESS_TOKEN_EXPIRES_AT
	              + " DATE, " + ACCESS_TOKEN_CREATED_AT + " DATE);");
	      
	      db.execSQL("CREATE TABLE " + REFRESH_TOKENS_TABLE_NAME + " (" + _ID
	              + " INTEGER PRIMARY KEY AUTOINCREMENT, " + REFRESH_TOKEN
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
	            Provider ob = new Provider();
	            ob.setName(cursor.getString(cursor.getColumnIndex(NAME)));
	            ob.setProviderUrl(cursor.getString(cursor.getColumnIndex(PROVIDER_URL)));
	            ob.setRedirectUrl(cursor.getString(cursor.getColumnIndex(REDIRECT_URL)));
	            ob.setClientId(cursor.getString(cursor.getColumnIndex(CLIENT_ID)));
	            ob.setClientSecret(cursor.getString(cursor.getColumnIndex(CLIENT_SECRET)));
	            resultList.add(ob);
	        } catch (Exception e) {
      			e.printStackTrace();
	        }
	    }

	    cursor.close();
	    db.close();
	    
	    return resultList;
	}
	
    public Provider getProvider(String providername) {
		Log.d(TAG,"XXX getProvider: " + providername);
		
		Provider provider;
		ArrayList<Provider> assetProviders;
		
    	try {
    		assetProviders = getProviders(PROVIDER_URL + " = ?", new String[] {providername});
    	} finally {
    		close();
    	}
    	
    	provider = assetProviders.get(0);
    	return provider;
    }
    
	public void addProvider(String name, String provider_url, String redirect_url, String client_id, String client_secret) {
    	Log.d(TAG,"ProviderData#addProvider: " + name);

		SQLiteDatabase db = getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(NAME, name);
		values.put(PROVIDER_URL, provider_url);
		values.put(REDIRECT_URL, redirect_url);
		values.put(CLIENT_ID, client_id);
		values.put(CLIENT_SECRET, client_secret);
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		values.put(PROVIDER_CREATED_AT, dateFormat.format(new Date()));
		
		//values.put(PROVIDER_CREATED_AT, System.currentTimeMillis());
		db.insertOrThrow(PROVIDERS_TABLE_NAME, null, values);
	}
}
