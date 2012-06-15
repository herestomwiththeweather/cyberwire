package org.opensourcecurrency.hack.db;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import org.opensourcecurrency.hack.model.Asset;
import org.opensourcecurrency.hack.model.Provider;
import org.opensourcecurrency.hack.model.AccessToken;
import org.opensourcecurrency.hack.model.RefreshToken;
import org.opensourcecurrency.hack.model.User;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {
	// name of the database file for your application -- change to something appropriate for your app
	private static final String DATABASE_NAME = "providers.sqlite";

	// any time you make changes to your database objects, you may have to increase the database version
	private static final int DATABASE_VERSION = 1;

	// the DAO object we use to access the SimpleData table
	private Dao<Asset, Integer> assetDao = null;
	private Dao<Provider, Integer> providerDao = null;
	private Dao<AccessToken, Integer> accessTokenDao = null;
	private Dao<RefreshToken, Integer> refreshTokenDao = null;
	private Dao<User, Integer> userDao = null;

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	public void clearDatabase() {
		try {
			TableUtils.clearTable(connectionSource, Asset.class);
			TableUtils.clearTable(connectionSource, Provider.class);
			TableUtils.clearTable(connectionSource, AccessToken.class);
			TableUtils.clearTable(connectionSource, RefreshToken.class);
			TableUtils.clearTable(connectionSource, User.class);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} catch (java.sql.SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onCreate(SQLiteDatabase database,ConnectionSource connectionSource) {
		try {
			TableUtils.createTable(connectionSource, Asset.class);
			TableUtils.createTable(connectionSource, Provider.class);
			TableUtils.createTable(connectionSource, AccessToken.class);
			TableUtils.createTable(connectionSource, RefreshToken.class);
			TableUtils.createTable(connectionSource, User.class);
		} catch (SQLException e) {
			Log.e(DatabaseHelper.class.getName(), "Can't create database", e);
			throw new RuntimeException(e);
		} catch (java.sql.SQLException e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db,ConnectionSource connectionSource, int oldVersion, int newVersion) {
		try {
			List<String> allSql = new ArrayList<String>(); 
			switch(oldVersion) 
			{
			  case 1: 
				  //allSql.add("alter table AdData add column `new_col` VARCHAR");
				  //allSql.add("alter table AdData add column `new_col2` VARCHAR");
			}
			for (String sql : allSql) {
				db.execSQL(sql);
			}
		} catch (SQLException e) {
			Log.e(DatabaseHelper.class.getName(), "exception during onUpgrade", e);
			throw new RuntimeException(e);
		}
	}

	public Dao<Asset, Integer> getAssetDao() {
		if(null == assetDao) {
			try {
				assetDao = getDao(Asset.class);
			} catch (java.sql.SQLException e) {
				e.printStackTrace();
			}
		}
		return assetDao;
	}
	
	public Dao<Provider, Integer> getProviderDao() {
		if(null == providerDao) {
			try {
				providerDao = getDao(Provider.class);
			} catch (java.sql.SQLException e) {
				e.printStackTrace();
			}
		}
		return providerDao;
	}
	
	public Dao<AccessToken, Integer> getAccessTokenDao() {
		if(null == accessTokenDao) {
			try {
				accessTokenDao = getDao(AccessToken.class);
			} catch (java.sql.SQLException e) {
				e.printStackTrace();
			}
		}
		return accessTokenDao;
	}
	
	public Dao<RefreshToken, Integer> getRefreshTokenDao() {
		if(null == refreshTokenDao) {
			try {
				refreshTokenDao = getDao(RefreshToken.class);
			} catch (java.sql.SQLException e) {
				e.printStackTrace();
			}
		}
		return refreshTokenDao;
	}
	
	public Dao<User, Integer> getUserDao() {
		if(null == userDao) {
			try {
				userDao = getDao(User.class);
			} catch (java.sql.SQLException e) {
				e.printStackTrace();
			}
		}
		return userDao;
	}
}
