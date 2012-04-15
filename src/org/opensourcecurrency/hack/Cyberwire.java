package org.opensourcecurrency.hack;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import android.view.View;
import android.view.View.OnClickListener;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import org.opensourcecurrency.hack.RestTask;
import org.opensourcecurrency.hack.WebViewActivity;

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

import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

public class Cyberwire extends Activity implements OnClickListener {
	private static final String OAUTH_TOKEN_ACTION = "org.opensourcecurrency.hack.OAUTH_TOKEN";
	private static final String TAG = "OpenTransact";
	private ProgressDialog progress;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

		String access_token = prefs.getString("access_token","none");
		String expires_in = prefs.getString("expires_in","none");

    	Log.d(TAG,"onCreate: " + expires_in + " : " + access_token);
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        View sendButton = findViewById(R.id.send_button);
        sendButton.setOnClickListener(this);
        View transactionsButton = findViewById(R.id.transactions_button);
        transactionsButton.setOnClickListener(this);
        View profileButton = findViewById(R.id.profile_button);
        profileButton.setOnClickListener(this);
        
		Toast toast = Toast.makeText(this, "expires_in: " + expires_in + " : " + access_token, Toast.LENGTH_LONG);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
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
    	String providerValues[];
    	Log.d(TAG,"onOptionsItemSelected");
    	switch (item.getItemId()) {
    	case R.id.exit:
    		return true;
    	case R.id.login:
    		Intent intent = new Intent(this,WebViewActivity.class);
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
    		String provider = prefs.getString("assetProviderPref","");
        	providerValues = provider.split(" ");
        	Log.d(TAG,"onOptionsItemSelected[login] host: " + providerValues[0]);
        	Log.d(TAG,"onOptionsItemSelected[login] clientid: " + providerValues[1]);
        	Log.d(TAG,"onOptionsItemSelected[login] redirect_uri: " + providerValues[3]);
    		intent.setData(Uri.parse(providerValues[0]+"/oauth/authorize?client_id="+providerValues[1]+"&response_type=code&redirect_uri="+providerValues[3]));
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
    	String providerValues[];
    	Log.d(TAG,"onActivityResult");

    	switch (requestCode) {
    	case 0:
    		if (resultCode != RESULT_OK || data == null) {
    			return;
    		}
    		String token = data.getStringExtra("token");
    		Log.d(TAG,token);
    		Toast toast = Toast.makeText(this, token, Toast.LENGTH_LONG);
    		toast.setGravity(Gravity.CENTER, 0, 0);
    		toast.show();
    		
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
    		String provider = prefs.getString("assetProviderPref","");
        	providerValues = provider.split(" ");
        	Log.d(TAG,"onActivityResult host: " + providerValues[0]);
        	Log.d(TAG,"onActivityResult clientid: " + providerValues[1]);
        	Log.d(TAG,"onActivityResult client secret: " + providerValues[2]);
        	Log.d(TAG,"onActivityResult redirect uri: " + providerValues[3]);

        	
       		try {
      		  String url = providerValues[0] + "/oauth/token";
      		  HttpPost tokenRequest = new HttpPost(new URI(url));
      		  List<NameValuePair> parameters = new ArrayList<NameValuePair>();
      		  parameters.add(new BasicNameValuePair("client_id", providerValues[1]));
      		  parameters.add(new BasicNameValuePair("client_secret", providerValues[2]));
      		  parameters.add(new BasicNameValuePair("code", token));
      		  parameters.add(new BasicNameValuePair("grant_type","authorization_code"));
      		  parameters.add(new BasicNameValuePair("redirect_uri",providerValues[3]));
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
    }
    
    @Override
    public void onPause() {
    	super.onPause();
    	unregisterReceiver(receiver);
    }
    
    private BroadcastReceiver receiver = new BroadcastReceiver() {
    	@Override
    	public void onReceive(Context context, Intent intent) {
    		Log.d(TAG,"onReceive called!");
    		if(progress != null) {
    			progress.dismiss();
    		}
    		String response = intent.getStringExtra(RestTask.HTTP_RESPONSE);
    		Log.d(TAG,"response: "+response);
    		
    		try {
              SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

              JSONObject access_token_response = new JSONObject(response);
        	  String access_token = access_token_response.getString("access_token");
        	  String refresh_token = access_token_response.getString("refresh_token");
        	  String expires_in = access_token_response.getString("expires_in");

        	  Log.d(TAG," access token: " + access_token);
        	  Log.d(TAG,"refresh token: " + refresh_token);
        	  Log.d(TAG,"   expires_in: " + expires_in);


              SharedPreferences.Editor editor = prefs.edit();
              editor.putString("access_token", access_token);
              editor.putString("refresh_token", refresh_token);
              editor.putString("expires_in", expires_in);

              editor.commit();

    		} catch (JSONException e) {
    		  e.printStackTrace();
    		}

    	}
    };
}