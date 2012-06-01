package org.opensourcecurrency.hack;

import java.net.URI;

import org.apache.http.client.methods.HttpGet;

import android.app.ProgressDialog;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;
import android.content.Context;

public class Asset {
	public Integer assetId;
	public String url;
	public String name;
	public String balance;
	
	public Integer m_providerId;
	private Provider m_provider;
	private static final String TAG = "OpenTransact";
	
	public boolean getTransactions(Context context, String action) {
		String access_token = m_provider.getAccessToken();
        Log.d(TAG,"access_token : " + access_token);
        
        if(access_token.equals("")) {
    		Toast toast = Toast.makeText(context, "No access token yet!", Toast.LENGTH_LONG);
    		toast.setGravity(Gravity.CENTER, 0, 0);
    		toast.show();
        	return false;
        }
    	try {
        	HttpGet transactionsRequest = new HttpGet(new URI(url));
      		transactionsRequest.setHeader("Accept","application/json");
      		transactionsRequest.setHeader("Authorization","Bearer " + access_token);
        	RestTask task = new RestTask(context, action);
        	task.execute(transactionsRequest);
    	} catch(Exception e) {
        	e.printStackTrace();    		
    	}
    	
    	return true;
	}
	
	public void setId(Integer id) {
		assetId = id;
	}
	
	public void setUrl(String asset_url) {
		url = asset_url;
	}
	
	public void setName(String asset_name) {
		name = asset_name;
	}
	
	public void setBalance(String asset_balance) {
		balance = asset_balance;
	}
	
	public void setProviderId(Integer provider_id) {
		m_providerId = provider_id;
	}
	
	public void setProvider(Provider provider) {
		m_provider = provider;
	}
	
	public Provider getProvider() {
		return m_provider;
	}
}
