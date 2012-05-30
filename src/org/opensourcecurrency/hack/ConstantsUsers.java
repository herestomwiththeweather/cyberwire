package org.opensourcecurrency.hack;

import static android.provider.BaseColumns._ID;
import android.provider.BaseColumns;

public interface ConstantsUsers extends BaseColumns {
	public static final String USERS_TABLE_NAME = "users";
	// columns in the assets table
	public static final String USER_PROVIDER_ID = "provider_id";
	public static final String USER_URL = "url";
	public static final String USER_WEBSITE_URL = "website_url";
	public static final String USER_PICTURE_URL = "picture_url";
	public static final String USER_NAME = "name";
	public static final String USER_EMAIL = "email";
	public static final String USER_USER_ID = "user_id";
	public static final String USER_CREATED_AT = "created_at";
}
