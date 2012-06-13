package org.opensourcecurrency.hack;

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

import org.json.JSONException;
import org.json.JSONObject;
import org.opensourcecurrency.hack.RestTask;

import android.util.Log;

import org.opensourcecurrency.hack.model.Asset;
import org.opensourcecurrency.hack.model.Provider;

public class Send extends Activity implements OnClickListener {
	private static final String OAUTH_PAYMENT_ACTION = "org.opensourcecurrency.hack.OAUTH_PAYMENT";
	private static final String OAUTH_PAYMENT_REFRESH_ACTION = "org.opensourcecurrency.hack.OAUTH_PAYMENT_REFRESH";


	private static final String TAG = "OpenTransact";
	private ProgressDialog progress;

	private EditText toText;
	private EditText amountText;
	private EditText noteText;

	private Button payButton;
	
	Asset m_Asset = null;
	Provider m_Provider = null;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send);  
        
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
		    	   
        toText = (EditText) findViewById(R.id.to_field);
        amountText = (EditText) findViewById(R.id.amount_field);
        noteText = (EditText) findViewById(R.id.note_field);

        payButton = (Button) findViewById(R.id.pay_button);
        payButton.setOnClickListener(this);
    }
    
    @Override
    public void onClick(View view) {    	
		m_Provider = m_Asset.provider;
		String access_token = m_Provider.getAccessToken();
        Log.d(TAG,"access_token : " + access_token);
        
        if(access_token.equals("")) {
    		Toast toast = Toast.makeText(this, "No access token yet!", Toast.LENGTH_LONG);
    		toast.setGravity(Gravity.CENTER, 0, 0);
    		toast.show();
        	return;
        }
    	
    	switch (view.getId()) {
    	case R.id.pay_button:      
    		m_Asset.postTransaction(this, 
    				                toText.getText().toString(), 
    				                amountText.getText().toString(),
    				                noteText.getText().toString(),
    				                OAUTH_PAYMENT_ACTION);
        	progress = ProgressDialog.show(this, "Making payment", "Waiting...", true);
    		break;
    	}
    }
    
    @Override
    public void onResume() {
      super.onResume();
      registerReceiver(receiver, new IntentFilter(OAUTH_PAYMENT_ACTION));
      registerReceiver(receiver, new IntentFilter(OAUTH_PAYMENT_REFRESH_ACTION));
    }
    
    @Override
    public void onPause() {
    	super.onPause();
    	unregisterReceiver(receiver);
    }
    
    private BroadcastReceiver receiver = new BroadcastReceiver() {
    	@Override
    	public void onReceive(Context context, Intent intent) {
    		Log.d(TAG,"onReceive called (Send)!");
    		if(progress != null) {
    			progress.dismiss();
    		}
    		String response = intent.getStringExtra(RestTask.HTTP_RESPONSE);
    		Log.d(TAG,"response(Send): "+response);
    		
    		if(null == response) {
				boolean fRefresh = m_Provider.handleNetworkError(context, intent, OAUTH_PAYMENT_REFRESH_ACTION);
				if(fRefresh) {
		          	progress = ProgressDialog.show(context, "Refreshing token", "Waiting...", true);
				}
				return;
    		}

    		if(intent.getAction().equals(OAUTH_PAYMENT_ACTION)) {
        		try {
                    JSONObject payment_response = new JSONObject(response);
          		} catch (JSONException e) {
          		  e.printStackTrace();
          		}
          		
      			finish();
    		} else if(intent.getAction().equals(OAUTH_PAYMENT_REFRESH_ACTION)) {
				String token = m_Provider.addAccessToken(context,intent);
        		m_Asset.postTransaction(context, 
		                toText.getText().toString(), 
		                amountText.getText().toString(),
		                noteText.getText().toString(),
		                OAUTH_PAYMENT_ACTION);
        				progress = ProgressDialog.show(context, "Retrying payment", "Waiting...", true);
    		}
    	}
    };
}
