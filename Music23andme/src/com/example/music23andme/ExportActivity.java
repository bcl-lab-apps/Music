package com.example.music23andme;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Arrays;

import com.facebook.*;
import com.facebook.widget.*;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.util.Log;
import android.view.Menu;
import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import android.os.Environment;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class ExportActivity extends Activity {
	MediaPlayer mp;
    TextView tv;
    Visualizer mVisualizer;
    int len; 
    Button buttonExport;
    Button buttonShare;
    Button buttonSave;
    double[] riskDouble;
    String[] riskScores;
	String[] diseases;
	Bundle extras;
	ProgressDialog dialog;
	
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
		overridePendingTransition(R.anim.fadein, R.anim.fadeout);
	    uiHelper.onCreate(savedInstanceState);
	    
		setContentView(R.layout.activity_export);
		extras = getIntent().getExtras();
		OnClickListener shareact_listener = new OnClickListener(){
			public void onClick(View v){
				if (FacebookDialog.canPresentShareDialog(getApplicationContext(), 
		                FacebookDialog.ShareDialogFeature.SHARE_DIALOG)) {
		    		FacebookDialog shareDialog = new FacebookDialog.ShareDialogBuilder(ExportActivity.this)
		    		.setLink("https://developers.facebook.com/android")
		    		.build();
		    		uiHelper.trackPendingDialogCall(shareDialog.present());
		    	}
			}
		};
		buttonShare = (Button)findViewById(R.id.facebook);
		buttonShare.setOnClickListener(shareact_listener);
		if(extras != null){
			Log.d("EXTRAS", "bundle is not null");
			Log.d("EXTRAS", Arrays.toString((String[])extras.get("diseases")));
		}
		else{
			Log.d("EXTRAS", "bundle is null");
		}
		File mididir=Environment.getExternalStorageDirectory();
		File wavFile=new File(mididir.getAbsolutePath()+"/geneMusic.wav");
		try {
			FileOutputStream out = new FileOutputStream(wavFile);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		buttonSave= (Button) findViewById(R.id.save);
		Log.d("risk scores", Arrays.toString((double[])extras.get("risk_scores")));
		
		buttonSave.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				Log.d("data storage", "onClicked");
				riskDouble= (double[]) extras.get("risk_scores");
				Log.d("risk scores", Arrays.toString((double[])extras.get("risk_scores")));
				Log.d("risk scores array", Arrays.toString(riskDouble));
				diseases= (String[]) extras.get("diseases");
				riskScores=new String[riskDouble.length];
				for( int r=0; r<riskDouble.length;r++ ){
						riskScores[r]=Double.toString(riskDouble[r]); 
				}
				String result_score = ("" + Arrays.asList(riskScores)).
			             replaceAll("(^.|.$)", "  ").replace(", ", "  , " );
				String result_diseases=("" + Arrays.asList(diseases)).
			             replaceAll("(^.|.$)", "  ").replace(", ", "  , " );
				Log.d("String Args",result_score);
				Log.d("String Args",result_diseases);
				dialog= ProgressDialog.show(ExportActivity.this, "Saving Data", "Please wait...", true);
				new saveData().execute(result_score,result_diseases);
			}
			
		});
		
	}
	
    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        if ((exception instanceof FacebookOperationCanceledException) ||
                (exception instanceof FacebookAuthorizationException)) {
                new AlertDialog.Builder(ExportActivity.this)
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
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.export, menu);
		return true;
	}
	
	class ExportWav extends AsyncTask<String,Void,String>{

		@Override
		protected String doInBackground(String... arg0) {

			return null;
		}
		
	}
	
	class saveData extends AsyncTask<String, Void, String>{


		@Override
		protected String doInBackground(String... args) {
			Log.d("data storage", "background thread started");
			RiskData riskData= new RiskData(getApplicationContext());			
			riskData.insert(args[0], args[1]);
			dialog.dismiss();
			return null;
		}
		
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			Toast toast=Toast.makeText(getApplicationContext(), "results saved", Toast.LENGTH_SHORT);
			toast.show();
		}
	}
	
	private void PassData(byte[] data) {
		InputStream byteArray = new ByteArrayInputStream(data);;
		


    }
	
	public void init_visualizer() {
        mVisualizer = new Visualizer(mp.getAudioSessionId());
        mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);

        Visualizer.OnDataCaptureListener captureListener = new Visualizer.OnDataCaptureListener() {
            @Override
            public void onWaveFormDataCapture(Visualizer visualizer,
                    byte[] bytes, int samplingRate) {
                PassData(bytes);
            }

            @Override
            public void onFftDataCapture(Visualizer visualizer, byte[] bytes,
                    int samplingRate) {

            }
        };

        mVisualizer.setDataCaptureListener(captureListener,
                Visualizer.getMaxCaptureRate(), true, false);

        mVisualizer.setEnabled(true);
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                mVisualizer.setEnabled(false);
            }
        });
    }

}
