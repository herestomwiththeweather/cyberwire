package org.opensourcecurrency.hack;

import java.net.URI;

import org.apache.http.client.methods.HttpGet;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

public class Profile extends Activity {
	
	private static final String PROFILE_ACTION = "org.opensourcecurrency.hack.PROFILE";
	private static final String TAG = "OpenTransact";
	private ProviderData providers;
	private ProgressDialog progress;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);
        
		providers = new ProviderData(this);
		Asset asset = providers.getCurrentAsset(this);
		Provider provider = asset.getProvider();
		
		User user = provider.getUser();
		if(null == user) {
			provider.getUserInfo(this, PROFILE_ACTION);
        	progress = ProgressDialog.show(this, "Fetching profile", "Waiting...", true);
		} else {
    		Toast toast = Toast.makeText(this, user.email, Toast.LENGTH_LONG);
    		toast.setGravity(Gravity.CENTER, 0, 0);
    		toast.show();
		}
    }
    
    @Override
    public void onResume() {
      super.onResume();
      registerReceiver(receiver, new IntentFilter(PROFILE_ACTION));
    }
    
    @Override
    public void onPause() {
    	super.onPause();
    	unregisterReceiver(receiver);
    }
    
    private BroadcastReceiver receiver = new BroadcastReceiver() {
    	@Override
    	public void onReceive(Context context, Intent intent) {
    		Log.d(TAG,"onReceive called (Profile)!");
    		if(progress != null) {
    			progress.dismiss();
    		}
    		String response = intent.getStringExtra(RestTask.HTTP_RESPONSE);
    		Log.d(TAG,"response(Profile): "+response);
    		
    		if(null == response) {
        		Toast toast = Toast.makeText(context, "Error connecting to provider!", Toast.LENGTH_LONG);
        		toast.setGravity(Gravity.CENTER, 0, 0);
        		toast.show();
    			return;
    		}

    		try {
              JSONObject profile_response = new JSONObject(response);
          	  String email = profile_response.getString("email");
          	  Log.d(TAG,"adding email: " + email);
      		  providers = new ProviderData(context);
    		  Asset asset = providers.getCurrentAsset(context);
    		  Provider provider = asset.getProvider();
    		  provider.addUser(profile_response);
    		} catch (JSONException e) {
    		  e.printStackTrace();
    		}
    		
			finish();

    	}
    };
}
