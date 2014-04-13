package com.bcl.music23andme;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class DiseaseListActivity extends ListActivity {
	String TAG="Disease Activity";
	TextView name;
	ListView LV;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_disease_list);
		overridePendingTransition(R.anim.fadein, R.anim.fadeout);
		final MusicApp app= (MusicApp) this.getApplication();
		ArrayList<String> diseases=new ArrayList<String>();
		//diseases.addAll(app.SNPs.keySet().toArray());
		LV = getListView();
		LV.setBackgroundResource(R.drawable.dnabg);
		String[] diseaseArray= app.SNPs.keySet().toArray(new String[app.SNPs.keySet().size()]);
		setListAdapter(new MobileArrayAdapter(this, diseaseArray));
		//setListAdapter(new ArrayAdapter<String>(this, R.layout.activity_list,R.id.label,(String[]) app.SNPs.keySet().toArray(new String[app.SNPs.keySet().size()])));
		Log.d(TAG, "array adapter set");
		ListView listView = getListView();
		listView.setTextFilterEnabled(true);
		name=(TextView) findViewById(R.id.label);
		
		
		
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
			    Toast.makeText(getApplicationContext(),
				"get SNP genotypes", Toast.LENGTH_SHORT).show();
			    String disease=(String) getListAdapter().getItem(position);
			    Log.d(TAG, "clicked: "+disease);
			    Intent intent=new Intent(getApplicationContext(),SnpActivity.class);
			    String[] dArray=app.SNPs.get(disease).toArray(new String[app.SNPs.get(disease).size()]);
			    Log.d(TAG, "array: "+ dArray.toString());
			    intent.putExtra("SNPs", dArray);
			    startActivity(intent);
			}
		});
	}
	/**
	 @Override
	  protected void onListItemClick(ListView l, View v, int position, long id) {
	    String item = (String) getListAdapter().getItem(position);
	    Toast.makeText(this, item + " selected", Toast.LENGTH_LONG).show();
	  }
	  **/
	 @Override
		protected void onListItemClick(ListView l, View v, int position, long id) {
	 
			//get selected items
			String selectedValue = (String) getListAdapter().getItem(position);
			Toast.makeText(this, selectedValue, Toast.LENGTH_SHORT).show();
	 
		}
	 

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.disease_list, menu);
		return true;
	}

}
