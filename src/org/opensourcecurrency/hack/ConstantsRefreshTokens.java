package org.opensourcecurrency.hack;

import android.provider.BaseColumns;

public interface ConstantsRefreshTokens extends BaseColumns {
	public static final String REFRESH_TOKENS_TABLE_NAME = "refresh_tokens";
	// columns in the refresh_tokens table
	public static final String REFRESH_TOKEN = "token";
	public static final String REFRESH_TOKEN_EXPIRES_AT = "expires_at";
	public static final String REFRESH_TOKEN_CREATED_AT = "created_at";
}
