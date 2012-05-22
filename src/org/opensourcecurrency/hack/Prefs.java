package org.opensourcecurrency.hack;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import static org.opensourcecurrency.hack.ConstantsAssets.ASSET_PROVIDER_ID;
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

	        String [] asset_urls;
	        String [] asset_names;
	        
			providers = new ProviderData(this);
			ArrayList<Asset> assets;
	    	try {
	    		assets = providers.getAssets("", new String[] {});
	    	} finally {
	    		providers.close();
	    	}
	
	    	Log.d(TAG, "XXX -----------------------------------------------------------");

	    	int count = assets.size();
	    	Log.d(TAG, "XXX createPreferenceHierarchy size: " + count);

	    	asset_names = new String[count];
	    	asset_urls = new String[count];
	    	
	    	int i = 0;
	    	
			while (i < count) {
				Asset a = assets.get(i);
				asset_names[i] = a.name;
				asset_urls[i] = a.url;

		    	Log.d(TAG, "[" + i + "]" + "Prefs  asset name: " + asset_names[i]);
		    	Log.d(TAG, "[" + i + "]" + "Prefs  asset url: " + asset_urls[i]);

		    	i++;
			}
			
	        // Dialog based preferences
	        PreferenceCategory dialogBasedPrefCat = new PreferenceCategory(this);
	        dialogBasedPrefCat.setTitle("");
	        root.addPreference(dialogBasedPrefCat);

	        // List preference
	        ListPreference listPref = new ListPreference(this);

	        listPref.setEntries(asset_names);
	        listPref.setEntryValues(asset_urls);
	        listPref.setDialogTitle("OpenTransact Asset");
	        listPref.setKey("assetProviderPref");
	        listPref.setTitle("Asset");
	        listPref.setSummary("Choose your Asset");
	        dialogBasedPrefCat.addPreference(listPref);

	        return root;
	   }
}
