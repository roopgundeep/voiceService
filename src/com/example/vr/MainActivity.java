package com.example.vr;
 
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Locale;

import com.example.vr.R;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.media.AudioManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.telephony.SmsManager; 
/**
 * A very simple application to handle Voice Recognition intents
 * and display the results
 */
public class MainActivity extends Activity implements TextToSpeech.OnInitListener 
{
 
	final Context context= this;
    private static final int REQUEST_CODE = 1234;
    private ListView wordsList;
    BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    private TextToSpeech mText2Speech;
    public static int flag = 0;
    public static final String CMDTOGGLEPAUSE = "togglepause";
    public static final String CMDPAUSE = "pause";
    public static final String CMDPREVIOUS = "previous";
    public static final String CMDNEXT = "next";
    public static final String SERVICECMD = "com.android.music.musicservicecommand";
    public static final String CMDNAME = "command";
    public static final String CMDSTOP = "stop";
    public static final String CMDPLAY = "start";
    
    /**
     * Called with the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.voice_recog);
        
        PhoneCallListener phoneListener = new PhoneCallListener();        
		TelephonyManager telephonyManager = (TelephonyManager) this
			.getSystemService(Context.TELEPHONY_SERVICE);
		telephonyManager.listen(phoneListener,PhoneStateListener.LISTEN_CALL_STATE);
	
        Button speakButton = (Button) findViewById(R.id.speakButton);
 
        wordsList = (ListView) findViewById(R.id.list);
 
        // Disable button if no recognition service is present
        PackageManager pm = getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(
                new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
        if (activities.size() == 0)
        {
            speakButton.setEnabled(false);
            speakButton.setText("Recognizer not present");
        }
        mText2Speech = new TextToSpeech(MainActivity.this, MainActivity.this);
       // startVoiceRecognitionActivity();
    }
 
    /**
     * Handle the action of the button being clicked
     */
    public void speakButtonClicked(View v)
    {
       startVoiceRecognitionActivity();
    }
    public void sp(String speakText)
    {
    	
		mText2Speech.speak(speakText, TextToSpeech.QUEUE_FLUSH, null);
        //startVoiceRecognitionActivity();
    }
    /**
     * Fire an intent to start the voice recognition activity.
     */
    private void startVoiceRecognitionActivity()
    {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Voice recognition Demo...");
        startActivityForResult(intent, REQUEST_CODE);
    }
    
    /**
     * Handle the results from the voice recognition activity.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK)
        {
            // Populate the wordsList with the String values the recognition engine thought it heard
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String[] stockArr = new String[matches.size()];
            stockArr = matches.toArray(stockArr);
            for(String s : stockArr)
            {            
            	String[] parts = s.split(" ");
            	int ff=0;
            	for(String part : parts){
            		if(part.equals("bluetooth")){
            			for(String part1: parts){            				
            				if(part1.equals("on")){
            					bluetoothOn();
            					ff=1;//call bluetooth On function
            					break;
            				}
            				else if(part1.equals("off")){
            					bluetoothOff();
            					ff=1;//call of bluetooth off
            					break;
            				}
            			}
            			if(ff==1)
            				break;
            			
            		}
            		else if(part.equals("wifi")){
            			for(String part1: parts){            				
            				if(part1.equals("on")){
            					wifiOn();
            					//sp("WIFI is ON");//call wifi On function
            					break;
            				}
            				else if(part1.equals("off")){
            					wifiOff();
            					//sp("WIFI is Off");//call of wifi off
            					break;
            				}
            			}
            			if(ff==1)
            				break;
            			
            		}
            		else if(part.equals("silent")){
            			setSilent();
            		}
            		else if(part.equals("vibrate")){
            			setVibrate();        			
            		}
            		else if(part.equals("ring")){
            			setRinging(); 
            		}
            		else if(part.equals("camera")){
            			camera(); 
            		}
            		else if(part.equals("pause")){
            			controlMusic("pause"); 
            		}
            		else if(part.equals("next")){
            			controlMusic("next"); 
            		}
            		else if(part.equals("previous")){
            			controlMusic("previous"); 
            			controlMusic("previous"); 
            			
            		}
            		else if(part.equals("stop")){
            			controlMusic("stop"); 
            		}
            		else if(part.equals("play")){
            			controlMusic("play"); 
            		}
            		else if(part.equals("call")){
            			make_call(); 
            		}
            		else if(part.equals("message")){
            			String[] part11 = s.split("saying");
            			Log.i(part11[0],part11[1]);
            			sendSMS(part11[1]); 
            		}
            		else if(part.equals("google")){
            			String[] part11 = s.split("for");
            			Intent intent = new Intent(this,WebViewActivity.class);
            			intent.putExtra("message", part11[1]);
            			startActivity(intent);
            		}
            		else if(part.equals("wait")){
            			try {
                            synchronized(this){
                                wait(3000);
                            }
                        }
                        catch(InterruptedException ex){                    
                        }
            		}
            		else if(part.equals("no")){
            			MainActivity.this.finish();
            		}
            		else{
            			sp("I don't know what you mean . Can I assest with anything else ?");
            		}

            	}
            	break;
            }
            wordsList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,matches));
            
        }
        super.onActivityResult(requestCode, resultCode, data);
        
        //sp("wait");
        
        //startVoiceRecognitionActivity();
    }
    private void bluetoothOn()
    {
    	if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
        	
        }
        else if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            int REQUEST_ENABLE_BT=1;
			startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
			sp("Do you want to switch On bluetooth ?");
        }
        else{
        	sp("Bluetooth is already switched on");
        	//Toast.makeText(getApplicationContext(), "Bluetooth is already ON", Toast.LENGTH_LONG).show();
        }
      }
    private void bluetoothOff()
    {
    	 if(mBluetoothAdapter.isEnabled()){
    		
         	mBluetoothAdapter.disable();
         	sp("Bluetooth has been switched off");
         }
    	 else{
    		 sp("Bluetooth is already switched off");
    		 //Toast.makeText(getApplicationContext(), "Bluetooth is already OFF", Toast.LENGTH_LONG).show();
          }
    }
    
    private void wifiOff()
    {
    	final WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        //wifi.setWifiEnabled(!wifi.isWifiEnabled());
        if(wifi.isWifiEnabled()){
    		wifi.setWifiEnabled(false);
    		 sp("wifi has been been switched off");
    	}
        else{
        	 sp("wifi is already switched off");
        	 //Toast.makeText(getApplicationContext(), "WIFI is already OFF", Toast.LENGTH_LONG).show();
        }
    }
    private void wifiOn()
    {
    	final WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
    	if(!wifi.isWifiEnabled()){
    		wifi.setWifiEnabled(true);
    		sp("wifi has been switched on");
    	}
    	else{
    		sp("wifi is already switched on");
    		 //Toast.makeText(getApplicationContext(), "WIFI is already ON", Toast.LENGTH_LONG).show();
    	}
    }    
       
    private void setVibrate()
    {    
    	
    	AudioManager audiomanage = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
    	switch (audiomanage.getRingerMode()) {
    	    case AudioManager.RINGER_MODE_SILENT:
    	    	audiomanage.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
    	    	sp("Set to vibration mode");
    	        break;
    	    case AudioManager.RINGER_MODE_VIBRATE:
    	    	//Toast.makeText(getApplicationContext(), "Its on Vibration Mode", Toast.LENGTH_LONG).show();
    	    	sp("Already in vibration mode");
    	        break;
    	    case AudioManager.RINGER_MODE_NORMAL:    	    	
    	    	audiomanage.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
    	    	sp("Set to vibration mode");
    	        break;
    	}
    	
    	
    }
    private void setSilent()
    {
    	AudioManager audiomanage = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
    	switch (audiomanage.getRingerMode()) {
    	    case AudioManager.RINGER_MODE_SILENT:
    	    	//Toast.makeText(getApplicationContext(), "Its on Silent Mode", Toast.LENGTH_LONG).show();
    	    	sp("Already in silent mode");
    	        break;
    	    case AudioManager.RINGER_MODE_VIBRATE:
    	    	audiomanage.setRingerMode(AudioManager.RINGER_MODE_SILENT);
    	    	sp("Set to silent mode");
    	        break;
    	    case AudioManager.RINGER_MODE_NORMAL:
    	    	audiomanage.setRingerMode(AudioManager.RINGER_MODE_SILENT);
    	    	sp("Set to silent mode");
    	        break;
    	}
    	
    }
    private void setRinging()
    {   
    	
    	AudioManager audiomanage = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
    	switch (audiomanage.getRingerMode()) {
    	    case AudioManager.RINGER_MODE_SILENT:
    	    	audiomanage.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
    	    	sp("Set to ringing mode");
    	        break;
    	    case AudioManager.RINGER_MODE_VIBRATE:
    	    	audiomanage.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
    	    	sp("Set to ringing mode");
    	        break;
    	    case AudioManager.RINGER_MODE_NORMAL:
    	    	sp("Already in ringing mode");
    	    	//Toast.makeText(getApplicationContext(), "Its on Ringing Mode", Toast.LENGTH_LONG).show();
    	        break;
    	}
    
    }
    private void camera()
    {       	
    	Intent intent = new Intent("android.media.action.IMAGE_CAPTURE"); startActivityForResult(intent, 0);
    }
       
    private void controlMusic(String value)
    {   
    	 AudioManager mAudioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
    	    if(mAudioManager.isMusicActive()) {
    	    	
    	        Intent i = new Intent(SERVICECMD);
    	        if(value.equals("pause")){
    	        	i.putExtra(CMDNAME , CMDPAUSE );
    	        	sp("Music Paused");
    	        }    	   
    	        else if(value.equals("next")){
    	        	i.putExtra(CMDNAME , CMDNEXT );
    	        	sp("Playing Next track");
    	        }
    	        else if(value.equals("previous")){
    	        	i.putExtra(CMDNAME , CMDPREVIOUS );    	        
    	        	sp("Playing previous track");
    	        }
    	        else if(value.equals("stop")){
    	        	i.putExtra(CMDNAME , CMDSTOP );
    	        	sp("Music Stopped");
    	        }
    	        else if(value.equals("play")){
    	        	i.putExtra(CMDNAME , CMDPLAY );
    	        }  
    	        MainActivity.this.sendBroadcast(i);
    	      
    	    } 	    	
    }

private class PhoneCallListener extends PhoneStateListener {
    	 
		private boolean isPhoneCalling = false;
 
		String LOG_TAG = "LOGGING 123";
 
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
 
			if (TelephonyManager.CALL_STATE_RINGING == state) {
				// phone ringing
				Log.i(LOG_TAG, "RINGING, number: " + incomingNumber);
			}
 
			if (TelephonyManager.CALL_STATE_OFFHOOK == state) {
				// active
				Log.i(LOG_TAG, "OFFHOOK");
 
				isPhoneCalling = true;
			}
 
			if (TelephonyManager.CALL_STATE_IDLE == state) {
				// run when class initial and phone call ended, 
				// need detect flag from CALL_STATE_OFFHOOK
				Log.i(LOG_TAG, "IDLE");
 
				if (isPhoneCalling) {
 
					Log.i(LOG_TAG, "restart app");
 
					// restart app
					Intent i = getBaseContext().getPackageManager()
						.getLaunchIntentForPackage(
							getBaseContext().getPackageName());
					i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(i);
 
					isPhoneCalling = false;
				}
 
			}
		}
	}
	private void make_call()
	{
	
	Intent callIntent = new Intent(Intent.ACTION_CALL);
	callIntent.setData(Uri.parse("tel:8886545470"));
	startActivity(callIntent);
	}

	@Override
	public void onInit(int status) {
		
		if(status == TextToSpeech.SUCCESS) {
            mText2Speech.setLanguage(Locale.getDefault());
        }
		String speakText="How Can I help you";
		if(flag==0)
			mText2Speech.speak(speakText, TextToSpeech.QUEUE_FLUSH, null);
			flag=1;		
	}
	public void sendSMS(String message) {
	    String phoneNumber = "8886545470";
	    SmsManager smsManager = SmsManager.getDefault();
	    smsManager.sendTextMessage(phoneNumber, null, message, null, null);
	}
}
