package com.example.music23andme;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity {
	OnClickListener login23_listener = null;
	OnClickListener music_listener = null;
	OnClickListener loginfb_listener = null;
	Button button_login23w;
	Button button_musicw;
	Button button_loginfbw;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		login23_listener = new OnClickListener() {
			public void onClick(View v) {
				Intent intent0 = new Intent(MainActivity.this, WebViewActivity.class);
				startActivity(intent0);
			}
		};
		music_listener = new OnClickListener() {
			public void onClick(View v) {
				Intent intent1 = new Intent(MainActivity.this, MusicActivity.class);
				startActivity(intent1);
			}
		};
		loginfb_listener = new OnClickListener() {
			public void onClick(View v) {
				Intent intent2 = new Intent(MainActivity.this, SharingActivity.class);
				startActivity(intent2);
			}
		};
		setContentView(R.layout.activity_main);
		button_login23w = (Button)findViewById(R.id.button_login23);
		button_login23w.setOnClickListener(login23_listener);
		button_musicw = (Button)findViewById(R.id.button_music);
		button_musicw.setOnClickListener(music_listener);
		button_loginfbw = (Button)findViewById(R.id.button_loginfb);
		button_loginfbw.setOnClickListener(loginfb_listener);
		
	}
	
	}