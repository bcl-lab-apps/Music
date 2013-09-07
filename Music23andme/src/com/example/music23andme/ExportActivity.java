package com.example.music23andme;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Arrays;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.util.Log;
import android.view.Menu;
import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_export);
		extras = getIntent().getExtras();
		OnClickListener shareact_listener = new OnClickListener(){
			public void onClick(View v){
				Intent intent1 = new Intent(ExportActivity.this, SharingActivity.class);
				//intent1.putExtra("transfertext","Data from MainActivity");
				startActivity(intent1);
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
