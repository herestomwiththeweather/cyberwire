package org.opensourcecurrency.hack;

import static org.opensourcecurrency.hack.ConstantsProviders.PROVIDER_URL;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import java.net.URI;
import java.util.ArrayList;
import android.view.Gravity;
import android.widget.Toast;

import org.apache.http.client.methods.HttpGet;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;
import org.opensourcecurrency.hack.RestTask;

public class Transactions extends ListActivity {    
	TextView selection;
	private static final String OAUTH_LISTPAYMENTS_ACTION = "org.opensourcecurrency.hack.OAUTH_LISTPAYMENTS";
	private static final String TAG = "OpenTransact";
	private ProviderData providers;
	private ProgressDialog progress;
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.transactions);

		providers = new ProviderData(this);
		Provider provider = providers.getCurrentProvider(this);

		
		String access_token = provider.getAccessToken();
        Log.d(TAG,"access_token : " + access_token);
        
        if(access_token.equals("")) {
    		Toast toast = Toast.makeText(this, "No access token yet!", Toast.LENGTH_LONG);
    		toast.setGravity(Gravity.CENTER, 0, 0);
    		toast.show();
        	return;
        }
		
		String asset_path="/transacts/hours";
    	
    	try {
        	HttpGet transactionsRequest = new HttpGet(new URI(provider.providerUrl + asset_path));
      		transactionsRequest.setHeader("Accept","application/json");
      		transactionsRequest.setHeader("Authorization","Bearer " + access_token);
        	RestTask task = new RestTask(this, OAUTH_LISTPAYMENTS_ACTION);
        	task.execute(transactionsRequest);
        	progress = ProgressDialog.show(this, "Fetching Transactions", "Waiting...", true);
    	} catch(Exception e) {
        	e.printStackTrace();    		
    	}
	}
    
	public void onListItemClick(ListView parent, View v, int position, long id) {
	 	//selection.setText(items[position]);
	}
	
    @Override
    public void onResume() {
      super.onResume();
      registerReceiver(receiver, new IntentFilter(OAUTH_LISTPAYMENTS_ACTION));
    }
    
    @Override
    public void onPause() {
    	super.onPause();
    	unregisterReceiver(receiver);
    }
    
    private BroadcastReceiver receiver = new BroadcastReceiver() {
    	private String counterparty(JSONObject transact,String emailAddress) {
    	    try {
          	    String to = transact.getString("to");
        	    String from = transact.getString("from");
                if(emailAddress.equals(to)) {
                	return from;
                } else {
                	return to;
                }
    	    } catch (Exception e) {
            	e.printStackTrace();    		    	    	
    	    }
    	    return "";
    	}
    	
    	private Boolean isPayer(JSONObject transact, String emailAddress) {
    		try {
                if(emailAddress.equals(transact.getString("from"))) {
                	return true;
                } else {
                	return false;
                }
    	    } catch (Exception e) {
            	e.printStackTrace();    		    	    	
    	    }
    	    return false;    		
    	}
    	
    	@Override
    	public void onReceive(Context context, Intent intent) {
    		Log.d(TAG,"onReceive called (Transactions)!");
    		if(progress != null) {
    			progress.dismiss();
    		}
    		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
    	    String emailAddress = prefs.getString("emailPref","");
    		String response = intent.getStringExtra(RestTask.HTTP_RESPONSE);
    		Log.d(TAG,"response(Transactions): "+response);

    		try {
              JSONArray transactions_response = new JSONArray(response);
          	  String[] items = new String[transactions_response.length()];
              for(int i=0;i<transactions_response.length();i++) {
            	  JSONObject transact = transactions_response.getJSONObject(i);
            	  String amount = transact.getString("amount");
            	  String sign = isPayer(transact,emailAddress) ? "-" : "+";
            	  items[i] = sign + amount + " " + counterparty(transact,emailAddress);
              }
              setListAdapter(new ArrayAdapter<String>(context,android.R.layout.simple_list_item_1,items));
    		  selection=(TextView)findViewById(R.id.selection);
    		} catch (JSONException e) {
    		  e.printStackTrace();
    		}
    	}
    };
}
