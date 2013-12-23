package com.bcl.music23andme;

import com.bcl.music23andme.R;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class ErrorActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.errorpage);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.error, menu);
		return true;
	}

}
