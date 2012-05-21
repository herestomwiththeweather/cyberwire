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
	        Integer [] provider_ids;
	        String [] provider_names;
	        String [] provider_urls;
	        String [] provider_client_ids;
	        
			providers = new ProviderData(this);
			ArrayList<Provider> assetProviders;
			
	    	try {
	    		assetProviders = providers.getProviders("",new String [] {});
	    	} finally {
	    		providers.close();
	    	}
	
	    	Log.d(TAG, "XXX -----------------------------------------------------------");

	    	int count = assetProviders.size();
	    	Log.d(TAG, "XXX createPreferenceHierarchy size: " + count);
	    	provider_ids = new Integer[count];
	    	provider_names = new String[count];
	    	provider_urls = new String[count];
	    	provider_client_ids = new String[count];
	    	
	    	int i = 0;
	    	
			while (i < assetProviders.size()) {
				Provider p = assetProviders.get(i);
				provider_ids[i] = p.providerId;
				provider_names[i] = p.providerName;
				provider_urls[i] = p.providerUrl;
				provider_client_ids[i] = p.clientId;

		    	Log.d(TAG, "[" + i + "]" + "Prefs  provider id: " + provider_ids[i]);
		    	Log.d(TAG, "[" + i + "]" + "Prefs  provider name: " + provider_names[i]);
		    	Log.d(TAG, "[" + i + "]" + "Prefs  provider url: " + provider_urls[i]);
		    	Log.d(TAG, "[" + i + "]" + "Prefs  provider clientid: " + provider_client_ids[i]);

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
}
