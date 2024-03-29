package com.bcl.music23andme;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Hashtable;
import java.util.Set;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.bcl.music23andme.R;
import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.widget.FacebookDialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
 
public class WebViewActivity extends Activity {
	private WebView gWebView;
	ProgressDialog pageDialog;
	ProgressDialog dataDialog;
	//have to use google.com because Android becomes wierd when the redirect_uri is't a working one
	final String REDIRECT_URI = "http://www.blankwebsite.com";
	final String CLIENT_ID = "021772bf49aa3d0b7b5623fa926eab02";
	final String CLIENT_SECRET = "7278d594495d28c17cc183ef3279be24";
	final String SCOPE = "basic names genomes analyses";
	final String TAG= "WEBVIEW";
	Hashtable<String, String> individual_risk=new Hashtable<String, String>();
	Hashtable<String, String> population_risk=new Hashtable<String, String>();
	private UiLifecycleHelper uiHelper;
	private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };
	public void onCreate(Bundle savedInstanceState){
		overridePendingTransition(R.anim.fadein, R.anim.fadeout);
		setContentView(R.layout.webview);
		
		uiHelper = new UiLifecycleHelper(this, callback);
	    uiHelper.onCreate(savedInstanceState);
		
		gWebView = (WebView) findViewById(R.id.webView1);

		pageDialog=ProgressDialog.show(WebViewActivity.this, "", "connecting to 23andme...");
		gWebView.loadUrl("https://api.23andme.com/authorize/?redirect_uri="
				+ REDIRECT_URI + "&response_type=code&client_id=" + CLIENT_ID
				+ "&scope=" + SCOPE);
		
		Log.d("WEBVIEW", "got to webpage");
		gWebView.setWebViewClient(new WebViewClient() {

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				if(url.startsWith("https://api.23andme")){
					pageDialog.dismiss();
				}
				if (url.startsWith(REDIRECT_URI)) {
					System.out.println("got to override");
					dataDialog=ProgressDialog.show(WebViewActivity.this, "retrieving data", "please wait...");
					if (url.indexOf("code=") != -1) {
						//if the query contains code
						String queryString = null;
						try {
							queryString = new URL(url).getQuery();
						} catch (MalformedURLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						System.out.println(queryString);
						String[] params = queryString.split("&");
						String code = null;
						for (String param : params) {
							if (param.startsWith("code=")) {
								code = param.substring(param.indexOf('=') + 1);
							}
						}
						gWebView.setVisibility(View.GONE);
						new PostRequest().execute(code);
						// don't go to redirectUri
					}
				}
			}
		});
		super.onCreate(savedInstanceState);
		

	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		 super.onActivityResult(requestCode, resultCode, data);

		    uiHelper.onActivityResult(requestCode, resultCode, data, new FacebookDialog.Callback() {
		        @Override
		        public void onError(FacebookDialog.PendingCall pendingCall, Exception error, Bundle data) {
		            Log.e("Activity", String.format("Error: %s", error.toString()));
		        }

		        @Override
		        public void onComplete(FacebookDialog.PendingCall pendingCall, Bundle data) {
		            Log.i("Activity", "Success!");
		        }
		    });
		}
	@Override
	protected void onPause() {
		super.onPause();
		overridePendingTransition(R.anim.fadein, R.anim.fadeout);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.actionbar, menu);
		return true;
	}
	
	private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        if ((exception instanceof FacebookOperationCanceledException) ||
                (exception instanceof FacebookAuthorizationException)) {
                new AlertDialog.Builder(WebViewActivity.this)
                    .setTitle(R.string.cancelled)
                    .setMessage(R.string.permission_not_granted)
                    .setPositiveButton(R.string.ok, null)
                    .show();
        }
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_connect:
			Toast.makeText(this, "Connecting to 23andme", Toast.LENGTH_SHORT)
			.show();
			Intent connectIt= new Intent(WebViewActivity.this,WebViewActivity.class);
			startActivity(connectIt);
			break;
		case R.id.action_home:
			Intent hintent = new Intent(this, MainActivity.class);
			  hintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			  startActivity(hintent);
			  break;
		case R.id.action_music:
			Intent mintent = new Intent(this, MusicActivity.class);
			  startActivity(mintent);
			  break;
		case R.id.action_share:
			if (FacebookDialog.canPresentShareDialog(getApplicationContext(), 
	                FacebookDialog.ShareDialogFeature.SHARE_DIALOG)) {
	    		FacebookDialog shareDialog = new FacebookDialog.ShareDialogBuilder(WebViewActivity.this)
	    		.setLink("https://developers.facebook.com/android")
	    		.build();
	    		uiHelper.trackPendingDialogCall(shareDialog.present());
	    	}
			break;
		case android.R.id.home:
			  Intent intent = new Intent(this, MainActivity.class);
			  intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			  startActivity(intent);
			  break; 
			
		default:
			break;
		}
		
		return true;
	}

	class PostRequest extends AsyncTask<String,Void,String>{
		

		@Override
		protected String doInBackground(String... params) {
			String code=params[0];
			if (code != null) {
				System.out.println(code);				

				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(
						"https://api.23andme.com/token/");
				

				try {
					// data pairs
					List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
							2);
					nameValuePairs.add(new BasicNameValuePair(
							"client_id", CLIENT_ID));
					nameValuePairs.add(new BasicNameValuePair(
							"client_secret", CLIENT_SECRET));
					nameValuePairs.add(new BasicNameValuePair(
							"grant_type", "authorization_code"));
					nameValuePairs.add(new BasicNameValuePair(
							"redirect_uri", REDIRECT_URI));
					nameValuePairs.add(new BasicNameValuePair(
							"code", code));
					nameValuePairs.add(new BasicNameValuePair(
							"scope", SCOPE));

					httppost.setEntity(new UrlEncodedFormEntity(
							nameValuePairs));

					// Execute HTTP Post Request
					if(httppost != null){
						
						HttpResponse response = httpclient
								.execute(httppost);
						
						String jsonString = EntityUtils
								.toString(response.getEntity());
						JSONObject jObject = new JSONObject(jsonString);
						String bearer_token = jObject
								.getString("access_token");
						List<String> bearer_tokens= new ArrayList<String>();
						if(bearer_token!=null){
							bearer_tokens.add(bearer_token);
						}
						else{
						}
						bearer_token=bearer_tokens.get(0);
					    MusicApp.bearer_token=bearer_token;
						HttpGet httpget = new HttpGet(
								"https://api.23andme.com/1/names/");
						httpget.setHeader("Authorization", "Bearer "
								+ bearer_token);
						HttpResponse getResponse = httpclient
								.execute(httpget);
						String nameString=  EntityUtils.toString(getResponse.getEntity());
						JSONObject nameObject = new JSONObject(nameString);
						String nameId = nameObject.getString("id");
						JSONArray profileArray= new JSONArray(nameObject.getString("profiles"));
						int size= profileArray.length();
						ArrayList<JSONObject> arrays = new ArrayList<JSONObject>();
						for(int i=0; i<size; i++){
							JSONObject object = profileArray.getJSONObject(i);
							arrays.add(object);
						}
						JSONObject profileObject=arrays.get(0);
						String profileId=profileObject.getString("id");
						MusicApp.profile_id=profileId;
						HttpGet riskGet= new HttpGet("https://api.23andme.com/1/risks/" + profileId + "/");
						riskGet.setHeader("Authorization", "Bearer "
								+ bearer_token);
						HttpResponse riskResponse = httpclient.execute(riskGet);
						// Get the response
						JSONObject risks= new JSONObject(EntityUtils.toString(riskResponse.getEntity()));
						Log.d(TAG, risks.toString());
						JSONArray riskArray = new JSONArray(risks.getString("risks"));
						Log.d("risk array", riskArray.toString());
						ArrayList<JSONObject> riskList= new ArrayList<JSONObject>();
						for(int x=0;x<riskArray.length();x++){ 
							JSONObject risk=riskArray.getJSONObject(x);
							individual_risk.put(risk.getString("description"), risk.getString("risk"));
							population_risk.put(risk.getString("description"), risk.getString("population_risk"));
						}	
						Log.d(TAG, "individual_risk: " + individual_risk.toString());
					}

				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					System.out.println("CPE" + e);
				} catch(SocketException ex)
			       {
			         Log.e("Error : " , "Error on soapPrimitiveData() " + ex.getMessage());
			           ex.printStackTrace();
			           //return "error occured";
			       } catch (JSONException e) {
					e.printStackTrace();
					//return "error occured";
				} catch (IllegalStateException e) {
					e.printStackTrace();
					//return "error occured";
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					//return "error occured";
				}
			}
			return "request complete";
		}
		
		
		
		@Override
		protected void onPostExecute(String result) {		
			super.onPostExecute(result);
			dataDialog.dismiss();
			Intent intent = new Intent(getApplicationContext(), MusicActivity.class);
			intent.putExtra("population_risk", population_risk);
			intent.putExtra("individual risk", individual_risk);
			startActivity(intent);
		}
		
		
		
	}
}