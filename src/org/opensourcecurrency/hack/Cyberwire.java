package org.opensourcecurrency.hack;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import android.view.View;
import android.view.View.OnClickListener;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.opensourcecurrency.hack.RestTask;
import org.opensourcecurrency.hack.WebViewActivity;
import org.opensourcecurrency.hack.Provider;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.database.Cursor;

import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import static org.opensourcecurrency.hack.ConstantsProviders.PROVIDER_URL;


public class Cyberwire extends Activity implements OnClickListener {
	private static final String OAUTH_TOKEN_ACTION = "org.opensourcecurrency.hack.OAUTH_TOKEN";
	private static final String WALLET_ACTION = "org.opensourcecurrency.hack.WALLET";
	private static final String TAG = "OpenTransact";
	private ProviderData providers;

	private ProgressDialog progress;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {

    	//this.deleteDatabase("providers.db");
		// XXX
		//ArrayList<Asset> assets = provider.getAssets();
    	//int n=0;
    	//while(n < assets.size()) {
		//	Asset a = assets.get(n);
		//	Log.d(TAG, a.name + ": " + a.url);
    	//	n++;
    	//}
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        View sendButton = findViewById(R.id.send_button);
        sendButton.setOnClickListener(this);
        View transactionsButton = findViewById(R.id.transactions_button);
        transactionsButton.setOnClickListener(this);
        View profileButton = findViewById(R.id.profile_button);
        profileButton.setOnClickListener(this);
        
    }
    
    @Override
    public void onClick(View view) {
    	switch (view.getId()) {
    	case R.id.send_button:
    		Intent i = new Intent(this, Send.class);
    		startActivity(i);
    		break;
    	case R.id.transactions_button:
    		Intent j = new Intent(this, Transactions.class);
    		startActivity(j);
    		break;
    	case R.id.profile_button:
    		Intent k = new Intent(this, Profile.class);
    		startActivity(k);
    		break;
    	}
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	super.onCreateOptionsMenu(menu);
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.menu, menu);
    	return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	Log.d(TAG,"onOptionsItemSelected");
    	switch (item.getItemId()) {
    	case R.id.addprovider:
        	Log.d(TAG,"onOptionsItemSelected[addprovider]");
    		Intent i = new Intent(this, AddProvider.class);
    		startActivity(i);
    		return true;
    	case R.id.exit:
    		return true;
    	case R.id.login:
    		Intent intent = new Intent(this,WebViewActivity.class);
    		
    		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

    		providers = new ProviderData(this);
    		Provider provider = providers.getProvider(prefs.getString("assetProviderPref",""));
    		
        	Log.d(TAG,"onOptionsItemSelected[login] provider_name: " + provider.providerName);
        	Log.d(TAG,"onOptionsItemSelected[login] provider_url: " + provider.providerUrl);
        	Log.d(TAG,"onOptionsItemSelected[login] clientid: " + provider.clientId);
        	Log.d(TAG,"onOptionsItemSelected[login] redirect_uri: " + provider.redirectUrl);
    		intent.setData(Uri.parse(provider.providerUrl+"/oauth/authorize?client_id="+provider.clientId+"&response_type=code&redirect_uri="+provider.redirectUrl));

    		startActivityForResult(intent,0);
    		return true;
    	case R.id.settings:
    		startActivity(new Intent(this, Prefs.class));
    		return true;
    	}
    	return super.onOptionsItemSelected(item);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	Log.d(TAG,"onActivityResult");

    	switch (requestCode) {
    	case 0:
    		if (resultCode != RESULT_OK || data == null) {
    	    	Log.d(TAG,"XXX onActivityResult: BAD RESULT");
    			return;
    		}
    		String token = data.getStringExtra("token");
    		Log.d(TAG,token);
    		Toast toast = Toast.makeText(this, token, Toast.LENGTH_LONG);
    		toast.setGravity(Gravity.CENTER, 0, 0);
    		toast.show();

    		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

    		providers = new ProviderData(this);
    		Provider provider = providers.getProvider(prefs.getString("assetProviderPref",""));
 
	    	String provider_url = provider.providerUrl;
			String redirect_url = provider.redirectUrl;
			String client_id = provider.clientId;
			String client_secret = provider.clientSecret;
			
        	Log.d(TAG,"onActivityResult provider_url: " + provider_url);
        	Log.d(TAG,"onActivityResult clientid: " + client_id);
        	Log.d(TAG,"onActivityResult client secret: " + client_secret);
        	Log.d(TAG,"onActivityResult redirect_uri: " + redirect_url);
        	
       		try {
      		  String url = provider_url + "/oauth/token";
      		  HttpPost tokenRequest = new HttpPost(new URI(url));
      		  List<NameValuePair> parameters = new ArrayList<NameValuePair>();
      		  parameters.add(new BasicNameValuePair("client_id", client_id));
      		  parameters.add(new BasicNameValuePair("client_secret", client_secret));
      		  parameters.add(new BasicNameValuePair("code", token));
      		  parameters.add(new BasicNameValuePair("grant_type","authorization_code"));
      		  parameters.add(new BasicNameValuePair("redirect_uri",redirect_url));
      		  tokenRequest.setEntity(new UrlEncodedFormEntity(parameters));
      		  
      		  RestTask task = new RestTask(this, OAUTH_TOKEN_ACTION);
      		  task.execute(tokenRequest);
            	  progress = ProgressDialog.show(this, "Fetching token", "Waiting...", true);

      		} catch (Exception e) {
      			e.printStackTrace();
      		}
    		return;
    	}
    	super.onActivityResult(requestCode, resultCode, data);
    }
    
    @Override
    public void onResume() {
      super.onResume();
      registerReceiver(receiver, new IntentFilter(OAUTH_TOKEN_ACTION));
      registerReceiver(receiver, new IntentFilter(WALLET_ACTION));
    }
    
    @Override
    public void onPause() {
    	super.onPause();
    	unregisterReceiver(receiver);
    }
    
    String addAccessToken(Context context, Intent intent, Provider provider) {
		String response = intent.getStringExtra(RestTask.HTTP_RESPONSE);
		Log.d(TAG,"response: "+response);
		
		String access_token = null;
		
		if(null == response) {
			return "";
		}

		try {
          JSONObject access_token_response = new JSONObject(response);
      	  access_token = access_token_response.getString("access_token");
      	  Log.d(TAG," access token: " + access_token);
      	  provider.addAccessToken(access_token, 0, "");

      	  //String refresh_token = access_token_response.getString("refresh_token");
      	  //String expires_in = access_token_response.getString("expires_in");

      	  //Log.d(TAG,"refresh token: " + refresh_token);
      	  //Log.d(TAG,"   expires_in: " + expires_in);


  		} catch (JSONException e) {
  		  e.printStackTrace();
  		  return "";
  		}
		
    	return access_token;
    }
    
    private void getWallet(Context context, String access_token, Provider provider) {
    	String wallet_path = "/wallet";
    	
       	try {
        	HttpGet walletRequest = new HttpGet(new URI(provider.providerUrl + wallet_path));
      		walletRequest.setHeader("Accept","application/json");
      		walletRequest.setHeader("Authorization","Bearer " + access_token);
        	RestTask task = new RestTask(this, WALLET_ACTION);
        	task.execute(walletRequest);
        	progress = ProgressDialog.show(this, "Fetching wallet", "Waiting...", true);
    	} catch(Exception e) {
        	e.printStackTrace();    		
    	}
    }
    
    boolean addAssets(Intent intent, Provider provider) {
		String response = intent.getStringExtra(RestTask.HTTP_RESPONSE);
		Log.d(TAG,"response: "+response);
		try {
	          JSONObject wallet_response = new JSONObject(response);
	      	  Integer total = wallet_response.getInt("total");
	      	  Log.d(TAG,"wallet total: " + total);
	      	  JSONArray assets = wallet_response.getJSONArray("assets");
	      	  for(int i=0;i<assets.length();i++) {
	      		  JSONObject asset = assets.getJSONObject(i);
	      		  provider.addAsset(asset.getString("name"), asset.getString("url"));
	      	  }
  		} catch (JSONException e) {
  		  e.printStackTrace();
  		  return false;
  		}
		
		return true;
    }
    
    private BroadcastReceiver receiver = new BroadcastReceiver() {
    	@Override
    	public void onReceive(Context context, Intent intent) {
    		Log.d(TAG,"onReceive called!");
    		if(progress != null) {
    			progress.dismiss();
    		}
    		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

    		providers = new ProviderData(context);
    		Provider provider = providers.getProvider(prefs.getString("assetProviderPref",""));
    		
    		if(intent.getAction().equals(OAUTH_TOKEN_ACTION)) {
    			String token = null;
    			token = addAccessToken(context,intent,provider);
    			if(token.equals("")) {
    				Log.d(TAG,"addAccessToken failed!");
    			} else {
    				getWallet(context,token,provider);
    			}
    		} else if (intent.getAction().equals(WALLET_ACTION)) {
    			// XXX add assets from wallet
    			Log.d(TAG,"onReceive WALLET!!!");
    			addAssets(intent,provider);
    		}
    	}
    };
}
