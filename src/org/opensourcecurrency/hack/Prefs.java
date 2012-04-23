package org.opensourcecurrency.hack;

import static org.opensourcecurrency.hack.ConstantsProviders.PROVIDERS_TABLE_NAME;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.preference.PreferenceCategory;

import android.preference.ListPreference;
import android.util.Log;

import static android.provider.BaseColumns._ID;
import static org.opensourcecurrency.hack.ConstantsProviders.PROVIDERS_TABLE_NAME;
import static org.opensourcecurrency.hack.ConstantsProviders.NAME;
import static org.opensourcecurrency.hack.ConstantsProviders.PROVIDER_URL;
import static org.opensourcecurrency.hack.ConstantsProviders.REDIRECT_URL;
import static org.opensourcecurrency.hack.ConstantsProviders.CLIENT_ID;
import static org.opensourcecurrency.hack.ConstantsProviders.CLIENT_SECRET;
import static org.opensourcecurrency.hack.ConstantsProviders.PROVIDER_CREATED_AT;

public class Prefs extends PreferenceActivity {
	private static final String TAG = "OpenTransact";
	private ProviderData providers;
	private static String[] FROM = { _ID, NAME,PROVIDER_URL,REDIRECT_URL,CLIENT_ID,CLIENT_SECRET,PROVIDER_CREATED_AT };
	
	   @Override
	   protected void onCreate(Bundle savedInstanceState) {
	      super.onCreate(savedInstanceState);
	      //addPreferencesFromResource(R.xml.settings);
	      setPreferenceScreen(createPreferenceHierarchy());
	   }
	   
	   private PreferenceScreen createPreferenceHierarchy() {
	        // Root
	        PreferenceScreen root = getPreferenceManager().createPreferenceScreen(this);
	        Cursor cursor;
	        String [] provider_names;
	        String [] provider_urls;

	    	// XXX
	    	providers = new ProviderData(this);
	
	    	try {
	    		//addProvider("picomoney", "https://picomoney.com", "https://picomoney.com/picos", "cc6f64d61ae2c903d1bb", "77338e2faebdc5e4a55a94f76c01940ad26d4ecf");
	    		//addProvider("ubuntu.local", "http://192.168.1.102:3000", "http://192.168.1.102:3000/transacts", "xBoHeeNNFt3LQ7U1tvAb8BVKr32duE6rdWtpCSFD", "HraYcLT5F5nRll5KF5tw8umdER3EOrsFXIEro67T");
	    		cursor = getProviders();
	    	} finally {
	    		providers.close();
	    	}
	    	Log.d(TAG, "XXX -----------------------------------------------------------");

	    	int count = cursor.getCount();
	    	Log.d(TAG, "XXX count: " + count);
	    	provider_names = new String[count];
	    	provider_urls = new String[count];
	    	int i = 0;
	    	
			while (cursor.moveToNext()) {
				long id = cursor.getLong(0);
				provider_names[i] = cursor.getString(1);
				provider_urls[i] = cursor.getString(2);
				String redirect_url = cursor.getString(3);
				String client_id = cursor.getString(4);
				String client_secret = cursor.getString(5);
				long created_at = cursor.getLong(6);
		    	Log.d(TAG,"Prefs            id: " + id);
		    	Log.d(TAG,"Prefs provider name: " + provider_names[i]);
		    	Log.d(TAG,"Prefs  provider url: " + provider_urls[i]);
		    	Log.d(TAG,"Prefs  redirect url: " + redirect_url);
		    	Log.d(TAG,"Prefs     client id: " + client_id);
		    	Log.d(TAG,"Prefs client secret: " + client_secret);
		    	Log.d(TAG,"Prefs    created at: " + created_at);
		    	i++;
			}
			
	        // Dialog based preferences
	        PreferenceCategory dialogBasedPrefCat = new PreferenceCategory(this);
	        dialogBasedPrefCat.setTitle("");
	        root.addPreference(dialogBasedPrefCat);

	        // List preference
	        ListPreference listPref = new ListPreference(this);
	        listPref.setEntries(provider_names);
	        listPref.setEntryValues(provider_urls);
	        listPref.setDialogTitle("OpenTransact Provider");
	        listPref.setKey("assetProviderPref");
	        listPref.setTitle("Asset Provider");
	        listPref.setSummary("Choose your Asset Provider");
	        dialogBasedPrefCat.addPreference(listPref);

	        return root;
	   }
	   
		private Cursor getProviders() {
	    	Log.d(TAG,"XXX Prefs#getProviders");
			SQLiteDatabase db = providers.getReadableDatabase();
			Cursor cursor = db.query(PROVIDERS_TABLE_NAME, FROM, null, null, null, null, null);
	    	Log.d(TAG,"XXX Prefs#getProviders count: " + cursor.getCount());
			startManagingCursor(cursor);
			return cursor;
		}
		
		private void addProvider(String name, String provider_url, String redirect_url, String client_id, String client_secret) {
	    	Log.d(TAG,"Cyberwire#addProvider: " + name);

			SQLiteDatabase db = providers.getWritableDatabase();
			ContentValues values = new ContentValues();
			values.put(NAME, name);
			values.put(PROVIDER_URL, provider_url);
			values.put(REDIRECT_URL, redirect_url);
			values.put(CLIENT_ID, client_id);
			values.put(CLIENT_SECRET, client_secret);
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			values.put(PROVIDER_CREATED_AT, dateFormat.format(new Date()));
			
			values.put(PROVIDER_CREATED_AT, System.currentTimeMillis());
			db.insertOrThrow(PROVIDERS_TABLE_NAME, null, values);
		}
}
