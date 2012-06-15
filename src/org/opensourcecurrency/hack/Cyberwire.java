package org.opensourcecurrency.hack;

import android.view.View;
import android.view.View.OnClickListener;

import android.app.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import org.opensourcecurrency.hack.model.Asset;
import org.opensourcecurrency.hack.db.DatabaseManager;

public class Cyberwire extends Activity implements OnClickListener {
	private static final String TAG = "OpenTransact";
	private static final String PREFCHANGE_ACTION = "org.opensourcecurrency.hack.PREF_CHANGE";
	
	Button btn;
	Asset m_Asset = null;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	Log.d(TAG,"Cyberwire#onCreate");
		
        super.onCreate(savedInstanceState);
        DatabaseManager.init(this);
        setContentView(R.layout.main);
        
	    m_Asset = Asset.getCurrentAsset(this);
		
        btn = (Button)findViewById(R.id.assetname_button);
        if(null == m_Asset) {
        	btn.setText("No asset");
        } else {
            btn.setText(m_Asset.balance + " " + m_Asset.name);
        }
        
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
    public void onResume() {
      super.onResume();
      registerReceiver(receiver, new IntentFilter(PREFCHANGE_ACTION));
    }
    
    @Override
    public void onPause() {
    	super.onPause();
    }
    
    private BroadcastReceiver receiver = new BroadcastReceiver() {
    	public void onReceive(Context context, Intent intent) {
    	    m_Asset = Asset.getCurrentAsset(context);
    		
            btn = (Button)findViewById(R.id.assetname_button);
            if(null == m_Asset) {
            	btn.setText("No asset");
            } else {
                btn.setText(m_Asset.balance + " " + m_Asset.name);
            }
    	}
    };
    
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
    	case R.id.reset:
        	DatabaseManager.clear();
    		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
    		SharedPreferences.Editor editor = prefs.edit();
    		editor.remove("assetProviderPref");
    		editor.commit();
            btn.setText("No asset");
    		Toast toast = Toast.makeText(this, "App database cleared!", Toast.LENGTH_LONG);
    		toast.setGravity(Gravity.CENTER, 0, 0);
    		toast.show();
    		return true;
    	case R.id.settings:
    		startActivity(new Intent(this, Prefs.class));
    		return true;
    	}
    	return super.onOptionsItemSelected(item);
    }
}
