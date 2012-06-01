package org.opensourcecurrency.hack;

import android.provider.BaseColumns;

public interface ConstantsAssets extends BaseColumns {
	public static final String ASSETS_TABLE_NAME = "assets";
	// columns in the assets table
	public static final String ASSET_PROVIDER_ID = "provider_id";
	public static final String ASSET_URL = "url";
	public static final String ASSET_NAME = "name";
	public static final String ASSET_BALANCE = "balance";
	public static final String ASSET_CREATED_AT = "created_at";
}
