package org.opensourcecurrency.hack;

import java.util.List;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.preference.PreferenceCategory;

import android.preference.ListPreference;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.util.Log;

import org.opensourcecurrency.hack.model.Asset;

public class Prefs extends PreferenceActivity implements OnSharedPreferenceChangeListener {
	private static final String TAG = "OpenTransact";
	private static final String PREFCHANGE_ACTION = "org.opensourcecurrency.hack.PREF_CHANGE";
	
	   @Override
	   protected void onCreate(Bundle savedInstanceState) {
	      super.onCreate(savedInstanceState);
	      //addPreferencesFromResource(R.xml.settings);
	      setPreferenceScreen(createPreferenceHierarchy());
	   }
	   
	   @Override
	   public void onResume() {
		   super.onResume();
		   getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
	   }
	    
	   @Override
	   public void onPause() {
		   super.onPause();
		   getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
	   }
	    
	   @Override
   	   public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
	       Intent intent = new Intent(PREFCHANGE_ACTION);
		   sendBroadcast(intent);
	   }
	   
	   private PreferenceScreen createPreferenceHierarchy() {
	        // Root
	        PreferenceScreen root = getPreferenceManager().createPreferenceScreen(this);

	        String [] asset_urls;
	        String [] asset_names;
	        

	    	List<Asset> assets = Asset.all();
	
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
