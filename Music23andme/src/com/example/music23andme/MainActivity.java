package com.example.music23andme;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import android.media.audiofx.Visualizer;
import android.media.AudioManager;

import com.leff.midi.MidiFile;
import com.leff.midi.MidiTrack;
import com.leff.midi.event.NoteOff;
import com.leff.midi.event.NoteOn;
import com.leff.midi.event.PitchBend;
import com.leff.midi.event.meta.Tempo;
import com.leff.midi.event.meta.TimeSignature;


public class MainActivity extends Activity {
	
	HashMap indriskHash;
	HashMap popriskHash;
	List<String> pop_string= new ArrayList<String>();
	List<String> ind_string=new ArrayList<String>();
	List<String> diseases= new ArrayList<String>();
	List<Double> population_risk= new ArrayList<Double>();
	List<Double> individual_risk =  new ArrayList<Double>();
	/**
	double[] riskscores={1.0,1.48,1.29,1.49,1.25,1.42,
			1.57,1.80,0.72,0.75,0.79,0.11,0.58,0.66,
			0.89,1.07,0.85,1.04,0.87,0.82,0.88,0.86,1.04,
			1.10,1.15,1.17,0.94,0.8,1,1};
	**/
	List<Double> scoresList;
	double[] riskscores;
	static int[] conintervals={2,4,5,7,9,11,12};
	static int[] disintervals={1,6,11};
	int pitch=60;
	private static final float VISUALIZER_HEIGHT_DIP = 50f;
	Random rn;
    Thread t;
 
    boolean isRunning = true;  
    boolean isPlaying=false;
    double sliderval;
    int changeFrequency;
    int bar_length;
    int risk_index=0;
    static int NaNCount;
    int index=0;
    int height = LayoutParams.WRAP_CONTENT;
    SeekBar fSlider;
    MediaPlayer mediaPlayer=new MediaPlayer();
    ImageButton startStopButton;
    ImageButton stopButton;
    SeekBar vSlider;
    VisualizerView mVisualizerView;
    private Visualizer mVisualizer;
    ImageButton connectButton;
    TextView average_risk_bar;
    TextView individual_risk_bar;   
    RelativeLayout.LayoutParams risk_bar_params;
    TextView viewinterval;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Bundle extras = getIntent().getExtras();
        viewinterval=(TextView) findViewById(R.id.textView2);     
        viewinterval.setText("");
        //data parsing
        if(extras != null){
        	Log.d("RISKS", "risk is not NULL");
        	scoresList=new ArrayList<Double>();
            indriskHash= (HashMap) extras.get("individual risk");
        	popriskHash=(HashMap) extras.get("population_risk");
        	pop_string.addAll(popriskHash.values());
        	ind_string.addAll(indriskHash.values());
        	diseases.addAll(popriskHash.keySet());

        	for(String pop_risk:pop_string){
        		population_risk.add(Double.valueOf(pop_risk));
        		Log.d("RISKS", pop_risk);
        	}
        	
        	for(String ind_risk:ind_string){
        		individual_risk.add(Double.valueOf(ind_risk));
        		Log.d("RISKS", ind_risk);
        	}
        	for(int e=0;e<individual_risk.size();e++){
        		scoresList.add(round(individual_risk.get(e)/population_risk.get(e),2));
        		/**
        		Log.d("Disease", diseases.get(e));
        		Log.d("RISK Score", String.valueOf(individual_risk.get(e)));
        		Log.d("RISK Score", String.valueOf(population_risk.get(e)));
        		**/
        		Log.d("RISK Score", String.valueOf(round(individual_risk.get(e)/population_risk.get(e),2)));
        	}
        	riskscores=new double[scoresList.size()-NaNCount];
        	for(int r=0;r<scoresList.size();r++){
        		if(scoresList.get(r)!=0.0){
        			
        			riskscores[index]=scoresList.get(r);
        			index++;       			
        		}
        	}
        	Log.d("RISK Score", Arrays.toString(riskscores));
        }
        else{
        	Log.d("RISKS", "received no data");
        } 
        if(new File("/mnt/sdcard/geneMusic.mid").exists() ){
        	DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        	    @Override
        	    public void onClick(DialogInterface dialog, int which) {
        	        switch (which){
        	        case DialogInterface.BUTTON_POSITIVE:
        	        	if(riskscores !=null){
        	            	viewinterval.setText("generating audio");
        	            	new MIDISequence().execute();        	
        	            }
        	        	else{
        	        		Toast toast=Toast.makeText(getApplicationContext(), "Login with 23andme first", Toast.LENGTH_LONG);
        	        		toast.setGravity(Gravity.CENTER, 50, 50);
        	        		toast.show();
        	        	}      	      
        	            break;

        	        case DialogInterface.BUTTON_NEGATIVE:
        	        	if(riskscores != null){       	        		
        	        		runOnExecute();
        	        	}
        	        	else{
        	        		Toast.makeText(getApplicationContext(), "Login with 23andme first", Toast.LENGTH_LONG).show();
        	        	}
        	            break;
        	        }
        	    }
        	};
        	
        	AlertDialog.Builder builder = new AlertDialog.Builder(this);
        	builder.setMessage("Do you want to generate a new music file?").setPositiveButton("Yes", dialogClickListener)
        	    .setNegativeButton("play the current one", dialogClickListener).show();
        }
               
        //set the risk bars invisible
        average_risk_bar=(TextView) findViewById(R.id.textView3);
        average_risk_bar.setVisibility(View.INVISIBLE);
        individual_risk_bar=(TextView) findViewById(R.id.textView1);
        //individual_risk_bar.setVisibility(View.INVISIBLE);
        rn = new Random();
        fSlider = (SeekBar) findViewById(R.id.frequency);        
        fSlider.setProgress(0);
        vSlider= (SeekBar) findViewById(R.id.seekBar2);
        vSlider.setMax(10);
        vSlider.setProgress(0);
        startStopButton=(ImageButton) findViewById(R.id.imageButton2);
        View activity= this.findViewById(R.id.playerActivity); 
        stopButton=(ImageButton) findViewById(R.id.imageButton1);
        RelativeLayout.LayoutParams params= new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, (int)(VISUALIZER_HEIGHT_DIP * getResources().getDisplayMetrics().density));
        params.addRule(RelativeLayout.ABOVE, R.id.textView1);
        params.setMargins(0, 0, 0, 0);
        //RelativeLayout.LayoutParams risk_bar_params= new RelativeLayout.LayoutParams(source)
        mVisualizerView = new VisualizerView(this);
        mVisualizerView.setLayoutParams(params);
        ((ViewGroup) activity).addView(mVisualizerView);
        connectButton=(ImageButton) findViewById(R.id.imageButton3);
        connectButton.setOnClickListener(new OnClickListener(){
        	
			@Override
			public void onClick(View arg0) {
				mediaPlayer.pause();
				Intent intent= new Intent(getApplicationContext(), WebViewActivity.class);
				startActivity(intent);
			}        	
        });
   };
           
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    public void onDestroy(){
          super.onDestroy();
          isRunning = false;
          try {
            t.join();
           } catch (InterruptedException e) {
             e.printStackTrace();
           }
           t = null;
     }
    
    @Override
	protected void onPause() {
		super.onPause();
		if(mediaPlayer.isPlaying()){
			mediaPlayer.pause();
		}
	}

	class MIDISequence extends AsyncTask<String,Void,String>{

		@Override
		protected String doInBackground(String... arg0) {
			MidiTrack tempoTrack = new MidiTrack();
			MidiTrack pianoTrack = new MidiTrack();
			MidiTrack pianoTrack2=new MidiTrack();
			MidiTrack pianoTrack3=new MidiTrack();
			TextView viewinterval=(TextView) findViewById(R.id.textView2);
			try {
				 TimeSignature ts = new TimeSignature();
	             ts.setTimeSignature(4, 4, TimeSignature.DEFAULT_METER, TimeSignature.DEFAULT_DIVISION);
	               
	             Tempo t = new Tempo();
	             t.setBpm(90); 
	             tempoTrack.insertEvent(ts);
	             tempoTrack.insertEvent(t);
	             int channel=0;
	             Random rn=new Random();
	             int TickCount=0;
	             PitchBend bend;
				 for (int x = 0; x < riskscores.length; x++) {
					
					if (riskscores[x] < 1) 
						for(int i=0;i<5;i++){
							if(pitch<48){
								pitch=pitch+7;
								
							}
							if(pitch>72){
								pitch=pitch-7;
							}
							pitch=AsOrDes(pitch,true,rn.nextInt(2),rn.nextInt(conintervals.length));
							NoteOn on=new NoteOn(TickCount*480,channel,pitch,100);
							NoteOn third=new NoteOn(TickCount*480,channel,pitch+4,100);
							NoteOn fifth = new NoteOn(TickCount*480,channel,pitch+7,100);
							NoteOff off=new NoteOff(TickCount*480+240,channel,pitch,0);
							NoteOff off2=new NoteOff(TickCount*480+240,channel,pitch+4,0);
							NoteOff off3=new NoteOff(TickCount*480+240,channel,pitch+7,0);
							pianoTrack.insertEvent(on);
							pianoTrack2.insertEvent(third);
							pianoTrack3.insertEvent(fifth);
							pianoTrack.insertEvent(off);
							pianoTrack2.insertEvent(off2);
							pianoTrack3.insertEvent(off3);
							TickCount++;
						}
					
					 else {
						for(int i=0;i<5;i++){
							if(pitch<40){
								pitch=pitch+7;
								
							}
							if(pitch>80){
								pitch=pitch-5;
							}
							pitch=AsOrDes(pitch,false,rn.nextInt(2),rn.nextInt(disintervals.length));
							NoteOn on=new NoteOn(TickCount*480,channel,pitch,127);
							NoteOff off=new NoteOff(TickCount*480+240,channel,pitch,0);
							NoteOff off2=new NoteOff(TickCount*480+240,channel,pitch+2,0);
							NoteOff off3=new NoteOff(TickCount*480+240,channel,pitch+3,0);
							NoteOn third=new NoteOn(TickCount*480,channel,pitch+2,50);
							NoteOn fifth = new NoteOn(TickCount*480,channel,pitch+3,50);
							pianoTrack.insertEvent(on);
							pianoTrack2.insertEvent(third);
							pianoTrack3.insertEvent(fifth);
							pianoTrack.insertEvent(off);
							pianoTrack2.insertEvent(off2);
							pianoTrack3.insertEvent(off3);
							TickCount++;
						}
						
					}
					}
					ArrayList<MidiTrack> tracks = new ArrayList<MidiTrack>();
					tracks.add(tempoTrack);
					tracks.add(pianoTrack);
					tracks.add(pianoTrack2);
					tracks.add(pianoTrack3);
					MidiFile midi = new MidiFile(MidiFile.DEFAULT_RESOLUTION, tracks);
					File mididir=Environment.getExternalStorageDirectory();
					File midiFile=new File(mididir.getAbsolutePath()+"/geneMusic.mid");
					midi.writeToFile(midiFile);
					System.out.println("midi file generated");
					if (midiFile.exists()){
						Log.d("File path",midiFile.getAbsolutePath());
					}
				return "successfully generated MIDI";
			} catch (Exception e) {
				Log.d("Audio error", e.toString());
				String lineNum=Integer.toString(getLineNumber());
				return e.toString()+" "+lineNum;
			}
		}
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			TextView viewinterval=(TextView) findViewById(R.id.textView2);
			viewinterval.setText(result);
			runOnExecute();
		}
		
		
	}
	 
	 public static int getLineNumber() {
		    return Thread.currentThread().getStackTrace()[2].getLineNumber();
		}
	
	 void runOnExecute(){
		 
		 try {
			mediaPlayer.setDataSource("/mnt/sdcard/geneMusic.mid");
			mediaPlayer.prepare();
		    mVisualizer = new Visualizer(mediaPlayer.getAudioSessionId());
		    mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
	        mVisualizer.setDataCaptureListener(new Visualizer.OnDataCaptureListener() {
	            public void onWaveFormDataCapture(Visualizer visualizer, byte[] bytes,
	                    int samplingRate) {
	                mVisualizerView.updateVisualizer(bytes);
	            }

	            public void onFftDataCapture(Visualizer visualizer, byte[] bytes, int samplingRate) {}
	        }, Visualizer.getMaxCaptureRate() / 2, true, false);
			mediaPlayer.setOnPreparedListener(new OnPreparedListener(){
				@Override
				public void onPrepared(MediaPlayer mediaPlayer) {
					mediaPlayer.start();
					Log.d("MIDITrack", "Started track" );
					TextView viewinterval=(TextView) findViewById(R.id.textView2);
					viewinterval.setText("MIDI Playback");
					fSlider = (SeekBar) findViewById(R.id.frequency);
					startStopButton.setImageDrawable(getResources().getDrawable(R.raw.ic_media_pause));
					Log.d("Progress","found progress bar");
				}
			});
			final int total=mediaPlayer.getDuration();
			fSlider.setMax(total);
			changeFrequency= total/riskscores.length;
			Log.d("Duration", Integer.toString(total));
			Log.d("Change Frequency",String.valueOf(changeFrequency));
			new progressTrack().execute();
			//startUI();
			Log.d("running threa","Thread");			 
		} catch (IllegalArgumentException e) {
			Log.d("MeidaPlayer Error",e.toString());
		} catch (SecurityException e) {
			Log.d("MeidaPlayer Error",e.toString());
		} catch (IllegalStateException e) {
			Log.d("MeidaPlayer Error",e.toString());
		} catch (IOException e) {
		
			Log.d("MeidaPlayer Error",e.toString());
		}
	 }	 	
	 
	 //randomly determines the intervals and whether the interval is ascending or descending
	 static int AsOrDes(int pitch,boolean IsCon,int bin, int rninterval){ 
		 if(bin==0){
			 if(IsCon)
				 pitch+=conintervals[rninterval];
			 else
				 pitch+=disintervals[rninterval];
		 }
		 else{
			 if(IsCon)
			 pitch=pitch-conintervals[rninterval];
			 else
			 pitch=pitch-disintervals[rninterval];
		 }
		 return pitch;
		 
	 }
	 
	
	 public static double round(double value, int places) {
		 NaNCount=0;
		 if (places < 0) throw new IllegalArgumentException();
		 if(Double.isInfinite(value) || Double.isNaN(value)){
			 NaNCount++;
			 return 0.0;
		 }
		 else{
			 BigDecimal bd = new BigDecimal(value);
			 bd = bd.setScale(places, BigDecimal.ROUND_HALF_UP);
			 return bd.doubleValue();		    	
		 }
	 }
	 
	 
	 class progressTrack extends AsyncTask<String,Void,String>{		 
				@Override
				protected String doInBackground(String... arg0) {
					System.out.println("");
					return "Starting player";
				}
				@Override
				protected void onPostExecute(String result) {
					average_risk_bar.setVisibility(View.VISIBLE);
					bar_length=average_risk_bar.getWidth();
					Log.d("Benmark length", String.valueOf(bar_length));
					risk_bar_params=new RelativeLayout.LayoutParams((int) Math.floor(bar_length*riskscores[0]),height);
					individual_risk_bar.setLayoutParams(risk_bar_params);
					individual_risk_bar.setText("Your Risk: " + String.valueOf(riskscores[risk_index]));
					individual_risk_bar.setVisibility(View.VISIBLE);
					if(riskscores[risk_index]<1){
						individual_risk_bar.setBackgroundResource(R.drawable.risk_low);
					}
					else if(riskscores[risk_index]>1 && riskscores[risk_index]<1.2){
						individual_risk_bar.setBackgroundResource(R.drawable.risk_elevated);
					}
					else if(riskscores[risk_index]>1.2){
						individual_risk_bar.setBackgroundResource(R.drawable.risk_high);
					}
					ScheduledExecutorService myScheduledExecutorService = Executors.newScheduledThreadPool(1);
					myScheduledExecutorService.scheduleWithFixedDelay(
							 new Runnable(){
							     @Override
							     public void run() {
							      monitorHandler.sendMessage(monitorHandler.obtainMessage());
							     }}, 
							          200, //initialDelay
							          200,
							          TimeUnit.MILLISECONDS//delay							          TimeUnit.MILLISECONDS
							 );					    						    						 				
				}			
	 }
	 
	 Handler monitorHandler = new Handler(){

		  @Override
		  public void handleMessage(Message msg) {
		   startUI();
		  }
		     
	 };
	 
	 void startUI(){
		 	//Log.d("UI", "Started player UI");				
			int currentPosition=0;
			int total=mediaPlayer.getDuration();
			TextView timer=(TextView) findViewById(R.id.timer);
			startStopButton=(ImageButton) findViewById(R.id.imageButton2);
			vSlider=(SeekBar) findViewById(R.id.seekBar2);
			vSlider.setMax(10);
			mVisualizer.setEnabled(true);
			if(mediaPlayer.isPlaying()){
				Log.d("Audio", "MediaPlayer audio session ID: " + mediaPlayer.getAudioSessionId());
				try {
					currentPosition = mediaPlayer
							.getCurrentPosition();
					double seconds=currentPosition/1000;
					int time= (int) Math.round(seconds);
					String timeS=Integer.toString(time);
					timer.setText(timeS+"s");					
					fSlider.setProgress(currentPosition);
					risk_index=(int) currentPosition/changeFrequency;
					individual_risk_bar.setWidth((int) Math.floor(bar_length*riskscores[risk_index]));
					Log.d("length", String.valueOf((int) Math.floor(bar_length*riskscores[risk_index])));
					//Log.d("Bar Length",String.valueOf(individual_risk_bar.getWidth()));
					individual_risk_bar.setText("Your Risk: " + String.valueOf(riskscores[risk_index]));
					//unfortunately, a switch statement can be only done on integers...
					if(riskscores[risk_index]<1){
						individual_risk_bar.setBackgroundResource(R.drawable.risk_low);
					}
					else if(riskscores[risk_index]>1 && riskscores[risk_index]<1.2){
						individual_risk_bar.setBackgroundResource(R.drawable.risk_elevated);
					}
					else if(riskscores[risk_index]>1.2){
						individual_risk_bar.setBackgroundResource(R.drawable.risk_high);
					}

				
				}  catch (Exception e) {
					
				}
				mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
		            public void onCompletion(MediaPlayer mediaPlayer) {
		                mVisualizer.setEnabled(false);
		                Intent exportIntent= new Intent(getApplicationContext(), ExportActivity.class);
		                startActivity(exportIntent);
		            }
		        });
				vSlider.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){
					
					@Override
					public void onProgressChanged(SeekBar seekBar, int progress,
							boolean fromUser) {
						float volume=(float)progress/10;
						Log.d("Progress", String.valueOf(progress));
						Log.d("Volume",String.valueOf(volume));
						mediaPlayer.setVolume(volume, volume);
						
					}
					@Override
					public void onStartTrackingTouch(SeekBar seekBar) { }
					@Override
					public void onStopTrackingTouch(SeekBar seekBar) { }						
				});
				stopButton.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View arg0) {
						mediaPlayer.stop();
					}
					
				});
				OnSeekBarChangeListener progressListener = new OnSeekBarChangeListener() {
					public void onStopTrackingTouch(SeekBar seekBar) { }
					public void onStartTrackingTouch(SeekBar seekBar) { }
					public void onProgressChanged(SeekBar seekBar, 
							int progress,
							boolean fromUser) {
						if(fromUser) 
							mediaPlayer.seekTo(progress);
					}
				};
				fSlider.setOnSeekBarChangeListener(progressListener);
				startStopButton.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View arg0) {
						mediaPlayer.pause();
						startStopButton.setImageDrawable(getResources().getDrawable(R.raw.ic_media_play));
					}					
				});
				
			}
			else{
				startStopButton.setOnClickListener(new OnClickListener(){

					@Override
					public void onClick(View arg0) {
						mediaPlayer.start();
						startStopButton.setImageDrawable(getResources().getDrawable(R.raw.ic_media_pause));
					}				
				});
			}
		
	 }




	 OnCheckedChangeListener pauseListener=(new OnCheckedChangeListener(){
		 
		 @Override
		 public void onCheckedChanged(CompoundButton buttonView,
				 boolean isChecked) {
			 if(isChecked)
				 mediaPlayer.start();
			 else
				 mediaPlayer.stop();
		 }
		 
	 });
	 
	 
	 class VisualizerView extends View {
		    private byte[] mBytes;
		    private float[] mPoints;
		    private Rect mRect = new Rect();

		    private Paint mForePaint = new Paint();

		    public VisualizerView(Context context) {
		        super(context);
		        init();
		    }

		    private void init() {
		        mBytes = null;

		        mForePaint.setStrokeWidth(1f);
		        mForePaint.setAntiAlias(true);
		        mForePaint.setColor(Color.rgb(0, 128, 255));
		    }

		    public void updateVisualizer(byte[] bytes) {
		        mBytes = bytes;
		        invalidate();
		    }

		    @Override
		    protected void onDraw(Canvas canvas) {
		        super.onDraw(canvas);

		        if (mBytes == null) {
		            return;
		        }

		        if (mPoints == null || mPoints.length < mBytes.length * 4) {
		            mPoints = new float[mBytes.length * 4];
		        }

		        mRect.set(0, 0, getWidth(), getHeight());		     
		        
		        for (int i = 0; i < mBytes.length - 1; i++) {
		            mPoints[i * 4] = mRect.width() * i / (mBytes.length - 1);
		            mPoints[i * 4 + 1] = mRect.height() / 2
		                    + ((byte) (mBytes[i] + 128)) * (mRect.height() / 2) / 128;
		            mPoints[i * 4 + 2] = mRect.width() * (i + 1) / (mBytes.length - 1);
		            mPoints[i * 4 + 3] = mRect.height() / 2
		                    + ((byte) (mBytes[i + 1] + 128)) * (mRect.height() / 2) / 128;
		        }

		        canvas.drawLines(mPoints, mForePaint);
		    }
		}
	}