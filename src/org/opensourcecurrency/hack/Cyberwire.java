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
	private static final String TAG = "OpenTransact";

	private ProgressDialog progress;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {

    	//this.deleteDatabase("providers.db");
    	
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
    	case R.id.settings:
    		startActivity(new Intent(this, Prefs.class));
    		return true;
    	}
    	return super.onOptionsItemSelected(item);
    }
}
