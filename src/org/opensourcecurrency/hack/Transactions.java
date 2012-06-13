package org.opensourcecurrency.hack;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.view.Gravity;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;
import org.opensourcecurrency.hack.RestTask;

import org.opensourcecurrency.hack.model.Asset;
import org.opensourcecurrency.hack.model.Provider;

public class Transactions extends ListActivity {    
	TextView selection;
	private static final String OAUTH_LISTPAYMENTS_ACTION = "org.opensourcecurrency.hack.OAUTH_LISTPAYMENTS";
	private static final String OAUTH_LISTPAYMENTS_USER_ACTION = "org.opensourcecurrency.hack.OAUTH_LISTPAYMENTS_USER";
	private static final String OAUTH_LISTPAYMENTS_REFRESH_ACTION = "org.opensourcecurrency.hack.OAUTH_LISTPAYMENTS_REFRESH";
	private static final String TAG = "OpenTransact";
	private ProgressDialog progress;
	Asset m_Asset = null;
	Provider m_Provider = null;
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.transactions);

		m_Asset = Asset.getCurrentAsset(this);
		if(null == m_Asset) {
    		Toast toast = Toast.makeText(this, "Please enter your asset provider.", Toast.LENGTH_LONG);
    		toast.setGravity(Gravity.TOP, 0, 60);
    		toast.show();
			finish();
    		Intent i = new Intent(this, AddProvider.class);
    		startActivity(i);
			return;
		}
		
		m_Provider = m_Asset.provider;
		
		if(null == m_Provider.getUser()) {
			m_Provider.getUserInfo(this, OAUTH_LISTPAYMENTS_USER_ACTION);
        	progress = ProgressDialog.show(this, "Fetching profile", "Waiting...", true);
		} else {
			m_Asset.getTransactions(this, OAUTH_LISTPAYMENTS_ACTION);
    		Log.d(TAG,"XXX Transactions#onCreate getTransactions request sent! ");
        	progress = ProgressDialog.show(this, "Fetching Transactions", "Waiting...", true);
		}
	}
    
	public void onListItemClick(ListView parent, View v, int position, long id) {
	 	//selection.setText(items[position]);
	}
	
    @Override
    public void onResume() {
      super.onResume();
      registerReceiver(receiver, new IntentFilter(OAUTH_LISTPAYMENTS_ACTION));
      registerReceiver(receiver, new IntentFilter(OAUTH_LISTPAYMENTS_USER_ACTION));
      registerReceiver(receiver, new IntentFilter(OAUTH_LISTPAYMENTS_REFRESH_ACTION));
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
    		
    		String response = intent.getStringExtra(RestTask.HTTP_RESPONSE);
    		Log.d(TAG,"response(Transactions): "+response);
    		
    		if(null == response) {
    				boolean fRefresh = m_Provider.handleNetworkError(context, intent, OAUTH_LISTPAYMENTS_REFRESH_ACTION);
    				if(fRefresh) {
    		          	progress = ProgressDialog.show(context, "Refreshing token", "Waiting...", true);
    				}
    				return;
    		}
    		
    		if(intent.getAction().equals(OAUTH_LISTPAYMENTS_ACTION)) {
        	    String emailAddress = m_Provider.getUser().email;

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
    		} else if(intent.getAction().equals(OAUTH_LISTPAYMENTS_USER_ACTION)) {
        		try {
                  JSONObject profile_response = new JSONObject(response);
                  String email = profile_response.getString("email");
                  Log.d(TAG,"adding email: " + email);
          		  m_Provider.addUser(profile_response);
          		} catch (JSONException e) {
          		  e.printStackTrace();
          		}
        		
    			m_Asset.getTransactions(context, OAUTH_LISTPAYMENTS_ACTION);
            	progress = ProgressDialog.show(context, "Fetching Transactions", "Waiting...", true);
    		} else if(intent.getAction().equals(OAUTH_LISTPAYMENTS_REFRESH_ACTION)) {
				String token = m_Provider.addAccessToken(context,intent);
				if(null == m_Provider.getUser()) {
					m_Provider.getUserInfo(context, OAUTH_LISTPAYMENTS_USER_ACTION);
		        	progress = ProgressDialog.show(context, "Fetching profile", "Waiting...", true);
				} else {
					m_Asset.getTransactions(context, OAUTH_LISTPAYMENTS_ACTION);
		    		Log.d(TAG,"XXX Transactions#onCreate getTransactions request sent! ");
		        	progress = ProgressDialog.show(context, "Fetching Transactions", "Waiting...", true);
				}
    		}
    	}
    };
}
