package org.opensourcecurrency.hack;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

public class RestTask extends AsyncTask<HttpUriRequest, Void, String> {

    public static final String HTTP_RESPONSE = "httpResponse";
    public static final String HTTP_ERROR = "httpError";
	
    private Context mContext;
    private HttpClient mClient;
    private String mAction;
    private String mHttpErrorMessage;
	
    public RestTask(Context context, String action) {
        mContext = context;
        mAction = action;
        mClient = new DefaultHttpClient();
    }
	
    public RestTask(Context context, String action, HttpClient client) {
        mContext = context;
        mAction = action;
        mClient = client;
    }
	
    @Override
    protected String doInBackground(HttpUriRequest... params) {
        try{
            HttpUriRequest request = params[0];
            HttpResponse serverResponse = mClient.execute(request);
            BasicResponseHandler handler = new BasicResponseHandler();
            String response = handler.handleResponse(serverResponse);
            return response;
        } catch (Exception e) {
        	mHttpErrorMessage = e.getMessage();
            e.printStackTrace();
            return null;
        }
    }
	
    @Override
    protected void onPostExecute(String result) {

        Intent intent = new Intent(mAction);
        intent.putExtra(HTTP_RESPONSE, result);
        if(null==result) {
        	intent.putExtra(HTTP_ERROR,mHttpErrorMessage);
        }
        //Broadcast the completion
        mContext.sendBroadcast(intent);
    }

}
