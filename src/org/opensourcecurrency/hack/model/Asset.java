package org.opensourcecurrency.hack.model;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;
import android.content.Context;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.opensourcecurrency.hack.db.DatabaseManager;
import org.opensourcecurrency.hack.RestTask;

@DatabaseTable
public class Asset {
	@DatabaseField(generatedId=true)
	public int id;
	
	@DatabaseField
	public String name;
	
	@DatabaseField(foreign=true,foreignAutoRefresh=true)
	public Provider provider;
	
	@DatabaseField
	public String url;
	
	@DatabaseField
	public String balance;
	
	@DatabaseField
	public String created_at;
	
	private static final String TAG = "OpenTransact";
	
	static public Asset getCurrentAsset(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		return Asset.find_by_url(prefs.getString("assetProviderPref",""));
	}
	
	public boolean postTransaction(Context context, String to, String amount, String note, String action) {
		String access_token = getAccessToken().token;
		
        Log.d(TAG,"Asset#postTransaction access_token : " + access_token);
        
      	try {
      		HttpPost paymentRequest = new HttpPost(new URI(url));
      		paymentRequest.setHeader("Accept","application/json");
      		paymentRequest.setHeader("Authorization","Bearer " + access_token);
    		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
    		parameters.add(new BasicNameValuePair("to", to));
    		parameters.add(new BasicNameValuePair("amount", amount));
    		parameters.add(new BasicNameValuePair("note", note));
    		paymentRequest.setEntity(new UrlEncodedFormEntity(parameters));
    		
    		RestTask task = new RestTask(context, action);
    		task.execute(paymentRequest);

      	} catch (Exception e) {
  			e.printStackTrace();
      	}
		return true;
	}
	
	public boolean getTransactions(Context context, String action) {
		String access_token = getAccessToken().token;
		
        Log.d(TAG,"Asset#getTransactions access_token : " + access_token);
        
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
	
	private AccessToken getAccessToken() {
		return provider.getAccessTokenObject();
	}
	
	static public Asset find(int assetId) {
		return DatabaseManager.getInstance().getAssetWithId(assetId);
	}
	
	static public Asset find_by_url(String url) {
		return DatabaseManager.getInstance().getAssetWithUrl(url);
	}
	
	static public List<Asset> all() {
		return DatabaseManager.getInstance().getAllAssets();
	}
	
	static public Asset create() {
		return DatabaseManager.getInstance().newAsset();
	}
	
	public boolean save() {
		DatabaseManager.getInstance().updateAsset(this);
		return true;
	}
	
	public void destroy() {
		DatabaseManager.getInstance().deleteAsset(this);
	}
}
