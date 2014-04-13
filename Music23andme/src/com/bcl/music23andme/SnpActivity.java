package com.bcl.music23andme;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.app.ListActivity;
import android.util.Log;
import android.view.Menu;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.view.View;

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

public class SnpActivity extends ListActivity {
	
	List<String> SNPs= new ArrayList<String>();
	final String TAG= "SNP Activity";
	String requestString="";
	ListView LV;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle extras=getIntent().getExtras();
		Log.d(TAG, "SNPs: "+ SNPs.toString());
		setListAdapter(new ArrayAdapter<String>(this, R.layout.activity_list,R.id.label,(String[]) extras.get("SNPs")));
		SNPs.addAll((List)Arrays.asList(extras.get("SNPs")));
		LV=getListView();
		LV.setBackgroundResource(R.drawable.dnabg);
		//setListAdapter(new MobileArrayAdapter(this, MOBILE_OS));
		HttpGet httpget = new HttpGet(
				"https://api.23andme.com/1/genotypes/"+ MusicApp.profile_id);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.list, menu);
		return true;
	}

}
