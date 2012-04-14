package org.opensourcecurrency.hack;

import org.opensourcecurrency.hack.WebViewActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

public class Cyberwire extends Activity {
	private static final String TAG = "OpenTransact";
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
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
    	case R.id.exit:
    		return true;
    	case R.id.login:
    		Intent intent = new Intent(this,WebViewActivity.class);
    		//intent.setData(Uri.parse("http://demo.opensourcecurrency.org/oauth/authorize?client_id=xBoHeeNNFt3LQ7U1tvAb8BVKr32duE6rdWtpCSFD&response_type=code"));
    		intent.setData(Uri.parse("http://192.168.1.26:3000/oauth/authorize?client_id=XH/Jov43AaMoE08mUtRvoQ==&response_type=code"));
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
    			return;
    		}
    		String token = data.getStringExtra("token");
    		Log.d(TAG,token);
    		Toast toast = Toast.makeText(this, token, Toast.LENGTH_LONG);
    		toast.setGravity(Gravity.CENTER, 0, 0);
    		toast.show();

    		return;
    	}
    	super.onActivityResult(requestCode, resultCode, data);
    }
}