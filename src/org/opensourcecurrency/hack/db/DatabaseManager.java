package org.opensourcecurrency.hack.db;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.content.Context;

import org.opensourcecurrency.hack.model.Asset;
import org.opensourcecurrency.hack.model.Provider;
import org.opensourcecurrency.hack.model.AccessToken;
import org.opensourcecurrency.hack.model.RefreshToken;
import org.opensourcecurrency.hack.model.User;

public class DatabaseManager {

	static private DatabaseManager instance;

	static public void init(Context ctx) {
		if (null==instance) {
			instance = new DatabaseManager(ctx);
		}
	}
	
	static public void clear() {
		getInstance().clearDatabase();
	}
	
	private void clearDatabase() {
		getHelper().clearDatabase();
	}

	static public DatabaseManager getInstance() {
		return instance;
	}

	private DatabaseHelper helper;
	private DatabaseManager(Context ctx) {
		helper = new DatabaseHelper(ctx);
	}

	private DatabaseHelper getHelper() {
		return helper;
	}

/*
	public void addWishList(WishList l) {
		try {
			getHelper().getWishListDao().create(l);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	} */
	public Asset getAssetWithId(int assetId) {
		Asset asset = null;
		try {
			asset = getHelper().getAssetDao().queryForId(assetId);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return asset;
	}
	
	public Asset getAssetWithUrl(String url) {
		Asset asset = null;
		try {
			asset = getHelper().getAssetDao().queryForFirst(getHelper().getAssetDao().queryBuilder().where().eq("url", url).prepare());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return asset;
	}
	
	public List<Asset> getAllAssets() {
		List<Asset> assets = null;
		try {
			assets = getHelper().getAssetDao().queryForAll();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return assets;
	}
	
	public Asset newAsset() {
		Asset asset = new Asset();
		try {
			getHelper().getAssetDao().create(asset);
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			asset.created_at = dateFormat.format(new Date());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return asset;
	}
	
	public void updateAsset(Asset asset) {
		try {
			getHelper().getAssetDao().update(asset);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void deleteAsset(Asset asset) {
		try {
			getHelper().getAssetDao().delete(asset);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public Provider getProviderWithId(int providerId) {
		Provider provider = null;
		try {
			provider = getHelper().getProviderDao().queryForId(providerId);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return provider;
	}
	
	public Provider getProviderWithUrl(String url) {
		Provider provider = null;
		try {
			provider = getHelper().getProviderDao().queryForFirst(getHelper().getProviderDao().queryBuilder().where().eq("url", url).prepare());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return provider;
	}

	public List<Provider> getAllProviders() {
		List<Provider> providers = null;
		try {
			providers = getHelper().getProviderDao().queryForAll();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return providers;
	}
	
	public Provider newProvider() {
		Provider provider = new Provider();
		try {
			getHelper().getProviderDao().create(provider);
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			provider.created_at = dateFormat.format(new Date());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return provider;
	}
	
	public void updateProvider(Provider provider) {
		try {
			getHelper().getProviderDao().update(provider);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void deleteProvider(Provider provider) {
		try {
			getHelper().getProviderDao().delete(provider);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public List<AccessToken> getAllAccessTokens() {
		List<AccessToken> access_tokens = null;
		try {
			access_tokens = getHelper().getAccessTokenDao().queryForAll();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return access_tokens;
	}
	
	public AccessToken newAccessToken() {
		AccessToken access_token = new AccessToken();
		try {
			getHelper().getAccessTokenDao().create(access_token);
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			access_token.created_at = dateFormat.format(new Date());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return access_token;
	}
	
	public void updateAccessToken(AccessToken access_token) {
		try {
			getHelper().getAccessTokenDao().update(access_token);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void deleteAccessToken(AccessToken access_token) {
		try {
			getHelper().getAccessTokenDao().delete(access_token);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public RefreshToken newRefreshToken() {
		RefreshToken refresh_token = new RefreshToken();
		try {
			getHelper().getRefreshTokenDao().create(refresh_token);
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			refresh_token.created_at = dateFormat.format(new Date());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return refresh_token;
	}
	
	public void updateRefreshToken(RefreshToken refresh_token) {
		try {
			getHelper().getRefreshTokenDao().update(refresh_token);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void deleteRefreshToken(RefreshToken refresh_token) {
		try {
			getHelper().getRefreshTokenDao().delete(refresh_token);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public User newUser() {
		User user = new User();
		try {
			getHelper().getUserDao().create(user);
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			user.created_at = dateFormat.format(new Date());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return user;
	}
	
	public void updateUser(User user) {
		try {
			getHelper().getUserDao().update(user);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void deleteUser(User user) {
		try {
			getHelper().getUserDao().delete(user);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
