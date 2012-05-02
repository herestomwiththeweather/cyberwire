package org.opensourcecurrency.hack;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;

import org.json.JSONException;
import org.json.JSONObject;
import org.opensourcecurrency.hack.RestTask;

import android.util.Log;


public class Send extends Activity implements OnClickListener {
	private static final String OAUTH_PAYMENT_ACTION = "org.opensourcecurrency.hack.OAUTH_PAYMENT";

	private static final String TAG = "OpenTransact";
	private ProviderData providers;
	private ProgressDialog progress;

	private EditText toText;
	private EditText amountText;
	private EditText noteText;

	private Button payButton;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.send);      	   
        toText = (EditText) findViewById(R.id.to_field);
        amountText = (EditText) findViewById(R.id.amount_field);
        noteText = (EditText) findViewById(R.id.note_field);

        payButton = (Button) findViewById(R.id.pay_button);
        payButton.setOnClickListener(this);
    }
    
    @Override
    public void onClick(View view) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
   		//String provider = prefs.getString("assetProviderPref","");
		String access_token = prefs.getString("access_token","none");
        Log.d(TAG,"access_token : " + access_token);
       	//String providerValues[];
       	//providerValues = provider.split(" ");
    	//final String asset_url = providerValues[4];
    	
		providers = new ProviderData(this);
		Provider provider = providers.getProvider(prefs.getString("assetProviderPref",""));
		String asset_path="/transacts/hours";
    
    	
    	switch (view.getId()) {
    	case R.id.pay_button:
            //Log.d(TAG,"onClick:     to=" + toText.getText().toString());
            //Log.d(TAG,"onClick: amount=" + amountText.getText().toString());
            //Log.d(TAG,"onClick:   note=" + noteText.getText().toString());        	
          	try {
          		HttpPost paymentRequest = new HttpPost(new URI(provider.providerUrl+asset_path));
          		paymentRequest.setHeader("Accept","application/json");
          		paymentRequest.setHeader("Authorization","Bearer " + access_token);
        		List<NameValuePair> parameters = new ArrayList<NameValuePair>();
        		parameters.add(new BasicNameValuePair("to", toText.getText().toString()));
        		parameters.add(new BasicNameValuePair("amount", amountText.getText().toString()));
        		parameters.add(new BasicNameValuePair("note", noteText.getText().toString()));
        		paymentRequest.setEntity(new UrlEncodedFormEntity(parameters));
        		
        		RestTask task = new RestTask(this, OAUTH_PAYMENT_ACTION);
        		task.execute(paymentRequest);
            	progress = ProgressDialog.show(this, "Making payment", "Waiting...", true);

          	} catch (Exception e) {
      			e.printStackTrace();
          	}
    		break;
    	}
    }
    
    @Override
    public void onResume() {
      super.onResume();
      registerReceiver(receiver, new IntentFilter(OAUTH_PAYMENT_ACTION));
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

    		try {
              JSONObject payment_response = new JSONObject(response);
    		} catch (JSONException e) {
    		  e.printStackTrace();
    		}
    		
			finish();

    	}
    };
}
