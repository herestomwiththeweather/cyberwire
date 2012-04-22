package org.opensourcecurrency.hack;

import android.provider.BaseColumns;

public interface ConstantsProviders extends BaseColumns {
	public static final String PROVIDERS_TABLE_NAME = "providers";
	// columns in the providers table
	public static final String NAME = "name";
	public static final String PROVIDER_URL = "url";
	public static final String REDIRECT_URL = "redirect_url";
	public static final String CLIENT_ID = "client_id";
	public static final String CLIENT_SECRET = "client_secret";
	public static final String PROVIDER_CREATED_AT = "created_at";
}