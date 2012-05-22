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
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.StringReader;
import org.w3c.dom.*;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class AddProvider extends Activity implements OnClickListener {
	private EditText providerUrlText;
	private Button addProviderButton;
	private static final String DYNREG_HOSTMETA_ACTION = "org.opensourcecurrency.hack.DYNREG_HOSTMETA";
	private static final String DYNREG_REGISTER_ACTION = "org.opensourcecurrency.hack.DYNREG_REGISTER";
	private static final String TAG = "OpenTransact";
	private ProgressDialog progress;
	String m_AssetProvider = null;

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
    }
    
    @Override
    public void onPause() {
    	super.onPause();
    	unregisterReceiver(receiver);
    }
    
    private BroadcastReceiver receiver = new BroadcastReceiver() {
    	static final String LINK = "Link";
    	static final String HREF = "href";
    	static final String CLIENT_NAME = "cyberwire";
    	static final String CLIENT_URL = "http://herestomwiththeweather.com/cyberwire";
    	static final String CLIENT_DESCRIPTION = "Android app with OpenTransact support";
    	static final String TYPE = "push";
    	static final String APPLICATION_TYPE = "noredirect";
    	private ProviderData providers;
    	String m_provider = null;
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
    	
    	private void createNewProvider(Context context, Intent intent) {
    		String response = intent.getStringExtra(RestTask.HTTP_RESPONSE);
    		Log.d(TAG,"createNewProvider() asset provider: "+m_AssetProvider);
    		Log.d(TAG,"createNewProvider() response(register): "+response);
    		providers=new ProviderData(context);
    		JSONObject clientRegistration = null;
    		try {
        		clientRegistration = new JSONObject(response);
        		providers.addProvider(m_AssetProvider, // XXX would be nice to have a friendly name
        				              m_AssetProvider, 
			                          clientRegistration.getString("redirect_url"), 
			                          clientRegistration.getString("client_id"), 
			                          clientRegistration.getString("client_secret"));
    		}catch (JSONException e) {
      		    e.printStackTrace();
      		}
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
    			registerOAuthClient(m_dynreg_endpoint, context);
    		} else if(intent.getAction().equals(DYNREG_REGISTER_ACTION)) {
        		createNewProvider(context,intent);
    		} else {
        		Log.d(TAG,"XXX unrecognized action for AddProvider");
    		}

    	}
    };

}
