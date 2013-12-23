package com.bcl.music23andme;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.bcl.music23andme.R;
import com.facebook.*;
import com.facebook.widget.*;

public class MainActivity extends Activity {
	OnClickListener login23_listener = null;
	OnClickListener music_listener = null;
	OnClickListener loginfb_listener = null;
	Button button_login23w;
	Button button_musicw;
	Button button_loginfbw;
	Button button_demo;
	
	private UiLifecycleHelper uiHelper;
	
	private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		uiHelper = new UiLifecycleHelper(this, callback);
	    uiHelper.onCreate(savedInstanceState);
	    overridePendingTransition(R.anim.fadein, R.anim.fadeout);
		
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
				if (FacebookDialog.canPresentShareDialog(getApplicationContext(), 
		                FacebookDialog.ShareDialogFeature.SHARE_DIALOG)) {
		    		FacebookDialog shareDialog = new FacebookDialog.ShareDialogBuilder(MainActivity.this)
		    		.setLink("https://developers.facebook.com/android")
		    		.build();
		    		uiHelper.trackPendingDialogCall(shareDialog.present());
		    	}
			}
		};
		setContentView(R.layout.activity_main);
		button_login23w = (Button)findViewById(R.id.button_login23);
		button_login23w.setOnClickListener(login23_listener);
		button_musicw = (Button)findViewById(R.id.button_music);
		button_musicw.setOnClickListener(music_listener);
		button_loginfbw = (Button)findViewById(R.id.button_loginfb);
		button_loginfbw.setOnClickListener(loginfb_listener);
		button_demo= (Button) findViewById(R.id.playDemo);
		button_demo.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				Intent demoIntent= new Intent(getApplicationContext(), MusicActivity.class);
				demoIntent.putExtra("demo", true);
				startActivity(demoIntent);
			}
			
		});
	} 
	
    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        if ((exception instanceof FacebookOperationCanceledException) ||
                (exception instanceof FacebookAuthorizationException)) {
                new AlertDialog.Builder(MainActivity.this)
                    .setTitle(R.string.cancelled)
                    .setMessage(R.string.permission_not_granted)
                    .setPositiveButton(R.string.ok, null)
                    .show();
        }
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
	protected void onResume() {
	    super.onResume();
	    uiHelper.onResume();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
	    super.onSaveInstanceState(outState);
	    uiHelper.onSaveInstanceState(outState);
	}

	@Override
	public void onPause() {
	    super.onPause();
	    uiHelper.onPause();
	    overridePendingTransition(R.anim.fadein, R.anim.fadeout);
	}

	@Override
	public void onDestroy() {
	    super.onDestroy();
	    uiHelper.onDestroy();
	}
	
}