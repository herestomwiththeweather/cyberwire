package org.opensourcecurrency.hack;

import static android.provider.BaseColumns._ID;
import static org.opensourcecurrency.hack.ConstantsProviders.PROVIDERS_TABLE_NAME;
import static org.opensourcecurrency.hack.ConstantsProviders.NAME;
import static org.opensourcecurrency.hack.ConstantsProviders.PROVIDER_URL;
import static org.opensourcecurrency.hack.ConstantsProviders.REDIRECT_URL;
import static org.opensourcecurrency.hack.ConstantsProviders.CLIENT_ID;
import static org.opensourcecurrency.hack.ConstantsProviders.CLIENT_SECRET;
import static org.opensourcecurrency.hack.ConstantsProviders.PROVIDER_CREATED_AT;

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

import android.content.Context;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class ProviderData extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "providers.db";
	private static final int DATABASE_VERSION = 1;
	private static final String TAG = "OpenTransact";

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

}
