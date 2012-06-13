package org.opensourcecurrency.hack;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.xml.sax.InputSource;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.StringReader;
import org.w3c.dom.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import org.opensourcecurrency.hack.model.Provider;

import android.util.Log;

public class AddProvider extends Activity implements OnClickListener {
	private EditText providerUrlText;
	private Button addProviderButton;
	private static final String DYNREG_HOSTMETA_ACTION = "org.opensourcecurrency.hack.DYNREG_HOSTMETA";
	private static final String DYNREG_REGISTER_ACTION = "org.opensourcecurrency.hack.DYNREG_REGISTER";
	private static final String OAUTH_TOKEN_ACTION = "org.opensourcecurrency.hack.OAUTH_TOKEN";
	private static final String WALLET_ACTION = "org.opensourcecurrency.hack.WALLET";

	private static final String TAG = "OpenTransact";
	private ProgressDialog progress;
	String m_AssetProvider = null;
	Provider m_Provider = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.addprovider);      	   
        providerUrlText = (EditText) findViewById(R.id.add_provider_field);

        addProviderButton = (Button) findViewById(R.id.add_provider_button);
        addProviderButton.setOnClickListener(this);
    }
    
    @Override
    public void onClick(View view) {
    	switch (view.getId()) {
    	case R.id.add_provider_button:
    		m_AssetProvider = providerUrlText.getText().toString();
            Log.d(TAG,"onClick:     providerUrl=" + m_AssetProvider);
    		String hostmeta="/.well-known/host-meta";

        	try {
            	HttpGet hostmetaRequest = new HttpGet(new URI(m_AssetProvider + hostmeta));
            	RestTask task = new RestTask(this, DYNREG_HOSTMETA_ACTION);
            	task.execute(hostmetaRequest);
            	progress = ProgressDialog.show(this, "Fetching Host-Meta", "Waiting...", true);
        	} catch(Exception e) {
            	e.printStackTrace();    		
        	}
    		break;
    	}
    }
    
    @Override
    public void onResume() {
      super.onResume();
      registerReceiver(receiver, new IntentFilter(DYNREG_HOSTMETA_ACTION));
      registerReceiver(receiver, new IntentFilter(DYNREG_REGISTER_ACTION));
      registerReceiver(receiver, new IntentFilter(OAUTH_TOKEN_ACTION));
      registerReceiver(receiver, new IntentFilter(WALLET_ACTION));
    }
    
    @Override
    public void onPause() {
    	super.onPause();
    	unregisterReceiver(receiver);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	Log.d(TAG,"AddProvider#onActivityResult");

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
 
	    	String provider_url = m_Provider.url;
			String redirect_url = m_Provider.redirect_url;
			String client_id = m_Provider.client_id;
			String client_secret = m_Provider.client_secret;
			
        	Log.d(TAG,"onActivityResult provider_url: " + provider_url);
        	Log.d(TAG,"onActivityResult clientid: " + client_id);
        	Log.d(TAG,"onActivityResult client secret: " + client_secret);
        	Log.d(TAG,"onActivityResult redirect_uri: " + redirect_url);
        	
       		try {
       		  String url = m_Provider.token_endpoint;
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
    
    private BroadcastReceiver receiver = new BroadcastReceiver() {
    	static final String LINK = "Link";
    	static final String HREF = "href";
    	static final String CLIENT_NAME = "cyberwire";
    	static final String CLIENT_URL = "http://herestomwiththeweather.com/cyberwire";
    	static final String CLIENT_DESCRIPTION = "Android app with OpenTransact support";
    	static final String TYPE = "push";
    	static final String APPLICATION_TYPE = "noredirect";
		String m_dynreg_endpoint = null;
    	
    	private String getClientRegistrationEndpoint(Intent intent) {
    		String response = intent.getStringExtra(RestTask.HTTP_RESPONSE);
    		Log.d(TAG,"getClientRegistrationEndpoint() response(host-meta): "+response);
    		
    		if(null == response) {
    			return "";
    		}
    		
    		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    		DocumentBuilder db = null;
    		try {
        		db = factory.newDocumentBuilder();
    		} catch (Exception e) {
      			e.printStackTrace();
          	}
    		InputSource is = new InputSource(new StringReader(response));
    		Document doc = null;
    		try {
        		doc = db.parse(is);
    		} catch (Exception e) {
      			e.printStackTrace();
          	}
    		String dynreg_endpoint = null;

    		NodeList nodes = doc.getElementsByTagName(LINK);
    		for(int i=0;i<nodes.getLength();i++) {
    			Element element = (Element) nodes.item(i);
    			dynreg_endpoint = element.getAttribute(HREF);
    			Log.d(TAG,"getClientRegistrationEndpoint() Link: " + dynreg_endpoint);
    		}
    		
    		return dynreg_endpoint;
    	}
    	
    	private void registerOAuthClient(String dynreg_endpoint, Context context) {
          	try {
          		HttpPost registrationRequest = new HttpPost(new URI(dynreg_endpoint));
          		registrationRequest.setHeader("Accept","application/json");
        		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
        		parameters.add(new BasicNameValuePair("client_name", CLIENT_NAME));
        		parameters.add(new BasicNameValuePair("client_url", CLIENT_URL));
        		parameters.add(new BasicNameValuePair("client_description", CLIENT_DESCRIPTION));
        		parameters.add(new BasicNameValuePair("type", TYPE));
        		parameters.add(new BasicNameValuePair("application_type", APPLICATION_TYPE));
        		registrationRequest.setEntity(new UrlEncodedFormEntity(parameters));
        		
        		RestTask task = new RestTask(context, DYNREG_REGISTER_ACTION);
        		task.execute(registrationRequest);
            	progress = ProgressDialog.show(context, "Registering client", "Waiting...", true);

          	} catch (Exception e) {
      			e.printStackTrace();
          	}
    	}
    	
    	private Provider createNewProvider(Context context, Intent intent) {
    		String response = intent.getStringExtra(RestTask.HTTP_RESPONSE);
    		Log.d(TAG,"createNewProvider() asset provider: "+m_AssetProvider);
    		Log.d(TAG,"createNewProvider() response(register): "+response);
    		Provider provider = null;
    		
    		JSONObject clientRegistration = null;
    		try {
        		clientRegistration = new JSONObject(response);
        		provider = Provider.create();
        		provider.name = m_AssetProvider;
        		provider.url = m_AssetProvider;
        		provider.redirect_url = clientRegistration.getString("redirect_url");
        		provider.client_id = clientRegistration.getString("client_id");
        		provider.client_secret = clientRegistration.getString("client_secret");
        		provider.authorization_endpoint = m_AssetProvider + "/oauth/authorize";
        		provider.token_endpoint = m_AssetProvider + "/oauth/token";
        		provider.save();
    		} catch (JSONException e) {
      		    e.printStackTrace();
    			/*provider = providers.getProvider(m_AssetProvider);
    			if(null == provider) {
    				Log.d(TAG,"createNewProvider() provider not found.");
    			}*/
      		}
    		
    		return provider;
    	}
    	

        
        private void getWallet(Context context, String access_token, Provider provider) {
        	String wallet_path = "/wallet";
        	
           	try {
            	HttpGet walletRequest = new HttpGet(new URI(provider.url + wallet_path));
          		walletRequest.setHeader("Accept","application/json");
          		walletRequest.setHeader("Authorization","Bearer " + access_token);
            	RestTask task = new RestTask(context, WALLET_ACTION);
            	task.execute(walletRequest);
            	progress = ProgressDialog.show(context, "Fetching wallet", "Waiting...", true);
        	} catch(Exception e) {
            	e.printStackTrace();    		
        	}
        }
        
        boolean addAssets(Context context, Intent intent, Provider provider) {
    		String response = intent.getStringExtra(RestTask.HTTP_RESPONSE);
    		Log.d(TAG,"response: "+response);
    		
    		if(null == response) {
	    		Toast toast = Toast.makeText(context, "Error retrieving wallet", Toast.LENGTH_LONG);
	    		toast.setGravity(Gravity.CENTER, 0, 0);
	    		toast.show();
    			return false;
    		}
    		
    		try {
    	          JSONObject wallet_response = new JSONObject(response);
    	      	  Integer total = wallet_response.getInt("total");
    	      	  Log.d(TAG,"wallet total: " + total);
    	      	  JSONArray assets = wallet_response.getJSONArray("assets");
    	      	  for(int i=0;i<assets.length();i++) {
    	      		  JSONObject asset = assets.getJSONObject(i);
    	      		  provider.addAsset(asset.getString("name"), asset.getString("url"), asset.getString("balance"));
    	      	  }
      		} catch (JSONException e) {
      		  e.printStackTrace();
      		  return false;
      		}
    		
    		return true;
        }
        
        private void startWebView(Context context) {
    		Log.d(TAG,"startWebView client id: " + m_Provider.client_id);
    		Intent i = new Intent(context,WebViewActivity.class);
    		i.setData(Uri.parse(m_Provider.authorization_endpoint + "?client_id="+Uri.encode(m_Provider.client_id)+"&response_type=code&redirect_uri="+m_Provider.redirect_url));
    		startActivityForResult(i,0);
        }
        
    	@Override
    	public void onReceive(Context context, Intent intent) {
    		Log.d(TAG,"onReceive called!");
    		if(progress != null) {
    			progress.dismiss();
    		}
    		
    		Log.d(TAG,"intent action(host-meta): "+intent.getAction());
    		
    		if(intent.getAction().equals(DYNREG_HOSTMETA_ACTION)) {
    			m_dynreg_endpoint = getClientRegistrationEndpoint(intent);
    			if(m_dynreg_endpoint.equals("")) {
    	    		Toast toast = Toast.makeText(context, "Error connecting with provider", Toast.LENGTH_LONG);
    	    		toast.setGravity(Gravity.CENTER, 0, 0);
    	    		toast.show();
    	    		return;
    			}

    			m_Provider = Provider.find_by_url(m_AssetProvider);
    			if(null == m_Provider) {
    				Log.d(TAG,"Provider not found. Initiating dynamic client registration...");
        			registerOAuthClient(m_dynreg_endpoint, context);
    			} else {
    				Log.d(TAG,"Provider already exists. Skipping dynamic client registration.");
    				startWebView(context);
    			}
    		} else if(intent.getAction().equals(DYNREG_REGISTER_ACTION)) {
        		m_Provider = createNewProvider(context,intent);
            	Log.d(TAG,"onReceive[login] provider_name: " + m_Provider.name);
            	Log.d(TAG,"onReceive[login] provider_url: " + m_Provider.url);
            	Log.d(TAG,"onReceive[login] clientid: " + m_Provider.client_id);
            	Log.d(TAG,"onReceive[login] redirect_uri: " + m_Provider.redirect_url);
            	startWebView(context);
    		} else if(intent.getAction().equals(OAUTH_TOKEN_ACTION)) {
    			String token = null;
    			token = m_Provider.addAccessToken(context,intent);
    			if(token.equals("")) {
    				Log.d(TAG,"addAccessToken failed!");
    			} else {
    				getWallet(context,token,m_Provider);
    			}
    		} else if(intent.getAction().equals(WALLET_ACTION)) {
    			// XXX add assets from wallet
    			Log.d(TAG,"onReceive WALLET!!!");
    			addAssets(context,intent,m_Provider);
    			finish();
        		startActivity(new Intent(context, Prefs.class));
    		}
    		else {
        		Log.d(TAG,"XXX unrecognized action for AddProvider");
    		}

    	}
    };

}
