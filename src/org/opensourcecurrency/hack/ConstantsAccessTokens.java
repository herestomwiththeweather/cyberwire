package org.opensourcecurrency.hack;

import android.provider.BaseColumns;

public interface ConstantsAccessTokens extends BaseColumns {
	public static final String ACCESS_TOKENS_TABLE_NAME = "access_tokens";
	// columns in the access_tokens table
	public static final String ACCESS_TOKEN_PROVIDER_ID = "provider_id";
	public static final String ACCESS_TOKEN = "token";
	public static final String REFRESH_TOKEN_ID = "refresh_token_id";
	public static final String ACCESS_TOKEN_EXPIRES_AT = "expires_at";
	public static final String ACCESS_TOKEN_CREATED_AT = "created_at";
}
