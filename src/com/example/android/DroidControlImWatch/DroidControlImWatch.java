package com.example.android.DroidControlImWatch;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import org.apache.http.client.ClientProtocolException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class DroidControlImWatch extends Activity {

    private static final int REQUEST_NW_SETTING = 0;

	private int ip_ad1 = 192;
	private int ip_ad2 = 168;
	private int ip_ad3 = 44;
	private int ip_ad4 = 1;
	private int ip_port = 8081;

    private Button config_button;
	
	CheckBox mCheckBox1;
	CheckBox mCheckBox2;
	SeekBar mSeekBar;
	Socket mSocket = null;
	PrintWriter mOStream = null;
	    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
	    
        SharedPreferences preferences = getSharedPreferences("SAVED_VALUES", MODE_PRIVATE);
        ip_ad1 = preferences.getInt("ip_ad1", ip_ad1);
		ip_ad2 = preferences.getInt("ip_ad2", ip_ad2);
		ip_ad3 = preferences.getInt("ip_ad3", ip_ad3);
		ip_ad4 = preferences.getInt("ip_ad4", ip_ad4);
		ip_port = preferences.getInt("ip_port", ip_port);

        config_button = (Button)findViewById(R.id.conf);
        config_button.setOnClickListener(
        		new View.OnClickListener(){
        			public void onClick(View view){     
        				
        				Intent nw_intent = new Intent(DroidControlImWatch.this, SettingsActivity.class);
        	        	nw_intent.putExtra("ip_ad1", ip_ad1);
        	        	nw_intent.putExtra("ip_ad2", ip_ad2);
        	        	nw_intent.putExtra("ip_ad3", ip_ad3);
        	        	nw_intent.putExtra("ip_ad4", ip_ad4);
        	        	nw_intent.putExtra("ip_port", ip_port);
        				startActivityForResult(nw_intent, REQUEST_NW_SETTING);
        			}
        		}        		
        );
        
        StringBuilder sb = new StringBuilder();
        String s_dot = ".";
        sb.append(ip_ad1);
        sb.append(s_dot);
        sb.append(ip_ad2);
        sb.append(s_dot);
        sb.append(ip_ad3);
        sb.append(s_dot);
        sb.append(ip_ad4);
        String url = new String(sb);        
        String port = Integer.toString(ip_port);

		new DoRead().execute(url, port);

		mCheckBox1 = (CheckBox) findViewById(R.id.led1);
		mCheckBox1.setChecked(false);
		mCheckBox1.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				CheckBox checkBox = (CheckBox) v;
				boolean checked = checkBox.isChecked();
				if(checked)
				{
		       		if(mOStream != null){
		       			mOStream.println("l11");
		       			mOStream.flush();
	                }
				}else{
		       		if(mOStream != null){
		       			mOStream.println("l10");
		       			mOStream.flush();
	                }
				}
			}
		});
		
		mCheckBox2 = (CheckBox) findViewById(R.id.led2);
		mCheckBox2.setChecked(false);
		mCheckBox2.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				CheckBox checkBox = (CheckBox) v;
				boolean checked = checkBox.isChecked();
				if(checked)
				{
		       		if(mOStream != null){
		       			mOStream.println("l21");
		       			mOStream.flush();
	                }
				}else{
		       		if(mOStream != null){
		       			mOStream.println("l20");
		       			mOStream.flush();
	                }
				}
			}
		});
		
		mSeekBar = (SeekBar) findViewById(R.id.seekBar);
		mSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				if(mOStream != null){
					mOStream.println(String.format("s%02d", seekBar.getProgress()));
					mOStream.flush();
				}
			}
			public void onStartTrackingTouch(SeekBar seekBar) { 
			}
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
		});        
	}
	    
	@Override
	public void onDestroy() {
		if(mOStream !=null){
			mOStream.close();
		}
		if(mSocket !=null){
			try {
				mSocket.close();
			}catch(IOException e){}
		}
		super.onDestroy();
	}
	
    
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
    	case REQUEST_NW_SETTING:
    		if (resultCode == Activity.RESULT_OK) {
	    		ip_ad1 = data.getIntExtra("ip_ad1", ip_ad1);
	    		ip_ad2 = data.getIntExtra("ip_ad2", ip_ad2);
	    		ip_ad3 = data.getIntExtra("ip_ad3", ip_ad3);
	    		ip_ad4 = data.getIntExtra("ip_ad4", ip_ad4);
	    		ip_port = data.getIntExtra("ip_port", ip_port);
	    			    		
	    		SharedPreferences preferences = getSharedPreferences("SAVED_VALUES", MODE_PRIVATE);
				SharedPreferences.Editor editor = preferences.edit();
				
				editor.putInt("ip_ad1", ip_ad1);
				editor.putInt("ip_ad2", ip_ad2);
				editor.putInt("ip_ad3", ip_ad3);
				editor.putInt("ip_ad4", ip_ad4);
				editor.putInt("ip_port", ip_port);
				
				editor.commit();

				new RestartApp().execute();
    		}
    		break;
        }
    }

	public class DoRead extends AsyncTask<String, Void, Void> {
	    	 
		protected Void doInBackground(String... str) {
			try {
				mSocket = new Socket(str[0], Integer.parseInt(str[1]));
				return null;
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}

		protected void onPostExecute(Void v) {
			if(mSocket!=null){
				try{
					mOStream = new PrintWriter(mSocket.getOutputStream(), true);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}   
	
	public class RestartApp extends AsyncTask<Void, Void, Void> {
		protected Void doInBackground(Void... v) {
			DroidControlImWatch.this.finish();
			return null;
		}

		protected void onPostExecute(Void v) {
			startActivity((new Intent(DroidControlImWatch.this,DroidControlImWatch.class)));
		}
	}
}